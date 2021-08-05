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
// Filename: nts/command/ExpandablePrim.java
// $Id: ExpandablePrim.java,v 1.1.1.1 2000/03/17 14:41:27 ksk Exp $
package nts.command;

import nts.io.Log;

public abstract class ExpandablePrim extends Expandable implements Primitive {

  /** The name of the primitive */
  private String name;

  /**
   * Creates a new Prim with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the Prim
   */
  protected ExpandablePrim(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final Command getCommand() {
    return this;
  }

  public final String toString() {
    return "@" + name;
  }

  /* TeXtp[367] */
  public final void doExpansion(Token src) {
    if (getConfig().getBoolParam(BOOLP_TRACING_ALL_COMMANDS)) traceExpandable(this);
    expand(src);
  }

  public abstract void expand(Token src);

  public void addExpandable(Log log, boolean full) {
    log.addEsc(name);
  }

  /* TeXtp[379] */
  protected static void insertRelax() {
    insertToken(new FrozenToken("relax", Relax.getRelax()));
  }
}
