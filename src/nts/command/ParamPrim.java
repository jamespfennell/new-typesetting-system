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
// Filename: nts/command/ParamPrim.java
// $Id: ParamPrim.java,v 1.1.1.1 1999/06/17 11:33:12 ksk Exp $
package nts.command;

import nts.base.EqTable;
import nts.io.EqTraceable;
import nts.io.Log;

/** Abstract ancestor of each parameter primitive. */
public abstract class ParamPrim extends AssignPrim implements EqTable.ExtEquiv, EqTraceable {

  private int eqLevel = 0;

  /**
   * Creates a new ParamPrim with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the ParamPrim
   */
  protected ParamPrim(String name) {
    super(name);
  }

  public final int getEqLevel() {
    return eqLevel;
  }

  public final void setEqLevel(int lev) {
    eqLevel = lev;
  }

  public final void addEqDescOn(Log log) {
    log.addEsc(getName());
  }

  public final void retainEqValue() {
    traceRestore(RETAINING, this);
  }

  public final void restoreEqValue(Object val) {
    setEqValue(val);
    traceRestore(RESTORING, this);
  }

  public abstract Object getEqValue();

  public abstract void setEqValue(Object val);

  public abstract void addEqValueOn(Log log);

  /**
   * Performs the assignment.
   *
   * @param src source token for diagnostic output.
   * @param glob indication that the assignment is global.
   */
  protected final void assign(Token src, boolean glob) {
    skipOptEquals();
    scanValue(src, glob);
  }

  protected abstract void scanValue(Token src, boolean glob);

  protected final void beforeSetting(boolean glob) {
    getEqt().beforeSetting(this, glob);
  }
}
