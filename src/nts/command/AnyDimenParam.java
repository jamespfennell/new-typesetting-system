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
// Filename: nts/command/AnyDimenParam.java
// $Id: AnyDimenParam.java,v 1.1.1.1 1999/06/08 22:03:01 ksk Exp $
package nts.command;

import nts.base.Dimen;
import nts.io.Log;

/** */
public abstract class AnyDimenParam extends PerformableParam {

  private Dimen value;

  /**
   * Creates a new AnyDimenParam with given name and value and stores it in language interpreter
   * |EqTable|.
   *
   * @param name the name of the AnyDimenParam
   * @param val the value of the AnyDimenParam
   */
  public AnyDimenParam(String name, Dimen val) {
    super(name);
    value = val;
  }

  /**
   * Creates a new AnyDimenParam with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the AnyDimenParam
   */
  public AnyDimenParam(String name) {
    this(name, Dimen.ZERO);
  }

  public final Object getEqValue() {
    return value;
  }

  public final void setEqValue(Object val) {
    value = (Dimen) val;
  }

  public final void addEqValueOn(Log log) {
    log.add(value.toString(getUnit()));
  }

  public abstract String getUnit();

  public final Dimen get() {
    return value;
  }

  public void set(Dimen val, boolean glob) {
    beforeSetting(glob);
    value = val;
  }
}
