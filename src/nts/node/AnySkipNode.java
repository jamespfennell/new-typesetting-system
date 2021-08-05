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
// Filename: nts/node/AnySkipNode.java
// $Id: AnySkipNode.java,v 1.1.1.1 2000/05/26 21:20:53 ksk Exp $
package	nts.node;

import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;

public abstract class	AnySkipNode	extends DiscardableNode {
    /* root corresponding to glue_node */

    protected final Glue	skip;

    public AnySkipNode(Glue skip) { this.skip = skip; }

    public Glue		getSkip() { return skip; }

    public boolean	isSkip() { return true; }

    public void		addOn(Log log, CntxLog cntx) {
	log.addEsc("glue");
	String	name = getName();
	if (name != null)
	    log.add('(').addEsc(name).add(')');
	log.add(' ').add(skip.toString());
    }

    public FontMetric	addShortlyOn(Log log, FontMetric metric)
	{ if (!skip.isZero()) log.add(' '); return metric; }

    public String	getName() { return null; }
    public boolean	canFollowKernBreak() { return true; }
    public NodeEnum	atBreakReplacement() { return NodeList.EMPTY_ENUM; }

    public int		breakPenalty(BreakingCntx brCntx) {
	return (brCntx.spaceBreaking() && brCntx.allowedAtSkip())
	     ? 0 : INF_PENALTY;
    }

}
