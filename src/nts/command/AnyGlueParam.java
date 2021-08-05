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
// Filename: nts/command/AnyGlueParam.java
// $Id: AnyGlueParam.java,v 1.1.1.1 2001/02/25 22:02:37 ksk Exp $
package nts.command;

import nts.base.Glue;
import nts.io.Log;

/** */
public abstract class AnyGlueParam extends PerformableParam {

  private Glue value;

  /**
   * Creates a new AnyGlueParam with given name and value and stores it in language interpreter
   * |EqTable|.
   *
   * @param name the name of the AnyGlueParam
   * @param val the value of the AnyGlueParam
   */
  public AnyGlueParam(String name, Glue val) {
    super(name);
    value = val;
  }

  /**
   * Creates a new AnyGlueParam with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the AnyGlueParam
   */
  public AnyGlueParam(String name) {
    this(name, Glue.ZERO);
  }

  public final Object getEqValue() {
    return value;
  }

  public final void setEqValue(Object val) {
    value = (Glue) val;
  }

  public final void addEqValueOn(Log log) {
    log.add(value.toString(getUnit()));
  }

  public abstract String getUnit();

  public final Glue get() {
    return value;
  }

  public void set(Glue val, boolean glob) {
    beforeSetting(glob);
    value = val;
  }

  /* STRANGE
   * only for making glue in a paragraph finite
   */
  public void makeShrinkFinite() {
    value = value.withFiniteShrink();
  }
}
