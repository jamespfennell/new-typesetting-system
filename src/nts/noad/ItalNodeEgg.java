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
// Filename: nts/noad/ItalNodeEgg.java
// $Id: ItalNodeEgg.java,v 1.1.1.1 2000/04/15 11:16:15 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.node.ChrKernNode;
import nts.node.Node;

public abstract class ItalNodeEgg extends BaseNodeEgg {

  public ItalNodeEgg(Node node) {
    super(node);
  }

  private boolean suppressed = false;

  public void suppressItalCorr() {
    suppressed = true;
  }

  public Dimen getItalCorr() {
    return (suppressed || node == Node.NULL) ? Dimen.NULL : node.getItalCorr();
  }

  public void chipShell(Nodery nodery) {
    if (node != Node.NULL) {
      nodery.append(node);
      if (!suppressed) {
        Dimen ital = node.getItalCorr();
        if (ital != Dimen.NULL && !ital.isZero()) nodery.append(new ChrKernNode(ital));
      }
    }
  }
}
