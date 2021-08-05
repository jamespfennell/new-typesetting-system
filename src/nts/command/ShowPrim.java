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
// Filename: nts/command/ShowPrim.java
// $Id: ShowPrim.java,v 1.1.1.1 1999/08/03 12:26:09 ksk Exp $
package	nts.command;

import	nts.base.BoolPar;

public class	ShowPrim extends ShowingPrim {

    public ShowPrim(String name) { super(name); }

    protected void		performShow() {
	BoolPar		exp = new BoolPar();
	Token		tok = nextRawToken(exp);
	normLog.add("> ");
	if (tok.definable()) normLog.add(tok).add('=');
	meaningOf(tok, exp.get()).addExpandable(normLog, true);
    }

}
