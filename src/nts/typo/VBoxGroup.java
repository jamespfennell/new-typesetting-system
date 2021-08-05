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
// Filename: nts/typo/VBoxGroup.java
// $Id: VBoxGroup.java,v 1.1.1.1 2001/04/27 16:02:16 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.node.Box;
import nts.node.NodeList;
import nts.node.TreatBox;

public class VBoxGroup extends VertGroup {

  protected Dimen size;
  protected boolean exactly;
  protected TreatBox proc;

  public VBoxGroup(Dimen size, boolean exactly, TreatBox proc) {
    this.size = size;
    this.exactly = exactly;
    this.proc = proc;
  }

  protected Dimen maxDepth;

  public void stop() {
    super.stop();
    maxDepth = getConfig().getDimParam(TypoCommand.DIMP_BOX_MAX_DEPTH);
  }

  public void close() {
    super.close();
    proc.execute(makeBox(builder.getList()), NodeList.EMPTY_ENUM);
  }

  protected Box makeBox(NodeList list) {
    return TypoCommand.packVBox(list, size, exactly, maxDepth);
  }
}
