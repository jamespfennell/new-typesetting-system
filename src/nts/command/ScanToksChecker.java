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
// Filename: nts/command/ScanToksChecker.java
// $Id: ScanToksChecker.java,v 1.1.1.1 1999/05/31 11:18:51 ksk Exp $
package	nts.command;

import	nts.base.BoolPar;
import	nts.io.Loggable;
import	nts.io.MaxLoggable;

public class	ScanToksChecker	extends BaseToksChecker {

    protected final String		desc;
    protected final MaxLoggable		list;
    protected final Token		insTok;

    public ScanToksChecker(String tokErr, String eofErr,
    			   String desc, MaxLoggable list,
			   Loggable src, Token ins) {
	super(tokErr, eofErr, src);
	this.desc = desc; this.list = list;
	insTok = ins;
    }

    public ScanToksChecker(String tokErr, String eofErr,
    			   String desc, MaxLoggable list, Loggable src) {
	super(tokErr, eofErr, src);
	this.desc = desc; this.list = list;
	insTok = RightBraceToken.TOKEN;
    }

    protected void	tryToFix()
	{ Command.insertTokenWithoutCleaning(insTok); }

    protected void	reportRunAway()
	{ Command.runAway(desc, list); }

}
