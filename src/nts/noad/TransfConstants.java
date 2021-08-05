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
// Filename: nts/noad/TransfConstants.java
// $Id: TransfConstants.java,v 1.1.1.1 2000/04/13 09:43:57 ksk Exp $
package nts.noad;

public interface TransfConstants {

  /* derivation code */
  byte CURRENT = 0;
  byte CRAMPED = 1;
  byte SUB_SCRIPT = 2;
  byte SUPER_SCRIPT = 3;
  byte NUMERATOR = 4;
  byte DENOMINATOR = 5;

  /* spacing type */
  byte SPACING_TYPE_NULL = -1;
  byte SPACING_TYPE_ORD = 0;
  byte SPACING_TYPE_OP = 1;
  byte SPACING_TYPE_BIN = 2;
  byte SPACING_TYPE_REL = 3;
  byte SPACING_TYPE_OPEN = 4;
  byte SPACING_TYPE_CLOSE = 5;
  byte SPACING_TYPE_PUNCT = 6;
  byte SPACING_TYPE_INNER = 7;
  byte NUMBER_OF_SPACING_TYPES = 8;

  /* dimension parameter */
  int DP_MATH_X_HEIGHT = 0;
  int DP_MATH_QUAD = 1;
  int DP_NUM1 = 2;
  int DP_NUM2 = 3;
  int DP_NUM3 = 4;
  int DP_DENOM1 = 5;
  int DP_DENOM2 = 6;
  int DP_SUP1 = 7;
  int DP_SUP2 = 8;
  int DP_SUP3 = 9;
  int DP_SUB1 = 10;
  int DP_SUB2 = 11;
  int DP_SUP_DROP = 12;
  int DP_SUB_DROP = 13;
  int DP_DELIM1 = 14;
  int DP_DELIM2 = 15;
  int DP_AXIS_HEIGHT = 16;
  int DP_DEFAULT_RULE_THICKNESS = 17;
  int DP_BIG_OP_SPACING1 = 18;
  int DP_BIG_OP_SPACING2 = 19;
  int DP_BIG_OP_SPACING3 = 20;
  int DP_BIG_OP_SPACING4 = 21;
  int DP_BIG_OP_SPACING5 = 22;
  int NUMBER_OF_DIM_PARS = 23;
}
