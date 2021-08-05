// Copyright 2001 by
// DANTE e.V. and any individual authors listed elsewhere in this file.
//
// This file is part of the NTS system.
// ------------------------------------
//
// It may be distributed and/or modified under the
// conditions of the NTS Public License (NTSPL), either version 1.0
// of this license or (at your option) any later version.
// The latest version of this license is in
//    http://www.dante.de/projects/nts/ntspl.txt
// and version 1.0 or later is part of all distributions of NTS
// version 1.0-beta or later.
//
// The list of all files belonging to the NTS distribution is given in
// the file `manifest.txt'.
//
// Filename: nts/align/Alignment.java
// $Id: Alignment.java,v 1.1.1.1 2001/04/20 05:18:15 ksk Exp $
package nts.align;

import java.io.ObjectStreamException;
import java.io.Serializable;
import nts.base.Dimen;
import nts.base.Glue;
import nts.builder.Builder;
import nts.command.BraceNesting;
import nts.command.Closing;
import nts.command.Command;
import nts.command.CommandBase;
import nts.command.Group;
import nts.command.InpTokChecker;
import nts.command.InsertedTokenList;
import nts.command.SimpleGroup;
import nts.command.Token;
import nts.command.TokenList;
import nts.node.AnyBoxNode;
import nts.node.AnySkipNode;
import nts.node.BoxSizes;
import nts.node.GlueSetting;
import nts.node.HBoxNode;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.RuleNode;
import nts.node.SizesEvaluator;
import nts.node.VShiftNode;
import nts.typo.Paragraph;
import nts.typo.TypoCommand;

public abstract class Alignment extends CommandBase {

  public static final Alignment NULL = null;

  protected final Dimen desired;
  protected final boolean exactly;
  protected final TokenList.Inserter everyCr;
  protected final Token frozenCr;
  protected final Token frozenEndTemplate;

  public Alignment(
      Dimen desired, boolean exactly, TokenList.Inserter everyCr, Token frzCr, Token frzEndt) {
    this.desired = desired;
    this.exactly = exactly;
    this.everyCr = everyCr;
    this.frozenCr = frzCr;
    this.frozenEndTemplate = frzEndt;
  }

  protected Preamble preamble;
  protected AlignSizesMatrix maxSizes;
  protected int currColumn;
  protected int currSpan;

  /* TeXtp[774] */
  public void start(Token src) {
    push();
    preamble = scanPreamble(src);
    maxSizes = new AlignSizesMatrix(preamble.length());
    everyCr.insertToks();
    nextRow();
  }

  public static final int GLUEP_TAB_SKIP = newGlueParam();

  /* TeXtp[777] */
  protected Preamble scanPreamble(Token src) {
    Command.Config cfg = getConfig();
    PreambleScanning scan = new PreambleScanning(src, frozenCr);
    InpTokChecker savedChk = setTokenChecker(scan);
    BraceNesting savedBrn = setBraceNesting(scan);
    boolean afterAssign = cfg.enableAfterAssignment(false);
    Glue firstSkip = cfg.getGlueParam(GLUEP_TAB_SKIP);
    String skipName = cfg.getGlueName(GLUEP_TAB_SKIP);
    while (scanRecord(scan))
      ;
    setBraceNesting(savedBrn);
    setTokenChecker(savedChk);
    cfg.enableAfterAssignment(afterAssign);
    return scan.toPreamble(firstSkip, skipName, frozenEndTemplate);
  }

  /* TeXtp[779,783,784] */
  protected boolean scanRecord(PreambleScanning scan) {
    Token tok;
    Command cmd;
    for (boolean fresh = true; ; ) {
      tok = getPreambleToken();
      cmd = meaningOf(tok);
      if (cmd.isMacroParam()) break;
      else if (scan.balanced() && (cmd.isTabMark() || cmd.isCarRet())) {
        if (!(fresh && cmd.isTabMark() && scan.setLoopIndex())) {
          backToken(tok);
          error("MissingSharp");
          break;
        }
      } else if (!(fresh && cmd.isSpacer())) {
        scan.append(tok);
        fresh = false;
      }
    }
    scan.finishHalf();
    for (; ; ) {
      tok = getPreambleToken();
      cmd = meaningOf(tok);
      if (scan.balanced() && (cmd.isTabMark() || cmd.isCarRet())) break;
      else if (cmd.isMacroParam()) error("SecondSharpInTab");
      else scan.append(tok);
    }
    scan.append(frozenEndTemplate);
    scan.finishRecord(getConfig().getGlueParam(GLUEP_TAB_SKIP));
    return (!cmd.isCarRet());
  }

