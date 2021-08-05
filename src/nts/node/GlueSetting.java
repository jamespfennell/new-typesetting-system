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
// Filename: nts/node/GlueSetting.java
// $Id: GlueSetting.java,v 1.1.1.1 2001/05/15 03:58:40 ksk Exp $
package nts.node;

import java.io.Serializable;
import nts.base.Dimen;
import nts.base.Glue;
import nts.io.Log;
import nts.io.Loggable;

public class GlueSetting implements Serializable, Loggable {

  public static final byte RIGID = 0, STRETCHING = 1, SHRINKING = 2;

  protected final byte sign;
  protected final byte order;
  protected final double ratio;

  public static final GlueSetting NATURAL = new GlueSetting(RIGID, Glue.NORMAL, 0.0);

  public GlueSetting(byte sign, byte order, double ratio) {
    this.sign = sign;
    this.order = order;
    this.ratio = ratio;
  }

  /* TeXtp[625, 634] */
  public Dimen set(Glue glue, boolean limited) {
    Dimen dimen = glue.getDimen();
    switch (sign) {
      case STRETCHING:
        if (glue.getStrOrder() == order)
          dimen =
              (limited)
                  ? dimen.plus(glue.getStretch().limitedTimes(ratio))
                  : dimen.plus(glue.getStretch().times(ratio));
        break;
      case SHRINKING:
        if (glue.getShrOrder() == order)
          dimen =
              (limited)
                  ? dimen.minus(glue.getShrink().limitedTimes(ratio))
                  : dimen.minus(glue.getShrink().times(ratio));
        break;
    }
    return dimen;
  }

  /* TeXtp[1148] */
  public boolean makesElastic(Glue glue) {
    switch (sign) {
      case STRETCHING:
        return (glue.getStrOrder() == order && !glue.getStretch().isZero());
      case SHRINKING:
        return (glue.getShrOrder() == order && !glue.getShrink().isZero());
    }
    return false;
  }

  public void addOn(Log log) {
    if (sign != RIGID && ratio != 0.0) {
      log.add(", glue set ");
      if (sign == SHRINKING) log.add("- ");
      if (Math.abs(ratio) > Dimen.MAX_DOUBLE_VALUE)
        log.add((ratio > 0.0) ? ">" : "< -").add(Glue.toString(Dimen.MAX_FROM_DOUBLE, order));
      else log.add(Glue.toString(Dimen.valueOf(ratio), order));
    }
  }
}
