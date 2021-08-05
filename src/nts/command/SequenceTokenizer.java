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
// Filename: nts/command/SequenceTokenizer.java
// $Id: SequenceTokenizer.java,v 1.1.1.1 1999/05/31 11:18:51 ksk Exp $
package nts.command;

import nts.base.BoolPar;

public abstract class SequenceTokenizer extends Tokenizer {

  /** The current input line */
  private Tokenizer curr;

  public SequenceTokenizer() {
    curr = Tokenizer.NULL;
  }

  public SequenceTokenizer(Tokenizer first) {
    curr = first;
  }

  /**
   * Gets the next |Token| from the input.
   *
   * @param canExpand boolean output parameter querying whether the acquired |Token| can be expanded
   *     (e.g. was not preceded by \noexpand).
   * @return the next token from the input or |Token.NULL| if the input is finished.
   */
  public Token nextToken(BoolPar canExpand) {
    if (curr == Tokenizer.NULL) curr = nextTokenizer();
    while (curr != Tokenizer.NULL) {
      Token tok = curr.nextToken(canExpand);
      if (tok != Token.NULL) return tok;
      curr = nextTokenizer();
    }
    return Token.NULL;
  }

  public boolean enoughContext() {
    // if (curr == Tokenizer.NULL) curr = nextTokenizer();
    return (curr != Tokenizer.NULL) ? curr.enoughContext() : false;
  }

  public int show(ContextDisplay disp, boolean force, int lines) {
    // if (curr == Tokenizer.NULL) curr = nextTokenizer();
    return (curr != Tokenizer.NULL) ? curr.show(disp, force, lines) : 0;
  }

  public abstract Tokenizer nextTokenizer();
}
