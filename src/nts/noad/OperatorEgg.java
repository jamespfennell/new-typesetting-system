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
// Filename: nts/noad/OperatorEgg.java
// $Id: OperatorEgg.java,v 1.1.1.1 2000/10/16 11:03:27 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.node.Node;
import	nts.node.Box;
import	nts.node.HBoxNode;
import	nts.node.HShiftNode;

public class	OperatorEgg	extends Egg {

    protected final Node	node;
    protected final Dimen	shift;
    protected final byte	spType;

    /* STRANGE
     * node might be null only if the metric or char was not available.
     * In that case it becomes empty hbox like in case of epmty field but
     * that hbox is surprisingly shifted -- see TeXtp[749].
     * It is the only reason for all tests (node == Node.NULL).
     */

    public OperatorEgg(Node node, Dimen shift, byte spType)
	{ this.node = node; this.shift = shift; this.spType = spType; }

    private boolean	suppressed = false;

    public void		suppressItalCorr() { suppressed = true; }

    public Dimen	getItalCorr() {
	return (suppressed || node == Node.NULL) ? Dimen.NULL
						 : node.getItalCorr();
    }

    public byte		spacingType() { return spType; }

    public Dimen	getHeight() {
	return (node == Node.NULL) ? Dimen.ZERO
	     : node.getHeight().max(Dimen.ZERO).minus(shift);
    }

    public Dimen	getDepth() {
	return (node == Node.NULL) ? Dimen.ZERO
	     : node.getDepth().max(Dimen.ZERO).plus(shift);
    }

    public void		chipShell(Nodery nodery) {
	Box		box = (node == Node.NULL) ? HBoxNode.EMPTY
						  : HBoxNode.packedOf(node);
	Dimen		ital = getItalCorr();
	if (ital != Dimen.NULL)
	    box = box.pretendingWidth(box.getWidth().plus(ital));
	nodery.append(HShiftNode.shiftingDown(box, shift));
    }

    public boolean	isPenalty()
	{ return (node != Node.NULL && node.isPenalty()); }

}
