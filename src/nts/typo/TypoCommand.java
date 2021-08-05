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
// Filename: nts/typo/TypoCommand.java
// $Id: TypoCommand.java,v 1.1.1.1 2001/03/20 09:56:21 ksk Exp $
package nts.typo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.builder.Builder;
import nts.builder.VertBuilder;
import nts.command.Command;
import nts.command.FileName;
import nts.command.Token;
import nts.io.CharCode;
import nts.io.CntxLog;
import nts.io.CntxLoggableEnum;
import nts.io.Log;
import nts.io.Loggable;
import nts.io.Name;
import nts.node.AnyBoxNode;
import nts.node.Box;
import nts.node.BoxSizes;
import nts.node.FontMetric;
import nts.node.HBoxNode;
import nts.node.HorizIterator;
import nts.node.Language;
import nts.node.LinesShape;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.RuleNode;
import nts.node.SizesEvaluator;
import nts.node.TreatBox;
import nts.node.TreatNode;
import nts.node.TypeSetter;
import nts.node.VBoxNode;
import nts.node.VertIterator;
import nts.node.WordBuilder;

public abstract class TypoCommand extends Command {

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public static final Builder getBld() {
    return Builder.top();
  }

  public static void illegalCase(Command cmd, Builder bld) {
    error("CantUseIn", cmd, bld);
  }

