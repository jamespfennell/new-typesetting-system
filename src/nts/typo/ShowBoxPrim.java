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
// Filename: nts/typo/ShowBoxPrim.java
// $Id: ShowBoxPrim.java,v 1.1.1.1 1999/09/04 08:14:35 ksk Exp $
package nts.typo;

import nts.command.Prim;

public class ShowBoxPrim extends TypoShowingPrim {

  private final SetBoxPrim reg;

  public ShowBoxPrim(String name, SetBoxPrim reg) {
    super(name);
    this.reg = reg;
  }

  /* STRANGE
   * Why not addEsc("box") ?
   */
  /* TeXtp[1296] */
  protected void performShow() {
    int idx = Prim.scanRegisterCode();
    diagLog.startLine().add("> \\box").add(idx).add('=');
    addBoxOn(diagLog, reg.get(idx));
    /*
    System.out.println("\n\\box" + idx + " = (" + reg + ") {");
    TypoCommand.addBoxOn(termLog, reg.get(idx));
    System.out.println("}");
       */
  }
}
