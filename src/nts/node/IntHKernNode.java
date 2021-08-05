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
// Filename: nts/node/IntHKernNode.java
// $Id: IntHKernNode.java,v 1.1.1.1 2000/05/27 02:22:54 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.io.Log;

public abstract class IntHKernNode extends BaseNode {
  /* root corresponding to kern_node */

  protected final Dimen kern;

  public IntHKernNode(Dimen kern) {
    this.kern = kern;
  }

  public Dimen getWidth() {
    return kern;
  }

  public boolean canBePartOfDiscretionary() {
    return true;
  }

  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    return metric;
  }

  public boolean isKernThatCanBeSpared() {
    return true;
  }

  public byte afterWord() {
    return SUCCESS;
  }
}
