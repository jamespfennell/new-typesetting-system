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
// Filename: nts/command/TokenListTokenizer.java
// $Id: TokenListTokenizer.java,v 1.1.1.1 2000/08/04 14:46:43 ksk Exp $
package nts.command;

import nts.base.BoolPar;

/** |Tokenizer| for sequence of |Token|s in an array. */
/* See TeXtp[323]. */
public abstract class TokenListTokenizer extends Tokenizer {

  /** Array of |Token|s where the sequence is stored */
  private final TokenList list;

  /** The position of the next |Token| in the list */
  private int pos;

  /** Position after the last |Token| in the sequence */
  private final int end;

  /**
   * Creates |Tokenizer| for a portion of a |TokenList|.
   *
   * @param list the |TokenList|.
   * @param start position of the first |Token| in the sequence.
   * @param end position after the last |Token| in the sequence.
   */
  public TokenListTokenizer(TokenList list, int start, int end) {
    this.list = list;
    this.pos = start;
    this.end = end;
  }

  /**
   * Creates |Tokenizer| for a |TokenList|.
   *
   * @param list the |TokenList|.
   */
  public TokenListTokenizer(TokenList list) {
    this.list = list;
    this.pos = 0;
    this.end = list.length();
  }

  /**
   * Gives the next |Token| from the sequence.
   *
   * @param canExpand boolean output parameter querying whether the acquired |Token| can be expanded
   *     (e.g. was not preceded by \noexpand).
   * @return next |Token| or |Token.NULL| when the sequence is finished.
   */
  public Token nextToken(BoolPar canExpand) {
    canExpand.set(true);
    return (pos < end) ? list.tokenAt(pos++) : Token.NULL;
  }

  public boolean finishedList() {
    return (pos >= end);
  }

  public int showList(ContextDisplay disp, int lines) {
    list.addContext(disp.left(), disp.right(), pos, 100000);
    disp.show(); // XXX why so much?
    return 1;
  }
}
