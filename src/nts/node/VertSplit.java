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
// Filename: nts/node/VertSplit.java
// $Id: VertSplit.java,v 1.1.1.1 2000/04/30 07:35:50 ksk Exp $
package nts.node;

import java.util.Vector;
import nts.base.Dimen;
import nts.base.Glue;

public class VertSplit extends NodeList {

  protected static final int INF_BAD = Dimen.INF_BAD;
  protected static final int DEPLORABLE = Dimen.DEPLORABLE;
  protected static final int AWFUL_BAD = Dimen.AWFUL_BAD;

  public Dimen goal;
  protected Dimen maxDepth;
  public NetDimen soFar;
  public Dimen depth;

  /* TeXtp[987] */
  public void setNullSpecs() {
    goal = Dimen.MAX_VALUE;
    maxDepth = Dimen.ZERO;
    soFar = new NetDimen();
    depth = Dimen.ZERO;
  }

  private int currIndex;
  private int bestIndex;
  private int leastCost;

  private void restart() {
    currIndex = 0;
    bestIndex = 0;
    leastCost = AWFUL_BAD;
  }

  {
    setNullSpecs();
    restart();
  }

  public VertSplit() {}

  public VertSplit(int initCap) {
    super(initCap);
  }

  public VertSplit(int initCap, int capIncrement) {
    super(initCap, capIncrement);
  }

  public VertSplit(NodeEnum nodes) {
    super(nodes);
  }

  public VertSplit(Node[] nodes) {
    super(nodes);
  }

  public VertSplit(Node[] nodes, int offset, int count) {
    super(nodes, offset, count);
  }

  public boolean allConsumed() {
    return (currIndex >= length());
  }

  protected NodeEnum currPageList() {
    return nodes(0, currIndex);
  }

  protected NodeEnum contribList() {
    return nodes(currIndex);
  }

  protected static class BreakingContext implements BreakingCntx {

    public boolean atSkip = true;

    public boolean spaceBreaking() {
      return true;
    }

    public boolean allowedAtSkip() {
      return atSkip;
    }

    public int hyphenPenalty() {
      return 0;
    }

    public int exHyphenPenalty() {
      return 0;
    }
  }

  /* TeXtp[972,1000] */
  protected boolean findBreak() {
    BreakingContext brkContext = new BreakingContext();
    while (currIndex < length()) {
      Node node = nodeAt(currIndex);
      passWhileBreaking(node);
      boolean tryThisBreak = true;
      if (node.isKernBreak())
        if (currIndex + 1 < length()) tryThisBreak = nodeAt(currIndex + 1).canFollowKernBreak();
        else if (waitingForMore()) return false;
        else tryThisBreak = false;
      if (tryThisBreak) {
        brkContext.atSkip = (currIndex > 0 && nodeAt(currIndex - 1).canPrecedeSkipBreak());
        int pen = node.breakPenalty(brkContext);
        if (tryBreak(pen)) return true;
      }
      if (!node.sizeIgnored()) {
        soFar.add(depth);
        soFar.add(node.getHeight());
        soFar.addShrink(node.getHshr());
        soFar.addStretch(node.getHstrOrd(), node.getHstr());
        depth = node.getDepth();
        // XXX[976,1004] infinite shrinkage
      }
      adjustDepth();
      currIndex++;
    }
    return false;
  }

  protected void passWhileBreaking(Node node) {}

  protected boolean waitingForMore() {
    return false;
  }

  protected void adjustDepth() {
    if (depth.moreThan(maxDepth)) {
      soFar.add(depth);
      depth = maxDepth;
      soFar.sub(depth);
    }
  }

  /* TeXtp[974,1005] */
  protected boolean tryBreak(int pen) {
    if (pen < Node.INF_PENALTY) {
      int badness;
      Dimen diff = goal.minus(soFar.getNatural());
      if (diff.moreThan(0))
        badness =
            (soFar.getMaxStrOrder() > Glue.NORMAL)
                ? 0
                : diff.badness(soFar.getStretch(Glue.NORMAL));
      else {
        diff = diff.negative();
        badness = (diff.moreThan(soFar.getShrink())) ? AWFUL_BAD : diff.badness(soFar.getShrink());
      }
      int extra = extraPenalty();
      int cost =
          (badness < AWFUL_BAD)
              ? (pen <= Node.EJECT_PENALTY)
                  ? pen
                  : (badness < INF_BAD) ? badness + pen + extra : DEPLORABLE
              : badness;
      if (extra >= INF_BAD) cost = AWFUL_BAD;
      if (cost <= leastCost) {
        bestIndex = currIndex;
        leastCost = cost;
        markBestPlace();
        traceCost(pen, badness, cost, true);
      } else traceCost(pen, badness, cost, false);
      return (cost == AWFUL_BAD || pen <= Node.EJECT_PENALTY);
    }
    return false;
  }

  protected int extraPenalty() {
    return 0;
  }

  protected void markBestPlace() {}

  protected void traceCost(int pen, int bad, int cost, boolean best) {}

  public Node breakNode() {
    return (bestIndex < length()) ? nodeAt(bestIndex) : Node.NULL;
  }

  public NodeList makeSplitting(Dimen height, Dimen depth) {
    goal = height;
    maxDepth = depth;
    if (!findBreak()) tryBreak(Node.EJECT_PENALTY);
    return split();
  }

  /* Vector.removeRange() is protected - why? */
  protected NodeList split() {
    NodeList list = new NodeList(new Vector(data.subList(0, bestIndex)));
    data = new Vector(data.subList(bestIndex, length()));
    restart();
    return list;
  }

  /* TeXtp[968,994] */
  public boolean pruneTop() {
    while (currIndex < length()) {
      Node node = nodeAt(currIndex);
      passWhilePrunning(node);
      if (node.discardable()) data.remove(currIndex);
      else if (node.sizeIgnored()) currIndex++;
      else {
        Node adj = topAdjustment(nodeAt(currIndex).getHeight());
        if (adj != Node.NULL) data.add(currIndex, adj);
        return true;
      }
    }
    return false;
  }

  protected void passWhilePrunning(Node node) {}

  protected Node topAdjustment(Dimen height) {
    return Node.NULL;
  }
}
