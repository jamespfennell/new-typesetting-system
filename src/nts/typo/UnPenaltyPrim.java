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
// Filename: nts/typo/UnPenaltyPrim.java
// $Id: UnPenaltyPrim.java,v 1.1.1.1 2000/01/10 21:55:13 ksk Exp $
package	nts.typo;

import	nts.node.Node;
import	nts.builder.Builder;
import	nts.command.Token;

public class	UnPenaltyPrim	extends BuilderPrim {

    public UnPenaltyPrim(String name) { super(name); }

    /* TeXtp[1105,1106] */
    public void		exec(Builder bld, Token src) {
        if (bld.canTakeLastNode()) {
	    Node	node = bld.lastNode();
	    if (node != Node.NULL && node.isPenalty())
		bld.removeLastNode();
	} else error("CantDeleteLastPenalty", this, bld);
	/* STRANGE
	 * See UnKernPrim.exec().
	 */
    }

}
