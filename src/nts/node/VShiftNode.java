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
// Filename: nts/node/VShiftNode.java
// $Id: VShiftNode.java,v 1.1.1.1 2000/10/05 11:11:32 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class VShiftNode extends AnyShiftNode {
  /* corresponding to ANY_node */

  protected VShiftNode(Node node, Dimen shift) {
    super(node, shift);
  }

  public static Node shiftingLeft(Node node, Dimen shift) {
    return (shift.isZero()) ? node : new VShiftNode(node, shift.negative());
  }

  public static Node shiftingRight(Node node, Dimen shift) {
    return (shift.isZero()) ? node : new VShiftNode(node, shift);
  }

  public Dimen getHeight() {
    return node.getHeight();
  }

  public Dimen getWidth() {
    return node.getWidth().plus(shift);
  }

  public Dimen getDepth() {
    return node.getDepth();
  }
  // XXX revide the treatment of LeftX. Where it becomes zero?
  // public Dimen	getLeftX() { return node.getLeftX().minus(shift); }
  public Dimen getLeftX() {
    return Dimen.ZERO;
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {
    setter.moveRight(shift);
    node.typeSet(setter, sctx.shiftedLeft(shift));
    setter.moveLeft(shift);
  }

  public Dimen getHeight(GlueSetting setting) {
    return node.getHeight(setting);
  }

  public Dimen getWidth(GlueSetting setting) {
    return node.getWidth(setting).plus(shift);
  }

  public Dimen getDepth(GlueSetting setting) {
    return node.getDepth(setting);
  }

  public Dimen getLeftX(GlueSetting setting) {
    return node.getLeftX(setting).minus(shift);
  }

  public String toString() {
    return "VShift(" + node + "; " + shift + ')';
  }
}
