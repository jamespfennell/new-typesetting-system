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
// Filename: nts/command/EndCondPrim.java
// $Id: EndCondPrim.java,v 1.1.1.1 2001/03/13 11:04:43 ksk Exp $
package	nts.command;

public class	EndCondPrim	extends CondPrim {

    private final int		endLev;

    public EndCondPrim(String name, int endLev)
	{ super(name); this.endLev = endLev; }

    /* TeXtp[510] */
    public void		expand(Token src) {
        int		limit = (noCond()) ? NOTHING : topCond().limit;
        if (conforms(endLev, limit)) {
	    if (endLev != FI) while (skipBranch() != FI);
	    popCond();
	} else {
	    if (limit == WAIT) { backToken(src); insertRelax(); }
	    else error("ExtraOrElseFi", exp(this));
	}
    }

    public int		endBranchLevel() { return endLev; }

}
