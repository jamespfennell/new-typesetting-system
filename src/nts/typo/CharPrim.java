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
// Filename: nts/typo/CharPrim.java
// $Id: CharPrim.java,v 1.1.1.1 2000/02/17 17:00:28 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Prim;
import nts.command.Token;
import nts.io.CharCode;

public class CharPrim extends BuilderPrim {

  public CharPrim(String name) {
    super(name);
  }

  /* TeXtp[1031] */
  public final Action NORMAL =
      new Action() {
        public void exec(Builder bld, Token src) {
          appendChar(bld, charCodeToAdd());
        }
      };

  public CharCode charCodeToAdd() {
    CharCode code = Token.makeCharCode(Prim.scanCharacterCode());
    if (code == CharCode.NULL) throw new RuntimeException("no char number scanned");
    return code;
  }
}
