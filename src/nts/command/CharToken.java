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
// Filename: nts/command/CharToken.java
// $Id: CharToken.java,v 1.1.1.1 2000/05/04 21:22:25 ksk Exp $
package nts.command;

import nts.io.CharCode;
import nts.io.Log;

/** Character Token. */
public abstract class CharToken extends Token {

  public interface Maker {
    Token make(CharCode code);
  }

  protected CharCode code;

  public CharToken(CharCode code) {
    this.code = code;
  }

  public final CharCode charCode() {
    return code;
  }

  public Token makeCharToken(CharCode code) {
    Maker maker = getMaker();
    return (maker != null) ? maker.make(code) : Token.NULL;
  }

  public CharCode nonActiveCharCode() {
    return code;
  }

  public int numValue() {
    return code.numValue();
  }

  public void addOn(Log log) {
    log.add(code);
  }

  public final boolean match(Token tok) {
    return (tok instanceof CharToken && match((CharToken) tok));
  }

  public final boolean match(CharCode chr) {
    return code.match(chr);
  }

  public abstract boolean match(CharToken tok);

  public Token category() {
    return this;
  }

  public abstract Maker getMaker();

  public String toString() {
    return "<Char: " + code + '>';
  }

  public abstract class Meaning extends Command {

    public final CharCode charCode() {
      return code;
    }

    public final void addOn(Log log) {
      log.add(description()).add(' ').add(code);
    }

    public boolean sameAs(Command cmd) {
      return (this.getClass() == cmd.getClass() && code.match(((Meaning) cmd).charCode()));
      // XXX is cast to Meaning necessary?
    }

    public final Token origin() {
      return CharToken.this;
    }

    public final String toString() {
      return "[" + description() + ' ' + code + ']';
    }

    protected abstract String description();
  }
}
