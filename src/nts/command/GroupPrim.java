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
// Filename: nts/command/GroupPrim.java
// $Id: GroupPrim.java,v 1.1.1.1 1999/08/23 16:01:05 ksk Exp $
package nts.command;

import nts.io.Log;

public abstract class GroupPrim extends GroupCommand implements Primitive {

  /** The name of the primitive */
  private String name;

  protected GroupPrim(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final Command getCommand() {
    return this;
  }

  public final void addOn(Log log) {
    log.addEsc(name);
  }

  public final String toString() {
    return "@" + name;
  }
}
