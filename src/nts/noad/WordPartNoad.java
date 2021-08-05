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
// Filename: nts/noad/WordPartNoad.java
// $Id: WordPartNoad.java,v 1.1.1.1 2000/10/17 13:25:47 ksk Exp $
package nts.noad;

import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.TreatNode;

public abstract class WordPartNoad extends NucleusNoad {

  public WordPartNoad(Field nucleus) {
    super(nucleus);
  }

  public boolean canBePartOfWord() {
    return nucleus.isJustChar();
  }

  public byte wordFamily() {
    return nucleus.wordFamily();
  }

  public MathWordBuilder getMathWordBuilder(Converter conv, TreatNode proc) {
    return nucleus.getMathWordBuilder(conv, proc);
  }

  public void contributeToWord(MathWordBuilder word) {
    nucleus.contributeToWord(word);
  }

  public Egg wordFinishingEgg(MathWordBuilder word, Converter conv) {
    if (word.lastHasCollapsed()) return Egg.NULL;
    return makeEgg(word.takeLastNode());
  }

  public Egg wordFinishingEggWithScripts(
      MathWordBuilder word, Converter conv, Field sup, Field sub) {
    Node node = word.takeLastNode();
    Egg egg = (word.lastHasCollapsed()) ? new StItalNodeEgg(node, SPACING_TYPE_ORD) : makeEgg(node);
    return makeScriptsTo(egg, false, conv, sup, sub);
  }
}
