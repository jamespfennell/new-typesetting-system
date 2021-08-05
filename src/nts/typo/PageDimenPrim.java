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
// Filename: nts/typo/PageDimenPrim.java
// $Id: PageDimenPrim.java,v 1.1.1.1 2000/01/13 16:00:09 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.PageSplit;
import	nts.command.Token;
import	nts.command.AssignPrim;

public abstract class	PageDimenPrim	extends AssignPrim
					implements Dimen.Provider {

    protected PageSplit		page;

    public PageDimenPrim(String name, PageSplit page)
	{ super(name); this.page = page; }

    /* STRANGE
     * \global\pagegoal is allowed but has no effect
     */
    /* TeXtp[1245] */
    protected void		assign(Token src, boolean glob) {
	skipOptEquals();
	Dimen		dim = scanDimen();
	if (page.canChangeDimens()) set(dim);
    }

    /* TeXtp[421] */
    public boolean		hasDimenValue() { return true; }
    public final Dimen		getDimenValue() { return get(); }

    protected abstract Dimen	get();
    protected abstract void	set(Dimen dim);

}
