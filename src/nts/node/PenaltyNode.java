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
// Filename: nts/node/PenaltyNode.java
// $Id: PenaltyNode.java,v 1.1.1.1 2000/06/06 08:27:53 ksk Exp $
package	nts.node;

import	nts.base.Num;
import	nts.io.Log;
import	nts.io.CntxLog;

public class	PenaltyNode	extends DiscardableNode {
    /* root corresponding to penalty_node */

    protected final Num		pen;

    public PenaltyNode(Num pen) { this.pen = pen; }

    public Num		getPenalty() { return pen; }

    public boolean	sizeIgnored() { return true; }
    public boolean	isPenalty() { return true; }

    public void		addOn(Log log, CntxLog cntx)
	{ log.addEsc("penalty ").add(pen.toString()); }

    public void		addBreakDescOn(Log log) { log.addEsc("penalty"); }

    public int		breakPenalty(BreakingCntx brCntx)
	{ return pen.intVal(); }

    public byte		afterWord() { return SUCCESS; }

    public String	toString() { return "Penalty(" + pen + ')'; }

}
