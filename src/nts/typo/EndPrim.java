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
// Filename: nts/typo/EndPrim.java
// $Id: EndPrim.java,v 1.1.1.1 2000/01/28 15:57:33 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.node.BoxSizes;
import	nts.node.GlueSetting;
import	nts.node.NodeList;
import	nts.node.HBoxNode;
import	nts.node.VSkipNode;
import	nts.node.PenaltyNode;
import	nts.builder.Builder;
import	nts.command.Token;

public class	EndPrim	extends BuilderPrim {

    private boolean		dumping;

    public EndPrim(String name, boolean dumping)
	{ super(name); this.dumping = dumping; }

    public static final int	DIMP_HSIZE = newDimParam();

    /* TeXtp[1054] */
    public final Action		NORMAL = new Action() {

	public void	exec(Builder bld, Token src) {
	    if (bld.isEmpty() && !getTypoConfig().pendingOutput())
		endMainLoop(dumping);
	    else {
		backToken(src);
		bld.addBox(new HBoxNode(
		    new BoxSizes(Dimen.ZERO,
				 getConfig().getDimParam(DIMP_HSIZE),
				 Dimen.ZERO, Dimen.ZERO),
		    GlueSetting.NATURAL, NodeList.EMPTY));
		bld.addNode(new VSkipNode(
		    Glue.valueOf(Dimen.ZERO, Dimen.UNITY, Glue.FILL,
		    		 Dimen.ZERO, Glue.NORMAL)));
		bld.addNode(new PenaltyNode(Num.valueOf(-0x40000000)));
		bld.buildPage();
	     }
	}

    };

}
