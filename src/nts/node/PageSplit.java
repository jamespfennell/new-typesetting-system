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
// Filename: nts/node/PageSplit.java
// $Id: PageSplit.java,v 1.1.1.1 2001/02/01 13:29:44 ksk Exp $
package nts.node;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CntxLog;
import nts.io.Log;

public abstract class PageSplit extends VertSplit {

  private boolean discarding;
  private boolean nullSpecs; // nullSpecs => discarding
  private Node last;
  private Map insMap = new TreeMap();
  private NodeList maybeOver = new NodeList();

  private void restart() {
    discarding = true;
    nullSpecs = true;
    last = Node.NULL;
    insMap.clear();
    maybeOver.clear();
  }

  {
    restart();
  }

  protected void freezeSpecs() {
    if (nullSpecs) {
      initSpecs();
      nullSpecs = false;
    }
  }

  protected abstract void initSpecs();

  public Node lastSpecialNode() {
    Node node = lastNode();
    return (node != Node.NULL) ? node : last;
  }

  public boolean canChangeDimens() {
    return (!nullSpecs);
  }

  protected boolean waitingForMore() {
    return true;
  }

  protected void passWhilePrunning(Node node) {
    pass(node);
  }

  protected void passWhileBreaking(Node node) {
    pass(node);
  }

  public int insertPenalties = 0;

  protected int extraPenalty() {
    return insertPenalties;
  }

  private class InsRecord {

    private final int num;
    private Dimen size;
    private boolean full = false;
    private Vector data = new Vector();
    private int bestCount = 0;

    /* TeXtp[1009] */
    public InsRecord(int number) {
      num = number;
      Box box = getInsVBox(num);
      Glue skip = getInsSkip(num);
      size = box.getHeight().plus(box.getDepth());
      goal = goal.minus(insScale(size, num)).minus(skip.getDimen());
      soFar.add(skip.resizedCopy(Dimen.ZERO));
    }

    public boolean isFull() {
      return full;
    }

    public void markBest() {
      bestCount = data.size();
    }

    /* TeXtp[1008] */
    public void record(Insertion ins) {
      Dimen delta = goal.plus(soFar.getShrink()).minus(soFar.getNatural()).minus(depth);
      Dimen scal = insScale(ins.size, num);
      if (!(scal.moreThan(0) && scal.moreThan(delta))
          && !size.plus(ins.size).moreThan(getInsSize(num))) {
        size = size.plus(ins.size);
        goal = goal.minus(scal);
        data.add(ins.list);
      } else split(ins);
    }

    /* TeXtp[1010] */
    private void split(Insertion ins) {
      int fac = getInsFactor(num);
      Dimen space;
      if (fac <= 0) space = Dimen.MAX_VALUE;
      else {
        space = goal.minus(soFar.getNatural()).minus(depth);
        if (fac != 1000) space = space.over(fac).times(1000);
      }
      space = space.min(getInsSize(num).minus(size));
      InsertSplit splitter = new InsertSplit(ins.list.nodes(), ins.topSkip);
      NodeList head = splitter.makeSplitting(space, ins.maxDepth);
      Dimen bestSize = splitter.getBestSize();
      Node breakNode = splitter.breakNode();
      int cost =
          (breakNode == Node.NULL)
              ? Node.EJECT_PENALTY
              : (breakNode.isPenalty()) ? breakNode.getPenalty().intVal() : 0;
      traceSplitCost(num, space, bestSize, cost);
      full = true;
      size = size.plus(bestSize);
      goal = goal.minus(insScale(bestSize, num));
      data.add(head);
      insertPenalties += cost;
      if (!splitter.isEmpty()) {
        splitter.pruneTop();
        NodeList tail = new NodeList(splitter.nodes());
        VBoxNode vbox = VBoxNode.packedOf(tail);
        maybeOver.append(
            new InsertNode(ins.makeCopy(tail, vbox.getHeight().plus(vbox.getDepth()))));
      }
    }

    /* TeXtp[1018,1021] */
    public void finish() {
      if (!data.isEmpty()) {
        NodeList list = new NodeList();
        NodeEnum boxList = getInsVBox(num).getVertList();
        if (boxList != NodeEnum.NULL) list.append(boxList);
        for (int i = 0; i < bestCount; i++) list.append((NodeList) data.get(i));
        setInsVBox(num, VBoxNode.packedOf(list));
        // XXX Maybe that the list can be constructed instead of list
        // XXX of lists. But beware of sequence of empty lists!!!
      }
    }

    /* STRANGE
     * why is xn_over_d not used?
     */
    private Dimen insScale(Dimen size, int num) {
      int fac = getInsFactor(num);
      return (fac == 1000) ? size : size.over(1000).times(fac);
    }

