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
// Filename: nts/align/DispAlignGroup.java
// $Id: DispAlignGroup.java,v 1.1.1.1 2001/03/22 13:47:54 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.base.Num;
import nts.builder.Builder;
import nts.command.Token;
import nts.math.DisplayGroup;
import nts.node.NodeEnum;

public class DispAlignGroup extends AlignGroup {

  public DispAlignGroup(Alignment align) {
    super(align);
  }

  public static final int INTP_PRE_DISPLAY_PENALTY = DisplayGroup.INTP_PRE_DISPLAY_PENALTY;
  public static final int INTP_POST_DISPLAY_PENALTY = DisplayGroup.INTP_POST_DISPLAY_PENALTY;
  public static final int DIMP_DISPLAY_INDENT = DisplayGroup.DIMP_DISPLAY_INDENT;
  public static final int GLUEP_ABOVE_DISPLAY_SKIP = DisplayGroup.GLUEP_ABOVE_DISPLAY_SKIP;
  public static final int GLUEP_BELOW_DISPLAY_SKIP = DisplayGroup.GLUEP_BELOW_DISPLAY_SKIP;

  /* TeXtp[812,1206] */
  public void stop() {
    Config cfg = getConfig();
    Dimen indent = cfg.getDimParam(DIMP_DISPLAY_INDENT);
    NodeEnum nodes = align.finish(indent);
    if (expectMathShift()) DisplayGroup.expectAnotherMathShift();
    Builder.pop();
    Builder bld = Builder.top();
    bld.addPenalty(Num.valueOf(cfg.getIntParam(INTP_PRE_DISPLAY_PENALTY)));
    bld.addSkip(
        cfg.getGlueParam(GLUEP_ABOVE_DISPLAY_SKIP), cfg.getGlueName(GLUEP_ABOVE_DISPLAY_SKIP));
    bld.addNodes(nodes);
    bld.addPenalty(Num.valueOf(cfg.getIntParam(INTP_POST_DISPLAY_PENALTY)));
    bld.addSkip(
        cfg.getGlueParam(GLUEP_BELOW_DISPLAY_SKIP), cfg.getGlueName(GLUEP_BELOW_DISPLAY_SKIP));
    align.copyPrevParameters(bld);
  }

  public void close() {
    killLevel();
    DisplayGroup.resumeAfterDisplay();
  }

  /* TeXtp[1207] */
  protected static boolean expectMathShift() {
    Token tok = nextNonAssignment();
    if (!meaningOf(tok).isMathShift()) {
      backToken(tok);
      error("MissingFormulaEnd");
      return false;
    }
    return true;
  }
}
