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
// Filename: nts/node/CenterLeaders.java
// $Id: CenterLeaders.java,v 1.1.1.1 1999/09/06 08:06:53 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class CenterLeaders extends BoxLeaders {

  public static final String DESCRIPTOR = "cleaders";

  public CenterLeaders(Node node, Mover mover) {
    super(node, mover);
  }

  protected String getDesc() {
    return DESCRIPTOR;
  }

  protected void typeSet(TypeSetter setter, SettingContext sctx, Dimen size, Dimen nodeSize) {
    int count = size.divide(nodeSize);
    Dimen rest = size.modulo(nodeSize);
    typeSet(setter, sctx, count, rest.over(2), Dimen.ZERO);
  }
}
