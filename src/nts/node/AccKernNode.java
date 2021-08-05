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
// Filename: nts/node/AccKernNode.java
// $Id: AccKernNode.java,v 1.1.1.1 2000/06/06 08:15:20 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;

public class	AccKernNode	extends IntHKernNode {
    /* corresponding to kern_node */

    public AccKernNode(Dimen kern) { super(kern); }

    public void		addOn(Log log, CntxLog cntx)
	{ log.addEsc("kern ").add(kern.toString()).add(" (for accent)"); }

    public String	toString() { return "AccKern(" + kern + ')'; }

}
