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
// Filename: nts/command/RightBrace.java
// $Id: RightBrace.java,v 1.1.1.1 2000/06/22 19:09:46 ksk Exp $
package	nts.command;

import	nts.io.Log;

public class	RightBrace	extends GroupCommand {

    public void		exec(Group grp, Token src) { popLevel(); }

    public void		addOn(Log log) { log.add("internal end group"); }

    public static final Closing		EXTRA = new Closing() {
        /* TeXtp[1069] */
	public void	exec(Group grp, Token src) {
	    error("ExtraOrForgotten", grp.expectedToken());
	    adjustBraceNesting(1);
	}
    };

}
