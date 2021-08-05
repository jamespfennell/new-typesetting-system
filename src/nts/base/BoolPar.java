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
// Filename: nts/base/BoolPar.java
// $Id: BoolPar.java,v 1.1.1.1 2001/05/17 23:26:40 ksk Exp $
package nts.base;

/**
 * Output parameter class for the |boolean| type. Used as a medium for (optional) returning values
 * of type |integer| when more return values are needed.
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 */
public class BoolPar {

  /** Null constant for the |BoolPar| */
  public static final BoolPar NULL = null;

  /** value of the parameter */
  private boolean value;

  /** default constructor (default value of parameter (false)) */
  public BoolPar() {}

  /**
   * Constructor for initial value of the parameter
   *
   * @param val initial value of the parameter
   */
  public BoolPar(boolean val) {
    value = val;
  }

  /**
   * Sets a new value of the parameter
   *
   * @param val new value of the parameter
   */
  public void set(boolean val) {
    value = val;
  }

  /**
   * Gets the value of the parameter
   *
   * @return value of the parameter
   */
  public boolean get() {
    return value;
  }

  /** Negates the value of the parameter */
  public void negate() {
    value = !value;
  }

  /**
   * Sets the value of the |BoolPar| if the |BoolPar| is given (non-|null|)
   *
   * @param par instance of |BoolPar| parameter or |null|
   * @param val new value of the parameter
   */
  public static void set(BoolPar par, boolean val) {
    if (par != NULL) par.value = val;
  }
}
