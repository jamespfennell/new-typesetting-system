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
// Filename: nts/typo/RulePrim.java
// $Id: RulePrim.java,v 1.1.1.1 1999/09/06 05:43:08 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.Token;
import nts.node.BoxSizes;

public class RulePrim extends BuilderPrim {

  private final Dimen defaultHeight;
  private final Dimen defaultWidth;
  private final Dimen defaultDepth;
  private final Dimen defaultLeftX;

  public RulePrim(String name) {
    super(name);
    defaultHeight = Dimen.NULL;
    defaultWidth = Dimen.NULL;
    defaultDepth = Dimen.NULL;
    defaultLeftX = Dimen.NULL;
  }

  public RulePrim(String name, Dimen h, Dimen w, Dimen d, Dimen l) {
    super(name);
    defaultHeight = h;
    defaultWidth = w;
    defaultDepth = d;
    defaultLeftX = l;
  }

  /* TeXtp[1056] */
  public final Action NORMAL =
      new Action() {
        public void exec(Builder bld, Token src) {
          bld.addRule(getRule());
        }
      };

  /* TeXtp[1094,1095] */
  public final Action BAD_HRULE =
      new Action() {
        public void exec(Builder bld, Token src) {
          error("CantUseHrule", RulePrim.this);
        }
      };

  public boolean hasRuleValue() {
    return true;
  }

  public BoxSizes getRuleValue() {
    return getRule();
  }

  public BoxSizes getRule() {
    return scanRule(defaultHeight, defaultWidth, defaultDepth, defaultLeftX);
  }

  /* TeXtp[463] */
  public static BoxSizes scanRule(Dimen h, Dimen w, Dimen d, Dimen l) {
    for (; ; )
      if (scanKeyword("width")) w = scanDimen();
      else if (scanKeyword("height")) h = scanDimen();
      else if (scanKeyword("depth")) d = scanDimen();
      else break;
    return new BoxSizes(h, w, d, l);
  }
}
