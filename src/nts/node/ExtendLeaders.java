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
// Filename: nts/node/ExtendLeaders.java
// $Id: ExtendLeaders.java,v 1.1.1.1 2000/10/15 05:22:31 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class ExtendLeaders extends BoxLeaders {

  public static final String DESCRIPTOR = "xleaders";

  public ExtendLeaders(Node node, Mover mover) {
    super(node, mover);
  }

  protected String getDesc() {
    return DESCRIPTOR;
  }

  /* STRANGE
   * we have to fake a TeX bug here
   */
  /* TeXtp[627] */
  protected void typeSet(TypeSetter setter, SettingContext sctx, Dimen size, Dimen nodeSize) {
    int count = size.divide(nodeSize);
    Dimen rest = size.modulo(nodeSize);
    Dimen gap = rest.roundDivide(count + 1);
    rest = rest.minus(gap.times(count - 1));
    if (rest.lessThan(0)) count--; // compensation for TeX bug [627]
    typeSet(setter, sctx, count, rest.over(2), gap);
  }
}
