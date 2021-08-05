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
// Filename: nts/command/CaseShiftPrim.java
// $Id: CaseShiftPrim.java,v 1.1.1.1 1999/06/06 13:32:16 ksk Exp $
package nts.command;

import nts.io.CharCode;

public abstract class CaseShiftPrim extends Prim {

  protected CaseShiftPrim(String name) {
    super(name);
  }

  public void exec(Token src) {
    TokenList list = scanTokenList(src, false);
    Token[] dest = new Token[list.length()];
    for (int i = 0; i < list.length(); i++) {
      Token tok = list.tokenAt(i);
      CharCode code = tok.charCode();
      if (code != CharCode.NULL) {
        Token ntok = tok.makeCharToken(shiftCase(code));
        if (ntok != Token.NULL) tok = ntok;
      }
      dest[i] = tok;
    }
    backList(new TokenList(dest));
  }

  protected abstract CharCode shiftCase(CharCode code);
}
