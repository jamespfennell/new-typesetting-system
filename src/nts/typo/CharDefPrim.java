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
// Filename: nts/typo/CharDefPrim.java
// $Id: CharDefPrim.java,v 1.1.1.1 2000/03/17 14:03:13 ksk Exp $
package nts.typo;

import nts.base.Num;
import nts.command.AssignPrim;
import nts.command.Command;
import nts.command.Primitive;
import nts.command.Relax;
import nts.command.Token;
import nts.io.CharCode;
import nts.io.Log;

public class CharDefPrim extends AssignPrim {

  private Primitive prim;

  public CharDefPrim(String name, Primitive prim) {
    super(name);
    this.prim = prim;
  }

  protected void assign(Token src, boolean glob) {
    Token tok = definableToken();
    tok.define(Relax.getRelax(), glob);
    skipOptEquals();
    CharCode code = Token.makeCharCode(scanCharacterCode());
    if (code != CharCode.NULL) tok.define(new CharGiven(code, prim.getName()), glob);
    else throw new RuntimeException("no char number scanned");
  }
}

class CharGiven extends Command implements Num.Provider {

  private CharCode code;
  private String name;

  public CharGiven(CharCode code, String name) {
    this.code = code;
    this.name = name;
  }

  public void exec(Token src) {
    BuilderCommand.handleChar(code, src);
  }

  public CharCode charCodeToAdd() {
    return code;
  }

  public boolean sameAs(Command cmd) {
    return (cmd instanceof CharGiven && code.match(((CharGiven) cmd).code));
  }

  public void addOn(Log log) {
    log.addEsc(name).add('"').add(Integer.toHexString(code.numValue()).toUpperCase());
  }

  public boolean hasNumValue() {
    return true;
  }

  public Num getNumValue() {
    return Num.valueOf(code.numValue());
  }

  public final String toString() {
    return "[character given: " + code + ']';
  }
}
