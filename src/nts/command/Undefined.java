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
// Filename: nts/command/Undefined.java
// $Id: Undefined.java,v 1.1.1.1 2000/03/17 14:00:38 ksk Exp $
package nts.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import nts.io.Log;

public class Undefined extends Expandable {

  public static final String NAME = "undefined";

  /* TeXtp[367] */
  public final void doExpansion(Token src) {
    if (getConfig().getBoolParam(BOOLP_TRACING_ALL_COMMANDS)) traceExpandable(this);
    error("UndefinedToken");
  }

  public final String toString() {
    return "@" + NAME;
  }

  public final void addExpandable(Log log, boolean full) {
    log.add(NAME);
  }

  private static Undefined undefined;

  public static Undefined getUndefined() {
    return undefined;
  }

  public static void makeStaticData() {
    undefined = new Undefined();
  }

  public static void writeStaticData(ObjectOutputStream output) throws IOException {
    output.writeObject(undefined);
  }

  public static void readStaticData(ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    undefined = (Undefined) input.readObject();
  }
}
