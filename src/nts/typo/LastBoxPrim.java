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
// Filename: nts/typo/LastBoxPrim.java
// $Id: LastBoxPrim.java,v 1.1.1.1 2000/02/15 09:54:43 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.node.Box;
import nts.node.Node;
import nts.node.VoidBoxNode;

public class LastBoxPrim extends FetchBoxPrim {

  public LastBoxPrim(String name) {
    super(name);
  }

  /* TeXtp[1080] */
  public Box getBoxValue() {
    Builder bld = getBld();
    if (bld.canTakeLastBox()) {
      Node node = bld.lastNode();
      if (node != Node.NULL && node.isBox()) {
        bld.removeLastNode();
        return node.getBox();
      }
    } else if (bld.canTakeLastNode()) error("LastBoxIn", this, bld);
    else error("CantTakeFromPage", this, bld);
    return VoidBoxNode.BOX;
  }
}
