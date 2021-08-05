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
// Filename: nts/noad/OverNoad.java
// $Id: OverNoad.java,v 1.1.1.1 2000/04/13 09:41:20 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.node.Node;

public class	OverNoad	extends NucleusNoad {

    public OverNoad(Field nucleus) { super(nucleus); }

    protected String	getDesc() { return "overline"; }
    protected byte	spacingType() { return SPACING_TYPE_ORD; }

    /* TeXtp[734] */
    public Egg		convert(Converter conv) {
	Node		node = nucleus.cleanBox(conv, CRAMPED);
	Dimen		dim = conv.getDimPar(DP_DEFAULT_RULE_THICKNESS);
	return new StSimpleNodeEgg(makeOverBar(node, dim.times(3), dim, dim),
				   spacingType());
    }

    public boolean	isJustChar() { return false; }

}
