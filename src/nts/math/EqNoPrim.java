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
// Filename: nts/math/EqNoPrim.java
// $Id: EqNoPrim.java,v 1.1.1.1 2001/03/13 05:28:04 ksk Exp $
package	nts.math;

import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.Command;
import	nts.command.Group;
import	nts.command.Closing;

public class	EqNoPrim	extends AdaptMathPrim {

    private /* final */ TokenList.Inserter	every;
    private /* final */ boolean			left;

    public EqNoPrim(String name, Command command,
		    TokenList.Inserter every, boolean left)
	{ super(name, command); this.every= every; this.left = left; }

    protected class	DisplayedClosing	extends Closing {
	
	/* TeXtp[1142] */
	public void		exec(Group grp, Token src) {
	    pushLevel(new EqNoGroup(left));
	    MathPrim.getMathConfig().initFormula();
	    every.insertToks();
	}

    }

    public Closing	makeDisplayedClosing()
	{ return new DisplayedClosing(); }

}
