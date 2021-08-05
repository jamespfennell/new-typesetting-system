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
// Filename: nts/typo/PrevDepthPrim.java
// $Id: PrevDepthPrim.java,v 1.1.1.1 1999/08/03 13:08:05 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.builder.Builder;
import	nts.command.Token;

public class	PrevDepthPrim	extends TypoAssignPrim
				implements Dimen.Provider {

    public PrevDepthPrim(String name) { super(name); }

    /* STRANGE
     * \global\prevdepth is allowed but has no effect
     */
    /* TeXtp[1243] */
    protected void	assign(Token src, boolean glob) {
        Builder		bld = getBld();
	if (bld.getPrevDepth() != Dimen.NULL)
	    { skipOptEquals(); bld.setPrevDepth(scanDimen()); }
	else illegalCase(bld);
    }

    public boolean	hasCrazyValue() {
	if (getBld().getPrevDepth() != Dimen.NULL) return false;
	else { error("ImproperSForPD", this); return true; }
    }

    public boolean	hasDimenValue() { return true; }

    /* TeXtp[418] */
    public Dimen	getDimenValue() {
	Dimen		val = getBld().getPrevDepth();
	if (val != Dimen.NULL) return val;
	else { error("ImproperSForPD", this); return Dimen.ZERO; }
    }

}
