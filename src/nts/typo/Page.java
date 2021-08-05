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
// Filename: nts/typo/Page.java
// $Id: Page.java,v 1.1.1.1 2001/02/01 13:34:14 ksk Exp $
package nts.typo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.builder.OutputBuilder;
import nts.command.CountPrim;
import nts.command.DimenPrim;
import nts.command.SkipPrim;
// XXX VSplitPrim dependency
import nts.command.TokenList;
import nts.io.Log;
import nts.node.Box;
import nts.node.Node;
import nts.node.NodeList;
import nts.node.PageSplit;
import nts.node.PenaltyNode;
import nts.node.SizesEvaluator;
import nts.node.VBoxNode;
import nts.node.VoidBoxNode;

public abstract class Page extends TypoCommand {

  public static final int BOOLP_TRACING_PAGES = newBoolParam();
  public static final int DIMP_VSIZE = newDimParam();
  public static final int DIMP_MAX_DEPTH = newDimParam();
  public static final int GLUEP_TOP_SKIP = newGlueParam();
  public static final int TOKSP_OUTPUT = newToksParam();
  public static final int INTP_MAX_DEAD_CYCLES = newIntParam();
  public static final int BOOLP_HOLDING_INSERTS = newBoolParam();

  private static boolean tracing() {
    return getConfig().getBoolParam(BOOLP_TRACING_PAGES);
  }

  private static String badToString(int b) {
    return (b >= Dimen.AWFUL_BAD) ? "*" : Integer.toString(b);
  }

  private static List pageList;

  public static PageSplit getPageSplit() {
    return pageList;
  }

  public static List getPageList() {
    return pageList;
  }

  public static void makeStaticData(
      CountPrim countReg,
      DimenPrim dimenReg,
      SkipPrim skipReg,
      SetBoxPrim boxReg,
      TokenList.Maintainer topMark,
      TokenList.Maintainer firstMark,
      TokenList.Maintainer botMark) {
    pageList = new List(countReg, dimenReg, skipReg, boxReg, topMark, firstMark, botMark);
  }

  public static void writeStaticData(ObjectOutputStream output) throws IOException {
    output.writeObject(pageList);
  }

  public static void readStaticData(ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    pageList = (List) input.readObject();
  }

  public static class List extends PageSplit {

    private final CountPrim countReg;
    private final DimenPrim dimenReg;
    private final SkipPrim skipReg;
    private final SetBoxPrim boxReg;
    private final TokenList.Maintainer topMark;
    private final TokenList.Maintainer firstMark;
    private final TokenList.Maintainer botMark;

    public List(
        CountPrim countReg,
        DimenPrim dimenReg,
        SkipPrim skipReg,
        SetBoxPrim boxReg,
        TokenList.Maintainer topMark,
        TokenList.Maintainer firstMark,
        TokenList.Maintainer botMark) {
      this.countReg = countReg;
      this.dimenReg = dimenReg;
      this.skipReg = skipReg;
      this.boxReg = boxReg;
      this.topMark = topMark;
      this.firstMark = firstMark;
      this.botMark = botMark;
    }

    /*
     * we suppose that the following holds in TeXtp:
     *
     * output_active -> page_contents = empty
     * fire_up called -> page_contents = box_there
     */

    protected boolean active = false;
    public int deadCycles = 0;

    public boolean outputActive() {
      return active;
    }

    public boolean canChangeNums() {
      return true;
    }

    public void build() {
      if (!active) super.build();
    }

    public boolean canChangeDimens() {
      return (super.canChangeDimens() && !active);
    }

    public void show(Log log, int depth, int breadth) {
      show(log, depth, breadth, active);
    }

    public int getDeadCycles() {
      return deadCycles;
    }

    public void resetDeadCycles() {
      deadCycles = 0;
    }

    /* TeXtp[987] */
    protected void initSpecs() {
      goal = getConfig().getDimParam(DIMP_VSIZE);
      maxDepth = getConfig().getDimParam(DIMP_MAX_DEPTH);
      if (tracing())
        diagLog
            .startLine()
            .add("%% goal height=")
            .add(goal.toString())
            .add(", max depth=")
            .add(maxDepth.toString())
            .startLine();
    }

    protected void setInsVBox(int num, Box box) {
      boxReg.foist(num, box);
    }

    /* TeXtp[993] */
    protected Box getInsVBox(int num) {
      Box box = boxReg.get(num);
      if (!box.isVoid() && !box.isVBox()) {
        error("MisplacedInsert");
        boxReg.showAndPurge(num);
        box = boxReg.get(num);
      }
      return box;
    }

