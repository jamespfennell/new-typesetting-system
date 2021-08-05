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
// Filename: nts/command/IfDimPrim.java
// $Id: IfDimPrim.java,v 1.1.1.1 1999/07/12 11:02:04 ksk Exp $
package nts.command;

import nts.base.Dimen;

public class IfDimPrim extends AnyIfPrim {

  public IfDimPrim(String name) {
    super(name);
  }

  protected final boolean holds() {
    Dimen left = scanDimen();
    char op = relOp();
    Dimen right = scanDimen();
    switch (op) {
      case '<':
        return (left.lessThan(right));
      case '>':
        return (left.moreThan(right));
      default:
        return (left.equals(right));
    }
  }
}
