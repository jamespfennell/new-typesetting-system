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
// Filename: nts/typo/AnyUnCopyPrim.java
// $Id: AnyUnCopyPrim.java,v 1.1.1.1 1999/08/02 17:29:36 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Prim;
import nts.command.Token;
import nts.node.Box;

public class AnyUnCopyPrim extends BuilderPrim {

  protected final SetBoxPrim reg;

  public AnyUnCopyPrim(String name, SetBoxPrim reg) {
    super(name);
    this.reg = reg;
  }

  protected Box getBox(int idx) {
    return reg.get(idx);
  }

  protected void finishBox(int idx) {}

  /* TeXtp[1109,1110] */
  public final Action NORMAL =
      new Action() {
        public void exec(Builder bld, Token src) {
          int idx = Prim.scanRegisterCode();
          Box box = getBox(idx);
          if (!box.isVoid()) {
            if (bld.unBox(box)) finishBox(idx);
            else error("IncompatibleUnbox");
          }
        }
      };
}
