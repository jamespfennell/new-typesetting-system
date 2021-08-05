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
// Filename: nts/node/FontMetric.java
// $Id: FontMetric.java,v 1.1.1.1 2001/02/09 12:53:47 ksk Exp $
package nts.node;

import java.io.Serializable;
import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.io.CharCode;
import nts.io.Log;
import nts.io.Name;

public interface FontMetric extends Serializable {

  FontMetric NULL = null;

  Name getIdent();

  Name getName();

  void addDescOn(Log log);

  boolean isNull();

  Glue getNormalSpace();

  Node getCharNode(CharCode code);

  Node getLargerNode(CharCode code);

  Node getSufficientNode(CharCode code, Dimen desired);

  Box getFittingWidthBox(CharCode code, Dimen desired);

  Dimen getKernBetween(CharCode left, CharCode right);

  WordBuilder getWordBuilder(TreatNode proc, boolean boundary, boolean discretionaries);

  WordRebuilder getWordRebuilder(TreatNode proc, boolean boundary);

  MathWordBuilder getMathWordBuilder(TreatNode proc);

  int NUM_PARAM_HYPHEN_CHAR = 0, NUM_PARAM_SKEW_CHAR = 1, NUMBER_OF_NUM_PARAMS = 2;

  Num getNumParam(int idx);

  Num setNumParam(int idx, Num val);

  boolean definesNumParams(int[] idxs);

  int DIMEN_PARAM_SLANT = 0,
      DIMEN_PARAM_SPACE = 1,
      DIMEN_PARAM_STRETCH = 2,
      DIMEN_PARAM_SHRINK = 3,
      DIMEN_PARAM_X_HEIGHT = 4,
      DIMEN_PARAM_QUAD = 5,
      DIMEN_PARAM_EXTRA_SPACE = 6,
      DIMEN_PARAM_MATH_X_HEIGHT = 7,
      DIMEN_PARAM_MATH_QUAD = 8,
      DIMEN_PARAM_NUM1 = 9,
      DIMEN_PARAM_NUM2 = 10,
      DIMEN_PARAM_NUM3 = 11,
      DIMEN_PARAM_DENOM1 = 12,
      DIMEN_PARAM_DENOM2 = 13,
      DIMEN_PARAM_SUP1 = 14,
      DIMEN_PARAM_SUP2 = 15,
      DIMEN_PARAM_SUP3 = 16,
      DIMEN_PARAM_SUB1 = 17,
      DIMEN_PARAM_SUB2 = 18,
      DIMEN_PARAM_SUP_DROP = 19,
      DIMEN_PARAM_SUB_DROP = 20,
      DIMEN_PARAM_DELIM1 = 21,
      DIMEN_PARAM_DELIM2 = 22,
      DIMEN_PARAM_AXIS_HEIGHT = 23,
      DIMEN_PARAM_DEFAULT_RULE_THICKNESS = 24,
      DIMEN_PARAM_BIG_OP_SPACING1 = 25,
      DIMEN_PARAM_BIG_OP_SPACING2 = 26,
      DIMEN_PARAM_BIG_OP_SPACING3 = 27,
      DIMEN_PARAM_BIG_OP_SPACING4 = 28,
      DIMEN_PARAM_BIG_OP_SPACING5 = 29,
      NUMBER_OF_DIMEN_PARAMS = 30;
  int[]
      ALL_TEXT_DIMEN_PARAMS =
          {
            DIMEN_PARAM_SLANT, DIMEN_PARAM_SPACE,
            DIMEN_PARAM_STRETCH, DIMEN_PARAM_SHRINK,
            DIMEN_PARAM_X_HEIGHT, DIMEN_PARAM_QUAD,
            DIMEN_PARAM_EXTRA_SPACE
          },
      ALL_MATH_SYMBOL_DIMEN_PARAMS =
          {
            DIMEN_PARAM_MATH_X_HEIGHT,
            DIMEN_PARAM_MATH_QUAD,
            DIMEN_PARAM_NUM1,
            DIMEN_PARAM_NUM2,
            DIMEN_PARAM_NUM3,
            DIMEN_PARAM_DENOM1,
            DIMEN_PARAM_DENOM2,
            DIMEN_PARAM_SUP1,
            DIMEN_PARAM_SUP2,
            DIMEN_PARAM_SUP3,
            DIMEN_PARAM_SUB1,
            DIMEN_PARAM_SUB2,
            DIMEN_PARAM_SUP_DROP,
            DIMEN_PARAM_SUB_DROP,
            DIMEN_PARAM_DELIM1,
            DIMEN_PARAM_DELIM2,
            DIMEN_PARAM_AXIS_HEIGHT
          },
      ALL_MATH_EXTENSION_DIMEN_PARAMS =
          {
            DIMEN_PARAM_DEFAULT_RULE_THICKNESS,
            DIMEN_PARAM_BIG_OP_SPACING1,
            DIMEN_PARAM_BIG_OP_SPACING2,
            DIMEN_PARAM_BIG_OP_SPACING3,
            DIMEN_PARAM_BIG_OP_SPACING4,
            DIMEN_PARAM_BIG_OP_SPACING5
          };

  Dimen getDimenParam(int idx);

  Dimen setDimenParam(int idx, Dimen val);

  boolean definesDimenParams(int[] idxs);
}