  /* TeXtp[782] */
  protected Token getPreambleToken() {
    for (; ; ) {
      Token tok = nextRawToken();
      while (meaningOf(tok).isSpan()) tok = nextExpToken();
      // XXX[782] curr_cmd = endv
      Command cmd = meaningOf(tok);
      if (cmd.isTabSkip()) cmd.exec(tok);
      else return tok;
    }
  }

  /* TeXtp[785,786] */
  protected void nextRow() {
    for (; ; ) {
      Token tok = nextExpNonSpacer();
      Command cmd = meaningOf(tok);
      if (cmd.isCrCr()) continue;
      else if (cmd.isNoAlign()) startNonAligned();
      else if (cmd.isRightBrace()) stop();
      else {
        pushNewRowBuilder();
        addNamedSkipToRow(preamble.firstSkip, preamble.skipName);
        startSpan(0);
        currColumn = 0;
        startColumn(tok, cmd);
      }
      break;
    }
  }

  protected void startNonAligned() {
    scanLeftBrace();
    pushLevel(new NoAlignGroup());
  }

  /* TeXtp[787] */
  protected void startSpan(int index) {
    // System.err.println("=== startSpan(" + index + ')');
    pushLevel(new SpanGroup());
    pushNewSpanBuilder();
    currSpan = index;
  }

  /* TeXtp[788] */
  protected void startColumn(Token tok, Command cmd) {
    // System.err.println("=== startColumn(" + tok + ") [" + currColumn + ')');
    if (cmd.isOmit()) startColumnBody(false);
    else {
      backToken(tok);
      getTokStack().push(new TemplateTokenizer(preamble.getUPart(currColumn)));
    }
  }

  public static final class ColumnEnding implements Serializable {

    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;
    private final String name;
    private static ColumnEnding[] VALS = new ColumnEnding[3];

    private ColumnEnding(String name) {
      this.name = name;
      VALS[ordinal] = this;
    }

    public String toString() {
      return name;
    }

    private Object readResolve() throws ObjectStreamException {
      return VALS[ordinal];
    }
  }

  public static final ColumnEnding SPAN_ENDING = new ColumnEnding("SPAN_ENDING"),
      TAB_ENDING = new ColumnEnding("TAB_ENDING"),
      CR_ENDING = new ColumnEnding("CR_ENDING"),
      NULL_ENDING = null;

  protected ColumnBraceNesting columnBalance = NULL_COLUMN_NESTING;
  protected BraceNesting savedColumnNesting;
  protected boolean insertTemplate;
  protected ColumnEnding columnEnding = NULL_ENDING;

  protected boolean balancedColumn() {
    return (columnBalance != NULL_COLUMN_NESTING && columnBalance.balanced());
  }

  public static final int NOALIGN_DISBALANCE = 1000000;

  protected int columnDisbalance() {
    return (columnBalance != NULL_COLUMN_NESTING) ? columnBalance.disbalance() : NOALIGN_DISBALANCE;
  }

  /* TeXtp[788] */
  protected void startColumnBody(boolean insert) {
    // System.err.println("=== startColumnBody()");
    columnBalance = new ColumnBraceNesting();
    savedColumnNesting = setBraceNesting(columnBalance);
    insertTemplate = insert;
  }

  /* TeXtp[789] */
  protected void finishColumnBody(ColumnEnding ending) {
    // System.err.println("=== finishColumnBody()");
    if (columnBalance == NULL_COLUMN_NESTING) throw new RuntimeException("no column scanned");
    // XXX[789] if (scanner_status=aligning)
    setBraceNesting(savedColumnNesting);
    savedColumnNesting = BraceNesting.NULL;
    columnBalance = NULL_COLUMN_NESTING;
    if (insertTemplate) pushList(preamble.getVPart(currColumn), "template");
    else pushToken(preamble.endTemplate, "template");
    columnEnding = ending;
  }

  /* TeXtp[791,799] */
  protected void finishColumn() {
    // System.err.println("=== finishColumn() [" + currColumn + ')');
    // XXX[791] if (align_state < 500000)
    if (columnEnding == NULL_ENDING)
      throw new RuntimeException("column body not started and finished");
    if (columnEnding != CR_ENDING && !preamble.hasRecord(currColumn + 1)) {
      error("ExtraAlignTab", frozenCr);
      columnEnding = CR_ENDING;
    }
    if (columnEnding != SPAN_ENDING) {
      popLevel();
      // System.err.println("=== finishSpan(" + currSpan + ')');
      maxSizes.setMax(currSpan, currColumn, packedSpanSize(currColumn - currSpan));
      addNamedSkipToRow(preamble.getSkip(currColumn), preamble.skipName);
      if (columnEnding == CR_ENDING) {
        packRow();
        everyCr.insertToks();
        nextRow();
        return;
      }
      startSpan(currColumn + 1);
    }
    Token tok = nextExpNonSpacer();
    Command cmd = meaningOf(tok);
    ++currColumn;
    startColumn(tok, cmd);
    columnEnding = NULL_ENDING;
  }

