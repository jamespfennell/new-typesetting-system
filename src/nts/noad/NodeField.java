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
// Filename: nts/noad/NodeField.java
// $Id: NodeField.java,v 1.1.1.1 2000/04/11 20:52:52 ksk Exp $
package nts.noad;

import nts.io.CntxLog;
import nts.io.Log;
import nts.node.HBoxNode;
import nts.node.Node;

public class NodeField extends Field {

  private final Node node;

  public NodeField(Node node) {
    this.node = node;
  }

  public Node getNode() {
    return node;
  }

  /* TeXtp[693] */
  public void addOn(Log log, CntxLog cntx, char p) {
    cntx.addOn(log, node, p);
  }

  public Node convertedBy(Converter conv) {
    return node;
  }

  /* TeXtp[720] */
  public Node cleanBox(Converter conv, byte how) {
    return (node.isCleanBox()) ? node.trailingKernSpared() : HBoxNode.packedOf(node);
  }
}
