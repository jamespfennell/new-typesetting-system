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
// Filename: nts/command/PrefixPrim.java
// $Id: PrefixPrim.java,v 1.1.1.1 2001/03/22 13:34:20 ksk Exp $
package nts.command;

import nts.io.Log;
import nts.io.Name;

/**
 * Prim command of the macro language which can be prefixed by one of prefix commands (as \global,
 * \long or \outer).
 */
public abstract class PrefixPrim extends Prim {

  public static final int LONG = 1;
  public static final int OUTER = 2;
  public static final int GLOBAL = 4;

  /** inaccessible but definable token */
  private static final Token INACCESSIBLE =
      new Token() {
        private final Name NAME = makeName("inaccessible");

        public boolean definable() {
          return true;
        }

        public void define(Command cmd, boolean glob) {}

        public boolean match(Token tok) {
          return false;
        }

        public void addOn(Log log) {
          NAME.addEscapedOn(log);
        }

        public void addProperlyOn(Log log) {
          NAME.addProperlyEscapedOn(log);
        }
      };

  /**
   * Creates a new PrefixPrim with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the PrefixPrim
   */
  protected PrefixPrim(String name) {
    super(name);
  }

  /** Non prefixed version of exec */
  public final void exec(Token src) {
    exec(src, 0);
  }

  public abstract void exec(Token src, int prefixes);

  public final boolean assignable() {
    return true;
  }

  public void doAssignment(Token src, int prefixes) {
    exec(src, prefixes);
  }

  /* TeXtp[1215] */
  public static Token definableToken() {
    for (; ; ) {
      Token tok;
      while ((tok = nextRawToken()).matchSpace())
        ;
      if (tok.definable()) return tok;
      else {
        if (!tok.frozen()) backToken(tok);
        insertToken(INACCESSIBLE);
        error("MissingCtrlSeq");
      }
    }
  }

  public static final int BOOLP_ALWAYS_GLOBAL = newBoolParam();
  public static final int BOOLP_NEVER_GLOBAL = newBoolParam();

  public static boolean globalAssignment(int prefixes) {
    return (getConfig().getBoolParam(BOOLP_ALWAYS_GLOBAL)
        || !getConfig().getBoolParam(BOOLP_NEVER_GLOBAL) && (prefixes & GLOBAL) != 0);
  }

  public static void beforeAssignment(Command cmd, int prefixes) {
    if ((prefixes & (LONG | OUTER)) != 0) error("NonDefineCommand", esc("long"), esc("outer"), cmd);
  }

  public static void afterAssignment() {
    getConfig().afterAssignment();
  }
}
