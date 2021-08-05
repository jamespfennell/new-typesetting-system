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
// Filename: nts/command/NormalCharToken.java
// $Id: NormalCharToken.java,v 1.1.1.1 1999/08/16 12:11:05 ksk Exp $
package nts.command;

import nts.io.CharCode;

public abstract class NormalCharToken extends CharToken {

  public NormalCharToken(CharCode code) {
    super(code);
  }

  public boolean matchNormal(char c) {
    return code.match(c);
  }

  /**
   * Gives the 7 bit ascii character code of this normal (letter or other) character |Token|. The
   * result is |CharCode.NO_CHAR| if its character code is not a 7 bit ascii.
   *
   * @return the 7 bit ascii code if the code is defined, |CharCode.NO_CHAR| otherwise.
   */
  public char normalChar() {
    return code.toChar();
  }

  public String toString() {
    return "<NormalChar: " + code + '>';
  }

  private static CharHandler handler;

  public static void setHandler(CharHandler hnd) {
    handler = hnd;
  }

  public abstract class Meaning extends CharToken.Meaning {
    public void exec(Token src) {
      handler.handle(code, src);
    }

    public CharCode charCodeToAdd() {
      return code;
    }
  }
}
