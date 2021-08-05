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
// Filename: nts/typo/LastSkipPrim.java
// $Id: LastSkipPrim.java,v 1.1.1.1 2000/01/10 17:23:57 ksk Exp $
package	nts.typo;

import	nts.base.Glue;
import	nts.node.Node;
import	nts.command.Token;

public class	LastSkipPrim	extends BuilderPrim
				implements Glue.Provider {

    public LastSkipPrim(String name) { super(name); }

    public boolean	hasGlueValue() { return !hasMuGlueValue(); }

    public boolean	hasMuGlueValue() {
        Node	node = getBld().lastSpecialNode();
	return (node != Node.NULL && node.isMuSkip());
    }

    /* TeXtp[424] */
    public Glue		getGlueValue() {
        Node	node = getBld().lastSpecialNode();
	return (node != Node.NULL && node.isSkip())
	     ? node.getSkip() : Glue.ZERO;
    }

    /* TeXtp[424] */
    public Glue		getMuGlueValue() {
        Node	node = getBld().lastSpecialNode();
	return (node != Node.NULL && node.isMuSkip())
	     ? node.getMuSkip() : Glue.ZERO;
    }

}
