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
// Filename: nts/tfm/FixWord.java
// $Id: FixWord.java,v 1.1.1.1 2000/01/31 15:58:53 ksk Exp $
package nts.tfm;

import nts.base.BinFraction;

/*
 * The dimensions are represented in the same way as in tfm files.
 * Higher 12 bits is the whole part and lower 20 bits is the fractional
 * part. Using the same representation we do not loose any precision.
 * The higher levels of the system may use different accuracy but the
 * rounding and scaling the font dimensions is made on the level of
 * interface to the enclosing class. It is also important that we do not
 * loose precision while printing the property lists.
 * See TFtoPL[9].
 *
 * During internal computation in cases where 32 bit |int| arithmetic can
 * overflow the operands are temporarily extended to 64 bit |long|s.
 *
 * That all is however only implementation detail, to the outside the
 * |FixWord| behaves as a fraction and communicate via numerator and
 * optional denominator.
 */

public final class FixWord extends BinFraction {

  private static final int POINT_SHIFT = 20;
  private static final int MAX_REPR_VALUE = Integer.MAX_VALUE;

  private FixWord(int val) {
    super(val);
  }

  protected final int pointShift() {
    return POINT_SHIFT;
  }

  public static final FixWord NULL = null;
  public static final FixWord ZERO = valueOf(0);
  public static final FixWord UNITY = valueOf(1);
  public static final FixWord MAX_VALUE = new FixWord(MAX_REPR_VALUE);
  public static final FixWord BAD_VALUE = new FixWord(-MAX_REPR_VALUE - 1);

  private static int makeRepr(int num) {
    return num << POINT_SHIFT;
  }

  private static int makeRepr(int num, int den) {
    return (int) (((long) num << POINT_SHIFT) / den);
  }

  private static int makeRepr(BinFraction x) {
    return makeRepr(x, POINT_SHIFT);
  }

  public static FixWord valueOf(FixWord d) {
    return new FixWord(d.value);
  }

  public static FixWord valueOf(int num) {
    return new FixWord(makeRepr(num));
  }

  public static FixWord valueOf(int num, int den) {
    return new FixWord(makeRepr(num, den));
  }

  public static FixWord valueOf(BinFraction x) {
    return new FixWord(makeRepr(x));
  }

  public static FixWord shiftedValueOf(int num, int offs) {
    return new FixWord(((offs += POINT_SHIFT) < 0) ? num >> -offs : num << offs);
  }

  public static FixWord valueOf(String s) throws NumberFormatException {
    int pointIndex = s.indexOf('.');
    if (pointIndex < 0) return new FixWord(makeRepr(Integer.parseInt(s)));
    else {
      long val = Integer.parseInt(s.substring(0, pointIndex));
      final int SHIFT = POINT_SHIFT + 1;
      int frac = 0;
      int i = pointIndex + SHIFT + 1;
      if (i > s.length()) i = s.length();
      while (--i > pointIndex) {
        int digit = Character.digit(s.charAt(i), 10);
        if (digit < 0) throw new NumberFormatException(s);
        frac = (frac + (digit << SHIFT)) / 10;
      }
      boolean negative = (val < 0);
      if (negative) val = -val;
      val <<= POINT_SHIFT;
      val |= (frac + 1) >>> 1;
      if (val > MAX_REPR_VALUE) throw new NumberFormatException(s);
      return new FixWord((negative) ? (int) -val : (int) val);
    }
  }

  public int sign() {
    return (value > 0) ? 1 : (value < 0) ? -1 : 0;
  }

  public boolean isZero() {
    return (value == 0);
  }

  public boolean equals(FixWord d) {
    return (value == d.value);
  }

  public boolean equals(int num) {
    return (value == makeRepr(num));
  }

  public boolean equals(int num, int den) {
    return (value == makeRepr(num, den));
  }

  public boolean equals(BinFraction x) {
    return (value == makeRepr(x));
  }

  public boolean lessThan(FixWord d) {
    return (value < d.value);
  }

  public boolean lessThan(int num) {
    return (value < makeRepr(num));
  }

  public boolean lessThan(int num, int den) {
    return (value < makeRepr(num, den));
  }

  public boolean lessThan(BinFraction x) {
    return (value < makeRepr(x));
  }

  public boolean moreThan(FixWord d) {
    return (value > d.value);
  }

  public boolean moreThan(int num) {
    return (value > makeRepr(num));
  }

  public boolean moreThan(int num, int den) {
    return (value > makeRepr(num, den));
  }

  public boolean moreThan(BinFraction x) {
    return (value > makeRepr(x));
  }

  public FixWord negative() {
    return new FixWord(-value);
  }

  public FixWord plus(FixWord d) {
    return new FixWord(value + d.value);
  }

  public FixWord plus(int num) {
    return new FixWord(value + makeRepr(num));
  }

  public FixWord plus(int num, int den) {
    return new FixWord(value + makeRepr(num, den));
  }

  public FixWord plus(BinFraction x) {
    return new FixWord(value + makeRepr(x));
  }

  public FixWord minus(FixWord d) {
    return new FixWord(value - d.value);
  }

  public FixWord minus(int num) {
    return new FixWord(value - makeRepr(num));
  }

  public FixWord minus(int num, int den) {
    return new FixWord(value - makeRepr(num, den));
  }

  public FixWord minus(BinFraction x) {
    return new FixWord(value - makeRepr(x));
  }

  public FixWord times(FixWord d) {
    return new FixWord((int) ((long) value * d.value >> POINT_SHIFT));
  }

  public FixWord times(int num) {
    return new FixWord((int) ((long) value * num));
  }

  public FixWord times(int num, int den) {
    return new FixWord((int) (((long) value * num) / den));
  }

  public FixWord times(BinFraction x) {
    return new FixWord(reprTimes(x));
  }

  public FixWord over(FixWord d) {
    return new FixWord((int) (((long) value << POINT_SHIFT) / d.value));
  }

  public FixWord over(int num) {
    return new FixWord((int) ((long) value / num));
  }

  public FixWord over(int num, int den) {
    return new FixWord((int) (((long) value * den) / num));
  }

  public FixWord over(BinFraction x) {
    return new FixWord(reprOver(x));
  }

  public FixWord shifted(int offs) {
    return new FixWord((offs < 0) ? value >> -offs : value << offs);
  }

  public int toInt() {
    return value >>> POINT_SHIFT;
  }

  public int toInt(int den) {
    return (int) ((long) value * den >>> POINT_SHIFT);
  }

  public String toString(String unit) {
    return toString() + unit;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    int v = value;
    final int UNITY = 1 << POINT_SHIFT;
    final int MASK = UNITY - 1;
    if (v < 0) {
      buf.append('-');
      v = -v;
    }
    buf.append(v >>> POINT_SHIFT);
    buf.append('.');
    v = 10 * (v & MASK) + 5;
    int delta = 10;
    do {
      if (delta > UNITY) v += UNITY / 2 - delta / 2;
      buf.append(Character.forDigit(v >>> POINT_SHIFT, 10));
      v = 10 * (v & MASK);
    } while (v > (delta *= 10));
    return buf.toString();
  }

  public int hashCode() {
    return 383 * POINT_SHIFT * value;
  }

  public boolean equals(Object o) {
    return (o != null && o instanceof FixWord && ((FixWord) o).value == value);
  }
}