  protected void stop() {
    Builder.pop();
    popLevel();
  }

  /* TeXtp[800] */
  public NodeEnum finish(Dimen indent) {
    pop();
    calculateWidths();
    return setAndTransform(indent).nodes();
  }

  /* TeXtp[801,803] */
  protected void calculateWidths() {
    for (int k = 0, i = 1; i < maxSizes.size(); k = i++) {
      Dimen tw = maxSizes.get(k);
      tw = (tw == Dimen.NULL) ? Dimen.ZERO : tw.plus(preamble.getSkip(k).getDimen());
      for (int j = i; j < maxSizes.size(); j++) {
        Dimen w = maxSizes.get(k, j);
        if (w != Dimen.NULL) maxSizes.setMax(i, j, w.minus(tw));
      }
    }
  }

  /* TeXtp[804] */
  protected NodeList setAndTransform(Dimen indent) {
    SizesEvaluator pack = new SizesEvaluator();
    pack.add(preamble.firstSkip);
    for (int i = 0; i < maxSizes.size(); i++) {
      Dimen w = maxSizes.get(i);
      if (w != Dimen.NULL) {
        pack.add(w);
        pack.add(preamble.getSkip(i));
      }
    }
    Dimen size = pack.getBody();
    if (exactly) {
      pack.evaluate(desired.minus(size), false);
      size = desired;
    } else {
      pack.evaluate(desired, false);
      size = size.plus(desired);
    }
    check(pack, size);
    return transform(size, pack.getSetting(), indent);
  }

  /* TeXtp[805,806] */
  protected NodeList transform(Dimen size, GlueSetting setting, Dimen indent) {
    BoxSizes around = transformSizes(BoxSizes.ZERO, size);
    NodeList list = new NodeList();
    NodeEnum nodes = getUnsetNodes();
    while (nodes.hasMoreNodes()) {
      Node node = nodes.nextNode();
      if (node instanceof AnyUnsetNode)
        node = transform((AnyUnsetNode) node, size, setting, indent);
      else if (node instanceof RuleNode) {
        // XXX[806] not general, fix it!!!
        BoxSizes sizes = ((RuleNode) node).getSizes();
        BoxSizes full = sizes.replenished(around);
        if (!sizes.equals(full)) node = new RuleNode(full);
        if (!indent.isZero()) {
          node = HBoxNode.packedOf(node);
          node = VShiftNode.shiftingRight(node, indent);
        }
      }
      list.append(node);
    }
    return list;
  }

  /* TeXtp[807] */
  protected Node transform(AnyUnsetNode row, Dimen size, GlueSetting setting, Dimen indent) {
    NodeList list = new NodeList();
    NodeEnum nodes = row.getList().nodes();
    list.append(nodes.nextNode());
    for (int i = 0; nodes.hasMoreNodes(); i++) {
      i = transform((AnyUnsetNode) nodes.nextNode(), list, i, row.getSizes(), setting);
      list.append(nodes.nextNode());
    }
    return VShiftNode.shiftingRight(
        makeBox(transformSizes(row.getSizes(), size), setting, list), indent);
  }

  /* TeXtp[808,809] */
  protected int transform(
      AnyUnsetNode col, NodeList list, int i, BoxSizes rowSizes, GlueSetting setting) {
    int n = i + col.getSpanCount();
    Dimen size = maxSizes.get(i);
    boolean empty = (size == Dimen.NULL);
    if (empty) size = Dimen.ZERO;
    NodeList filler = new NodeList();
    Dimen total = size;
    if (i < n) {
      Glue tab = (empty) ? Glue.ZERO : preamble.getSkip(i);
      for (int j = i; ; ) {
        total = total.plus(setting.set(tab, false));
        filler.append(makeSkip(tab, preamble.skipName));
        Dimen w = maxSizes.get(++j);
        if (w != Dimen.NULL) {
          tab = preamble.getSkip(j);
          total = total.plus(w);
        } else {
          w = Dimen.ZERO;
          tab = Glue.ZERO;
        }
        filler.append(
            makeBox(transformSizes(BoxSizes.ZERO, w), GlueSetting.NATURAL, NodeList.EMPTY));
        if (j == n) break;
      }
    }
    list.append(
            makeBox( // XXX beware of depth and leftX
                transformSizes(rowSizes, size),
                col.getSetting(total.minus(getRelevantSize(col.getSizes()))),
                col.getList()))
        .append(filler);
    return n;
  }

