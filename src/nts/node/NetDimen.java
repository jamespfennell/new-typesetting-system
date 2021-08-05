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
// Filename: nts/node/NetDimen.java
// $Id: NetDimen.java,v 1.1.1.1 2000/01/28 17:17:18 ksk Exp $
package nts.node;

import java.io.Serializable;
import nts.base.Dimen;
import nts.base.Glue;

public class NetDimen implements Serializable {

  public static final NetDimen NULL = null;

  private Dimen natural;
  private Dimen shrink;
  private Dimen[] stretch = new Dimen[Glue.MAX_ORDER + 1];

  public Dimen getNatural() {
    return natural;
  }

  public Dimen getShrink() {
    return shrink;
  }

  public Dimen getStretch(byte ord) {
    return stretch[ord];
  }

  public void setNatural(Dimen dim) {
    natural = dim;
  }

  public void setShrink(Dimen dim) {
    shrink = dim;
  }

  public void setStretch(Dimen dim, byte ord) {
    stretch[ord] = dim;
  }

  public byte getMaxStrOrder() {
    byte ord = Glue.MAX_ORDER;
    while (ord > Glue.NORMAL && stretch[ord].isZero()) ord--;
    return ord;
  }

  public NetDimen() {
    this(Dimen.ZERO);
  }

  public NetDimen(Dimen d) {
    natural = d;
    shrink = Dimen.ZERO;
    for (int i = 0; i < stretch.length; stretch[i++] = Dimen.ZERO)
      ;
  }

  public NetDimen(Glue g) {
    natural = g.getDimen();
    shrink = g.getShrink();
    for (int i = 0; i < stretch.length; stretch[i++] = Dimen.ZERO)
      ;
    stretch[g.getStrOrder()] = g.getStretch();
  }

  public NetDimen(NetDimen nd) {
    natural = nd.natural;
    shrink = nd.shrink;
    for (int i = 0; i < stretch.length; i++) stretch[i] = nd.stretch[i];
  }

  public final void add(Dimen d) {
    natural = natural.plus(d);
  }

  public final void addShrink(Dimen d) {
    shrink = shrink.plus(d);
  }

  public final void addStretch(byte ord, Dimen d) {
    stretch[ord] = stretch[ord].plus(d);
  }

  public void add(Glue g) {
    add(g.getDimen());
    addShrink(g.getShrink());
    addStretch(g.getStrOrder(), g.getStretch());
  }

  public void add(NetDimen nd) {
    add(nd.natural);
    addShrink(nd.shrink);
    for (byte o = 0; o < stretch.length; o++) addStretch(o, nd.stretch[o]);
  }

  public final void sub(Dimen d) {
    natural = natural.minus(d);
  }

  public final void subShrink(Dimen d) {
    shrink = shrink.minus(d);
  }

  public final void subStretch(byte ord, Dimen d) {
    stretch[ord] = stretch[ord].minus(d);
  }

  public void sub(Glue g) {
    sub(g.getDimen());
    subShrink(g.getShrink());
    subStretch(g.getStrOrder(), g.getStretch());
  }

  public void sub(NetDimen nd) {
    sub(nd.natural);
    subShrink(nd.shrink);
    for (byte o = 0; o < stretch.length; o++) subStretch(o, nd.stretch[o]);
  }

  public String toString() {
    return toString(null);
  }

  /* TeXtp[985] */
  public String toString(String unit) {
    StringBuffer buf = new StringBuffer(128);
    buf.append(natural.toString());
    if (unit != null) buf.append(unit);
    for (byte o = 0; o < stretch.length; o++)
      if (!stretch[o].isZero()) Glue.append(buf.append(" plus "), stretch[o], o, unit);
    if (!shrink.isZero()) Glue.append(buf.append(" minus "), shrink, Glue.NORMAL, unit);
    return buf.toString();
  }
}
