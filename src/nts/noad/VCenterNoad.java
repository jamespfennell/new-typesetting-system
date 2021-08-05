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
// Filename: nts/noad/VCenterNoad.java
// $Id: VCenterNoad.java,v 1.1.1.1 2001/03/22 15:44:37 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.Node;
import	nts.node.Box;
import	nts.node.BoxSizes;
import	nts.node.HShiftNode;

public class	VCenterNoad	extends ScriptableNoad {

    protected final NodeField		nucleus;

    public VCenterNoad(NodeField nucleus) { this.nucleus = nucleus; }

    /* TeXtp[696] */
    public void		addOn(Log log, CntxLog cntx) {
	log.addEsc(getDesc());
	nucleus.addOn(log, cntx, '.');
    }

    protected String	getDesc() { return "vcenter"; }

    public boolean	isJustChar() { return nucleus.isJustChar(); }
    //XXX would it be true (for script drops) even if the result of nucleus
    //XXX conversion was really just char?

    /* STRANGE
     * why \vcenter lays about box sizes instead of simply shifting it?
     */
    /* TeXtp[736] */
    public Egg		convert(Converter conv) {
	Node		node = nucleus.getNode();
	Dimen		middle = conv.getDimPar(DP_AXIS_HEIGHT);
	if (node.isBox()) {
	    Box		box = node.getBox();
	    BoxSizes	sizes = box.getSizes();
	    Dimen	shift = delta(sizes.getHeight(),
				      sizes.getDepth(), middle);
	    node = box.pretendSizesCopy(sizes.shiftedUp(shift));
	} else {
	    Dimen	shift = delta(node.getHeight(),
				      node.getDepth(), middle);
	    node = HShiftNode.shiftingUp(node, shift);
	}
	return new StItalNodeEgg(node, SPACING_TYPE_ORD);
    }

    private Dimen	delta(Dimen height, Dimen depth, Dimen middle) {
	return depth.minus(height).halved().plus(middle);
	// Dimen		total = height.plus(depth);
	// return total.halved().plus(middle).minus(height);
    }

}
