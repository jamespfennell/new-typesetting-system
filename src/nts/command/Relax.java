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
// Filename: nts/command/Relax.java
// $Id: Relax.java,v 1.1.1.1 2000/03/17 14:16:45 ksk Exp $
package nts.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import nts.io.Log;

public class Relax extends Command {

  public static final String NAME = "relax";

  public final boolean isRelax() {
    return true;
  }

  public final void exec(Token src) {}

  public final void exec(Token src, int prefixes) {
    Token tok = nextExpToken();
    meaningOf(tok).execute(tok, prefixes);
  }

  public final void addOn(Log log) {
    log.addEsc(NAME);
  }

  private static Relax relax;

  public static Relax getRelax() {
    return relax;
  }

  public static void makeStaticData() {
    relax = new Relax();
  }

  public static void writeStaticData(ObjectOutputStream output) throws IOException {
    output.writeObject(relax);
  }

  public static void readStaticData(ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    relax = (Relax) input.readObject();
  }

  public String toString() {
    return (sameAs(Expandable.getUnExpanded())) ? "@RELAX" : "@relax";
  }
}
