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
// Filename: nts/base/Num.java
// $Id: Num.java,v 1.1.1.1 2001/05/17 23:29:55 ksk Exp $
package nts.base;

import java.io.Serializable;

/**
 * The whole number for representing the \TeX\ values of counters. It is introduced for symetry with
 * other kinds of registers which have their own object types for representation of values (|Dimen|,
 * |Glue|). The instances of class |Num| are immutable. It means that each instance represents the
 * same value during its whole life-time and all its methods are free of side-effects. If you need
 * to represent a new value create a new instance. This is done if necessary by all arithmetic
 * operation methods.
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 */
public class Num implements Serializable, IntProvider {

  /** Null constant */
  public static final Num NULL = null;

  /** |Num| constant representing zero */
  public static final Num ZERO = Num.valueOf(0);

  /** maximum |int| value which is allowed to be converted to |Num| */
  public static final int MAX_INT_VALUE = 0x7fffffff;

  /** Provider of |Num| value. */
  public interface Provider {
    /**
     * Provides the value of type |Num|
     *
     * @return the provided |Num| value
     */
    Num getNumValue();
  }

  /** Internal representation of the whole number */
  private final int value;

  /**
   * Creates |Num| representing the given integer.
   *
   * @param num the integer value.
   */
  private Num(int num) {
    value = num;
  }

  public static Num valueOf(int num) {
    return new Num(num);
  }

  public final int intVal() {
    return value;
  }

  public int sign() {
    return (value > 0) ? 1 : (value < 0) ? -1 : 0;
  }

  public boolean equals(int i) {
    return (value == i);
  }

  public boolean equals(Num n) {
    return (value == n.intVal());
  }

  public boolean lessThan(int i) {
    return (value < i);
  }

  public boolean lessThan(Num n) {
    return (value < n.intVal());
  }

  public boolean moreThan(int i) {
    return (value > i);
  }

  public boolean moreThan(Num n) {
    return (value > n.intVal());
  }

  public Num negative() {
    return new Num(-value);
  }

  public Num plus(int i) {
    return new Num(value + i);
  }

  public Num plus(Num n) {
    return new Num(value + n.intVal());
  }

  public Num minus(int i) {
    return new Num(value - i);
  }

  public Num minus(Num n) {
    return new Num(value - n.intVal());
  }

  public Num times(int i) {
    return new Num(value * i);
  }

  public Num times(Num n) {
    return new Num(value * n.intVal());
  }

  public Num over(int i) {
    return new Num(value / i);
  }

  public Num over(Num n) {
    return new Num(value / n.intVal());
  }

  /**
   * Gives the decimal representation of the |Num| as a character string.
   *
   * @return the string representation of the |Num|.
   */
  public String toString() {
    return Integer.toString(value);
  }

  public String toOctString() {
    return Integer.toOctalString(value);
  }

  public String toHexString() {
    return Integer.toHexString(value);
  }

  /**
   * Gives a hash code for a |Num|.
   *
   * @return the hash code for this object.
   */
  public int hashCode() {
    return value;
  }

  /**
   * Compares this |Num| to the specified object. The result is |true| if and only if the the
   * argument is not |null| and is the |Num| object which represents the same number.
   *
   * @param o the object to compare this |Num| against.
   * @return |true| if the argument is equal, |false| otherwise.
   */
  public boolean equals(Object o) {
    return (o != null && o instanceof Num && ((Num) o).value == value);
  }

  private static final class RomanDigit {
    char digit;
    int value;
    boolean precedable;

    RomanDigit(char d, int v, boolean p) {
      digit = d;
      value = v;
      precedable = p;
    }
  }

  private static final RomanDigit[] romanDigits = {
    new RomanDigit('m', 1000, false),
    new RomanDigit('d', 500, false),
    new RomanDigit('c', 100, true),
    new RomanDigit('l', 50, false),
    new RomanDigit('x', 10, true),
    new RomanDigit('v', 5, false),
    new RomanDigit('i', 1, true)
  };

  public static String romanString(int n) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; ; i++) {
      RomanDigit dig = romanDigits[i];
      while (n >= dig.value) {
        buf.append(dig.digit);
        n -= dig.value;
      }
      if (n <= 0) break;
      int j = i;
      while (!romanDigits[++j].precedable)
        ;
      if (n + romanDigits[j].value >= dig.value) {
        buf.append(romanDigits[j].digit).append(dig.digit);
        n -= dig.value - romanDigits[j].value;
      }
    }
    return buf.toString();
  }
}
