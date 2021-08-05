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
// Filename: nts/command/MacroParamToken.java
// $Id: MacroParamToken.java,v 1.1.1.1 2000/02/16 11:01:01 ksk Exp $
package	nts.command;

import	nts.io.Log;
import	nts.io.CharCode;

public class	MacroParamToken extends CharToken {

    public static final CharCode	CODE = makeCharCode('#');
    public static final MacroParamToken	TOKEN = new MacroParamToken(CODE);
    public static final Maker		MAKER = new Maker() {
        public Token	make(CharCode code)
	    { return new MacroParamToken(code); }
    };

    public MacroParamToken(CharCode code) { super(code); }

    public boolean	matchMacroParam() { return true; }

    public void		addProperlyOn(Log log) { log.add(code).add(code); }

    public boolean	match(CharToken tok)
	{ return (tok instanceof MacroParamToken && tok.match(code)); }

    public Maker	getMaker() { return MAKER; }

    public String	toString() { return "<MacroParam: " + code + '>'; }

    public Command	meaning() {
	return new Meaning() {

	    public final boolean	isMacroParam() { return true; }

	    /* STRANGE
	     * why does it talk about curren mode?
	     */
	    public void		exec(Token src)
		{ illegalCommand(this); }

	    protected String	description()
		{ return "macro parameter character"; }

	};
    }

}
