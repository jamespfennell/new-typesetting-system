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
// Filename: nts/command/CountPrim.java
// $Id: CountPrim.java,v 1.1.1.1 1999/06/24 10:29:59 ksk Exp $
package nts.command;

import nts.base.Num;
import nts.io.Log;

/** Setting number register primitive. */
public class CountPrim extends RegisterPrim implements Num.Provider {

  /**
   * Creates a new |CountPrim| with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the |CountPrim|
   */
  public CountPrim(String name) {
    super(name);
  }

  public final void set(int idx, Num val, boolean glob) {
    if (glob) getEqt().gput(tabKind, idx, val);
    else getEqt().put(tabKind, idx, val);
  }

  public final Num get(int idx) {
    Num val = (Num) getEqt().get(tabKind, idx);
    return (val != Num.NULL) ? val : Num.ZERO;
  }

  public void addEqValueOn(int idx, Log log) {
    log.add(get(idx).toString());
  }

  public void perform(int idx, int operation, boolean glob) {
    set(idx, performFor(get(idx), operation), glob);
  }

  public final void setIntVal(int idx, int val, boolean glob) {
    set(idx, Num.valueOf(val), glob);
  }

  public final int intVal(int idx) {
    return get(idx).intVal();
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
    set(idx, scanNum(), glob);
  }

  public final void perform(int operation, boolean glob, Command after) {
    perform(scanRegisterCode(), operation, glob);
  }

  public boolean hasNumValue() {
    return true;
  }

  public Num getNumValue() {
    return get(scanRegisterCode());
  }
}
