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
// Filename: nts/node/AnyBoxedNode.java
// $Id: AnyBoxedNode.java,v 1.1.1.1 2000/05/26 21:14:42 ksk Exp $
package nts.node;

import nts.base.Dimen;

public abstract class AnyBoxedNode extends BaseNode {
  /* corresponding to hlist_node, vlist_node, rule_node, disc_node */

  protected final BoxSizes sizes;

  public AnyBoxedNode(BoxSizes sizes) {
    this.sizes = sizes;
  }

  public BoxSizes getSizes() {
    return sizes;
  }

  public Dimen getHeight() {
    return sizes.getHeight();
  }

  public Dimen getWidth() {
    return sizes.getWidth();
  }

  public Dimen getDepth() {
    return sizes.getDepth();
  }

  public Dimen getLeftX() {
    return sizes.getLeftX();
  }
}
