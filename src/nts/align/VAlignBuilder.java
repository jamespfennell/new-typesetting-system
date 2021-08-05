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
// Filename: nts/align/VAlignBuilder.java
// $Id: VAlignBuilder.java,v 1.1.1.1 2000/08/08 04:45:02 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.builder.VBoxBuilder;

public class VAlignBuilder extends VBoxBuilder {
  public VAlignBuilder(int line) {
    super(line);
  }

  public Dimen getPrevDepth() {
    return Dimen.ZERO;
  }

  public void setPrevDepth(Dimen pd) {}

  protected void uncheckedSetPrevDepth(Dimen pd) {}
}
