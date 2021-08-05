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
// Filename: nts/typo/FontNumPrim.java
// $Id: FontNumPrim.java,v 1.1.1.1 1999/06/24 10:31:59 ksk Exp $
package nts.typo;

import nts.base.Num;
import nts.command.Token;
import nts.node.FontMetric;

public class FontNumPrim extends TypoAssignPrim implements Num.Provider {

  private int index;

  public FontNumPrim(String name, int index) {
    super(name);
    this.index = index;
  }

  /* STRANGE
   * \global\fontdimen is allowed but has no effect
   */
  /* TeXtp[1253] */
  protected void assign(Token src, boolean glob) {
    FontMetric metric = scanFontMetric();
    skipOptEquals();
    metric.setNumParam(index, scanNum());
  }

  public boolean hasNumValue() {
    return true;
  }

  /* TeXtp[426] */
  public Num getNumValue() {
    FontMetric metric = scanFontMetric();
    return metric.getNumParam(index);
  }
}
