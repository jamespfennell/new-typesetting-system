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
// Filename: nts/noad/MathNode.java
// $Id: MathNode.java,v 1.1.1.1 2000/05/26 21:16:46 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.BreakingCntx;
import nts.node.DiscardableNode;
import nts.node.FontMetric;
import nts.node.NodeEnum;
import nts.node.NodeList;

public abstract class MathNode extends DiscardableNode {
  /* root corresponding to math_node */

  protected final Dimen kern;

  public MathNode(Dimen kern) {
    this.kern = kern;
  }

  public Dimen getKern() {
    return kern;
  }

  public Dimen getWidth() {
    return kern;
  }

  /* TeXtp[192] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc((isOn()) ? "mathon" : "mathoff");
    if (!kern.isZero()) log.add(", surrounded ").add(kern.toString());
  }

  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    log.add('$');
    return metric;
  }

  public void addBreakDescOn(Log log) {
    log.addEsc("math");
  }

  public boolean isKernBreak() {
    return true;
  }

  public int breakPenalty(BreakingCntx brCntx) {
    return (brCntx.spaceBreaking()) ? 0 : INF_PENALTY;
  }

  public NodeEnum atBreakReplacement() {
    return NodeList.nodes(resizedCopy(Dimen.ZERO));
  }

  protected abstract boolean isOn();

  protected abstract MathNode resizedCopy(Dimen kern);
}
