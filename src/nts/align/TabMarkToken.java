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
// Filename: nts/align/TabMarkToken.java
// $Id: TabMarkToken.java,v 1.1.1.1 2000/08/06 20:21:14 ksk Exp $
package	nts.align;

import	nts.io.CharCode;
import	nts.command.Token;
import	nts.command.CharToken;
import	nts.command.Command;

public class	TabMarkToken	extends CharToken {

    public static final CharCode	CODE = makeCharCode('&');
    public static final TabMarkToken	TOKEN = new TabMarkToken(CODE);

    public static final Maker		MAKER = new Maker() {
        public Token	make(CharCode code)
	    { return new TabMarkToken(code); }
    };

    public TabMarkToken(CharCode code) { super(code); }

    public boolean	match(CharToken tok)
	{ return (tok instanceof TabMarkToken && tok.match(code)); }

    public Maker	getMaker() { return MAKER; }

    public String	toString() { return "<TabMark: " + code + '>'; }

    private static Command		command;
    public static void		setCommand(Command cmd) { command = cmd; }

    //XXX keep only one instance
    public Command	meaning() {
	return new Meaning() {

	    public boolean	isTabMark() { return true; }
	    public void		exec(Token src) { command.exec(src); }
	    public boolean	explosive() { return command.explosive(); }
	    public void		detonate(Token src) { command.detonate(src); }

	    protected String	description()
		{ return "alignment tab character"; }

	};
    }

}
