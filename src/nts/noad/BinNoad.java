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
// Filename: nts/noad/BinNoad.java
// $Id: BinNoad.java,v 1.1.1.1 2000/10/04 21:24:29 ksk Exp $
package	nts.noad;

import	nts.node.Node;

public class	BinNoad	extends WordPartNoad {

    public BinNoad(Field nucleus) { super(nucleus); }

    protected Egg	makeEgg(Node node)
	{ return new BinItalNodeEgg(node); }

    protected byte	spacingType() { return SPACING_TYPE_BIN; }
    protected String	getDesc() { return "mathbin"; }
    public boolean	canPrecedeBin() { return false; }

}
