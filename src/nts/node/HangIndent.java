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
// Filename: nts/node/HangIndent.java
// $Id: HangIndent.java,v 1.1.1.1 1999/11/16 17:55:52 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class HangIndent implements LinesShape {

  private final int boundary;
  private final Dimen firstWidth;
  private final Dimen firstIndent;
  private final Dimen secondWidth;
  private final Dimen secondIndent;

  public HangIndent(int b, Dimen fw, Dimen fi, Dimen sw, Dimen si) {
    boundary = b;
    firstWidth = fw;
    firstIndent = fi;
    secondWidth = sw;
    secondIndent = si;
  }

  public HangIndent(Dimen sw, Dimen si) {
    this(0, Dimen.ZERO, Dimen.ZERO, sw, si);
  }

  public boolean isFinal(int idx) {
    return (idx >= boundary);
  }

  public Dimen getWidth(int idx) {
    return (idx < boundary) ? firstWidth : secondWidth;
  }

  public Dimen getIndent(int idx) {
    return (idx < boundary) ? firstIndent : secondIndent;
  }

  /* TeXtp[849] */
  public static LinesShape makeShape(int ha, Dimen hi, Dimen hs) {
    if (hi.isZero()) return new HangIndent(hs, Dimen.ZERO);
    Dimen size, ind;
    if (hi.lessThan(0)) {
      ind = Dimen.ZERO;
      size = hs.plus(hi);
    } else {
      ind = hi;
      size = hs.minus(hi);
    }
    return (ha < 0)
        ? new HangIndent(-ha, size, ind, hs, Dimen.ZERO)
        : new HangIndent(ha, hs, Dimen.ZERO, size, ind);
  }
}
