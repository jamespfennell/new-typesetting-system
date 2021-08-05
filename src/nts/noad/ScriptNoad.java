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
// Filename: nts/noad/ScriptNoad.java
// $Id: ScriptNoad.java,v 1.1.1.1 2000/11/04 20:51:14 ksk Exp $
package nts.noad;

import nts.node.MathWordBuilder;

public abstract class ScriptNoad extends ScriptableNoad {

  protected final Noad body;
  protected final Field script;

  public ScriptNoad(Noad body, Field script) {
    this.body = body;
    this.script = script;
  }

  public boolean canPrecedeBin() {
    return body.canPrecedeBin();
  }

  public boolean canFollowBin() {
    return body.canFollowBin();
  }

  public boolean acceptsLimits() {
    return body.acceptsLimits();
  }

  public boolean canBePartOfWord() {
    return body.canBePartOfWord();
  }

  public byte wordFamily() {
    return body.wordFamily();
  }

  public void contributeToWord(MathWordBuilder word) {
    body.contributeToWord(word);
  }

  public Noad withLimits(byte limits) {
    return rebodiedCopy(body.withLimits(limits));
  }

  protected abstract ScriptNoad rebodiedCopy(Noad body);
}
