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
// Filename: nts/base/BinFraction.java
// $Id: BinFraction.java,v 1.1.1.1 2001/05/14 15:55:40 ksk Exp $
package nts.base;

import java.io.Serializable;

/**
 * The abstract superclass for fixed-point rational numbers.
 *
 * <p>Subclasses must provide method |pointShift| which determines the position of fixed-point.
 *
 * <p>The interface of this class is quite limited and serves only for alowing numeric operations
 * between different subtypes of |Number| (rational numbers with different fixed-point position).
 * Most of methods is unfortunatelly not shared in subclasses (although the methods are the same)
 * and the code is simply duplicated. It is so because most of them create a new instance or have
 * more efficient variants for the other operand of the same subtype. It is not clear if sharing is
 * feasible. It would need a virtual constructor (method returning a factory object for creating
 * instances).
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 * @see Dimen
 * @see nts.tfm.FixWord
 */
public abstract class BinFraction implements Serializable {

  /** Internal 32 bits representation of the rational number */
  protected final int value;

  /**
   * Constructs a |BinFraction| of certain internal representation value
   *
   * @param val the internal representation value
   */
  protected BinFraction(int val) {
    value = val;
  }

  /**
   * Position of the fixed-point
   *
   * @return the position of the fixed-point counted from the right
   */
  protected abstract int pointShift();

  /**
   * Converts the value of a |BinFraction| to another internal representation with different
   * fixed-point position. It is used for conversion (before operations) between instances of
   * different subclasses.
   *
   * @param x |BinFraction| to be converted
   * @param ps new fixed-point position
   * @return internal representation for different fixed-point position
   */
  protected static int makeRepr(BinFraction x, int ps) {
    return (int) ((long) x.value << ps >> x.pointShift());
  }

  /**
   * Makes an internal representation of the product with another |BinFraction|. It is used for
   * multiplication with instance of different subclass.
   *
   * @param x |BinFraction| to multiply with
   * @return internal representation of the product with |x|
   */
  protected final int reprTimes(BinFraction x) {
    return (int) ((long) value * x.value >> x.pointShift());
  }

  /**
   * Makes an internal representation of the quotient by another |BinFraction|. It is used for
   * division by instance of different subclass.
   *
   * @param x |BinFraction| to divide by
   * @return internal representation of the quotient by |x|
   */
  protected final int reprOver(BinFraction x) {
    return (int) (((long) value << x.pointShift()) / x.value);
  }
}
