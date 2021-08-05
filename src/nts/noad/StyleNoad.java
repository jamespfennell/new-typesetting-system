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
// Filename: nts/noad/StyleNoad.java
// $Id: StyleNoad.java,v 1.1.1.1 2000/04/07 20:51:59 ksk Exp $
package nts.noad;

import nts.io.CntxLog;
import nts.io.Log;

public class StyleNoad extends PureNoad {

  protected final byte style;

  public StyleNoad(byte style) {
    this.style = style;
  }

  public final boolean influencesBin() {
    return false;
  }

  /* TeXtp[730,763] */
  public Egg convert(Converter conv) {
    conv.setStyle(style);
    return new StyleEgg(style);
  }

  /* TeXtp[1170,694] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getStyleName(style));
  }

  private static final String[] styleNames = new String[NUMBER_OF_STYLES];

  static {
    styleNames[DISPLAY_STYLE] = "displaystyle";
    styleNames[TEXT_STYLE] = "textstyle";
    styleNames[SCRIPT_STYLE] = "scriptstyle";
    styleNames[SCRIPT_SCRIPT_STYLE] = "scriptscriptstyle";
  }

  public static String getStyleName(byte style) {
    return styleNames[style];
  }
}
