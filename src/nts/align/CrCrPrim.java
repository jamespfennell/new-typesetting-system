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
// Filename: nts/align/CrCrPrim.java
// $Id: CrCrPrim.java,v 1.1.1.1 2000/08/05 02:08:28 ksk Exp $
package nts.align;

public class CrCrPrim extends CrPrim {

  public CrCrPrim(String name) {
    super(name);
  }

  public final boolean isCrCr() {
    return true;
  }
}
