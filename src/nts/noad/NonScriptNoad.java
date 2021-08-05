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
// Filename: nts/noad/NonScriptNoad.java
// $Id: NonScriptNoad.java,v 1.1.1.1 2000/05/26 21:14:42 ksk Exp $
package	nts.noad;

import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.Node;
import	nts.node.HSkipNode;

public class	NonScriptNoad	extends BaseNodeNoad {

    public Egg		convert(Converter conv)
	{ return new NonScriptEgg(getNode()); }

    public void		addOn(Log log, CntxLog cntx)
	{ log.addEsc("glue").add('(').addEsc("nonscript").add(')'); }

    public Node		getNode() { return new NonScriptNode(); }

    protected static class NonScriptNode	extends HSkipNode {
	/* corresponding to glue_node */

	public NonScriptNode() { super(Glue.ZERO); }
	public void		addOn(Log log, CntxLog cntx)
	    { log.addEsc("glue").add('(').addEsc("nonscript").add(')'); }

    }

}
