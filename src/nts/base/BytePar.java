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
// Filename: nts/base/BytePar.java
// $Id: BytePar.java,v 1.1.1.1 2001/05/17 23:27:00 ksk Exp $
package nts.base;

/**
 * Output parameter class for the |byte| type. Used as a medium for (optional) returning values of
 * type |integer| when more return values are needed.
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 */
public class BytePar {

  /** Null constant for the |BytePar| */
  public static final BytePar NULL = null;

  /** value of the parameter */
  private byte value;

  /** default constructor (default value of parameter (0)) */
  public BytePar() {}

  /**
   * Constructor for initial value of the parameter
   *
   * @param val initial value of the parameter
   */
  public BytePar(byte val) {
    value = val;
  }

  /**
   * Sets a new value of the parameter
   *
   * @param val new value of the parameter
   */
  public void set(byte val) {
    value = val;
  }

  /**
   * Gets the value of the parameter
   *
   * @return value of the parameter
   */
  public byte get() {
    return value;
  }

  /**
   * Sets the value of the |BytePar| if the |BytePar| is given (non-|null|)
   *
   * @param par instance of |BytePar| parameter or |null|
   * @param val new value of the parameter
   */
  public static void set(BytePar par, byte val) {
    if (par != NULL) par.value = val;
  }
}
