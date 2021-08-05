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
// Filename: nts/command/CsNamePrim.java
// $Id: CsNamePrim.java,v 1.1.1.1 2000/03/17 14:44:13 ksk Exp $
package nts.command;

import nts.io.CharCode;
import nts.io.Name;

/* TeXtp[372] */
public class CsNamePrim extends ExpandablePrim {

  public CsNamePrim(String name) {
    super(name);
  }

  public void expand(Token src) {
    Name.Buffer buf = new Name.Buffer();
    Token tok;
    CharCode code;
    while ((code = (tok = nextExpToken()).nonActiveCharCode()) != CharCode.NULL) buf.append(code);
    if (!meaningOf(tok).isEndCsName()) {
      backToken(tok);
      error("MissingEndcsname", esc("endcsname"));
    }
    tok = new CtrlSeqToken(buf.toName());
    if (meaningOf(tok).sameAs(Undefined.getUndefined())) tok.define(Relax.getRelax(), false);
    backToken(tok);
  }
}
