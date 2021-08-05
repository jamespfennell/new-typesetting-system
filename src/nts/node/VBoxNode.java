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
// Filename: nts/node/VBoxNode.java
// $Id: VBoxNode.java,v 1.1.1.1 2000/06/06 08:28:33 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class VBoxNode extends AnyBoxNode {
  /* root corresponding to vlist_node */

  public VBoxNode(BoxSizes sizes, GlueSetting setting, NodeList list) {
    super(sizes, setting, list);
  }

  public final boolean isVBox() {
    return true;
  }

  public String getDesc() {
    return "vbox";
  }

  protected void moveStart(TypeSetter setter) {
    setter.moveUp(getHeight());
  }

  protected void movePrev(TypeSetter setter, Node node) {
    setter.moveDown(node.getHeight(setting));
    node.syncVertIfBox(setter);
  }

  protected void movePast(TypeSetter setter, Node node) {
    setter.moveDown(node.getDepth(setting));
  }

  public Box pretendSizesCopy(BoxSizes sizes) {
    return new VBoxNode(sizes, setting, list);
  }

  public static VBoxNode packedOf(NodeList list, Dimen maxDepth) {
    return new VBoxNode(
        VertIterator.naturalSizes(list.nodes(), maxDepth), GlueSetting.NATURAL, list);
  }

  public static VBoxNode packedOf(NodeList list) {
    return packedOf(list, Dimen.NULL);
  }

  // XXX could be optimized
  public static VBoxNode packedOf(Node node) {
    return packedOf(new NodeList(node));
  }

  public String toString() {
    return "VBox(" + sizes + "; " + setting + "; " + list + ')';
  }
}
