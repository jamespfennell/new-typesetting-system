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
// Filename: nts/noad/DelFractionNoad.java
// $Id: DelFractionNoad.java,v 1.1.1.1 2001/03/22 21:50:26 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.Log;

public class DelFractionNoad extends FractionNoad {

  protected final Delimiter left;
  protected final Delimiter right;

  public DelFractionNoad(
      Field numerator, Field denominator, Dimen thickness, Delimiter left, Delimiter right) {
    super(numerator, denominator, thickness);
    this.left = left;
    this.right = right;
  }

  public Delimiter getLeftDelimiter() {
    return left;
  }

  public Delimiter getRightDelimiter() {
    return right;
  }

  public FractionNoad numerated(Field numerator) {
    return new DelFractionNoad(numerator, denominator, thickness, left, right);
  }

  public FractionNoad denominated(Field denominator) {
    return new DelFractionNoad(numerator, denominator, thickness, left, right);
  }

  /* TeXtp[697] */
  protected void addDelimitersOn(Log log) {
    if (!left.isVoid()) log.add(", left-delimiter ").add(left);
    if (!right.isVoid()) log.add(", right-delimiter ").add(right);
  }
}
