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
// Filename: nts/command/GlueParam.java
// $Id: GlueParam.java,v 1.1.1.1 2000/06/27 10:37:35 ksk Exp $
package nts.command;

import nts.base.Glue;

/** Setting glue parameter primitive. */
public class GlueParam extends AnyGlueParam implements Glue.Provider {

  /**
   * Creates a new GlueParam with given name and value and stores it in language interpreter
   * |EqTable|.
   *
   * @param name the name of the GlueParam
   * @param val the value of the GlueParam
   */
  public GlueParam(String name, Glue val) {
    super(name, val);
  }

  /**
   * Creates a new GlueParam with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the GlueParam
   */
  public GlueParam(String name) {
    super(name);
  }

  protected void scanValue(Token src, boolean glob) {
    set(scanGlue(), glob);
    // System.err.println("= Assignment \\" + getName() + " = " + get());
  }

  protected void perform(int operation, boolean glob) {
    set(performFor(get(), operation, false), glob);
  }

  public String getUnit() {
    return "pt";
  }

  public boolean hasGlueValue() {
    return true;
  }

  public Glue getGlueValue() {
    return get();
  }
}
