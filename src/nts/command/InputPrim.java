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
// Filename: nts/command/InputPrim.java
// $Id: InputPrim.java,v 1.1.1.1 1999/06/09 14:14:03 ksk Exp $
package nts.command;

/* TeXtp[378] */
public class InputPrim extends ExpandablePrim {

  private boolean enabled = true;

  public InputPrim(String name) {
    super(name);
  }

  public boolean enable(boolean val) {
    boolean oldVal = enabled;
    enabled = val;
    return oldVal;
  }

  public void expand(Token src) {
    if (enabled) startInput();
    else {
      backToken(src);
      insertRelax();
    }
  }
}
