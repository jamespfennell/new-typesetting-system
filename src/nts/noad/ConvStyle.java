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
// Filename: nts/noad/ConvStyle.java
// $Id: ConvStyle.java,v 1.1.1.1 2001/03/22 07:02:19 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.base.Num;
import	nts.io.CharCode;
import	nts.node.Box;
import	nts.node.Node;
import	nts.node.FontMetric;
import	nts.node.TreatNode;
import	nts.node.MathWordBuilder;

public abstract class	ConvStyle	implements TransfConstants {

    protected final byte		style;
    protected final boolean		cramped;
    protected final Dimen		currMu;

    /* TeXtp[702] */
    protected ConvStyle(byte style, boolean cramped) {
	this.style = style; this.cramped = cramped; setupFontSize();
	currMu = getDimPar(DP_MATH_QUAD).over(18);	//XXX config
    }

    protected abstract void		setupFontSize();
    protected abstract FontMetric	fetchFontMetric(byte fam);

    public byte		getStyle() { return style; }
    public boolean	isCramped() { return cramped; }

    public Dimen	muToPt(Dimen dim) { return dim.times(currMu); }

    public Glue		muToPt(Glue skip)
	{ return skip.timesTheFinite(currMu); }

    public abstract Node	fetchCharNode(byte fam, CharCode code);
    public abstract Node	fetchLargerNode(byte fam, CharCode code);
    public abstract Node	fetchSufficientNode(Delimiter delimiter,
						    Dimen desired);
    public abstract Box		fetchFittingWidthBox(byte fam, CharCode code,
						     Dimen desired);
    public abstract Dimen	skewAmount(byte fam, CharCode code);
    public abstract Dimen	getXHeight(byte fam);
    public abstract Dimen	delimiterSize(Dimen height, Dimen depth);
    public abstract Dimen	scriptSpace();
    public abstract Node	getSpacing(byte left, byte right);
    public abstract Num		getPenalty(byte left, byte right);
    public abstract boolean	isScript();
    public abstract ConvStyle	makeNew(byte style);

    public abstract boolean		forcedItalCorr(byte fam);
    public abstract MathWordBuilder	getWordBuilder(byte fam,
						       TreatNode proc);

    /* TeXtp[702] */
    public ConvStyle		derived(byte how) {
	byte		s = style;
	boolean		c = cramped;
	switch (how) {
	    case CRAMPED:	c = true;		break;
	    case SUB_SCRIPT:	c = true;		/* fall */
	    case SUPER_SCRIPT:	switch (style) {
		case Noad.DISPLAY_STYLE:
		case Noad.TEXT_STYLE:	s = Noad.SCRIPT_STYLE;		break;
		default:		s = Noad.SCRIPT_SCRIPT_STYLE;	break;
	    };						break;
	    case DENOMINATOR:	c = true;		/* fall */
	    case NUMERATOR:		switch (style) {
		case Noad.DISPLAY_STYLE:	s = Noad.TEXT_STYLE;	break;
		case Noad.TEXT_STYLE:	s = Noad.SCRIPT_STYLE;		break;
		default:		s = Noad.SCRIPT_SCRIPT_STYLE;	break;
	    };						break;
	}
	return deriveNew(s, c);
    }

    protected abstract ConvStyle	deriveNew(byte style, boolean cramped);

    private static final byte[]		dimFamilies;
    private static final int[]		dimFontPars;

    public Dimen	getDimPar(int param) {
	return fetchFontMetric(dimFamilies[param])
		    .getDimenParam(dimFontPars[param]);
    }

    public static final byte	SYMBOL_FAMILY = 2;	//XXX config
    public static final byte	EXTENSION_FAMILY = 3;	//XXX config

    private static void		initSymbolPar(int param, int fontPar) {
	dimFamilies[param] = SYMBOL_FAMILY;
	dimFontPars[param] = fontPar;
    }

    private static void		initExtensionPar(int param, int fontPar) {
	dimFamilies[param] = EXTENSION_FAMILY;
	dimFontPars[param] = fontPar;
    }

    static {

	dimFamilies = new byte[NUMBER_OF_DIM_PARS];
	dimFontPars = new int[NUMBER_OF_DIM_PARS];

	initSymbolPar(DP_MATH_X_HEIGHT,
			FontMetric.DIMEN_PARAM_MATH_X_HEIGHT);
	initSymbolPar(DP_MATH_QUAD,
			FontMetric.DIMEN_PARAM_MATH_QUAD);
	initSymbolPar(DP_NUM1,
			FontMetric.DIMEN_PARAM_NUM1);
	initSymbolPar(DP_NUM2,
			FontMetric.DIMEN_PARAM_NUM2);
	initSymbolPar(DP_NUM3,
			FontMetric.DIMEN_PARAM_NUM3);
	initSymbolPar(DP_DENOM1,
			FontMetric.DIMEN_PARAM_DENOM1);
	initSymbolPar(DP_DENOM2,
			FontMetric.DIMEN_PARAM_DENOM2);
	initSymbolPar(DP_SUP1,
			FontMetric.DIMEN_PARAM_SUP1);
	initSymbolPar(DP_SUP2,
			FontMetric.DIMEN_PARAM_SUP2);
	initSymbolPar(DP_SUP3,
			FontMetric.DIMEN_PARAM_SUP3);
	initSymbolPar(DP_SUB1,
			FontMetric.DIMEN_PARAM_SUB1);
	initSymbolPar(DP_SUB2,
			FontMetric.DIMEN_PARAM_SUB2);
	initSymbolPar(DP_SUP_DROP,
			FontMetric.DIMEN_PARAM_SUP_DROP);
	initSymbolPar(DP_SUB_DROP,
			FontMetric.DIMEN_PARAM_SUB_DROP);
	initSymbolPar(DP_DELIM1,
			FontMetric.DIMEN_PARAM_DELIM1);
	initSymbolPar(DP_DELIM2,
			FontMetric.DIMEN_PARAM_DELIM2);
	initSymbolPar(DP_AXIS_HEIGHT,
			FontMetric.DIMEN_PARAM_AXIS_HEIGHT);
	initExtensionPar(DP_DEFAULT_RULE_THICKNESS,
			    FontMetric.DIMEN_PARAM_DEFAULT_RULE_THICKNESS);
	initExtensionPar(DP_BIG_OP_SPACING1,
			    FontMetric.DIMEN_PARAM_BIG_OP_SPACING1);
	initExtensionPar(DP_BIG_OP_SPACING2,
			    FontMetric.DIMEN_PARAM_BIG_OP_SPACING2);
	initExtensionPar(DP_BIG_OP_SPACING3,
			    FontMetric.DIMEN_PARAM_BIG_OP_SPACING3);
	initExtensionPar(DP_BIG_OP_SPACING4,
			    FontMetric.DIMEN_PARAM_BIG_OP_SPACING4);
	initExtensionPar(DP_BIG_OP_SPACING5,
			    FontMetric.DIMEN_PARAM_BIG_OP_SPACING5);

    }

}
