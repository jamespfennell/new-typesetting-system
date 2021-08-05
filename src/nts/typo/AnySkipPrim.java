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
// Filename: nts/typo/AnySkipPrim.java
// $Id: AnySkipPrim.java,v 1.1.1.1 2000/02/16 01:01:49 ksk Exp $
package	nts.typo;

import	nts.base.Glue;
import	nts.builder.Builder;
import	nts.command.Token;

public class	AnySkipPrim	extends BuilderPrim {

    private final Glue		skip;

    public AnySkipPrim(String name, Glue skip)
	{ super(name); this.skip = skip; }

    public AnySkipPrim(String name)
	{ super(name); this.skip = Glue.NULL; }

    public Glue		getSkip()
	{ return (skip != Glue.NULL) ? skip : scanGlue(); }

    /* TeXtp[1057,1060] */
    public final Action		NORMAL = new Action() {

	public void	exec(Builder bld, Token src)
	    { bld.addSkip(getSkip()); }

	public Glue	getSkipForLeaders(Builder bld) { return getSkip(); }

    };

}
