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
// Filename: nts/command/ActiveCharToken.java
// $Id: ActiveCharToken.java,v 1.1.1.1 2000/02/16 11:00:31 ksk Exp $
package nts.command;

import nts.io.CharCode;

public class ActiveCharToken extends CharToken {

  public static final Maker MAKER =
      new Maker() {
        public Token make(CharCode code) {
          return new ActiveCharToken(code);
        }
      };

  public ActiveCharToken(CharCode code) {
    super(code);
  }

  public interface Meaninger {
    Command get(CharCode code);

    void set(CharCode code, Command cmd, boolean glob);
  }

  protected static Meaninger meaninger;

  public static void setMeaninger(Meaninger m) {
    meaninger = m;
  }

  /**
   * Gives Command associated in table of equivalents.
   *
   * @return the Command to interpret this token.
   */
  public Command meaning() {
    return meaninger.get(code);
  }

  /**
   * Tells that the meaning of the |Token| can be redefined.
   *
   * @return |true|.
   */
  public boolean definable() {
    return true;
  }

  /**
   * Define given |Command| to be equivalent in table of equivalents.
   *
   * @param cmd the object to interpret this token.
   * @param glob if |true| the equivalent is defined globaly.
   */
  public void define(Command cmd, boolean glob) {
    meaninger.set(code, cmd, glob);
  }

  /**
   * Gives |CharCode.NULL| to indicate that this |Token| is an active character |Token| and is not
   * willing to provide |CharCode| upon this method call.
   *
   * @return |CharCode.NO_CHAR|.
   */
  public CharCode nonActiveCharCode() {
    return CharCode.NULL;
  }

  public boolean match(CharToken tok) {
    return (tok instanceof ActiveCharToken && tok.match(code));
  }

  public Maker getMaker() {
    return MAKER;
  }

  public String toString() {
    return "<ActiveChar: " + code + '>';
  }
}
