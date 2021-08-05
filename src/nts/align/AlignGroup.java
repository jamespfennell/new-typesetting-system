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
// Filename: nts/align/AlignGroup.java
// $Id: AlignGroup.java,v 1.1.1.1 2001/03/12 03:53:03 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.SimpleGroup;
import nts.node.NodeEnum;

public class AlignGroup extends SimpleGroup {

  protected final Alignment align;

  public AlignGroup(Alignment align) {
    this.align = align;
  }

  /* TeXtp[812] */
  public void stop() {
    NodeEnum nodes = align.finish(Dimen.ZERO);
    Builder bld = Builder.top();
    bld.addNodes(nodes);
    align.copyPrevParameters(bld);
    bld.buildPage();
  }
}