  public void illegalCase(Builder bld) {
    illegalCase(this, bld);
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public interface FontDimen {
    Dimen get();

    void set(Dimen dim);
  }

  public interface TypoHandler {
    FontMetric getMetric(FileName name, Dimen size, Num scale, Name ident, Loggable tok);

    FontDimen getFontDimen(FontMetric metric, int num);

    TypeSetter getSetter();
  }

  private static TypoHandler typoHandler;

  public static TypoHandler getTypoHandler() {
    return typoHandler;
  }

  public static void setTypoHandler(TypoHandler hand) {
    typoHandler = hand;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  private static FontMetricEquiv currFontMetric;

  public static FontMetric getCurrFontMetric() {
    return currFontMetric.get();
  }

  public static void setCurrFontMetric(FontMetric val, boolean glob) {
    currFontMetric.set(val, glob);
  }

  public static void makeStaticData() {
    currFontMetric = new FontMetricEquiv();
  }

  public static void writeStaticData(ObjectOutputStream output) throws IOException {
    output.writeObject(currFontMetric);
  }

  public static void readStaticData(ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    currFontMetric = (FontMetricEquiv) input.readObject();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public interface Config {
    void setLastBadness(int badness);

    void setOutputPenalty(int penalty);

    int[] currPageNumbers();

    void checkParagraph(Token src);

    void resetParagraph();

    void setMarginSkipsShrinkFinite();

    boolean activeOutput();

    boolean pendingOutput();

    void resetOutput();

    LinesShape linesShape();

    Language getLanguage();

    Language getLanguage(int langNum);

    boolean languageDiffers(Language lang);

    boolean patternsAllowed();

    void preparePatterns();
  }

  private static Config config;

  public static void setTypoConfig(Config conf) {
    config = conf;
  }

  public static Config getTypoConfig() {
    return config;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public static final int BOOLP_TRACING_LOST_CHARS = newBoolParam();

  /* TeXtp[581] */
  public static void charWarning(FontMetric metric, CharCode code) {
    if (getConfig().getBoolParam(BOOLP_TRACING_LOST_CHARS))
      diagLog
          .startLine()
          .add("Missing character: There is no ")
          .add(code)
          .add(" in font ")
          .add(metric.getName())
          .add('!')
          .startLine();
  }

  public static final TreatNode APPENDER =
      new TreatNode() {
        public void execute(Node node) {
          getBld().addNode(node);
        }
      };

  /* TeXtp[1038] */
  public static void appendCharsTo(Builder bld, WordBuilder word) {
    for (; ; ) {
      Token tok = nextExpToken();
      Command cmd = meaningOf(tok);
      CharCode code = cmd.charCodeToAdd();
      if (code == CharCode.NULL) {
        word.close(!cmd.isNoBoundary());
        cmd.execute(tok);
        break;
      }
      bld.adjustSpaceFactor(code.spaceFactor());
      if (!word.add(code)) {
        charWarning(getCurrFontMetric(), code);
        break;
      }
    }
  }

  /* TeXtp[1034,1376] */
  public static void appendChar(Builder bld, CharCode code) {
    WordBuilder word = getCurrFontMetric().getWordBuilder(APPENDER, true, bld.willBeBroken());
    bld.adjustSpaceFactor(code.spaceFactor());
    fixLanguage(bld);
    if (word.add(code)) appendCharsTo(bld, word);
    else charWarning(getCurrFontMetric(), code);
  }

  /* TeXtp[1376] */
  public static void fixLanguage(Builder bld) {
    if (bld.willBeBroken()) {
      Language lang = bld.getCurrLang();
      if (lang != Language.NULL && getTypoConfig().languageDiffers(lang))
        bld.setCurrLang(getTypoConfig().getLanguage());
    }
  }

  public static final int GLUEP_SPACE = newGlueParam();
  public static final int GLUEP_XSPACE = newGlueParam();

  /* TeXtp[1041] */
  public static void appendNormalSpace(Builder bld) {
    Glue skip = getConfig().getGlueParam(GLUEP_SPACE);
    if (skip.isZero()) bld.addSkip(getCurrFontMetric().getNormalSpace());
    else bld.addSkip(skip, getConfig().getGlueName(GLUEP_SPACE));
  }

  public static class HorizCharHandler implements CharHandler {

    /* TeXtp[1034] */
    public void handle(Builder bld, CharCode code, Token src) {
      appendChar(bld, code);
    }

    /* TeXtp[1030,1043] */
    public void handleSpace(Builder bld, Token src) {
      int sf = bld.getSpaceFactor();
      if (sf == 1000) appendNormalSpace(bld);
      else if (sf > 0) {
        if (sf >= 2000) {
          Glue skip = getConfig().getGlueParam(GLUEP_XSPACE);
          if (!skip.isZero()) {
            bld.addSkip(skip, getConfig().getGlueName(GLUEP_XSPACE));
            return;
          }
        }
        Glue skip = getConfig().getGlueParam(GLUEP_SPACE);
        if (skip.isZero()) skip = getCurrFontMetric().getNormalSpace();
        Dimen dim = skip.getDimen();
        if (sf >= 2000) {
          Dimen extra = getCurrFontMetric().getDimenParam(FontMetric.DIMEN_PARAM_EXTRA_SPACE);
          if (extra != Dimen.NULL) dim = dim.plus(extra);
        }
        skip =
            Glue.valueOf(
                dim,
                skip.getStretch().times(sf, 1000),
                skip.getStrOrder(),
                skip.getShrink().times(1000, sf),
                skip.getShrOrder());
        bld.addSkip(skip);
      }
    }
  }

  public static class VertCharHandler implements CharHandler {

    /* TeXtp[1090] */
    public void handle(Builder bld, CharCode code, Token src) {
      backToken(src);
      Paragraph.start(true);
    }

    public void handleSpace(Builder bld, Token src) {}
  }

  public static final int DIMP_LINE_SKIP_LIMIT = newDimParam();
  public static final int GLUEP_LINE_SKIP = newGlueParam();
  public static final int GLUEP_BASELINE_SKIP = newGlueParam();

  /* TeXtp[679] */
  private static void addBoxToBuilder(Builder bld, Node node) {
    Dimen dim = bld.getPrevDepth();
    if (dim != Dimen.NULL && dim.moreThan(VertBuilder.IGNORE_DEPTH)) {
      Command.Config cfg = getConfig();
      Glue bls = cfg.getGlueParam(GLUEP_BASELINE_SKIP);
      dim = bls.getDimen().minus(dim).minus(node.getHeight());
      if (dim.lessThan(cfg.getDimParam(DIMP_LINE_SKIP_LIMIT)))
        bld.addSkip(cfg.getGlueParam(GLUEP_LINE_SKIP), cfg.getGlueName(GLUEP_LINE_SKIP));
      else bld.addSkip(bls.resizedCopy(dim), cfg.getGlueName(GLUEP_BASELINE_SKIP));
    }
    bld.addBox(node);
  }

  public static void appendBox(Builder bld, Node box) {
    addBoxToBuilder(bld, box);
    bld.buildPage();
  }

  public static void appendBox(Builder bld, Node box, boolean page) {
    addBoxToBuilder(bld, box);
    if (page) bld.buildPage();
  }

  public static void appendBox(Builder bld, Node box, NodeEnum mig) {
    addBoxToBuilder(bld, box);
    bld.addNodes(mig);
    bld.buildPage();
  }

  public static void appendBox(Builder bld, Node box, NodeEnum mig, boolean page) {
    addBoxToBuilder(bld, box);
    bld.addNodes(mig);
    if (page) bld.buildPage();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public static FontMetric scanFontMetric() {
    Token tok = nextExpNonSpacer();
    Command cmd = meaningOf(tok);
    if (cmd.hasFontMetricValue()) return cmd.getFontMetricValue();
    backToken(tok);
    error("MissingFontIdent");
    return NullFontMetric.METRIC;
  }

  public static void scanBox(TreatBox proc) {
    Token tok = nextNonRelax();
    Command cmd = meaningOf(tok);
    if (cmd.hasBoxValue()) proc.execute(cmd.getBoxValue(), NodeList.EMPTY_ENUM);
    else if (cmd.canMakeBoxValue()) cmd.makeBoxValue(proc);
    else {
      backToken(tok);
      error("BoxExpected");
    }
  }

  public static final int INTP_SHOW_BOX_DEPTH = newIntParam();
  public static final int INTP_SHOW_BOX_BREADTH = newIntParam();

  /* TeXtp[198] */
  public static void addBoxOn(Log log, Box box) {
    int depth = getConfig().getIntParam(INTP_SHOW_BOX_DEPTH);
    int breadth = getConfig().getIntParam(INTP_SHOW_BOX_BREADTH);
    box.addOn(log, depth, breadth);
    log.endLine();
  }

  public static void addItemsOn(Log log, CntxLoggableEnum items) {
    int depth = getConfig().getIntParam(INTP_SHOW_BOX_DEPTH);
    int breadth = getConfig().getIntParam(INTP_SHOW_BOX_BREADTH);
    CntxLog.addItems(log, items, depth, breadth);
    log.endLine();
  }

  public static void addBoxOnDiagLog(String desc, Box box) {
    diagLog.startLine().add(desc);
    addBoxOn(diagLog, box);
    diagLog.startLine().endLine();
  }

  public static void addBoxOnDiagLog(Box box) {
    addBoxOn(diagLog, box);
    diagLog.startLine().endLine();
  }

  public static void addItemsOnDiagLog(String desc, CntxLoggableEnum items) {
    diagLog.startLine().add(desc);
    addItemsOn(diagLog, items);
    diagLog.startLine().endLine();
  }

  public static void addItemsOnDiagLog(CntxLoggableEnum items) {
    addItemsOn(diagLog, items);
    diagLog.startLine().endLine();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  /* TeXtp[666] */
  public abstract static class AnyBoxPacker {

    // XXX better name
    public boolean check(SizesEvaluator pack) {
      int badness = pack.getBadness();
      getTypoConfig().setLastBadness(badness);
      boolean serious = false;
      switch (pack.getReport()) {
        case SizesEvaluator.UNDERFULL:
          serious = underfull(badness);
          break;
        case SizesEvaluator.TIGHT:
          serious = tight(badness);
          break;
        case SizesEvaluator.OVERFULL:
          serious = overfull(pack.getOverfull());
          break;
      }
      return serious;
    }

    public void reportBox(AnyBoxNode box) {
      if (!takingOverLocationReported(normLog)) {
        reportLocation(normLog);
        normLog.endLine();
      }
      addBoxOnDiagLog(box);
    }

    /* TeXtp[660,674] */
    protected boolean underfull(int badness) {
      if (badness > getConfig().getIntParam(getBadnessParam())) {
        normLog
            .endLine()
            .add((badness > Dimen.UNI_BAD) ? "Underfull" : "Loose")
            .add(' ')
            .addEsc(getName())
            .add(" (badness ")
            .add(badness)
            .add(") ");
        return true;
      }
      return false;
    }

    /* TeXtp[667,678] */
    protected boolean tight(int badness) {
      if (badness > getConfig().getIntParam(getBadnessParam())) {
        normLog.endLine().add("Tight ").addEsc(getName()).add(" (badness ").add(badness).add(") ");
        return true;
      }
      return false;
    }

    /* TeXtp[666,677] */
    protected boolean overfull(Dimen excess) {
      if (getConfig().getIntParam(getBadnessParam()) < Dimen.UNI_BAD
          || getConfig().getDimParam(getFuzzParam()).lessThan(excess)) {
        normLog
            .endLine()
            .add("Overfull ")
            .addEsc(getName())
            .add(" (")
            .add(excess.toString("pt"))
            .add(" too ")
            .add(getDim())
            .add(") ");
        return true;
      }
      return false;
    }

    protected static boolean takingOverLocationReported(Log log) {
      if (getTypoConfig().activeOutput()) {
        log.add("has occurred while \\output is active");
        return true;
      }
      return false;
    }

    protected void reportLocation(Log log) {
      log.add("detected at line ").add(currLineNumber());
    }

    protected abstract String getName();

    protected abstract String getDim();

    protected abstract int getBadnessParam();

    protected abstract int getFuzzParam();
  }

  public static final int INTP_HBADNESS = newIntParam();
  public static final int DIMP_HFUZZ = newDimParam();
  public static final int DIMP_OVERFULL_RULE = newDimParam();

  public static class HBoxPacker extends AnyBoxPacker {

    /* TeXtp[666] */
    public HBoxNode packHBox(NodeList list, Dimen desired, boolean exactly) {
      SizesEvaluator pack = new SizesEvaluator();
      HorizIterator.summarize(list.nodes(), pack);
      Dimen size = pack.getBody().plus(pack.getDepth());
      boolean empty = list.isEmpty();
      if (exactly) {
        pack.evaluate(desired.minus(size), empty);
        size = desired;
      } else {
        pack.evaluate(desired, empty);
        size = size.plus(desired);
      }
      BoxSizes sizes = new BoxSizes(pack.getWidth(), size, pack.getLeftX(), pack.getHeight());
      HBoxNode hbox = new HBoxNode(sizes, pack.getSetting(), list);
      if (pack.getReport() == SizesEvaluator.OVERFULL) addOverfullRule(list, pack.getOverfull());
      if (check(pack)) reportBox(hbox);
      return hbox;
    }

    protected void addOverfullRule(NodeList list, Dimen excess) {
      if (getConfig().getDimParam(DIMP_OVERFULL_RULE).moreThan(0)
          && getConfig().getDimParam(getFuzzParam()).lessThan(excess)) {
        BoxSizes sizes =
            new BoxSizes(
                Dimen.NULL, getConfig().getDimParam(DIMP_OVERFULL_RULE), Dimen.NULL, Dimen.ZERO);
        list.append(new RuleNode(sizes));
      }
    }

    public void reportBox(AnyBoxNode box) {
      if (!takingOverLocationReported(normLog)) reportLocation(normLog);
      normLog.endLine();
      box.addListShortlyOn(normLog);
      normLog.endLine();
      addBoxOnDiagLog(box);
      /* STRANGE: note the asymetry of VBoxPacker & HBoxPacker */
    }

    protected String getName() {
      return "hbox";
    }

    protected String getDim() {
      return "wide";
    }

    protected int getBadnessParam() {
      return INTP_HBADNESS;
    }

    protected int getFuzzParam() {
      return DIMP_HFUZZ;
    }
  }

  public static final int INTP_VBADNESS = newIntParam();
  public static final int DIMP_VFUZZ = newDimParam();

  public static class VBoxPacker extends AnyBoxPacker {

    public VBoxNode packVBox(NodeList list, Dimen desired, boolean exactly, Dimen maxDepth) {
      SizesEvaluator pack = new SizesEvaluator();
      VertIterator.summarize(list.nodes(), pack);
      if (maxDepth != Dimen.NULL) pack.restrictDepth(maxDepth);
      Dimen size = pack.getBody().plus(pack.getHeight());
      boolean empty = list.isEmpty();
      if (exactly) {
        pack.evaluate(desired.minus(size), empty);
        size = desired;
      } else {
        pack.evaluate(desired, empty);
        size = size.plus(desired);
      }
      BoxSizes sizes = new BoxSizes(size, pack.getWidth(), pack.getDepth(), pack.getLeftX());
      VBoxNode vbox = new VBoxNode(sizes, pack.getSetting(), list);
      if (check(pack)) reportBox(vbox);
      return vbox;
    }

    protected String getName() {
      return "vbox";
    }

    protected String getDim() {
      return "high";
    }

    protected int getBadnessParam() {
      return INTP_VBADNESS;
    }

    protected int getFuzzParam() {
      return DIMP_VFUZZ;
    }
  }

  private static final HBoxPacker hPacker = new HBoxPacker();
  private static final VBoxPacker vPacker = new VBoxPacker();

  public static HBoxNode packHBox(NodeList list, Dimen desired) {
    return packHBox(list, desired, true);
  }

  public static HBoxNode packHBox(NodeList list, Dimen desired, boolean exactly) {
    return hPacker.packHBox(list, desired, exactly);
  }

  public static VBoxNode packVBox(NodeList list, Dimen desired) {
    return packVBox(list, desired, true);
  }

  public static VBoxNode packVBox(NodeList list, Dimen desired, Dimen maxDepth) {
    return packVBox(list, desired, true, maxDepth);
  }

  public static VBoxNode packVBox(NodeList list, Dimen desired, boolean exactly) {
    return packVBox(list, desired, exactly, Dimen.NULL);
  }

  public static VBoxNode packVBox(NodeList list, Dimen desired, boolean exactly, Dimen maxDepth) {
    return vPacker.packVBox(list, desired, exactly, maxDepth);
  }

  public static final int DIMP_BOX_MAX_DEPTH = newDimParam();
  public static final int DIMP_SPLIT_MAX_DEPTH = newDimParam();
  public static final int GLUEP_SPLIT_TOP_SKIP = newGlueParam();
  public static final int INTP_OUTPUT_BOX_NUM = newIntParam();

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public static final int BOOLP_TRACING_OUTPUT = newBoolParam();
  public static final int DIMP_H_OFFSET = newDimParam();
  public static final int DIMP_V_OFFSET = newDimParam();

  /* STRANGE
   * Why is height of box checked standalone and width is not?
   */
  /* TeXtp[638-641] */
  public static void shipOut(Box box) {
    boolean tracing = getConfig().getBoolParam(BOOLP_TRACING_OUTPUT);
    int[] nums = getTypoConfig().currPageNumbers();
    if (tracing) normLog.startLine().endLine().add("Completed box being shipped out");
    normLog.sepRoom(9).add('[');
    if (nums.length > 0)
      for (int i = 0; ; ) {
        normLog.add(nums[i++]);
        if (i >= nums.length) break;
        else normLog.add('.');
      }
    normLog.flush(); // XXX check all update_terminal
    if (tracing) {
      normLog.add(']');
      addBoxOnDiagLog(box);
    }
    Dimen hOffset = getConfig().getDimParam(DIMP_H_OFFSET);
    Dimen vOffset = getConfig().getDimParam(DIMP_V_OFFSET);
    Dimen height = box.getHeight().plus(box.getDepth());
    Dimen width = box.getWidth().plus(box.getLeftX());
    if (box.getHeight().moreThan(Dimen.MAX_VALUE)
        || box.getDepth().moreThan(Dimen.MAX_VALUE)
        || vOffset.plus(height).moreThan(Dimen.MAX_VALUE)
        || hOffset.plus(width).moreThan(Dimen.MAX_VALUE)) {
      error("PageTooLarge");
      if (!tracing) addBoxOnDiagLog("The following box has been deleted:", box);
    } else {
      TypeSetter setter = getTypoHandler().getSetter();
      if (setter != TypeSetter.NULL) {
        setter.startPage(vOffset, hOffset, height, width, nums);
        setter.moveDown(box.getHeight());
        setter.moveRight(box.getLeftX());
        box.typeSet(setter);
        setter.endPage();
      }
    }
    if (!tracing) normLog.add(']');
    normLog.flush();
    getTypoConfig().resetOutput();
  }
}
