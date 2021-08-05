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
// Filename: nts/typo/TypoAssignPrim.java
// $Id: TypoAssignPrim.java,v 1.1.1.1 2001/09/09 22:17:59 ksk Exp $
package	nts.typo;

import	nts.command.Token;
import	nts.command.PrefixPrim;

public abstract class	TypoAssignPrim	extends TypoPrim {

    protected	TypoAssignPrim(String name) { super(name); }

    /** Non prefixed version of exec */
    public final void		exec(Token src) { exec(src, 0); }

    public final boolean	assignable() { return true; }
    public void			doAssignment(Token src, int prefixes)
				    { exec(src, prefixes); }

    /**
     * Performs itself in the process of interpretation of the macro language
     * after sequence of prefix commands.
     * @param	src source token for diagnostic output.
     * @param	prefixes accumulated code of prefixes.
     */
    public final void		exec(Token src, int prefixes) {
	PrefixPrim.beforeAssignment(this, prefixes);
	assign(src, PrefixPrim.globalAssignment(prefixes));
	PrefixPrim.afterAssignment();
    }

    /**
     * Performs the assignment.
     * @param	src source token for diagnostic output.
     * @param	glob indication that the assignment is global.
     */
    protected abstract void	assign(Token src, boolean glob);

}
