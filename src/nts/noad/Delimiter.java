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
// Filename: nts/noad/Delimiter.java
// $Id: Delimiter.java,v 1.1.1.1 2000/04/08 11:54:34 ksk Exp $
package nts.noad;

import nts.io.CharCode;
import nts.io.Log;
import nts.io.Loggable;

public class Delimiter implements Loggable {

  public static final Delimiter NULL = null;
  public static final Delimiter VOID =
      new Delimiter((byte) 0, CharCode.NULL, (byte) 0, CharCode.NULL);

  private final byte smallFam;
  private final CharCode smallCode;
  private final byte largeFam;
  private final CharCode largeCode;

  public Delimiter(byte smallFam, CharCode smallCode, byte largeFam, CharCode largeCode) {
    this.smallFam = smallFam;
    this.smallCode = smallCode;
    this.largeFam = largeFam;
    this.largeCode = largeCode;
  }

  public byte getSmallFam() {
    return smallFam;
  }

  public CharCode getSmallCode() {
    return smallCode;
  }

  public byte getLargeFam() {
    return largeFam;
  }

  public CharCode getLargeCode() {
    return largeCode;
  }

  public boolean isVoid() {
    return (smallFam == 0
        && smallCode == CharCode.NULL
        && largeFam == 0
        && largeCode == CharCode.NULL);
  }

  /* STRANGE
   * why not print mnemonic values?
   */
  /* TeXtp[691] */
  public void addOn(Log log) {
    int n = (famCharNum(smallFam, smallCode) << 12) + famCharNum(largeFam, largeCode);
    log.add('"').add(Integer.toHexString(n).toUpperCase());
  }

  private int famCharNum(byte fam, CharCode code) {
    int n = fam << 8;
    if (code != CharCode.NULL) n += code.numValue();
    return n;
  }
}
