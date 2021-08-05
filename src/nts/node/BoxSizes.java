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
// Filename: nts/node/BoxSizes.java
// $Id: BoxSizes.java,v 1.1.1.1 2000/04/13 09:22:05 ksk Exp $
package nts.node;

import java.io.Serializable;
import nts.base.Dimen;
import nts.io.Log;
import nts.io.Loggable;

public final class BoxSizes implements Serializable, Loggable {

  public static final BoxSizes NULL = null;

  public static final BoxSizes ZERO = new BoxSizes(Dimen.ZERO, Dimen.ZERO, Dimen.ZERO, Dimen.ZERO);

  private final Dimen height;
  private final Dimen width;
  private final Dimen depth;
  private final Dimen leftX;

  public BoxSizes(Dimen h, Dimen w, Dimen d, Dimen l) {
    height = h;
    width = w;
    depth = d;
    leftX = l;
  }

  public Dimen rawHeight() {
    return height;
  }

  public Dimen rawWidth() {
    return width;
  }

  public Dimen rawDepth() {
    return depth;
  }

  public Dimen rawLeftX() {
    return leftX;
  }

  public Dimen getHeight() {
    return getSize(height);
  }

  public Dimen getWidth() {
    return getSize(width);
  }

  public Dimen getDepth() {
    return getSize(depth);
  }

  public Dimen getLeftX() {
    return getSize(leftX);
  }

  private static Dimen getSize(Dimen x) {
    return (x != Dimen.NULL) ? x : Dimen.ZERO;
  }

  public BoxSizes replenished(BoxSizes around) {
    Dimen h = height;
    Dimen w = width;
    Dimen d = depth;
    Dimen l = leftX;
    boolean change = false;
    if (h == Dimen.NULL) {
      h = around.getHeight();
      change = true;
    }
    if (w == Dimen.NULL) {
      w = around.getWidth();
      change = true;
    }
    if (d == Dimen.NULL) {
      d = around.getDepth();
      change = true;
    }
    if (l == Dimen.NULL) {
      l = around.getLeftX();
      change = true;
    }
    return (change) ? new BoxSizes(h, w, d, l) : this;
  }

  public BoxSizes shiftedUp(Dimen shift) {
    return new BoxSizes(
        (height != Dimen.NULL) ? height.plus(shift) : height, width,
        (depth != Dimen.NULL) ? depth.minus(shift) : depth, leftX);
  }

  public BoxSizes shiftedLeft(Dimen shift) {
    return new BoxSizes(
        height, (width != Dimen.NULL) ? width.minus(shift) : width,
        depth, (leftX != Dimen.NULL) ? leftX.plus(shift) : leftX);
  }

  public BoxSizes withHeight(Dimen height) {
    return new BoxSizes(height, width, depth, leftX);
  }

  public BoxSizes withWidth(Dimen width) {
    return new BoxSizes(height, width, depth, leftX);
  }

  public BoxSizes withDepth(Dimen depth) {
    return new BoxSizes(height, width, depth, leftX);
  }

  public BoxSizes withLeftX(Dimen leftX) {
    return new BoxSizes(height, width, depth, leftX);
  }

  public void addOn(Log log) {
    log.add('(');
    addSize(log, height);
    log.add('+');
    addSize(log, depth);
    log.add(")x");
    addSize(log, width);
  }

  private static void addSize(Log log, Dimen x) {
    if (x == Dimen.NULL) log.add('*');
    else log.add(x.toString());
  }
}
