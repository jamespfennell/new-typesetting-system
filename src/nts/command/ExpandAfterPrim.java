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
// Filename: nts/command/ExpandAfterPrim.java
// $Id: ExpandAfterPrim.java,v 1.1.1.1 1999/05/31 11:18:46 ksk Exp $
package nts.command;

/* TeXtp[368] */
public class ExpandAfterPrim extends ExpandablePrim {

  public ExpandAfterPrim(String name) {
    super(name);
  }

  public void expand(Token src) {
    Token first = nextRawToken();
    Token second = onlyRawToken();
    if (second != Token.NULL) backToken(second);
    backToken(first);
  }
}
