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
// Filename: nts/noad/FollowNodeEgg.java
// $Id: FollowNodeEgg.java,v 1.1.1.1 2000/04/11 00:21:01 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.node.Node;

public class	FollowNodeEgg	extends Egg {
 
    private final Egg		egg;
    private final Node		node;

    public FollowNodeEgg(Egg egg, Node node)
	{ this.egg = egg; this.node = node; }

    public Dimen	getHeight()
	{ return egg.getHeight().max(node.getHeight()); }

    public Dimen	getDepth()
	{ return egg.getDepth().max(node.getDepth()); }

    public void		chipShell(Nodery nodery)
	{ egg.chipShell(nodery); nodery.append(node); }

    public byte		spacingType() { return egg.spacingType(); }

}
