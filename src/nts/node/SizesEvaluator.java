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
// Filename: nts/node/SizesEvaluator.java
// $Id: SizesEvaluator.java,v 1.1.1.1 2000/10/25 07:16:30 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.base.Glue;

public class	SizesEvaluator	extends SizesSummarizer {

    public static final int	OK = 0;
    public static final int	UNDERFULL = 1;
    public static final int	TIGHT = 2;
    public static final int	OVERFULL = 3;

    protected int		badness;
    protected int		report;
    protected Dimen		overfull;
    protected GlueSetting	setting;

    public int		getBadness() { return badness; }
    public int		getReport() { return report; }
    public Dimen	getOverfull() { return overfull; }
    public GlueSetting	getSetting() { return setting; }

    /* TeXtp[668] */
    public void		evaluate(Dimen excess, boolean empty) {
	badness = 0; report = OK; overfull = Dimen.ZERO;
	byte		sign = GlueSetting.RIGID;
	byte		order = Glue.NORMAL;
	double		ratio = 0.0;
	if (!excess.isZero()) {
	    byte		trySign;
	    Dimen		total;
	    if (excess.moreThan(0)) {
		trySign = GlueSetting.STRETCHING;
		order = maxTotalStr();
		total = getTotalStr(order);
	    } else {
		trySign = GlueSetting.SHRINKING;
		order = maxTotalShr();
		total = getTotalShr(order);
	        excess = excess.negative();
	    }
	    if (!total.isZero())
		{ ratio = excess.doubleOver(total); sign = trySign; }
	    if (!empty && order == Glue.NORMAL) {
		if (trySign == GlueSetting.STRETCHING)
		    { report = UNDERFULL; badness = excess.badness(total); }
		else if (excess.moreThan(total)) {
		    report = OVERFULL; badness = Dimen.OVERFULL_BAD;
		    ratio = 1.0; overfull = excess.minus(total);
		} else { report = TIGHT; badness = excess.badness(total); }
	    }
	}
	setting = new GlueSetting(sign, order, ratio);
    }

}
