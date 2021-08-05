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
// Filename: nts/command/BackedTokenList.java
// $Id: BackedTokenList.java,v 1.1.1.1 2000/08/04 14:46:36 ksk Exp $
package	nts.command;

public class	BackedTokenList	extends TokenListTokenizer {

    public BackedTokenList(TokenList list) { super(list); }

    public int	show(ContextDisplay disp, boolean force, int lines) {
        String		desc;
        if (finishedList())
	    { if (!force) return 0; desc = "<recently read> "; }
	else  desc = "<to be read again> ";
	disp.normal().startLine().add(desc);
	return showList(disp, lines);
    }

}
