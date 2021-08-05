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
// Filename: nts/math/MKernPrim.java
// $Id: MKernPrim.java,v 1.1.1.1 2000/03/02 14:31:21 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.noad.MKernNoad;
import	nts.command.Token;

public class	MKernPrim	extends MathPrim {

    private final Dimen		kern;

    public MKernPrim(String name, Dimen kern)
	{ super(name); this.kern = kern; }

    public MKernPrim(String name)
	{ super(name); this.kern = Dimen.NULL; }

    public Dimen		getKern()
	{ return (kern != Dimen.NULL) ? kern : scanMuDimen(); }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1057,1061] */
    public final MathAction		NORMAL = new MathAction() {
	public void	exec(MathBuilder bld, Token src)
	    { bld.addNoad(new MKernNoad(getKern())); }
    };

}
