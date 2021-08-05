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
// Filename: nts/node/NamedVSkipNode.java
// $Id: NamedVSkipNode.java,v 1.1.1.1 2000/06/06 08:32:10 ksk Exp $
package nts.node;

import nts.base.Glue;

public class NamedVSkipNode extends VSkipNode {
  /* corresponding to glue_node */

  private final String name;

  public NamedVSkipNode(Glue skip, String name) {
    super(skip);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return "NamedVSkip(" + skip + "; " + name + ')';
  }
}
