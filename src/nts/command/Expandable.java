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
// Filename: nts/command/Expandable.java
// $Id: Expandable.java,v 1.1.1.1 2000/03/17 13:56:25 ksk Exp $
package nts.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import nts.io.Log;
import nts.io.Loggable;

public abstract class Expandable extends Relax {

  public static final int BOOLP_TRACING_ALL_COMMANDS = newBoolParam();

  public final boolean expandable() {
    return true;
  }

  public abstract void doExpansion(Token src);

  public abstract void addExpandable(Log log, boolean full);

  private static Relax unExpanded;

  public static Relax getUnExpanded() {
    return unExpanded;
  }

  public final Command meaning(boolean expOK) {
    return (expOK) ? this : unExpanded;
  }

  public static void makeStaticData() {
    unExpanded = new Relax();
  }

  public static void writeStaticData(ObjectOutputStream output) throws IOException {
    output.writeObject(unExpanded);
  }

  public static void readStaticData(ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    unExpanded = (Relax) input.readObject();
  }

  protected static Loggable exp(final Expandable e) {
    return new Loggable() {
      public void addOn(Log log) {
        e.addExpandable(log, false);
      }
    };
  }
}
