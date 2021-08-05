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
// Filename: nts/command/LetPrim.java
// $Id: LetPrim.java,v 1.1.1.1 2000/03/17 14:43:31 ksk Exp $
package nts.command;

import nts.base.BoolPar;

public class LetPrim extends AssignPrim {

  public LetPrim(String name) {
    super(name);
  }

  protected void assign(Token src, boolean glob) {
    Token tok = definableToken();
    Command cmd = newMeaning();
    if (cmd.sameAs(Undefined.getUndefined())) cmd = Command.NULL;
    tok.define(cmd, glob);
  }

  protected Command newMeaning() {
    BoolPar exp = new BoolPar();
    Token tok;
    Command cmd;
    do {
      tok = nextRawToken(exp);
      cmd = meaningOf(tok, exp.get());
    } while (cmd.isSpacer());
    if (tok.matchOther('=')) {
      tok = nextRawToken(exp);
      cmd = meaningOf(tok, exp.get());
      if (cmd.isSpacer()) {
        tok = nextRawToken(exp);
        cmd = meaningOf(tok, exp.get());
      }
    }
    return cmd;
  }
}
