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
// Filename: nts/typo/VSplitPrim.java
// $Id: VSplitPrim.java,v 1.1.1.1 2000/03/20 13:04:52 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.base.Glue;
import nts.command.Prim;
import nts.command.TokenList;
import nts.node.Box;
import nts.node.NamedVSkipNode;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.VBoxNode;
import nts.node.VertSplit;
import nts.node.VoidBoxNode;

public class VSplitPrim extends FetchBoxPrim {

  private final SetBoxPrim reg;
  private final TokenList.Maintainer firstMark;
  private final TokenList.Maintainer lastMark;

  public VSplitPrim(
      String name, SetBoxPrim reg, TokenList.Maintainer firstMark, TokenList.Maintainer lastMark) {
    super(name);
    this.reg = reg;
    this.firstMark = firstMark;
    this.lastMark = lastMark;
  }

  /* TeXtp[1082,977] */
  public Box getBoxValue() {
    int idx = Prim.scanRegisterCode();
    if (!scanKeyword("to")) error("MissingToForVsplit");
    Dimen size = scanDimen();
    Box box = reg.get(idx);
    firstMark.setToksValue(TokenList.EMPTY);
    lastMark.setToksValue(TokenList.EMPTY);
    if (box.isVoid()) return box;
    NodeEnum nodes = box.getVertList();
    if (nodes == NodeEnum.NULL) {
      error("SplittingNonVbox", this, esc("vbox"));
      return VoidBoxNode.BOX;
    }
    Dimen maxDepth = getConfig().getDimParam(DIMP_SPLIT_MAX_DEPTH);
    VSplitSplit splitter = new VSplitSplit(nodes);
    NodeList head = splitter.makeSplitting(size, maxDepth);
    setMarks(head.nodes(), firstMark, lastMark);
    splitter.pruneTop();
    NodeList tail = new NodeList(splitter.nodes());
    if (tail.isEmpty()) box = VoidBoxNode.BOX;
    else box = VBoxNode.packedOf(tail);
    reg.foist(idx, box);
    return packVBox(head, size, maxDepth);
  }

  protected static class VSplitSplit extends VertSplit {

    public VSplitSplit(NodeEnum nodes) {
      super(nodes);
    }

    protected Node topAdjustment(Dimen height) {
      return makeTopAdjustment(height, GLUEP_SPLIT_TOP_SKIP);
    }
  }

  public static boolean setMarks(
      NodeEnum nodes, TokenList.Maintainer first, TokenList.Maintainer last) {
    boolean noMark = true;
    while (nodes.hasMoreNodes()) {
      Node node = nodes.nextNode();
      if (node.isMark()) {
        if (noMark) {
          noMark = false;
          first.setToksValue(node.getMark());
        }
        last.setToksValue(node.getMark());
      }
    }
    return (!noMark);
  }

  /* TeXtp[969,1001] */
  public static Node makeTopAdjustment(Dimen height, int param, Glue skip) {
    Dimen dim = skip.getDimen();
    dim = (dim.moreThan(height)) ? dim.minus(height) : Dimen.ZERO;
    skip = skip.resizedCopy(dim);
    return new NamedVSkipNode(skip, getConfig().getGlueName(param));
  }

  public static Node makeTopAdjustment(Dimen height, int param) {
    return makeTopAdjustment(height, param, getConfig().getGlueParam(param));
  }
}
