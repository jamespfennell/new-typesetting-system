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
// Filename: nts/typo/KernPrim.java
// $Id: KernPrim.java,v 1.1.1.1 1999/06/10 07:54:50 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.Token;

public class KernPrim extends BuilderPrim {

  private final Dimen kern;

  public KernPrim(String name) {
    super(name);
    kern = Dimen.NULL;
  }

  public KernPrim(String name, Dimen kern) {
    super(name);
    this.kern = kern;
  }

  public Dimen getKern() {
    return (kern != Dimen.NULL) ? kern : scanDimen();
  }

  public void exec(Builder bld, Token src) {
    bld.addKern(getKern());
  }
}
