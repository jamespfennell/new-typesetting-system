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
// Filename: nts/typo/FontDimenPrim.java
// $Id: FontDimenPrim.java,v 1.1.1.1 1999/08/27 15:33:51 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.FontMetric;
import	nts.command.Token;

public class	FontDimenPrim	extends TypoAssignPrim
				implements Dimen.Provider {

    public FontDimenPrim(String name) { super(name); }

    /* STRANGE
     * \global\fontdimen is allowed but has no effect
     */
    /* TeXtp[1253,578] */
    protected void		assign(Token src, boolean glob) {
        int		num = scanInt();
        FontMetric	metric = scanFontMetric();
	FontDimen	fDim = getTypoHandler().getFontDimen(metric, num);
	skipOptEquals(); fDim.set(scanDimen());
    }

    public boolean	hasDimenValue() { return true; }

    /* TeXtp[425,578] */
    public Dimen	getDimenValue() {
        int		num = scanInt();
        FontMetric	metric = scanFontMetric();
	return getTypoHandler().getFontDimen(metric, num).get();
    }

}
