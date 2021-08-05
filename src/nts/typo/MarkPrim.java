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
// Filename: nts/typo/MarkPrim.java
// $Id: MarkPrim.java,v 1.1.1.1 2000/05/26 21:25:01 ksk Exp $
package	nts.typo;

import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.MigratingNode;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.Prim;

public class	MarkPrim	extends BuilderPrim {

    public	MarkPrim(String name) { super(name); }

    /* TeXtp[1101] */
    public void		exec(Builder bld, Token src)
	{ bld.addNode(new MarkNode(Prim.scanTokenList(src, true))); }

    protected static class	MarkNode	extends MigratingNode {
	/* root corresponding to mark_node */

	protected TokenList		list;

	public MarkNode(TokenList list) { this.list = list; }

	public boolean		isMark() { return true; }

	public TokenList	getMark() { return list; }

	/* TeXtp[196] */
	public void		addOn(Log log, CntxLog cntx)
	    { addNodeToks(log.addEsc("mark"), list); }

    }

}
