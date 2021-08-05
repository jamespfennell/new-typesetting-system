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
// Filename: nts/noad/NucleusNoad.java
// $Id: NucleusNoad.java,v 1.1.1.1 2000/10/04 21:24:07 ksk Exp $
package nts.noad;

import nts.io.CntxLog;
import nts.io.Log;
import nts.node.Node;

public abstract class NucleusNoad extends ScriptableNoad {

  protected final Field nucleus;

  public NucleusNoad(Field nucleus) {
    this.nucleus = nucleus;
  }

  protected abstract String getDesc();

  protected abstract byte spacingType();

  public boolean isJustChar() {
    return nucleus.isJustChar();
  }

  /* TeXtp[696] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc());
    nucleus.addOn(log, cntx, '.');
  }

  public Egg convert(Converter conv) {
    return makeEgg(nucleus.convertedBy(conv));
  }

  protected Egg makeEgg(Node node) {
    return new StItalNodeEgg(node, spacingType());
  }
}
