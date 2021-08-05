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
// Filename: nts/node/HShiftNode.java
// $Id: HShiftNode.java,v 1.1.1.1 2000/06/06 08:23:11 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class HShiftNode extends AnyShiftNode {
  /* corresponding to ANY_node */

  protected HShiftNode(Node node, Dimen shift) {
    super(node, shift);
  }

  public static Node shiftingUp(Node node, Dimen shift) {
    return (shift.isZero()) ? node : new HShiftNode(node, shift.negative());
  }

  public static Node shiftingDown(Node node, Dimen shift) {
    return (shift.isZero()) ? node : new HShiftNode(node, shift);
  }

  public Dimen getHeight() {
    return node.getHeight().minus(shift);
  }

  public Dimen getWidth() {
    return node.getWidth();
  }

  public Dimen getDepth() {
    return node.getDepth().plus(shift);
  }

  public Dimen getLeftX() {
    return node.getLeftX();
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {
    setter.moveDown(shift);
    node.typeSet(setter, sctx.shiftedUp(shift));
    setter.moveUp(shift);
  }

  public Dimen getHeight(GlueSetting setting) {
    return node.getHeight(setting).minus(shift);
  }

  public Dimen getWidth(GlueSetting setting) {
    return node.getWidth(setting);
  }

  public Dimen getDepth(GlueSetting setting) {
    return node.getDepth(setting).plus(shift);
  }

  public Dimen getLeftX(GlueSetting setting) {
    return node.getLeftX(setting);
  }

  public String toString() {
    return "HShift(" + node + "; " + shift + ')';
  }
}
