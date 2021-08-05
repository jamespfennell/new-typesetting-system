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
// Filename: nts/node/BaseNode.java
// $Id: BaseNode.java,v 1.1.1.1 2000/06/06 11:52:47 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.command.TokenList; // DDD
import nts.io.CntxLog;
import nts.io.Log;
import nts.io.Name;

public abstract class BaseNode implements Node {
  /* corresponding to ANY_node */

  public Dimen getHeight() {
    return Dimen.ZERO;
  }

  public Dimen getDepth() {
    return Dimen.ZERO;
  }

  public Dimen getHstr() {
    return Dimen.ZERO;
  }

  public byte getHstrOrd() {
    return Glue.NORMAL;
  }

  public Dimen getHshr() {
    return Dimen.ZERO;
  }

  public byte getHshrOrd() {
    return Glue.NORMAL;
  }

  public Dimen getWidth() {
    return Dimen.ZERO;
  }

  public Dimen getLeftX() {
    return Dimen.ZERO;
  }

  public Dimen getWstr() {
    return Dimen.ZERO;
  }

  public byte getWstrOrd() {
    return Glue.NORMAL;
  }

  public Dimen getWshr() {
    return Dimen.ZERO;
  }

  public byte getWshrOrd() {
    return Glue.NORMAL;
  }

  public boolean sizeIgnored() {
    return false;
  }

  public boolean isPenalty() {
    return false;
  }

  public boolean isKern() {
    return false;
  }

  public boolean hasKern() {
    return false;
  }

  public boolean isSkip() {
    return false;
  }

  public boolean isMuSkip() {
    return false;
  }

  public boolean isBox() {
    return false;
  }

  public boolean isCleanBox() {
    return false;
  }

  public boolean isMigrating() {
    return false;
  }

  public boolean isMark() {
    return false;
  }

  public boolean isInsertion() {
    return false;
  }

  public Num getPenalty() {
    return Num.NULL;
  }

  public Dimen getKern() {
    return Dimen.NULL;
  }

  public Glue getSkip() {
    return Glue.NULL;
  }

  public Glue getMuSkip() {
    return Glue.NULL;
  }

  public Box getBox() {
    return Box.NULL;
  }

  public Dimen getItalCorr() {
    return Dimen.NULL;
  }

  public NodeEnum getMigration() {
    return NodeList.EMPTY_ENUM;
  }

  public TokenList getMark() {
    return TokenList.EMPTY;
  }

  public Insertion getInsertion() {
    return Insertion.NULL;
  }

  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    log.add("[]");
    return metric;
  }

  public void addOn(Log log, CntxLog cntx, Dimen shift) {
    addOn(log, cntx);
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {}

  public void syncVertIfBox(TypeSetter setter) {}

  public Dimen getHeight(GlueSetting setting) {
    return getHeight();
  }

  public Dimen getDepth(GlueSetting setting) {
    return getDepth();
  }

  public Dimen getWidth(GlueSetting setting) {
    return getWidth();
  }

  public Dimen getLeftX(GlueSetting setting) {
    return getLeftX();
  }

  public void addBreakDescOn(Log log) {}

  public boolean discardable() {
    return false;
  }

  public boolean isKernBreak() {
    return false;
  }

  public boolean canPrecedeSkipBreak() {
    return !discardable();
  }

  public boolean canFollowKernBreak() {
    return false;
  }

  public boolean allowsSpaceBreaking() {
    return false;
  }

  public boolean forbidsSpaceBreaking() {
    return false;
  }

  public boolean isHyphenBreak() {
    return false;
  }

  public Dimen preBreakWidth() {
    return Dimen.ZERO;
  }

  public Dimen postBreakWidth() {
    return Dimen.ZERO;
  }

  public NodeEnum atBreakReplacement() {
    return NodeList.nodes(this);
  }

  public NodeEnum postBreakNodes() {
    return NodeEnum.NULL;
  }

  public boolean discardsAfter() {
    return true;
  }

  public boolean canBePartOfDiscretionary() {
    return false;
  }

  public int breakPenalty(BreakingCntx brCntx) {
    return INF_PENALTY;
  }

  public void contributeVisible(VisibleSummarizer summarizer) {
    summarizer.add(
        (summarizer.addingLeftX()) ? getLeftX().plus(getWidth()) : getWidth(), allegedlyVisible());
  }

  protected boolean allegedlyVisible() {
    return false;
  }

  public boolean kernAfterCanBeSpared() {
    return false;
  }

  public boolean isKernThatCanBeSpared() {
    return false;
  }

  public Node trailingKernSpared() {
    return this;
  }

  public Node reboxedToWidth(Dimen width) {
    return HBoxNode.reboxedToWidth(this, width);
  }

  public boolean startsWordBlock() {
    return false;
  }

  public byte beforeWord() {
    return FAILURE;
  }

  public boolean canBePartOfWord() {
    return false;
  }

  public FontMetric uniformMetric() {
    return FontMetric.NULL;
  }

  public Language alteringLanguage() {
    return Language.NULL;
  }

  public void contributeCharCodes(Name.Buffer buf) {}

  public byte afterWord() {
    return FAILURE;
  }

  public boolean rightBoundary() {
    return false;
  }

  public boolean providesRebuilder(boolean prev) {
    return false;
  }

  public WordRebuilder makeRebuilder(TreatNode proc, boolean prev) {
    return WordRebuilder.NULL;
  }
}
