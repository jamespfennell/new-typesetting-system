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
// Filename: nts/typo/PenaltyPrim.java
// $Id: PenaltyPrim.java,v 1.1.1.1 2000/04/29 13:09:59 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.builder.Builder;
import	nts.command.Token;

public class	PenaltyPrim	extends BuilderPrim {

    private final Num		pen;

    public PenaltyPrim(String name)
	{ super(name); pen = Num.NULL; }

    public PenaltyPrim(String name, Num pen)
	{ super(name); this.pen = pen; }

    public Num		getPenalty()
	{ return (pen != Num.NULL) ? pen : scanNum(); }

    /* TeXtp[1103] */
    public void		exec(Builder bld, Token src)
	{ bld.addPenalty(getPenalty()); bld.buildPage(); }

}