    /* TeXtp[1009] */
    protected Glue getInsSkip(int num) {
      Glue skip = skipReg.get(num);
      if (skip.getShrOrder() != Glue.NORMAL && !skip.getShrink().isZero())
        error("InfShrinkInsert", skipReg, num(num));
      return skip;
    }

    protected Dimen getInsSize(int num) {
      return dimenReg.get(num);
    }

    protected int getInsFactor(int num) {
      return countReg.get(num).intVal();
    }

    protected void foistOutputBox(Box box) {
      boxReg.foist(getConfig().getIntParam(INTP_OUTPUT_BOX_NUM), box);
    }

    protected void checkOutputBox(String ident) {
      int num = getConfig().getIntParam(INTP_OUTPUT_BOX_NUM);
      if (!boxReg.get(num).isVoid()) {
        error(ident, esc(boxReg.getDesc()), num(num));
        boxReg.showAndPurge(num);
      }
    }

    /* TeXtp[1011] */
    protected void traceSplitCost(int num, Dimen space, Dimen best, int cost) {
      if (tracing())
        diagLog
            .startLine()
            .add("% split")
            .add(num)
            .add(" to ")
            .add(space.toString())
            .add(',')
            .add(best.toString())
            .add(" p=")
            .add(cost)
            .startLine();
    }

    protected boolean insertsWanted() {
      return !getConfig().getBoolParam(BOOLP_HOLDING_INSERTS);
    }

    protected Node splitTopAdjustment(Dimen height, Glue topSkip) {
      return VSplitPrim.makeTopAdjustment(height, GLUEP_SPLIT_TOP_SKIP, topSkip);
    }

    protected Node topAdjustment(Dimen height) {
      return VSplitPrim.makeTopAdjustment(height, GLUEP_TOP_SKIP);
    }

    /* TeXtp[1006] */
    protected void traceCost(int pen, int bad, int cost, boolean best) {
      if (tracing()) {
        diagLog
            .startLine()
            .add("% t=")
            .add(soFar.toString())
            .add(" g=")
            .add(goal.toString())
            .add(" b=")
            .add(badToString(bad))
            .add(" p=")
            .add(pen)
            .add(" c=")
            .add(badToString(cost));
        if (best) diagLog.add('#');
        diagLog.startLine();
      }
    }

    private static final VBoxPacker pagePacker =
        new VBoxPacker() {
          public boolean check(SizesEvaluator pack) {
            return false;
          }
        };

    /* TeXtp[1012,1024,1025] */
    protected boolean performOutput(NodeList list, Dimen height) {
      setOutputPenalty();
      setMarks(list);
      checkOutputBox("NonEmptyOutBox");
      VBoxNode box = pagePacker.packVBox(list, height, true, maxDepth);
      TokenList.Inserter output = getConfig().getToksInserter(TOKSP_OUTPUT);
      if (!output.isEmpty()) {
        foistOutputBox(box);
        if (deadCycles < getConfig().getIntParam(INTP_MAX_DEAD_CYCLES)) {
          active = true;
          deadCycles++;
          pushLevel(new OutputGroup());
          output.insertToks();
          scanLeftBrace();
          /* STRANGE
           * Why is depth zeroed before
           * performing the output routine?
           */
          depth = Dimen.ZERO;
          return true;
        } else error("TooMuchDead", num(deadCycles));
      }
      foistOutputBox(VoidBoxNode.BOX);
      shipOut(box);
      startNextPage();
      return false;
    }

    /* TeXtp[1013] */
    private void setOutputPenalty() {
      int pen = Node.EJECT_PENALTY;
      if (!isEmpty()) {
        pen = Node.INF_PENALTY;
        Node node = nodeAt(0);
        if (node.isPenalty()) {
          data.set(0, new PenaltyNode(Num.valueOf(pen)));
          pen = node.getPenalty().intVal();
        }
      }
      getTypoConfig().setOutputPenalty(pen);
    }

    private void setMarks(NodeList list) {
      topMark.setToksValue(botMark.getToksValue());
      if (!VSplitPrim.setMarks(list.nodes(), firstMark, botMark))
        firstMark.setToksValue(topMark.getToksValue());
    }

    public class OutputGroup extends VertGroup {

      protected OutputGroup() {
        super(new OutputBuilder(currLineNumber()));
      }

      public void stop() {
        getTokStack().dropFinishedPop();
        // XXX[1026] unbalanced output routine
        super.stop();
      }

      public void close() {
        /* STRANGE
         * insertPenalties are not zeroed after default
         * output routine; maybe it is even a bug
         */
        active = false;
        insertPenalties = 0;
        checkOutputBox("NonEmptyOutBoxAfter");
        startNextPage(builder.getList().nodes());
        super.close();
        build();
      }
    }
  }
}
