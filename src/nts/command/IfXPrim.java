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
// Filename: nts/command/IfXPrim.java
// $Id: IfXPrim.java,v 1.1.1.1 1999/05/31 11:18:47 ksk Exp $
package nts.command;

import nts.base.BoolPar;

public class IfXPrim extends AnyIfPrim {

  public IfXPrim(String name) {
    super(name);
  }

  protected final boolean holds() {
    BoolPar exp = new BoolPar();
    Token tok = nextUncheckedRawToken(exp);
    Command first = meaningOf(tok, exp.get());
    tok = nextUncheckedRawToken(exp);
    Command second = meaningOf(tok, exp.get());
    return (first == second || first.sameAs(second));
  }
}
