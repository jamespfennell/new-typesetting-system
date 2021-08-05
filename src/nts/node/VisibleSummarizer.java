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
// Filename: nts/node/VisibleSummarizer.java
// $Id: VisibleSummarizer.java,v 1.1.1.1 2000/04/30 15:13:44 ksk Exp $
package nts.node;

import nts.base.Dimen;

public class VisibleSummarizer {

  public final GlueSetting setting;
  private Dimen visibleWidth = Dimen.ZERO;
  private Dimen width = Dimen.ZERO;
  private boolean inMiddle = false;
  private boolean elasticSeen = false;

  public VisibleSummarizer(GlueSetting setting) {
    this.setting = setting;
  }

  public Dimen getVisibleWidth() {
    return visibleWidth;
  }

  public boolean addingLeftX() {
    return inMiddle;
  }

  /* TeXt[1146] */
  public void add(Dimen size, boolean visible) {
    if (elasticSeen) {
      if (visible) visibleWidth = Dimen.NULL;
    } else {
      width = width.plus(size);
      if (visible) visibleWidth = width;
    }
    inMiddle = true;
  }

  public void claimElastic() {
    elasticSeen = true;
  }

  public void summarize(NodeEnum nodes) {
    while (visibleWidth != Dimen.NULL && nodes.hasMoreNodes())
      nodes.nextNode().contributeVisible(this);
  }
}
