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
// Filename: nts/command/BaseToksChecker.java
// $Id: BaseToksChecker.java,v 1.1.1.1 1999/05/31 11:18:43 ksk Exp $
package nts.command;

import nts.base.BoolPar;
import nts.io.Loggable;

public abstract class BaseToksChecker implements InpTokChecker {

  protected final String tokError;
  protected final String eofError;
  protected final Loggable source;

  public BaseToksChecker(String tokErr, String eofErr, Loggable src) {
    tokError = tokErr;
    eofError = eofErr;
    source = src;
  }

  public Token checkToken(Token tok, BoolPar canExpand) {
    if (Command.meaningOf(tok, canExpand.get()).isOuter()) {
      handleOuterToken(tok);
      reportError(tokError);
      canExpand.set(true);
      return SpaceToken.TOKEN;
    }
    return tok;
  }

  public void checkEndOfFile() {
    reportError(eofError);
  }

  protected void reportError(String ident) {
    tryToFix();
    reportRunAway();
    Loggable[] params = {source};
    Command.nonDelError(ident, params);
  }

  protected void handleOuterToken(Token tok) {
    Command.backTokenWithoutCleaning(tok);
  }

  protected abstract void tryToFix();

  protected abstract void reportRunAway();
}
