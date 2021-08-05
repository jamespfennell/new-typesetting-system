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
// Filename: nts/align/AnyUnsetNode.java
// $Id: AnyUnsetNode.java,v 1.1.1.1 2001/03/22 05:38:30 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.AnyBoxedNode;
import nts.node.BoxSizes;
import nts.node.GlueSetting;
import nts.node.NodeList;
import nts.node.SizesEvaluator;

public class AnyUnsetNode extends AnyBoxedNode {
  /* corresponding to unset_node */

  protected final NodeList list;
  protected final int spanCount;
  protected final Dimen stretch;
  protected final byte strOrder;
  protected final Dimen shrink;
  protected final byte shrOrder;

  public AnyUnsetNode(
      BoxSizes sizes,
      NodeList list,
      int spanCount,
      Dimen stretch,
      byte strOrder,
      Dimen shrink,
      byte shrOrder) {
    super(sizes);
    this.list = list;
    this.spanCount = spanCount;
    this.stretch = stretch;
    this.strOrder = strOrder;
    this.shrink = shrink;
    this.shrOrder = shrOrder;
  }

  public AnyUnsetNode(BoxSizes sizes, NodeList list) {
    this(sizes, list, 0, Dimen.ZERO, Glue.NORMAL, Dimen.ZERO, Glue.NORMAL);
  }

  public AnyUnsetNode() {
    this(BoxSizes.ZERO, NodeList.EMPTY);
  }

  public NodeList getList() {
    return list;
  }

  public int getSpanCount() {
    return spanCount;
  }

  /* TeXtp[184,185] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc()).add(sizes);
    StringBuffer buf = new StringBuffer(80);
    if (spanCount != 0) buf.append(" (").append(spanCount + 1).append(" columns)");
    if (!stretch.isZero()) Glue.append(buf.append(", stretch "), stretch, strOrder, null);
    if (!shrink.isZero()) Glue.append(buf.append(", shrink "), shrink, shrOrder, null);
    log.add(buf.toString());
    cntx.addOn(log, list.nodes());
  }

  public String getDesc() {
    return "unsetbox";
  }

  /* TeXtp[810,811] */
  public GlueSetting getSetting(Dimen excess) {
    SizesEvaluator pack = new SizesEvaluator();
    pack.addStretch(stretch, strOrder);
    pack.addShrink(shrink, shrOrder);
    pack.evaluate(excess, false);
    return pack.getSetting();
  }
}
