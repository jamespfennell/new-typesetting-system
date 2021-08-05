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
// Filename: nts/noad/SideNoad.java
// $Id: SideNoad.java,v 1.1.1.1 2000/04/08 13:04:18 ksk Exp $
package nts.noad;

import nts.io.CntxLog;
import nts.io.Log;

public abstract class SideNoad extends PureNoad {

  protected final Delimiter delimiter;

  public SideNoad(Delimiter delimiter) {
    this.delimiter = delimiter;
  }

  public final boolean influencesBin() {
    return true;
  }

  public Egg convert(Converter conv) {
    return new DelimiterEgg(delimiter, spacingType());
  }

  /* TeXtp[696] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc()).add(delimiter);
  }

  protected abstract String getDesc();

  protected abstract byte spacingType();
}
