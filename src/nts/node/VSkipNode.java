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
// Filename: nts/node/VSkipNode.java
// $Id: VSkipNode.java,v 1.1.1.1 2001/03/06 14:54:55 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;

public class	VSkipNode	extends AnySkipNode {
    /* corresponding to glue_node */

    public VSkipNode(Glue skip) { super(skip); }

    public Dimen	getHeight() { return skip.getDimen(); }
    public Dimen	getHstr() { return skip.getStretch(); }
    public byte		getHstrOrd() { return skip.getStrOrder(); }
    public Dimen	getHshr() { return skip.getShrink(); }
    public byte		getHshrOrd() { return skip.getShrOrder(); }

    public Dimen	getHeight(GlueSetting setting)
	{ return setting.set(skip, true); }

    public String	toString() { return "VSkip(" + skip + ')'; }

}
