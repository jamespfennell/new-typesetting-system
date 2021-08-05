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
// Filename: nts/typo/LastKernPrim.java
// $Id: LastKernPrim.java,v 1.1.1.1 2000/04/30 08:49:16 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.node.Node;

public class LastKernPrim extends BuilderPrim implements Dimen.Provider {

  public LastKernPrim(String name) {
    super(name);
  }

  public boolean hasDimenValue() {
    return true;
  }

  /* STRANGE
   * \mskip\lastbox is silently converted to pt.
   * A (conceptual) bug?
   */

  /* TeXtp[424] */
  public Dimen getDimenValue() {
    Node node = getBld().lastSpecialNode();
    return (node != Node.NULL && node.hasKern()) ? node.getKern() : Dimen.ZERO;
  }
}
