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
// Filename: nts/node/HSkipNode.java
// $Id: HSkipNode.java,v 1.1.1.1 2001/03/06 14:55:56 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.base.Glue;

public class HSkipNode extends AnySkipNode {
  /* corresponding to glue_node */

  public HSkipNode(Glue skip) {
    super(skip);
  }

  public Dimen getWidth() {
    return skip.getDimen();
  }

  public Dimen getWstr() {
    return skip.getStretch();
  }

  public byte getWstrOrd() {
    return skip.getStrOrder();
  }

  public Dimen getWshr() {
    return skip.getShrink();
  }

  public byte getWshrOrd() {
    return skip.getShrOrder();
  }

  public Dimen getWidth(GlueSetting setting) {
    return setting.set(skip, true);
  }

  public void contributeVisible(VisibleSummarizer summarizer) {
    if (summarizer.setting.makesElastic(skip)) summarizer.claimElastic();
    super.contributeVisible(summarizer);
  }

  public boolean startsWordBlock() {
    return true;
  }

  public byte afterWord() {
    return SUCCESS;
  }

  public String toString() {
    return "HSkip(" + skip + ')';
  }
}
