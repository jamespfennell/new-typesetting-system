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
// Filename: nts/noad/ChoiceNoad.java
// $Id: ChoiceNoad.java,v 1.1.1.1 2000/04/07 15:08:50 ksk Exp $
package nts.noad;

import nts.io.CntxLog;
import nts.io.Log;

public class ChoiceNoad extends PureNoad {

  private final NoadList[] choices;

  public ChoiceNoad(NoadList[] choices) {
    this.choices = choices;
    if (choices.length != NUMBER_OF_STYLES) throw new RuntimeException("Bad size of math choices");
  }

  public final boolean influencesBin() {
    return false;
  }

  /* TeXtp[731] */
  public Egg convert(Converter conv) {
    conv.push(choices[conv.getStyle()].noads());
    return VoidEgg.EGG;
  }

  /* TeXtp[695] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc());
    for (byte i = 0; i < NUMBER_OF_STYLES; i++)
      cntx.addOn(log, choices[i].noads(), getStylePrefix(i));
  }

  protected String getDesc() {
    return "mathchoice";
  }

  private static final char[] stylePrefixes = new char[NUMBER_OF_STYLES];

  static {
    stylePrefixes[DISPLAY_STYLE] = 'D';
    stylePrefixes[TEXT_STYLE] = 'T';
    stylePrefixes[SCRIPT_STYLE] = 'S';
    stylePrefixes[SCRIPT_SCRIPT_STYLE] = 's';
  }

  public static char getStylePrefix(byte style) {
    return stylePrefixes[style];
  }
}
