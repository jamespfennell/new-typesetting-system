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
// Filename: nts/math/MathPrim.java
// $Id: MathPrim.java,v 1.1.1.1 2001/03/09 11:06:32 ksk Exp $
package nts.math;

import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.builder.Builder;
import nts.command.ActiveCharToken;
import nts.command.Command;
import nts.command.Prim;
import nts.command.Token;
import nts.io.CharCode;
import nts.noad.CharField;
import nts.noad.Delimiter;
import nts.noad.EmptyField;
import nts.noad.Field;
import nts.noad.Noad;
import nts.noad.OrdNoad;
import nts.noad.TreatField;
import nts.node.ChrKernNode;
import nts.node.FontMetric;
import nts.typo.Action;
import nts.typo.BuilderPrim;
import nts.typo.CharHandler;

public abstract class MathPrim extends BuilderPrim {

  public MathPrim(String name) {
    super(name);
  }

  public abstract MathAction mathAction();

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public interface MathSpacer {
    Glue getGlue(byte fontSize);

    String getName();
  }

  public interface Config {
    void initFormula();

    void initDisplay(Dimen preSize, Dimen width, Dimen indent);

    Noad noadForCode(int code);

    CharField fieldForCode(int code);

    Noad noadForDelCode(int code);

    CharField fieldForDelCode(int code);

    Delimiter delimiterForDelCode(int code);

    FontMetric familyFont(byte size, byte fam);

    String familyName(byte size, byte fam);

    MathSpacer mathSpacing(byte left, byte right);

    Num mathPenalty(byte left, byte right);
  }

  private static Config config;

  public static void setMathConfig(Config conf) {
    config = conf;
  }

  public static Config getMathConfig() {
    return config;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  public static final int INTP_MAX_MATH_CODE = newIntParam();
  public static final int INTP_MAX_DEL_CODE = newIntParam();

  /* TeXtp[436] */
  public static int scanMathCharCode() {
    return Prim.scanAnyCode(INTP_MAX_MATH_CODE, "BadMathCharCode");
  }

  /* TeXtp[437] */
  public static int scanDelimiterCode() {
    return Prim.scanAnyCode(INTP_MAX_DEL_CODE, "BadDelimiterCode");
  }

  /* TeXtp[1155] */
  public static void setMathChar(MathBuilder bld, CharCode code) {
    Noad noad = getMathConfig().noadForCode(code.mathCode());
    if (noad != Noad.NULL) bld.addNoad(noad);
    else backActiveChar(code);
  }

  public static void setMathChar(MathBuilder bld, int code) {
    bld.addNoad(getMathConfig().noadForCode(code));
  }

  public static void handleMathCode(int code, Token src) {
    Builder bld = getBld();
    CharHandler hnd = getCharHandler(bld.getClass());
    if (hnd == CharHandler.NULL) error("CantUseIn", str("character"), bld);
    else if (hnd instanceof MathCharHandler) ((MathCharHandler) hnd).handleMath(bld, code, src);
    else insertDollar(src);
  }

  public static class MathCharHandler implements CharHandler {

    public void handle(Builder bld, CharCode code, Token src) {
      setMathChar((MathBuilder) bld, code);
    }

    public void handleSpace(Builder bld, Token src) {}

    public void handleMath(Builder bld, int code, Token src) {
      setMathChar((MathBuilder) bld, code);
    }
  }

  public static final Action DOLLAR =
      new Action() {
        public void exec(Builder bld, Token src) {
          insertDollar(src);
        }
      };

  public static final Action ZERO_KERN =
      new Action() {
        public void exec(Builder bld, Token src) {
          bld.addNode(new ChrKernNode(Dimen.ZERO));
        }
      };

  /* STRANGE
   * Empty OrdNoad is added and then removed only for the case when
   * \showlists appear inside the group.
   */
  public static final MathAction LEFT_BRACE =
      new MathAction() {
        public void exec(final MathBuilder bld, Token src) {
          bld.addNoad(new OrdNoad(EmptyField.FIELD));
          pushLevel(
              new MathGroup(
                  new TreatField() {
                    public void execute(Field field) {
                      bld.replaceLastNoad(makeOrdNoad(field));
                    }
                  }));
        }
      };

  /* TeXtp[1186] */
  public static Noad makeOrdNoad(Field field) {
    Noad noad = field.ordinaryNoad();
    return (noad != Noad.NULL) ? noad : new OrdNoad(field);
  }

  /* TeXtp[1047] */
  public static void insertDollar(Token src) {
    backToken(src);
    insertToken(MathShiftToken.TOKEN);
    error("MissingDollar");
  }

  /* TeXtp[1151] */
  public static void scanField(TreatField proc) {
    Field field = Field.NULL;
    boolean again;
    do {
      again = false;
      Token tok = nextNonRelax();
      Command cmd = meaningOf(tok);
      if (cmd.hasMathCodeValue()) field = getMathConfig().fieldForCode(cmd.getMathCodeValue());
      else if (cmd.hasDelCodeValue())
        field = getMathConfig().fieldForDelCode(cmd.getDelCodeValue());
      else {
        CharCode code = cmd.charCodeToAdd();
        if (code == CharCode.NULL) backToken(tok);
        else {
          field = getMathConfig().fieldForCode(code.mathCode());
          if (field == Field.NULL) {
            backActiveChar(code);
            again = true;
          }
        }
      }
    } while (again);
    if (field != Field.NULL) proc.execute(field);
    else {
      scanLeftBrace();
      pushLevel(new MathGroup(proc));
    }
  }

  /* TeXtp[1152] */
  private static void backActiveChar(CharCode code) {
    backToken(new ActiveCharToken(code));
    /*
    BoolPar		exp = new BoolPar(true);
    Token		tok = new ActiveCharToken(code);
    for (;;) {
        Command	cmd = meaningOf(tok);
        if (cmd.expandable() && exp.get()) cmd.doExpansion(tok);
        else { backToken(tok); break; }
        tok = nextRawToken(exp);
    }
       */
  }

  /* STRANGE
   * cmd.charCode() is tested only to exclude \chargiven and \char; Why?
   */
  /* TeXtp[1160] */
  public static Delimiter scanDelimiter() {
    Delimiter del = Delimiter.NULL;
    Token tok = nextNonRelax();
    Command cmd = meaningOf(tok);
    if (cmd.hasDelCodeValue()) del = getMathConfig().delimiterForDelCode(cmd.getDelCodeValue());
    CharCode code = cmd.charCodeToAdd();
    if (code != CharCode.NULL && cmd.charCode() != CharCode.NULL)
      del = getMathConfig().delimiterForDelCode(code.delCode());
    if (del != Delimiter.NULL) return del;
    backToken(tok);
    error("MissingDelim");
    return Delimiter.VOID;
  }
}
