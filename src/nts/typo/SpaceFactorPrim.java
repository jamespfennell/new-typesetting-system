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
// Filename: nts/typo/SpaceFactorPrim.java
// $Id: SpaceFactorPrim.java,v 1.1.1.1 1999/08/03 13:54:10 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.builder.Builder;
import	nts.command.Token;

public class	SpaceFactorPrim	extends TypoAssignPrim
				implements Num.Provider {

    public SpaceFactorPrim(String name) { super(name); }

    public static final int	MIN_SPACE_FACTOR = 1;
    public static final int	MAX_SPACE_FACTOR = 32767;

    /* STRANGE
     * \global\spacefactor is allowed but has no effect
     */
    /* TeXtp[1243] */
    protected void	assign(Token src, boolean glob) {
        Builder		bld = getBld();
	if (bld.getSpaceFactor() > 0) {
	    skipOptEquals();
	    int		sf = scanInt();
	    if (MIN_SPACE_FACTOR <= sf && sf <= MAX_SPACE_FACTOR)
		bld.setSpaceFactor(sf);
	    else error("BadSpaceFactor", num(sf),
		       num(MIN_SPACE_FACTOR), num(MAX_SPACE_FACTOR));
	}
	else illegalCase(bld);
    }

    public boolean	hasCrazyValue() {
	if (getBld().getSpaceFactor() > 0) return false;
	else { error("ImproperSForPD", this); return true; }
    }

    public boolean	hasDimenValue()
	{ return !(getBld().getSpaceFactor() > 0); }

    public Dimen	getDimenValue() {
	int		sf = getBld().getSpaceFactor();
	if (sf > 0) return Dimen.NULL;
	else { error("ImproperSForPD", this); return Dimen.ZERO; }
    }

    public boolean	hasNumValue() { return true; }

    /* TeXtp[418] */
    public Num	getNumValue() {
	int		sf = getBld().getSpaceFactor();
	if (sf > 0) return Num.valueOf(sf);
	else { error("ImproperSForPD", this); return Num.ZERO; }
    }

}
