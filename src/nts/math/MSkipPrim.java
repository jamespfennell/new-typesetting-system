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
// Filename: nts/math/MSkipPrim.java
// $Id: MSkipPrim.java,v 1.1.1.1 2000/03/02 14:10:35 ksk Exp $
package	nts.math;

import	nts.base.Glue;
import	nts.noad.MSkipNoad;
import	nts.builder.Builder;
import	nts.command.Token;

public class	MSkipPrim	extends MathPrim {

    private final Glue		skip;

    public MSkipPrim(String name, Glue skip)
	{ super(name); this.skip = skip; }

    public MSkipPrim(String name)
	{ super(name); this.skip = Glue.NULL; }

    public Glue		getSkip()
	{ return (skip != Glue.NULL) ? skip : scanMuGlue(); }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1057,1060] */
    public final MathAction		NORMAL = new MathAction() {

	public void	exec(MathBuilder bld, Token src)
	    { bld.addNoad(new MSkipNoad(getSkip())); }

	public Glue	getSkipForLeaders(Builder bld) { return getSkip(); }

    };

}
