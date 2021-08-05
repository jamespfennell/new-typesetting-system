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
// Filename: nts/command/InsertedToken.java
// $Id: InsertedToken.java,v 1.1.1.1 2000/08/04 14:46:25 ksk Exp $
package	nts.command;

import	nts.base.BoolPar;
import	nts.io.Log;

/**
 * |Tokenizer| for a single |Token|.
 */
 /* See TeXtp[325]. */
public class	InsertedToken	extends Tokenizer {

    /** The |Token| */
    private final Token		token;

    private boolean		readed = false;

    private final String	desc;

    /**
     * Creates |Tokenizer| for a single |Token|.
     * @param	tok the single |Token|
     * @param	desc desc of inserted |Token| for diagnostic
     */
    public InsertedToken(Token tok, String desc)
	{ token = tok; this.desc = desc; }

    /**
     * Gives the single |Token| on the first call.
     * @param	canExpand boolean output parameter querying whether the
     *			  acquired |Token| can be expanded (e.g. was not
     *			  preceded by \noexpand).
     * @return	the single |Token| or |Token.NULL| on next calls.
     */
    public Token	nextToken(BoolPar canExpand) {
        canExpand.set(true);
	if (readed) return Token.NULL;
	readed = true; return token;
    }

    public boolean	finishedList() { return readed; }

    public int		show(ContextDisplay disp, boolean force, int lines) {
	Log	where = (readed) ? disp.left() : disp.right();
	disp.normal().startLine().add(desc);
	token.addProperlyOn(where); disp.show();
	return 1;
    }

}
