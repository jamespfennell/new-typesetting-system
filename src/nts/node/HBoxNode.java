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
// Filename: nts/node/HBoxNode.java
// $Id: HBoxNode.java,v 1.1.1.1 2000/10/25 07:13:52 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.base.Glue;

public class HBoxNode extends AnyBoxNode {
  /* root corresponding to hlist_node */

  public static final HBoxNode EMPTY =
      new HBoxNode(BoxSizes.ZERO, GlueSetting.NATURAL, NodeList.EMPTY);

  public HBoxNode(BoxSizes sizes, GlueSetting setting, NodeList list) {
    super(sizes, setting, list);
  }

  public final boolean isHBox() {
    return true;
  }

  public String getDesc() {
    return "hbox";
  }

  protected void moveStart(TypeSetter setter) {
    setter.moveLeft(getLeftX());
  }

  protected void movePrev(TypeSetter setter, Node node) {
    setter.moveRight(node.getLeftX(setting));
  }

  protected void movePast(TypeSetter setter, Node node) {
    setter.moveRight(node.getWidth(setting));
  }

  public Box pretendSizesCopy(BoxSizes sizes) {
    return new HBoxNode(sizes, setting, list);
  }

  public static HBoxNode packedOf(NodeList list) {
    return new HBoxNode(HorizIterator.naturalSizes(list.nodes()), GlueSetting.NATURAL, list);
  }

  // XXX could be optimized
  public static HBoxNode packedOf(Node node) {
    return packedOf(new NodeList(node));
  }

  /* TeXtp[721] */
  public Node trailingKernSpared() {
    return (list.length() == 2
            && list.nodeAt(0).kernAfterCanBeSpared()
            && list.nodeAt(1).isKernThatCanBeSpared())
        ? new HBoxNode(sizes, setting, new NodeList(list.nodeAt(0)))
        : this;
  }

  /* TeXtp[715] */
  public Node reboxedToWidth(Dimen width) {
    if (getWidth().equals(width)) return this;
    if (list.isEmpty()) return pretendingWidth(width);
    NodeList forPacking = list;
    if (list.length() == 1 && list.nodeAt(0).kernAfterCanBeSpared()) {
      Node node = list.nodeAt(0);
      Dimen delta = getWidth().minus(node.getWidth());
      if (!delta.isZero()) {
        forPacking = new NodeList(2);
        forPacking.append(node).append(new ChrKernNode(delta));
      }
    }
    return packedToWidth(forPacking.nodes(), width);
  }

  public static Node reboxedToWidth(Node node, Dimen width) {
    return (node.getWidth().equals(width)) ? node : packedToWidth(NodeList.nodes(node), width);
  }

  private static HBoxNode packedToWidth(NodeEnum nodes, Dimen width) {
    HSkipNode filler =
        new HSkipNode(Glue.valueOf(Dimen.ZERO, Dimen.UNITY, Glue.FIL, Dimen.UNITY, Glue.FIL));
    NodeList list = new NodeList();
    list.append(filler).append(nodes).append(filler);
    SizesEvaluator pack = new SizesEvaluator();
    HorizIterator.summarize(list.nodes(), pack);
    width = width.minus(pack.getHeight());
    Dimen size = pack.getBody().plus(pack.getDepth());
    pack.evaluate(width.minus(size), false);
    BoxSizes sizes = new BoxSizes(pack.getWidth(), width, pack.getLeftX(), pack.getHeight());
    return new HBoxNode(sizes, pack.getSetting(), list);
  }

  /* TeXt[1146] */
  public Dimen allegedlyVisibleWidth() {
    /*
    	Dimen		visible = Dimen.ZERO;
    	Dimen		width = Dimen.ZERO;
    	boolean		addingLeftX = false;
    	boolean		elasticSeen = false;
    	NodeEnum	nodes = list.nodes();
    	while (nodes.hasMoreNodes()) {
    	    Node	node = nodes.nextNode();
    	    elasticSeen = (elasticSeen || node.hasElasticWidth(setting));
    	    if (!elasticSeen) {
    		if (addingLeftX) width = width.plus(node.getLeftX());
    		width = width.plus(node.getWidth());
    	    }
    	    addingLeftX = true;
    	    //XXX what if it grows more than Dimen.MAX_VALUE
    	    if (node.allegedlyVisible())
    		if (elasticSeen) return Dimen.NULL;
    		else visible = width;
    	}
    	return visible;
    */
    VisibleSummarizer summarizer = new VisibleSummarizer(setting);
    summarizer.summarize(list.nodes());
    return summarizer.getVisibleWidth();
  }

  public String toString() {
    return "HBox(" + sizes + "; " + setting + "; " + list + ')';
  }
}
