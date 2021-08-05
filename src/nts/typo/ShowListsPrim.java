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
// Filename: nts/typo/ShowListsPrim.java
// $Id: ShowListsPrim.java,v 1.1.1.1 2000/02/08 06:42:39 ksk Exp $
package nts.typo;

import nts.builder.Builder;

public class ShowListsPrim extends TypoShowingPrim {

  public ShowListsPrim(String name) {
    super(name);
  }

  /* TeXtp[218] */
  protected void performShow() {
    int depth = getConfig().getIntParam(INTP_SHOW_BOX_DEPTH);
    int breadth = getConfig().getIntParam(INTP_SHOW_BOX_BREADTH);
    diagLog.startLine().endLine();
    Builder.showStack(diagLog, depth, breadth);
  }
}
