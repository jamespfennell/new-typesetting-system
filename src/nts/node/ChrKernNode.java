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
// Filename: nts/node/ChrKernNode.java
// $Id: ChrKernNode.java,v 1.1.1.1 2000/06/06 08:15:32 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;

public class ChrKernNode extends IntHKernNode {
  /* corresponding to kern_node */

  public ChrKernNode(Dimen kern) {
    super(kern);
  }

  public void addOn(Log log, CntxLog cntx) {
    log.addEsc("kern").add(kern.toString());
  }

  public byte beforeWord() {
    return SKIP;
  }

  public boolean canBePartOfWord() {
    return true;
  }

  public byte afterWord() {
    return SKIP;
  }

  public boolean rightBoundary() {
    return true;
  }

  public String toString() {
    return "ChrKern(" + kern + ')';
  }
}
