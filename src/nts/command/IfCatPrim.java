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
// Filename: nts/command/IfCatPrim.java
// $Id: IfCatPrim.java,v 1.1.1.1 1999/05/31 11:18:47 ksk Exp $
package	nts.command;

public class	IfCatPrim	extends AnyIfPrim {

    public IfCatPrim(String name) { super(name); }

    protected final boolean	holds() {
	Token		first = getTok();
	Token		second = getTok();
	return (first != Token.NULL)
	     ? (second != Token.NULL && first.sameCatAs(second))
	     : (second == Token.NULL);
    }

    private Token		getTok() {
        Token		tok = nextExpToken();
	Command		cmd = meaningOf(tok);
	return (cmd.expandable()) ? tok.category() : cmd.origin();
    }

}