  /* STRANGE
   * Isn't this Named?SkipNode vs. ?SkipNode crazy?
   * Although the whole complex construction of the list is completely
   * artificial for TeX compatibility only.
   */
  /* TeXtp[804] */
  protected void check(SizesEvaluator pack, Dimen size) {
    TypoCommand.AnyBoxPacker packer = makeBoxPacker();
    if (packer.check(pack)) {
      NodeList list = new NodeList();
      list.append(makeSkip(preamble.firstSkip, preamble.skipName));
      int i = 0;
      for (; i < maxSizes.size(); i++) {
        Dimen w = maxSizes.get(i);
        if (w != Dimen.NULL)
          list.append(new AnyUnsetNode(transformSizes(BoxSizes.ZERO, w), NodeList.EMPTY))
              .append(
                  (i < preamble.length())
                      ? makeSkip(preamble.getSkip(i), preamble.skipName)
                      : makeSkip(preamble.getSkip(i)));
        else list.append(new AnyUnsetNode()).append(makeSkip(Glue.ZERO, preamble.skipName));
      }
      for (; i < preamble.length(); i++)
        list.append(new AnyUnsetNode()).append(makeSkip(Glue.ZERO, preamble.skipName));
      packer.reportBox(makeBox(transformSizes(BoxSizes.ZERO, size), pack.getSetting(), list));
    }
  }

  protected abstract void pushNewRowBuilder();

  protected abstract void pushNewSpanBuilder();

  protected abstract void addNamedSkipToRow(Glue skip, String name);

  protected abstract Dimen packedSpanSize(int spanCount);

  protected abstract void packRow();

  protected abstract NodeEnum getUnsetNodes();

  protected abstract Dimen getRelevantSize(BoxSizes sizes);

  protected abstract BoxSizes transformSizes(BoxSizes sizes, Dimen dim);

  protected abstract AnyBoxNode makeBox(BoxSizes sizes, GlueSetting setting, NodeList list);

  protected abstract AnySkipNode makeSkip(Glue skip);

  protected abstract AnySkipNode makeSkip(Glue skip, String name);

  protected abstract TypoCommand.AnyBoxPacker makeBoxPacker();

  public abstract void copyPrevParameters(Builder bld);

  protected static final ColumnBraceNesting NULL_COLUMN_NESTING = null;

  protected static class ColumnBraceNesting implements BraceNesting {
    private int braceNesting = 0;

    public void adjust(int count) {
      braceNesting += count;
    }

    public int disbalance() {
      return braceNesting;
    }

    public boolean balanced() {
      return (braceNesting == 0);
    }
  }

  protected class TemplateTokenizer extends InsertedTokenList {

    public TemplateTokenizer(TokenList list) {
      super(list, "<template> ");
    }

    /* TeXtp[324] */
    public boolean close() {
      // XXX[324] if (align_state <= 500000)
      startColumnBody(true);
      return false;
    }
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private static Alignment top = NULL;
  private Alignment next = NULL;

  private void push() {
    next = top;
    top = this;
  }

  private static void pop() {
    top = top.next;
  }

  private static void checkTop() {
    if (top == Alignment.NULL) throw new RuntimeException("no alignment active");
  }

  public static boolean columnBodyIsActiveAndBalanced() {
    return (top != Alignment.NULL && top.balancedColumn());
  }

  public static void finishActiveColumnBody(ColumnEnding ending) {
    checkTop();
    top.finishColumnBody(ending);
  }

  public static int activeColumnDisbalance() {
    return (top != Alignment.NULL) ? top.columnDisbalance() : NOALIGN_DISBALANCE;
  }

  public static class SpanGroup extends SimpleGroup {}

  public static /*final*/ Closing CLOSE_COLUMN =
      new Closing() {
        /* TeXtp[1131] */
        public void exec(Group grp, Token src) {
          Paragraph.finish();
          checkTop();
          top.finishColumn();
        }
      };

  public static /*final*/ Closing MISSING_CR =
      new Closing() {
        /* TeXtp[1132] */
        public void exec(Group grp, Token src) {
          backToken(src);
          checkTop();
          insertToken(top.frozenCr);
          error("MissingCr", top.frozenCr);
        }
      };

  public static class NoAlignGroup extends SimpleGroup {
    public void stop() {
      Paragraph.finish();
    }

    public void close() {
      checkTop();
      top.nextRow();
    }
  }
}
