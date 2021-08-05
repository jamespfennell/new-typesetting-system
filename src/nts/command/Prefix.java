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
// Filename: nts/command/Prefix.java
// $Id: Prefix.java,v 1.1.1.1 2001/03/22 13:43:20 ksk Exp $
package	nts.command;

/**
 * Prefix Prim which can prefix definition or assignment
 * with LONG, OUTER or GLOBAL.
 */
public class	Prefix	extends PrefixPrim {

    /** Built in prefixes (LONG, OUTER, GLOBAL) */
    private final int			prefix;

    /** Creates and registers the prefix primitive */
    public Prefix(String name, int prefix)
        { super(name); this.prefix = prefix; }

    public void		exec(Token src, int prefixes) {
    	Token		tok = nextExpToken();
	meaningOf(tok).execute(tok, prefixes | prefix);
    }

    public void		doAssignment(Token src, int prefixes) {
    	Token		tok = nextExpToken();
	meaningOf(tok).doAssignment(tok, prefixes | prefix);
    }

}
