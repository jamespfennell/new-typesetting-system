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
// Filename: nts/command/SkipPrim.java
// $Id: SkipPrim.java,v 1.1.1.1 1999/06/24 10:47:29 ksk Exp $
package nts.command;

import nts.base.Glue;
import nts.io.Log;

/** Setting glue register primitive. */
public class SkipPrim extends RegisterPrim implements Glue.Provider {

  /**
   * Creates a new |SkipPrim| with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the |SkipPrim|
   */
  public SkipPrim(String name) {
    super(name);
  }

  public final void set(int idx, Glue val, boolean glob) {
    if (glob) getEqt().gput(tabKind, idx, val);
    else getEqt().put(tabKind, idx, val);
  }

  public final Glue get(int idx) {
    Glue val = (Glue) getEqt().get(tabKind, idx);
    return (val != Glue.NULL) ? val : Glue.ZERO;
  }

  public void addEqValueOn(int idx, Log log) {
    log.add(get(idx).toString("pt"));
  }

  public void perform(int idx, int operation, boolean glob) {
    set(idx, performFor(get(idx), operation, false), glob);
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
    set(idx, scanGlue(), glob);
  }

  public final void perform(int operation, boolean glob, Command after) {
    perform(scanRegisterCode(), operation, glob);
  }

  public boolean hasGlueValue() {
    return true;
  }

  public Glue getGlueValue() {
    return get(scanRegisterCode());
  }
}
