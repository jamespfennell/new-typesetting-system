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
// Filename: nts/typo/BoxDimenPrim.java
// $Id: BoxDimenPrim.java,v 1.1.1.1 1999/08/02 12:09:24 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.BoxSizes;
import	nts.node.Box;
import	nts.command.Token;
import	nts.command.AssignPrim;

public abstract class	BoxDimenPrim	extends AssignPrim
					implements Dimen.Provider {

    protected final SetBoxPrim		reg;

    public BoxDimenPrim(String name, SetBoxPrim reg)
	{ super(name); this.reg = reg; }

    /* STRANGE
     * \global\hd is allowed but has no effect
     */
    /* TeXtp[1247] */
    protected void	assign(Token src, boolean glob) {
	int		idx = scanRegisterCode();
        Box		box = reg.get(idx);
	skipOptEquals();
	Dimen		dimen = scanDimen();
	BoxSizes	sizes = box.getSizes();
	if (sizes != BoxSizes.NULL)
	    reg.foist(idx, box.pretendSizesCopy(changeSizes(sizes, dimen)));
    }

    public boolean	hasDimenValue() { return true; }

    /* TeXtp[420] */
    public Dimen	getDimenValue() {
	BoxSizes	sizes = reg.get(scanRegisterCode()).getSizes();
	return (sizes != BoxSizes.NULL) ? selectSize(sizes) : Dimen.ZERO;
    }

    protected abstract BoxSizes	changeSizes(BoxSizes sizes, Dimen dimen);
    protected abstract Dimen	selectSize(BoxSizes sizes);

}
