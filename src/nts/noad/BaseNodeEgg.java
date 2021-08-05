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
// Filename: nts/noad/BaseNodeEgg.java
// $Id: BaseNodeEgg.java,v 1.1.1.1 2000/08/13 01:22:36 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.node.Node;

public abstract class	BaseNodeEgg	extends Egg {

    protected final Node	node;

    public BaseNodeEgg(Node node) { this.node = node; }
    
    public Dimen	getHeight()
	{ return (node != Node.NULL) ? node.getHeight() : Dimen.ZERO; }

    public Dimen	getDepth()
	{ return (node != Node.NULL) ? node.getDepth() : Dimen.ZERO; }

    public void		chipShell(Nodery nodery) {
	if (  node != Node.NULL
	   && !(  (node.isSkip() || node.isKern())
	       && nodery.ignoresSpace()  )  )
	    nodery.append(node);
	/* There could by a special SpaceEgg which knows that the node is skip
	 * or kern, it is probably known when the NodeNoad containing the node
	 * is constructed. The BaseNodeEgg then need not care. */
    }

    public boolean	isPenalty()
	{ return (node != Node.NULL && node.isPenalty()); }

}
