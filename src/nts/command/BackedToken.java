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
// Filename: nts/command/BackedToken.java
// $Id: BackedToken.java,v 1.1.1.1 2000/08/04 14:46:32 ksk Exp $
package nts.command;

import nts.base.BoolPar;
import nts.io.Log;

/** |Tokenizer| for a single |Token|. */
/* See TeXtp[325]. */
public class BackedToken extends Tokenizer {

  /** The |Token| */
  private final Token token;

  private boolean readed = false;

  private final boolean expOK;

  /**
   * Creates |Tokenizer| for a single |Token|.
   *
   * @param tok the single |Token|
   * @param exp tells whether the backed |Token| can be expanded (e.g. was not preceded by
   *     \noexpand).
   */
  public BackedToken(Token tok, boolean exp) {
    token = tok;
    expOK = exp;
  }

  /**
   * Gives the single |Token| on the first call.
   *
   * @param canExpand boolean output parameter querying whether the acquired |Token| can be expanded
   *     (e.g. was not preceded by \noexpand).
   * @return the single |Token| or |Token.NULL| on next calls.
   */
  public Token nextToken(BoolPar canExpand) {
    canExpand.set(expOK);
    if (readed) return Token.NULL;
    readed = true;
    return token;
  }

  public boolean finishedList() {
    return readed;
  }

  public int show(ContextDisplay disp, boolean force, int lines) {
    String desc;
    Log where;
    if (readed) {
      if (!force) return 0;
      desc = "<recently read> ";
      where = disp.left();
    } else {
      desc = "<to be read again> ";
      where = disp.right();
    }
    disp.normal().startLine().add(desc);
    if (!expOK) where.addEsc("notexpanded: ");
    token.addProperlyOn(where);
    disp.show();
    return 1;
  }
}
