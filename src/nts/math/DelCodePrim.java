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
// Filename: nts/math/DelCodePrim.java
// $Id: DelCodePrim.java,v 1.1.1.1 1999/12/12 23:59:52 ksk Exp $
package nts.math;

import nts.base.Num;
import nts.command.DefCodePrim;
import nts.command.Token;

/** */
public class DelCodePrim extends DefCodePrim {

  public DelCodePrim(String name, int defVal, int maxVal) {
    super(name, defVal, maxVal);
  }

  /**
   * Performs the assignment.
   *
   * @param src source token for diagnostic output.
   * @param glob indication that the assignment is global.
   */
  /* TeXtp[1232] */
  protected final void assign(Token src, boolean glob) {
    int idx = scanCharacterCode();
    skipOptEquals();
    int val = scanInt();
    if (val > maxVal) {
      error("CodeGreater", num(val), num(maxVal));
      val = 0;
    }
    set(idx, Num.valueOf(val), glob);
  }
}
