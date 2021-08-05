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
// Filename: nts/math/VCenterPrim.java
// $Id: VCenterPrim.java,v 1.1.1.1 2000/08/03 14:27:44 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.node.Box;
import	nts.node.TreatBox;
import	nts.node.NodeEnum;
import	nts.noad.NodeField;
import	nts.noad.VCenterNoad;
import	nts.command.Token;
import	nts.command.TokenList;

public class	VCenterPrim	extends MathPrim {

    private final TokenList.Inserter	every;

    public VCenterPrim(String name, TokenList.Inserter every)
	{ super(name); this.every = every; }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1167] */
    public final MathAction	NORMAL = new MathAction() {
	public void		exec(final MathBuilder bld, Token src) {
	    makeBoxValue(new TreatBox() {
		public void		execute(Box box, NodeEnum mig)
		    { bld.addNoad(new VCenterNoad(new NodeField(box))); }
	    });
	}
    };

    /* TeXtp[645] */
    public void		makeBoxValue(TreatBox proc) {
	Dimen		size = Dimen.ZERO;
	boolean		exactly = false;
	if (scanKeyword("to")) { size = scanDimen(); exactly = true; }
	else if (scanKeyword("spread")) size = scanDimen();
	pushLevel(new VCenterGroup(size, exactly, proc));
	scanLeftBrace(); every.insertToks();
    }

}
