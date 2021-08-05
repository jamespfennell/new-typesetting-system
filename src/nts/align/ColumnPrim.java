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
// Filename: nts/align/ColumnPrim.java
// $Id: ColumnPrim.java,v 1.1.1.1 2001/03/20 02:42:10 ksk Exp $
package nts.align;

import nts.command.LeftBraceToken;
import nts.command.Prim;
import nts.command.RightBraceToken;
import nts.command.Token;

public class ColumnPrim extends Prim {

  private final Alignment.ColumnEnding ending;

  ColumnPrim(String name, Alignment.ColumnEnding ending) {
    super(name);
    this.ending = ending;
  }

  /* TeXtp[342] */
  public final boolean explosive() {
    return Alignment.columnBodyIsActiveAndBalanced();
  }

  /* TeXtp[342] */
  public void detonate(Token src) {
    Alignment.finishActiveColumnBody(ending);
  }

  /* TeXtp[1127,1128] */
  public void exec(Token src) {
    int disbalance = Alignment.activeColumnDisbalance();
    if (Math.abs(disbalance) > 2)
      error(
          (src.match(TabMarkToken.TOKEN)) ? "MisplacedTabMark" : "MisplacedCrSpan", meaningOf(src));
    else {
      backToken(src);
      if (disbalance < 0) {
        insertToken(LeftBraceToken.TOKEN);
        error("AllignLeftError");
      } else {
        insertToken(RightBraceToken.TOKEN);
        error("AllignRightError");
      }
    }
  }
}
