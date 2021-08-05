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
// Filename: nts/math/OpenMath.java
// $Id: OpenMath.java,v 1.1.1.1 2001/03/12 21:48:59 ksk Exp $
package nts.math;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.Token;
import nts.command.TokenList;
import nts.io.Log;
import nts.node.FontMetric;
import nts.node.LinesShape;
import nts.node.NodeList;
import nts.typo.Action;
import nts.typo.BuilderCommand;
import nts.typo.Paragraph;

public class OpenMath extends BuilderCommand {

  private /* final */ TokenList.Inserter everyMath;
  private /* final */ TokenList.Inserter everyDisplay;

  public OpenMath(TokenList.Inserter everyMath, TokenList.Inserter everyDisplay) {
    this.everyMath = everyMath;
    this.everyDisplay = everyDisplay;
  }

  /* TeXtp[1138] */
  public final Action OPEN_MATH =
      new Action() {
        public void exec(Builder bld, Token src) {
          Token tok = nextRawToken();
          if (meaningOf(tok).isMathShift()) startDisplay();
          else {
            backToken(tok);
            startFormula();
          }
        }
      };

  /* STRANGE
   * Why is next token read when it is not needed at all?
   */
  /* TeXtp[1138] */
  public final Action OPEN_FORMULA =
      new Action() {
        public void exec(Builder bld, Token src) {
          backToken(nextRawToken());
          startFormula();
        }
      };

  public static final int INTP_DISPLAY_WIDOW_PENALTY = newIntParam();
  private static final Dimen NO_WIDTH = Dimen.MAX_VALUE.negative();

  protected void startDisplay() {
    Dimen.Par lastVisibleWidth = new Dimen.Par(NO_WIDTH);
    Builder parBld = getBld();
    NodeList list = parBld.getParagraph();
    if (list != NodeList.NULL)
      Paragraph.lineBreak(
          list,
          parBld.getStartLine(),
          getConfig().getIntParam(INTP_DISPLAY_WIDOW_PENALTY),
          parBld.getInitLang(),
          lastVisibleWidth);
    Dimen preSize = lastVisibleWidth.get();
    if (preSize == Dimen.NULL) preSize = Dimen.MAX_VALUE;
    else if (preSize != NO_WIDTH)
      preSize =
          preSize.plus(getCurrFontMetric().getDimenParam(FontMetric.DIMEN_PARAM_QUAD).times(2));
    LinesShape shape = getTypoConfig().linesShape();
    Builder old = getBld();
    int lineNo = old.getPrevGraf() + 1;
    pushLevel(new DisplayGroup());
    MathPrim.getMathConfig().initDisplay(preSize, shape.getWidth(lineNo), shape.getIndent(lineNo));
    everyDisplay.insertToks();
    old.buildPage(); // XXX is this order unavoidable?
  }

  protected void startFormula() {
    pushLevel(new FormulaGroup());
    MathPrim.getMathConfig().initFormula();
    everyMath.insertToks();
  }

  public void addOn(Log log) {
    log.add("internal open math");
  }
}
