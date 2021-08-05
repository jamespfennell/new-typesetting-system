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
// Filename: nts/typo/LeftBrace.java
// $Id: LeftBrace.java,v 1.1.1.1 2000/02/16 10:03:34 ksk Exp $
package	nts.typo;

import	nts.io.Log;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.SimpleGroup;

public class	LeftBrace	extends BuilderCommand {

    /* TeXtp[1063] */
    public void		exec(Builder bld, Token src) {
	//XXX incr(align_state)	[347] [357]
	pushLevel(new SimpleGroup());
    }

    public void		addOn(Log log) { log.add("internal begin group"); }

}
