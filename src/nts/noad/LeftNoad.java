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
// Filename: nts/noad/LeftNoad.java
// $Id: LeftNoad.java,v 1.1.1.1 2000/04/08 12:55:45 ksk Exp $
package nts.noad;

public class LeftNoad extends SideNoad {

  public LeftNoad(Delimiter delimiter) {
    super(delimiter);
  }

  protected String getDesc() {
    return "left";
  }

  public boolean canPrecedeBin() {
    return false;
  }

  protected byte spacingType() {
    return SPACING_TYPE_OPEN;
  }
}
