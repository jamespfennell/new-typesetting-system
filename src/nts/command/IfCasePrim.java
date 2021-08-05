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
// Filename: nts/command/IfCasePrim.java
// $Id: IfCasePrim.java,v 1.1.1.1 2001/03/13 10:25:49 ksk Exp $
package	nts.command;

public class	IfCasePrim	extends CondPrim {

    public IfCasePrim(String name) { super(name); }

    /* TeXtp[509] */
    public void		expand(Token src) {
	CondEntry	currEntry = pushCond(WAIT, this);
	int		num = scanInt();
	if (getConfig().getBoolParam(BOOLP_TRACING_ALL_COMMANDS))
	    diagLog.add("{case ").add(num).add('}').startLine();
	while (num != 0) {
	    int		endLev = skipBranch();
	    if (topCond() == currEntry) {	//SSS
		if (endLev == OR) --num;
		else {
		    if (endLev == FI) popCond();
		    else topCond().limit = FI;
		    return;
		}
	    } else if (endLev == FI) popCond();
	}
	currEntry.limit = OR;
    }

    public final boolean		isConditional() { return true; }

}
