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
// Filename: nts/math/FamilyPrim.java
// $Id: FamilyPrim.java,v 1.1.1.1 2001/02/01 16:20:20 ksk Exp $
package nts.math;

import nts.command.AssignPrim;
import nts.command.CtrlSeqToken;
import nts.command.Token;
import nts.command.TokenList;
import nts.io.Log;
import nts.node.FontMetric;
import nts.typo.NullFontMetric;
import nts.typo.TypoCommand;

public class FamilyPrim extends AssignPrim implements TokenList.Provider {

  protected final NumKind tabKind = new NumKind();

  protected final FontMetric defVal;

  public FamilyPrim(String name, FontMetric defVal) {
    super(name);
    this.defVal = defVal;
  }

  public FamilyPrim(String name) {
    super(name);
    this.defVal = NullFontMetric.METRIC;
  }

  protected final void set(int idx, FontMetric val, boolean glob) {
    if (glob) getEqt().gput(tabKind, idx, val);
    else getEqt().put(tabKind, idx, val);
  }

  public final FontMetric get(int idx) {
    FontMetric val = (FontMetric) getEqt().get(tabKind, idx);
    return (val != FontMetric.NULL) ? val : defVal;
  }

  public void addEqValueOn(int idx, Log log) {
    get(idx).getIdent().addEscapedOn(log);
  }

  /**
   * Performs the assignment.
   *
   * @param src source token for diagnostic output.
   * @param glob indication that the assignment is global.
   */
  /* TeXtp[1234] */
  protected void assign(Token src, boolean glob) {
    int idx = scanFamilyCode();
    skipOptEquals();
    set(idx, TypoCommand.scanFontMetric(), glob);
  }

  public boolean hasFontTokenValue() {
    return true;
  }

  public boolean hasFontMetricValue() {
    return true;
  }

  /* TeXtp[415,465] */
  public Token getFontTokenValue() {
    return new CtrlSeqToken(get(scanFamilyCode()).getIdent());
  }

  /* TeXtp[577] */
  public FontMetric getFontMetricValue() {
    return get(scanFamilyCode());
  }

  public final void init(int idx, FontMetric val) {
    set(idx, val, true);
  }

  public static int scanFamilyCode() {
    return scanFileCode();
  }
}
