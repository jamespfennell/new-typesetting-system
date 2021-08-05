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
// Filename: nts/node/AnyShiftNode.java
// $Id: AnyShiftNode.java,v 1.1.1.1 2000/06/06 11:52:29 ksk Exp $
package	nts.node;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.Name;
import	nts.io.CntxLog;

public abstract class	AnyShiftNode	extends BaseNode {
    /* corresponding to ANY_node */

    protected final Node		node;
    protected final Dimen		shift;

    public AnyShiftNode(Node node, Dimen shift)
	{ this.node = node; this.shift = shift; }

    public Dimen	getShift() { return shift; }

    public abstract Dimen	getHeight();
    public abstract Dimen	getDepth();
    public abstract Dimen	getWidth();
    public abstract Dimen	getLeftX();

    public Dimen	getHstr() { return node.getHstr(); }
    public byte		getHstrOrd() { return node.getHstrOrd(); }
    public Dimen	getHshr() { return node.getHshr(); }
    public byte		getHshrOrd() { return node.getHshrOrd(); }

    public Dimen	getWstr() { return node.getWstr(); }
    public byte		getWstrOrd() { return node.getWstrOrd(); }
    public Dimen	getWshr() { return node.getWshr(); }
    public byte		getWshrOrd() { return node.getWshrOrd(); }

    public boolean	sizeIgnored() { return node.sizeIgnored(); }
    public boolean	isPenalty() { return node.isPenalty(); }
    public boolean	isKern() { return node.isKern(); }
    public boolean	hasKern() { return node.hasKern(); }
    public boolean	isSkip() { return node.isSkip(); }
    public boolean	isMuSkip() { return node.isMuSkip(); }
    public boolean	isBox() { return node.isBox(); }
    public boolean	isMigrating() { return node.isMigrating(); }

    public Num		getPenalty() { return node.getPenalty(); }
    public Dimen	getKern() { return node.getKern(); }
    public Glue		getSkip() { return node.getSkip(); }
    public Glue		getMuSkip() { return node.getMuSkip(); }
    public Box		getBox() { return node.getBox(); }
    public Dimen	getItalCorr() { return node.getItalCorr(); }
    public NodeEnum	getMigration() { return node.getMigration(); }

    public abstract Dimen	getHeight(GlueSetting setting);
    public abstract Dimen	getDepth(GlueSetting setting);
    public abstract Dimen	getWidth(GlueSetting setting);
    public abstract Dimen	getLeftX(GlueSetting setting);

    public abstract void	typeSet(TypeSetter setter,
    					SettingContext sctx);

    public FontMetric	addShortlyOn(Log log, FontMetric metric)
	{ return node.addShortlyOn(log, metric); }

    public void		addOn(Log log, CntxLog cntx)
	{ node.addOn(log, cntx, shift); }

    public void		addOn(Log log, CntxLog cntx, Dimen shift)
	{ node.addOn(log, cntx, shift.plus(shift)); }

    public void		syncVertIfBox(TypeSetter setter)
	{ node.syncVertIfBox(setter); }

    public void		addBreakDescOn(Log log) { node.addBreakDescOn(log); }
    public boolean	discardable() { return node.discardable(); }
    public boolean	isKernBreak() { return node.isKernBreak(); }

    public boolean	canPrecedeSkipBreak()
	{ return node.canPrecedeSkipBreak(); }

    public boolean	canFollowKernBreak()
	{ return node.canFollowKernBreak(); }

    public int		breakPenalty(BreakingCntx brCntx)
	{ return node.breakPenalty(brCntx); }

    public NodeEnum	atBreakReplacement()
	{ return node.atBreakReplacement(); }

    public boolean	canBePartOfDiscretionary()
	{ return node.canBePartOfDiscretionary(); }

    public void		contributeVisible(VisibleSummarizer summarizer)
	{ node.contributeVisible(summarizer); }

    public boolean	startsWordBlock() { return node.startsWordBlock(); }
    public byte		beforeWord() { return node.beforeWord(); }
    public boolean	canBePartOfWord() { return node.canBePartOfWord(); }
    public FontMetric	uniformMetric() { return node.uniformMetric(); }
    public Language	alteringLanguage() { return node.alteringLanguage(); }
    public byte		afterWord() { return node.afterWord(); }
    public boolean	rightBoundary() { return node.rightBoundary(); }

    public void		contributeCharCodes(Name.Buffer buf)
	{ node.contributeCharCodes(buf); }

    public boolean	providesRebuilder(boolean prev)
	{ return node.providesRebuilder(prev); }

    public WordRebuilder	makeRebuilder(TreatNode proc, boolean prev)
	{ return node.makeRebuilder(proc, prev); }

}
