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
// Filename: nts/node/Node.java
// $Id: Node.java,v 1.1.1.1 2000/06/06 11:51:31 ksk Exp $
package	nts.node;

import	java.io.Serializable;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.Name;
import	nts.io.CntxLog;
import	nts.io.CntxLoggable;
import	nts.command.TokenList;	//DDD

public interface	Node	extends Serializable, CntxLoggable {
    /* corresponding to ANY_node */

    Node	NULL = null;
    int		INF_PENALTY = Dimen.INF_BAD;
    int		EJECT_PENALTY = -INF_PENALTY;

    public interface	Provider { Node	getNodeValue(); }
    //CCC jikes requires public here

    Dimen		getHeight();
    Dimen		getDepth();
    Dimen		getHstr();
    byte		getHstrOrd();
    Dimen		getHshr();
    byte		getHshrOrd();

    Dimen		getWidth();
    Dimen		getLeftX();
    Dimen		getWstr();
    byte		getWstrOrd();
    Dimen		getWshr();
    byte		getWshrOrd();

    boolean		sizeIgnored();
    boolean		isPenalty();
    boolean		isKern();
    boolean		hasKern();		/* STRANGE : only for [424] */
    boolean		isSkip();
    boolean		isMuSkip();
    boolean		isBox();
    boolean		isCleanBox();		/* STRANGE : only for [720] */
    boolean		isMigrating();
    boolean		isMark();
    boolean		isInsertion();

    Num			getPenalty();
    Dimen		getKern();
    Glue		getSkip();
    Glue		getMuSkip();
    Box			getBox();
    Dimen		getItalCorr();
    NodeEnum		getMigration();
    TokenList		getMark();
    Insertion		getInsertion();

    void		typeSet(TypeSetter setter, SettingContext sctx);
    FontMetric		addShortlyOn(Log log, FontMetric metric);
    void		addOn(Log log, CntxLog cntx);
    void		addOn(Log log, CntxLog cntx, Dimen shift);
    void		syncVertIfBox(TypeSetter setter);

    Dimen		getHeight(GlueSetting setting);
    Dimen		getDepth(GlueSetting setting);
    Dimen		getWidth(GlueSetting setting);
    Dimen		getLeftX(GlueSetting setting);

    void		addBreakDescOn(Log log);
    boolean		discardable();
    boolean		isKernBreak();
    boolean		canPrecedeSkipBreak();
    boolean		canFollowKernBreak();
    boolean		allowsSpaceBreaking();
    boolean		forbidsSpaceBreaking();
    int			breakPenalty(BreakingCntx brCntx);
    boolean		isHyphenBreak();
    Dimen		preBreakWidth();
    Dimen		postBreakWidth();
    NodeEnum		atBreakReplacement();
    NodeEnum		postBreakNodes();
    boolean		discardsAfter();
    boolean		canBePartOfDiscretionary();

    void		contributeVisible(VisibleSummarizer summarizer);
    boolean		kernAfterCanBeSpared();
    boolean		isKernThatCanBeSpared();
    Node		trailingKernSpared();
    Node		reboxedToWidth(Dimen width);

    byte	FAILURE = 0;
    byte	SKIP = 1;
    byte	SUCCESS = 2;

    boolean		startsWordBlock();
    byte		beforeWord();
    boolean		canBePartOfWord();
    FontMetric		uniformMetric();
    Language		alteringLanguage();
    void		contributeCharCodes(Name.Buffer buf);
    byte		afterWord();

    boolean		rightBoundary();
    boolean		providesRebuilder(boolean prev);
    WordRebuilder	makeRebuilder(TreatNode proc, boolean prev);

}