    /* STRANGE
     * Why is the size divided by 1000 even if \count<num> is 1000?
     * And why log.endLine() instead of log.startLine() ?
     */
    /* TeXtp[986] */
    public void show(Log log) {
      log.endLine()
          .addEsc("insert")
          .add(num)
          .add(" adds ")
          .add(size.over(1000).times(getInsFactor(num)).toString());
      if (full) log.add(", #").add(data.size()).add(" might split");
    }
  }

  protected class InsertSplit extends VertSplit {

    private final Glue topSkip;

    public InsertSplit(NodeEnum nodes, Glue topSkip) {
      super(nodes);
      this.topSkip = topSkip;
    }

    private Dimen bestSize;

    public Dimen getBestSize() {
      return bestSize;
    }

    protected void markBestPlace() {
      bestSize = this.soFar.getNatural().plus(this.depth);
    }

    protected Node topAdjustment(Dimen height) {
      return splitTopAdjustment(height, topSkip);
    }
  }

  protected abstract void setInsVBox(int num, Box box);

  protected abstract Box getInsVBox(int num);

  protected abstract Glue getInsSkip(int num);

  protected abstract Dimen getInsSize(int num);

  protected abstract int getInsFactor(int num);

  protected abstract void traceSplitCost(int num, Dimen space, Dimen best, int cost);

  protected abstract Node splitTopAdjustment(Dimen height, Glue topSkip);

  /* TeXtp[996,1008] */
  private void pass(Node node) {
    last = (node.discardable()) ? node : Node.NULL;
    if (node.isInsertion()) {
      freezeSpecs();
      Insertion ins = node.getInsertion();
      Integer key = Integer.valueOf(ins.num);
      InsRecord rec = (InsRecord) insMap.get(key);
      if (rec == null) {
        rec = new InsRecord(ins.num);
        insMap.put(key, rec);
      }
      if (rec.isFull()) {
        maybeOver.append(node);
        insertPenalties += ins.floatCost.intVal();
      } else rec.record(ins);
    }
  }

  private Dimen bestGoal;
  private NodeList heldOver = new NodeList();

  /* TeXtp[1005] */
  protected void markBestPlace() {
    Iterator iterator = insMap.values().iterator();
    while (iterator.hasNext()) ((InsRecord) iterator.next()).markBest();
    heldOver.append(maybeOver);
    maybeOver.clear();
    bestGoal = goal;
  }

  /* TeXtp[994] */
  private boolean step() {
    if (discarding) {
      boolean pruned = pruneTop();
      if (!nullSpecs) adjustDepth();
      if (pruned) {
        freezeSpecs();
        discarding = false;
      } else return false;
    }
    return findBreak();
  }

  /* TeXtp[1014,1017] */
  public void build() {
    while (step()) {
      NodeList list = split();
      if (insertsWanted()) {
        Iterator iterator = insMap.values().iterator();
        while (iterator.hasNext()) ((InsRecord) iterator.next()).finish();
        insertPenalties = heldOver.length();
        list = withoutInserts(list);
      } else {
        insertPenalties = 0;
        heldOver.clear();
      }
      restart();
      if (performOutput(list, bestGoal)) return;
    }
  }

  protected abstract boolean insertsWanted();

  protected abstract boolean performOutput(NodeList list, Dimen size);

  /* TeXtp[1018,1020] */
  private NodeList withoutInserts(NodeList list) {
    NodeList result = new NodeList();
    NodeEnum nodes = list.nodes();
    while (nodes.hasMoreNodes()) {
      Node node = nodes.nextNode();
      if (!node.isInsertion()) result.append(node);
    }
    return result;
  }

  public void startNextPage(NodeEnum output) {
    setNullSpecs();
    NodeList contrib = new NodeList(data);
    data = new Vector();
    append(heldOver);
    heldOver.clear();
    append(output);
    append(contrib);
  }

  public void startNextPage() {
    startNextPage(EMPTY_ENUM);
  }

  /* TeXtp[218,986] */
  protected void show(Log log, int depth, int breadth, boolean act) {
    NodeEnum nodes;
    if (act) {
      nodes = heldOver.nodes();
      if (nodes.hasMoreNodes()) {
        log.startLine().add("### current page:").add(" (held over for next output)");
        CntxLog.addItems(log, nodes, depth, breadth);
      }
    } else {
      nodes = currPageList();
      if (nodes.hasMoreNodes()) {
        log.startLine().add("### current page:");
        CntxLog.addItems(log, nodes, depth, breadth);
        if (!nullSpecs) {
          log.startLine().add("total height ").add(soFar.toString());
          log.startLine().add(" goal height ").add(goal.toString());
          Iterator iterator = insMap.values().iterator();
          while (iterator.hasNext()) ((InsRecord) iterator.next()).show(log);
        }
      }
    }
    nodes = contribList();
    if (nodes.hasMoreNodes()) {
      log.startLine().add("### recent contributions:");
      CntxLog.addItems(log, nodes, depth, breadth);
    }
  }

  public abstract void show(Log log, int depth, int breadth);
}
