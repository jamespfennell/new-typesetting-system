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
// Filename: nts/command/MacroBody.java
// $Id: MacroBody.java,v 1.1.1.1 2000/01/02 17:54:19 ksk Exp $
package nts.command;

import nts.io.CharCode;
import nts.io.Log;
import nts.io.Name;

public class MacroBody extends TokenList {

  public static final MacroBody NULL = null;

  public static final MacroBody EMPTY = new MacroBody();

  public static class Buffer extends TokenList.Buffer {

    public Buffer() {
      super();
    }

    public Buffer(int initCap) {
      super(initCap);
    }

    public Buffer(int initCap, int capIncrement) {
      super(initCap, capIncrement);
    }

    public TokenList.Buffer appendParam(int digit, CharCode matchCode) {
      return append(new ParamToken(digit, matchCode));
    }

    public MacroBody toMacroBody() {
      if (super.length() == 0) return EMPTY;
      Token[] tokens = new Token[super.length()];
      // CCC super. required by jikes
      data.copyInto(tokens);
      return new MacroBody(tokens);
    }

    public TokenList toTokenList() {
      return toMacroBody();
    }
  }

  public MacroBody() {
    super();
  }

  public MacroBody(Token[] tokens) {
    super(tokens);
  }

  public MacroBody(Token[] tokens, int offset, int count) {
    super(tokens, offset, count);
  }

  public MacroBody(Token tok) {
    super(tok);
  }

  public MacroBody(String str) {
    super(str);
  }

  public MacroBody(Name name) {
    super(name);
  }

  private static final String PREP = "->";
  private static final int PLEN = PREP.length();

  public void addOn(Log log) {
    log.add(PREP);
    super.addOn(log);
  }

  public void addOn(Log log, int maxCount) {
    if (maxCount > PLEN) {
      log.add(PREP);
      super.addOn(log, maxCount - PLEN);
    } else if (maxCount > 0) log.add(PREP.substring(0, maxCount));
  }

  public void addContext(Log left, Log right, int pos, int maxCount) {
    if (maxCount > PLEN) {
      left.add(PREP);
      super.addContext(left, right, pos, maxCount - PLEN);
    } else if (maxCount > 0) left.add(PREP.substring(0, maxCount));
  }
}

/** Token for substituting a parameter of a macro. */
class ParamToken extends Token {

  /** the number of the parameter 1-9 (coded as 0..8) */
  private int number;

  /** the character code for symbolic printing */
  private CharCode code;

  /**
   * Creates parameter token with given parameter number.
   *
   * @param number the parameter number
   * @param code the parameter character code
   */
  public ParamToken(int number, CharCode code) {
    this.number = number;
    this.code = code;
  }

  /**
   * Tells that this |Token| is a macro parameter.
   *
   * @return |true|.
   */
  public boolean isMacroParameter() {
    return true;
  }

  /**
   * Tells the number of parameter.
   *
   * @return the macro parameter number.
   */
  public int macroParameterNumber() {
    return number;
  }

  public boolean match(Token tok) {
    return (number == tok.macroParameterNumber());
  }

  /**
   * Prints its symbolic representation on the Log.
   *
   * @param log the Log to log on
   */
  public void addOn(Log log) {
    log.add(code);
    log.add((0 <= number && number < 9) ? Character.forDigit(number + 1, 10) : '!');
  }
}
