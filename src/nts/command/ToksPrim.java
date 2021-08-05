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
// Filename: nts/command/ToksPrim.java
// $Id: ToksPrim.java,v 1.1.1.1 1999/06/24 10:59:05 ksk Exp $
package nts.command;

import nts.io.Log;

/** Setting tokens register primitive. */
public class ToksPrim extends RegisterPrim implements TokenList.Provider {

  /**
   * Creates a new |ToksPrim| with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the |ToksPrim|
   */
  public ToksPrim(String name) {
    super(name);
  }

  public final void set(int idx, TokenList val, boolean glob) {
    if (glob) getEqt().gput(tabKind, idx, val);
    else getEqt().put(tabKind, idx, val);
  }

  public final TokenList get(int idx) {
    TokenList val = (TokenList) getEqt().get(tabKind, idx);
    return (val != TokenList.NULL) ? val : TokenList.EMPTY;
  }

  public void addEqValueOn(int idx, Log log) {
    get(idx).addOn(log, getConfig().getIntParam(INTP_MAX_TLRES_TRACE));
  }

  /**
   * Performs the assignment.
   *
   * @param src source token for diagnostic output.
   * @param glob indication that the assignment is global.
   */
  protected final void assign(Token src, boolean glob) {
    int idx = scanRegisterCode();
    skipOptEquals();
    set(idx, scanToks(src, false), glob);
  }

  public boolean hasToksValue() {
    return true;
  }

  public TokenList getToksValue() {
    return get(scanRegisterCode());
  }
}
