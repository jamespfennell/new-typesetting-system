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
// Filename: nts/command/LetterToken.java
// $Id: LetterToken.java,v 1.1.1.1 2000/02/16 11:00:49 ksk Exp $
package	nts.command;

import	nts.io.CharCode;

public class	LetterToken extends NormalCharToken {

    public static final Maker		MAKER = new Maker() {
        public Token	make(CharCode code)
	    { return new LetterToken(code); }
    };

    public LetterToken(CharCode code) { super(code); }

    public boolean	matchLetter(char c) { return code.match(c); }

    /**
     * Gives the 7 bit ascii character code of this letter character |Token|.
     * The result is |CharCode.NO_CHAR| if its character code is not
     * a 7 bit ascii.
     * @return	the 7 bit ascii code if the code is defined,
     *		|CharCode.NO_CHAR| otherwise.
     */
    public char		letterChar() { return code.toChar(); }

    public boolean	match(CharToken tok)
	{ return (tok instanceof LetterToken && tok.match(code)); }

    public Maker	getMaker() { return MAKER; }

    public String	toString() { return "<Letter: " + code + '>'; }

    public Command	meaning() {
	return new Meaning() {
	    protected String	description() { return "the letter"; }
	};
    }

}
