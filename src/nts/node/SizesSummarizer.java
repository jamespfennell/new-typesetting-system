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
// Filename: nts/node/SizesSummarizer.java
// $Id: SizesSummarizer.java,v 1.1.1.1 2000/08/09 08:28:31 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.base.Glue;

public class	SizesSummarizer {

    private Dimen	height;
    private Dimen	body;
    private Dimen	depth;
    private Dimen	width;
    private Dimen	leftX;
    private Dimen[]	totalStr = new Dimen[Glue.MAX_ORDER + 1];
    private Dimen[]	totalShr = new Dimen[Glue.MAX_ORDER + 1];

    public final Dimen	getHeight() { return height; }
    public final Dimen	getBody() { return body; }
    public final Dimen	getDepth() { return depth; }
    public final Dimen	getWidth() { return width; }
    public final Dimen	getLeftX() { return leftX; }
    public final Dimen	getTotalStr(byte ord) { return totalStr[ord]; }
    public final Dimen	getTotalShr(byte ord) { return totalShr[ord]; }
    public final byte	maxTotalStr() { return maxOrd(totalStr); }
    public final byte	maxTotalShr() { return maxOrd(totalShr); }

    public SizesSummarizer() {
	height = Dimen.ZERO; body = Dimen.ZERO; depth = Dimen.ZERO;
	width = Dimen.ZERO; leftX = Dimen.ZERO;
	setZero(totalStr); setZero(totalShr);
    }

    public final void	add(Dimen dim) { body = body.plus(dim); }
    public final void	setHeight(Dimen h) { add(height); height = h; }
    public final void	setDepth(Dimen d) { add(depth); depth = d; }
    public final void	setMaxWidth(Dimen w) { width = width.max(w); }
    public final void	setMaxLeftX(Dimen l) { leftX = leftX.max(l); }

    public final void	addStretch(Dimen dim, byte ord)
	{ addOrd(totalStr, dim, ord); }

    public final void	addShrink(Dimen dim, byte ord)
	{ addOrd(totalShr, dim, ord); }

    public final void	add(Glue glue) {
	body = body.plus(glue.getDimen());
	addStretch(glue.getStretch(), glue.getStrOrder());
	addShrink(glue.getShrink(), glue.getShrOrder());
    }

    public final void	restrictDepth(Dimen maxDepth) {
	if (depth.moreThan(maxDepth))
	    { body = body.plus(depth).minus(maxDepth); depth = maxDepth; }
    }

    private void	setZero(Dimen[] total)
	{ for (int i = total.length; i-- > 0; total[i] = Dimen.ZERO); }

    private void	addOrd(Dimen[] total, Dimen dim, byte ord)
	{ if (!dim.isZero()) total[ord] = total[ord].plus(dim); }

    private byte	maxOrd(Dimen[] total) {
        byte		ord = Glue.MAX_ORDER;
	while (ord > Glue.NORMAL && total[ord].isZero()) ord--;
	return ord;
    }

}
