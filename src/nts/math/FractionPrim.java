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
// Filename: nts/math/FractionPrim.java
// $Id: FractionPrim.java,v 1.1.1.1 2000/03/09 16:56:16 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.noad.Delimiter;
import	nts.command.Token;

public class	FractionPrim	extends MathPrim {

    public static final byte	ZERO_THICKNESS = 0;
    public static final byte	DEFAULT_THICKNESS = 1;
    public static final byte	EXPLICIT_THICKNESS = 2;

    private /* final */ boolean	withDelims;
    private /* final */ byte		thickMode;

    public	FractionPrim(String name, boolean withDelims,
			     byte thickMode) {
	super(name);
	this.withDelims = withDelims;
	this.thickMode = thickMode;
    }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1181] */
    public final MathAction	NORMAL = new MathAction() {
	public void		exec(final MathBuilder bld, Token src) {
	    Delimiter		left = Delimiter.NULL;
	    Delimiter		right = Delimiter.NULL;
	    Dimen		thickness;
	    if (withDelims)
		{ left = scanDelimiter(); right = scanDelimiter(); }
	    switch (thickMode) {
		case EXPLICIT_THICKNESS:
		    thickness = scanDimen(); break;
		case ZERO_THICKNESS:
		    thickness = Dimen.ZERO; break;
		default:
		    thickness = Dimen.NULL; break;
	    }
	    if (bld.alreadyFractioned()) error("AmbiguousFraction");
	    else if (withDelims) bld.fractione(thickness, left, right);
	    else bld.fractione(thickness);
	}
    };

}
