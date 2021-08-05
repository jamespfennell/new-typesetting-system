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
// Filename: nts/noad/Noad.java
// $Id: Noad.java,v 1.1.1.1 2000/10/17 13:34:53 ksk Exp $
package nts.noad;

import java.io.Serializable;
import nts.io.CntxLog;
import nts.io.CntxLoggable;
import nts.io.Log;
import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.TreatNode;

public interface Noad extends Serializable, CntxLoggable, TransfConstants {

  Noad NULL = null;

  byte DISPLAY_STYLE = 0;
  byte TEXT_STYLE = 1;
  byte SCRIPT_STYLE = 2;
  byte SCRIPT_SCRIPT_STYLE = 3;
  byte NUMBER_OF_STYLES = 4;

  public interface Provider {
    Noad getNoadValue();
  }

  boolean acceptsLimits();

  boolean isScriptable();

  boolean isOrdinary();

  boolean isJustChar();

  boolean alreadySuperScripted();

  boolean alreadySubScripted();

  void addOnWithScripts(Log log, CntxLog cntx, Field sup, Field sub);

  Noad withLimits(byte limits);

  Field ordinaryField();

  boolean isNode();

  Node getNode();

  Egg convert(Converter conv);

  Egg convertWithScripts(Converter conv, Field sup, Field sub);

  boolean influencesBin();

  boolean canPrecedeBin();

  boolean canFollowBin();

  boolean startsWord();

  boolean canBePartOfWord();

  boolean finishesWord();

  MathWordBuilder getMathWordBuilder(Converter conv, TreatNode proc);

  byte wordFamily();

  void contributeToWord(MathWordBuilder word);

  Egg wordFinishingEgg(MathWordBuilder word, Converter conv);

  Egg wordFinishingEggWithScripts(MathWordBuilder word, Converter conv, Field sup, Field sub);
}
