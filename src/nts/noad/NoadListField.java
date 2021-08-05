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
// Filename: nts/noad/NoadListField.java
// $Id: NoadListField.java,v 1.1.1.1 2000/10/11 21:15:04 ksk Exp $
package	nts.noad;

import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.HBoxNode;

public class	NoadListField	extends Field {

    private final NoadList	list;

    public NoadListField(NoadList list) { this.list = list; }

    /* TeXtp[692] */
    public void		addOn(Log log, CntxLog cntx, char p)
	{ cntx.addOn(log, list.noads(), p); }

    public Noad			ordinaryNoad() {
	if (list.length() == 1) {
	    Noad	noad = list.noadAt(0);
	    if (noad.isOrdinary()) return noad;
	}
	return Noad.NULL;
    }

    public Node		convertedBy(Converter conv)
	{ return HBoxNode.packedOf(conv.convert(list.noads())); }

    /* TeXtp[720] */
    public Node		cleanBox(Converter conv, byte how) {
	NodeList	nodeList = conv.convert(list.noads(), how);
	if (nodeList.length() == 1) {
	    Node	node = nodeList.nodeAt(0);
	    if (node.isCleanBox()) return node;
	}
	return HBoxNode.packedOf(nodeList).trailingKernSpared();
    }

}
