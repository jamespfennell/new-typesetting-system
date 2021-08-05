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
// Filename: nts/command/NumParam.java
// $Id: NumParam.java,v 1.1.1.1 1999/06/24 10:37:26 ksk Exp $
package nts.command;

import nts.base.IntProvider;
import nts.base.Num;
import nts.io.Log;

/** Setting number parameter primitive. */
public class NumParam extends PerformableParam implements IntProvider, Num.Provider {

  private Num value;

  /**
   * Creates a new NumParam with given name and value and stores it in language interpreter
   * |EqTable|.
   *
   * @param name the name of the NumParam
   * @param val the value of the NumParam
   */
  public NumParam(String name, Num val) {
    super(name);
    value = val;
  }

  public NumParam(String name, int val) {
    this(name, Num.valueOf(val));
  }

  /**
   * Creates a new NumParam with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the NumParam
   */
  public NumParam(String name) {
    this(name, Num.ZERO);
  }

  public final Object getEqValue() {
    return value;
  }

  public final void setEqValue(Object val) {
    value = (Num) val;
  }

  public final void addEqValueOn(Log log) {
    log.add(value.toString());
  }

  public Num get() {
    return value;
  }

  public void set(Num val, boolean glob) {
    beforeSetting(glob);
    value = val;
  }

  public final int intVal() {
    return get().intVal();
  }

  protected void scanValue(Token src, boolean glob) {
    set(scanNum(), glob);
    // System.err.println("= Assignment \\" + getName() + " = " + get());
  }

  protected void perform(int operation, boolean glob) {
    set(performFor(get(), operation), glob);
  }

  public boolean hasNumValue() {
    return true;
  }

  public Num getNumValue() {
    return get();
  }
}
