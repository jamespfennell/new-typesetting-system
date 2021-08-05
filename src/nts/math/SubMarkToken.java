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
// Filename: nts/math/SubMarkToken.java
// $Id: SubMarkToken.java,v 1.1.1.1 2000/03/03 18:23:01 ksk Exp $
package	nts.math;

import	nts.io.CharCode;
import	nts.command.Token;
import	nts.command.CharToken;
import	nts.command.Command;

public class	SubMarkToken	extends CharToken {

    public static final CharCode	CODE = makeCharCode('_');
    public static final SubMarkToken	TOKEN = new SubMarkToken(CODE);

    public static final Maker		MAKER = new Maker() {
        public Token	make(CharCode code)
	    { return new SubMarkToken(code); }
    };

    public SubMarkToken(CharCode code) { super(code); }

    public boolean	match(CharToken tok)
	{ return (tok instanceof SubMarkToken && tok.match(code)); }

    public Maker	getMaker() { return MAKER; }

    public String	toString() { return "<SubScript: " + code + '>'; }

    private static Command		command;
    public static void		setCommand(Command cmd) { command = cmd; }

    public Command	meaning() {
	return new Meaning() {

	    public void		exec(Token src) { command.exec(src); }

	    protected String	description()
		{ return "subscript character"; }

	};
    }

}
