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
// Filename: nts/node/VertIterator.java
// $Id: VertIterator.java,v 1.1.1.1 2000/08/09 06:10:07 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class VertIterator extends EnumSizesIterator {

  public VertIterator(NodeEnum nodes) {
    super(nodes);
  }

  public Dimen currHeight() {
    return curr.getHeight();
  }

  public Dimen currWidth() {
    return curr.getWidth();
  }

  public Dimen currDepth() {
    return curr.getDepth();
  }

  public Dimen currLeftX() {
    return curr.getLeftX();
  }

  public Dimen currStr() {
    return curr.getHstr();
  }

  public Dimen currShr() {
    return curr.getHshr();
  }

  public byte currStrOrd() {
    return curr.getHstrOrd();
  }

  public byte currShrOrd() {
    return curr.getHshrOrd();
  }

  public static BoxSizes naturalSizes(NodeEnum nodes, Dimen maxDepth) {
    SizesSummarizer pack = new SizesSummarizer();
    summarize(nodes, pack);
    if (maxDepth != Dimen.NULL) pack.restrictDepth(maxDepth);
    return new BoxSizes(
        pack.getHeight().plus(pack.getBody()), pack.getWidth(), pack.getDepth(), pack.getLeftX());
  }

  public static void summarize(NodeEnum nodes, SizesSummarizer pack) {
    (new VertIterator(nodes)).summarize(pack);
  }
}
