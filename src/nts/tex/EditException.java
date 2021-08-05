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
// Filename: nts/tex/EditException.java
// $Id: EditException.java,v 1.1.1.1 1999/06/01 09:16:04 ksk Exp $
package nts.tex;

import nts.command.FilePos;

public class EditException extends RuntimeException {

  private FilePos pos;

  public EditException(FilePos pos) {
    this.pos = pos;
  }

  public Process exec() {
    // String		temp = System.getProperty("nts.editor");
    // if (temp == null) temp = TeXConfig.EDIT_TEMPLATE;
    System.err.println();
    System.err.println("You want to edit file " + pos.name + " at line " + pos.line);
    return null;
  }

  public String toString() {
    return "EditException of `" + pos.name + "' at line " + pos.line;
  }
}
