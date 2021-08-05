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
// Filename: nts/typo/IfVBoxPrim.java
// $Id: IfVBoxPrim.java,v 1.1.1.1 1999/07/16 12:49:27 ksk Exp $
package nts.typo;

import nts.command.AnyIfPrim;
import nts.command.Prim;

public class IfVBoxPrim extends AnyIfPrim {

  private final SetBoxPrim reg;

  public IfVBoxPrim(String name, SetBoxPrim reg) {
    super(name);
    this.reg = reg;
  }

  protected final boolean holds() {
    return reg.get(Prim.scanRegisterCode()).isVBox();
  }
}
