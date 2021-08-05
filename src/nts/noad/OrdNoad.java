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
// Filename: nts/noad/OrdNoad.java
// $Id: OrdNoad.java,v 1.1.1.1 2000/10/05 08:11:37 ksk Exp $
package nts.noad;

public class OrdNoad extends WordPartNoad {

  public OrdNoad(Field nucleus) {
    super(nucleus);
  }

  protected String getDesc() {
    return "mathord";
  }

  public boolean isOrdinary() {
    return true;
  }

  public Field ordinaryField() {
    return nucleus;
  }

  protected byte spacingType() {
    return SPACING_TYPE_ORD;
  }

  public boolean startsWord() {
    return nucleus.isJustChar();
  }

  public boolean finishesWord() {
    return false;
  }
}
