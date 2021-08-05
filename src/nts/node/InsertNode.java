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
// Filename: nts/node/InsertNode.java
// $Id: InsertNode.java,v 1.1.1.1 2000/06/06 08:25:40 ksk Exp $
package nts.node;

import nts.io.CntxLog;
import nts.io.Log;

public class InsertNode extends MigratingNode {
  /* root corresponding to ins_node */

  protected final Insertion ins;

  public InsertNode(Insertion ins) {
    this.ins = ins;
  }

  public boolean isInsertion() {
    return true;
  }

  public Insertion getInsertion() {
    return ins;
  }

  public void addOn(Log log, CntxLog cntx) {
    ins.addOn(log, cntx);
  }

  public String toString() {
    return "Insert(" + ins + ')';
  }
}
