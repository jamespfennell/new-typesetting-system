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
// Filename: nts/typo/FetchBoxPrim.java
// $Id: FetchBoxPrim.java,v 1.1.1.1 2000/01/10 21:58:14 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Token;
import nts.node.Box;

public abstract class FetchBoxPrim extends BuilderPrim implements Box.Provider {

  public FetchBoxPrim(String name) {
    super(name);
  }

  public final void exec(Builder bld, Token src) {
    Box box = getBoxValue();
    if (!box.isVoid()) appendBox(bld, box);
  }

  public final boolean hasBoxValue() {
    return true;
  }

  public abstract Box getBoxValue();
}
