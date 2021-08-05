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
// Filename: nts/command/RegisterPrim.java
// $Id: RegisterPrim.java,v 1.1.1.1 2000/01/28 04:58:53 ksk Exp $
package nts.command;

import nts.io.Log;

/** */
public abstract class RegisterPrim extends AssignPrim {

  /** Kind of equivalence in |EqTable| */
  protected final NumKind tabKind = new NumKind();

  /**
   * Creates a new RegisterPrim with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the RegisterPrim
   */
  protected RegisterPrim(String name) {
    super(name);
  }

  public abstract void addEqValueOn(int idx, Log log);
}
