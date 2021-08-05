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
// Filename: nts/noad/MKernNoad.java
// $Id: MKernNoad.java,v 1.1.1.1 2000/05/26 21:14:42 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.HKernNode;
import nts.node.Node;

public class MKernNoad extends BaseNodeNoad {

  private /* final */ Dimen mkern;

  public MKernNoad(Dimen mkern) {
    this.mkern = mkern;
  }

  public Egg convert(Converter conv) {
    return new SimpleNodeEgg(new HKernNode(conv.muToPt(mkern)));
  }

  public void addOn(Log log, CntxLog cntx) {
    log.addEsc("mkern").add(mkern.toString("mu"));
  }

  public Node getNode() {
    return new InternalKernNode();
  }

  protected class InternalKernNode extends HKernNode {
    /* corresponding to kern_node */
    public InternalKernNode() {
      super(MKernNoad.this.mkern);
    }

    public boolean isMuKern() {
      return true;
    }

    public Dimen getMuKern() {
      return mkern;
    }

    public void addOn(Log log, CntxLog cntx) {
      MKernNoad.this.addOn(log, cntx);
    }
  }
}
