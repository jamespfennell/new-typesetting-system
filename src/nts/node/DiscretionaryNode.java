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
// Filename: nts/node/DiscretionaryNode.java
// $Id: DiscretionaryNode.java,v 1.1.1.1 2000/06/13 22:26:09 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;

public class DiscretionaryNode extends AnyBoxedNode {
  /* root corresponding to disc_node */

  /* STRANGE
   * why two different limits?
   */
  public static final int MAX_LIST_LENGTH = 255;
  public static final int MAX_LIST_RECONS_LENGTH = 127;

  public static final DiscretionaryNode EMPTY =
      new DiscretionaryNode(NodeList.EMPTY, NodeList.EMPTY, NodeList.EMPTY);

  protected final NodeList pre;
  protected final NodeList post;
  protected final NodeList list;

  public DiscretionaryNode(NodeList pre, NodeList post, NodeList list) {
    super(HorizIterator.naturalSizes(list.nodes()));
    this.pre = pre;
    this.post = post;
    this.list = list;
  }

  /* STRANGE
   * isn't it inconsistent?
   */
  public boolean hasKern() {
    Node last = list.lastNode();
    return (last != Node.NULL && last.hasKern());
  }

  public Dimen getKern() {
    Node last = list.lastNode();
    return (last != Node.NULL) ? last.getKern() : Dimen.NULL;
  }

  /* STRANGE
   * at break the list is replaced by pre+post but \/ remains
   */
  public Dimen getItalCorr() {
    Node last = list.lastNode();
    return (last != Node.NULL) ? last.getItalCorr() : Dimen.NULL;
  }

  /* TeXtp[175] */
  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    return post.addShortlyOn(log, pre.addShortlyOn(log, metric));
  }

  /* TeXtp[195] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc());
    if (!list.isEmpty()) log.add(" replacing ").add(list.length());
    cntx.addOn(log, pre.nodes(), '.');
    cntx.addOn(log, post.nodes(), '|');
    cntx.addItems(log, list.nodes());
  }

  public String getDesc() {
    return "discretionary";
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {
    setter.moveLeft(getLeftX());
    NodeEnum nodes = list.nodes();
    while (nodes.hasMoreNodes()) {
      Node node = nodes.nextNode();
      setter.moveRight(node.getLeftX());
      node.typeSet(setter, sctx);
      setter.moveRight(node.getWidth());
    }
    setter.moveLeft(getWidth());
  }

  /* TeXtp[856] */
  public void addBreakDescOn(Log log) {
    log.addEsc("discretionary");
  }

  public int breakPenalty(BreakingCntx brCntx) {
    return (pre.isEmpty()) ? brCntx.exHyphenPenalty() : brCntx.hyphenPenalty();
  }

  public boolean isHyphenBreak() {
    return true;
  }

  public Dimen preBreakWidth() {
    return HorizIterator.totalWidth(pre.nodes());
  }

  public Dimen postBreakWidth() {
    return HorizIterator.totalWidth(post.nodes());
  }

  /* STRANGE
   * Why is EMPTY inserted?
   * Emptied discretionary is left in list (TeXtp[882]), why?
   */
  public NodeEnum atBreakReplacement() {
    NodeList replacement = new NodeList(1 + pre.length());
    replacement.append(EMPTY).append(pre);
    return replacement.nodes();
  }

  public NodeEnum postBreakNodes() {
    return post.nodes();
  }

  public boolean discardsAfter() {
    return post.isEmpty();
  }

  public void contributeVisible(VisibleSummarizer summarizer) {
    summarizer.summarize(list.nodes());
  }

  public String toString() {
    return "Discretionary(" + pre + "; " + post + "; " + list + ')';
  }
}
