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
// Filename: nts/command/Tokenizer.java
// $Id: Tokenizer.java,v 1.1.1.1 2000/01/27 16:54:19 ksk Exp $
package nts.command;

import nts.base.BoolPar;

/** Base abstract class for all |Tokenizer|s. */
public abstract class Tokenizer {

  public static final Tokenizer NULL = null;

  /** The stack where this instance is pushed to */
  private TokenizerStack stack = null;

  /** The next |Tokenizer| in the stack (the one under this one) */
  private Tokenizer next = NULL;

  /**
   * Gets the stack of |Tokenizer|s where this |Tokenizer| is pushed.
   *
   * @return the stack which this |Tokenizer| belongs to or |null| if this |Tokenizer| is not
   *     pushed.
   */
  public TokenizerStack getStack() {
    return stack;
  }

  public Tokenizer getNext() {
    return next;
  }

  synchronized void pushTo(TokenizerStack stack, Tokenizer next) {
    if (this.stack != null) throw new RuntimeException("Tokenizer is already in a stack");
    if (this != stack.getTop()) // SSS
    throw new RuntimeException("Misused Tokenizer.pushTo()");
    this.stack = stack;
    this.next = next;
  }

  synchronized void popFrom(TokenizerStack stack) {
    if (this.stack != stack || this.next != stack.getTop()) // SSS
    throw new RuntimeException("Misused Tokenizer.popFrom()");
    this.stack = null;
    this.next = null;
  }

  /**
   * Gets the next |Token| from this |Tokenizer|.
   *
   * @param canExpand boolean output parameter querying whether the acquired |Token| can be expanded
   *     (e.g. was not preceded by \noexpand).
   * @return the next |Token| or |Token.NULL| if this |Tokenizer| is finished.
   */
  public abstract Token nextToken(BoolPar canExpand);

  /** Should be overriden by a subclass in order to release resources used by this |Tokenizer|. */
  public boolean close() {
    return false;
  }

  public boolean finishedList() {
    return false;
  }

  public boolean finishedInsert() {
    return false;
  }

  public boolean finished() {
    return finishedList();
  }

  public boolean endInput() {
    return false;
  }

  public boolean enoughContext() {
    return false;
  }

  public FilePos filePos() {
    return FilePos.NULL;
  }

  public abstract int show(ContextDisplay disp, boolean force, int lines);
}
