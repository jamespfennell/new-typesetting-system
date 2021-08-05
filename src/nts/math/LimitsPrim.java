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
// Filename: nts/math/LimitsPrim.java
// $Id: LimitsPrim.java,v 1.1.1.1 2000/03/09 16:57:37 ksk Exp $
package	nts.math;

import	nts.noad.Noad;
import	nts.command.Token;

public class	LimitsPrim	extends MathPrim {

    private /* final */ byte		limits;

    public	LimitsPrim(String name, byte limits)
	{ super(name); this.limits = limits; }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1159] */
    public final MathAction	NORMAL = new MathAction() {
	public void		exec(final MathBuilder bld, Token src) {
	    Noad	noad = bld.lastNoad();
	    if (noad != Noad.NULL && noad.acceptsLimits())
		bld.replaceLastNoad(noad.withLimits(limits));
	    else error("MisplacedLimits");
	}
    };

}
