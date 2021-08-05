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
// Filename: nts/math/EqNoGroup.java
// $Id: EqNoGroup.java,v 1.1.1.1 2000/10/18 10:31:00 ksk Exp $
package nts.math;

import nts.builder.Builder;
import nts.noad.Conversion;
import nts.noad.Noad;
import nts.node.HBoxNode;
import nts.node.NodeList;
import nts.typo.TypoCommand;

public class EqNoGroup extends FormulaGroup {

  private /* final */ boolean left;

  public EqNoGroup(FormulaBuilder builder, boolean left) {
    super(builder);
    this.left = left;
  }

  public EqNoGroup(boolean left) {
    this(new FormulaBuilder(currLineNumber()), left);
  }

  /* TeXtp[1194] */
  public void stop() {
    Builder.pop();
    MathBuilder bld = (MathBuilder) TypoCommand.getBld();
    Config cfg = getConfig();
    boolean success = necessaryParamsDefined();
    expectAnotherMathShift();
    NodeList list =
        (success)
            ? Conversion.madeOf(builder.getList().noads(), new FormulaStyle(Noad.TEXT_STYLE, false))
            : NodeList.EMPTY;
    HBoxNode box = HBoxNode.packedOf(list);
    bld.setEqNo(box, left);
  }

  public void close() {
    popLevel();
  }
}
