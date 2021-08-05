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
// Filename: nts/noad/VoidField.java
// $Id: VoidField.java,v 1.1.1.1 2000/10/12 11:06:49 ksk Exp $
package	nts.noad;

import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.io.CntxLoggable;
import	nts.node.Node;
import	nts.node.HBoxNode;

/* replaces sub_mlist with info(p) = null [681] */
public class	VoidField	extends Field
				implements CntxLoggable {

    public static final VoidField	FIELD = new VoidField();

    /* TeXtp[692] */
    public void		addOn(Log log, CntxLog cntx) { log.add("{}"); }

    public void		addOn(Log log, CntxLog cntx, char p)
	{ cntx.addOn(log, this, p); }

    public Node		convertedBy(Converter conv)
	{ return HBoxNode.EMPTY; }

    /* TeXtp[720] */
    public Node		cleanBox(Converter conv, byte how)
	{ return HBoxNode.EMPTY; }

}
