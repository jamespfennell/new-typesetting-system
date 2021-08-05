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
// Filename: nts/command/DimenPrim.java
// $Id: DimenPrim.java,v 1.1.1.1 1999/06/24 10:44:01 ksk Exp $
package nts.command;

import nts.base.Dimen;
import nts.io.Log;

/** Setting dimension register primitive. */
public class DimenPrim extends RegisterPrim implements Dimen.Provider {

  /**
   * Creates a new |DimenPrim| with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the |DimenPrim|
   */
  public DimenPrim(String name) {
    super(name);
  }

  public final void set(int idx, Dimen val, boolean glob) {
    if (glob) getEqt().gput(tabKind, idx, val);
    else getEqt().put(tabKind, idx, val);
  }

  public final Dimen get(int idx) {
    Dimen val = (Dimen) getEqt().get(tabKind, idx);
    return (val != Dimen.NULL) ? val : Dimen.ZERO;
  }

  public void addEqValueOn(int idx, Log log) {
    log.add(get(idx).toString("pt"));
  }

  public void perform(int idx, int operation, boolean glob) {
    set(idx, performFor(get(idx), operation), glob);
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
    set(idx, scanDimen(), glob);
  }

  public final void perform(int operation, boolean glob, Command after) {
    perform(scanRegisterCode(), operation, glob);
  }

  public boolean hasDimenValue() {
    return true;
  }

  public Dimen getDimenValue() {
    return get(scanRegisterCode());
  }
}
