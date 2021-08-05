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
// Filename: nts/math/FormulaGroup.java
// $Id: FormulaGroup.java,v 1.1.1.1 2001/03/13 05:05:39 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.base.Num;
import	nts.io.CharCode;
import	nts.builder.Builder;
import	nts.node.Box;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.HBoxNode;
import	nts.node.FontMetric;
import	nts.node.NamedHSkipNode;
import	nts.node.GlueSetting;
import	nts.node.BoxSizes;
import	nts.node.TreatNode;
import	nts.node.MathWordBuilder;
import	nts.noad.Noad;
import	nts.noad.MathOnNode;
import	nts.noad.MathOffNode;
import	nts.noad.Conversion;
import	nts.noad.ConvStyle;
import	nts.noad.Delimiter;
import	nts.command.Token;
import	nts.command.SimpleGroup;
import	nts.typo.TypoCommand;

public class	FormulaGroup	extends SimpleGroup {

    protected /* final */ MathBuilder		builder;

    protected FormulaGroup(MathBuilder builder) { this.builder = builder; }

    public FormulaGroup(FormulaBuilder builder)
	{ this((MathBuilder) builder); }

    public FormulaGroup() { this(new FormulaBuilder(currLineNumber())); }

    public void		start() { Builder.push(builder); }

    public static final int	DIMP_MATH_SURROUND = newDimParam();

    /* TeXtp[1194,1196] */
    public void		stop() {
	Builder.pop();
	Dimen		surr = getConfig().getDimParam(DIMP_MATH_SURROUND);
	Builder		bld = TypoCommand.getBld();
	bld.addNode(new MathOnNode(surr));
	if (necessaryParamsDefined())
	    bld.addNodes(Conversion.madeOf(builder.getList().noads(),
			new FormulaStyle(Noad.TEXT_STYLE, bld.willBeBroken()))
				   .nodes());
	bld.addNode(new MathOffNode(surr));
	bld.resetSpaceFactor();
    }

    public Token	expectedToken() { return MathShiftToken.TOKEN; }

    /* TeXtp[1197] */
    public static boolean	expectAnotherMathShift() {
	Token		tok = nextExpToken();
	if (!meaningOf(tok).isMathShift())
	    { backToken(tok); error("BadFormulaEnd"); return false; }
	return true;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final int	INTP_DELIMITER_FACTOR = newIntParam();
    public static final int	DIMP_NULL_DELIMITER_SPACE = newDimParam();
    public static final int	DIMP_DELIMITER_SHORTFALL = newDimParam();
    public static final int	DIMP_SCRIPT_SPACE = newDimParam();

    public static final byte	TEXT_SIZE = 0;
    public static final byte	SCRIPT_SIZE = 1;
    public static final byte	SCRIPT_SCRIPT_SIZE = 2;
    public static final byte	NUMBER_OF_FONT_SIZES = 3;

    private static boolean	isScriptSize(byte size)
	{ return (size >= SCRIPT_SIZE); }

    /* TeXtp[1195] */
    public static boolean	necessaryParamsDefined() {
	if (!necessaryParamsDefined(ConvStyle.SYMBOL_FAMILY,
			FontMetric.ALL_MATH_SYMBOL_DIMEN_PARAMS))
	    { error("InsufSymFonts"); return false; }
	if (!necessaryParamsDefined(ConvStyle.EXTENSION_FAMILY,
			FontMetric.ALL_MATH_EXTENSION_DIMEN_PARAMS))
	    { error("InsufExtFonts"); return false; }
	return true;
    }

    public static boolean	necessaryParamsDefined(byte fam, int[] idxs) {
	for (byte s = TEXT_SIZE; s < NUMBER_OF_FONT_SIZES; s++)
	    if (!MathPrim.getMathConfig().familyFont(s, fam)
			    .definesDimenParams(idxs)) return false;
	return true;
    }

    protected static class	FormulaStyle	extends ConvStyle {

	protected byte			fontSize;
	protected final boolean		penalties;

	protected FormulaStyle(byte style, boolean cramped, boolean penalties)
	    { super(style, cramped); this.penalties = penalties; }

	public FormulaStyle(byte style, boolean penalties)
	    { this(style, false, penalties); }

	protected void		setupFontSize() {
	    switch (style) {
		case Noad.SCRIPT_SCRIPT_STYLE:
		    fontSize = SCRIPT_SCRIPT_SIZE;		break;
		case Noad.SCRIPT_STYLE:
		    fontSize = SCRIPT_SIZE;			break;
		default:		
		    fontSize = TEXT_SIZE;			break;
	    }
	}

	protected FontMetric	fetchFontMetric(byte fam)
	    { return MathPrim.getMathConfig().familyFont(fontSize, fam); }

	/* TeXtp[723] */
	private void		metricError(byte fam, CharCode code) {
	    error("UndefFamily",
		  esc(MathPrim.getMathConfig().familyName(fontSize, fam)),
		  num(fam), code);
	}

	/* TeXtp[722] */
	public Node		fetchCharNode(byte fam, CharCode code) {
	    FontMetric	metric = fetchFontMetric(fam);
	    if (metric.isNull()) { metricError(fam, code); return Node.NULL; }
	    Node	node = metric.getCharNode(code);
	    if (node == Node.NULL) MathPrim.charWarning(metric, code);
	    return node;
	}

	public Node		fetchLargerNode(byte fam, CharCode code) {
	    FontMetric	metric = fetchFontMetric(fam);
	    if (metric.isNull()) { metricError(fam, code); return Node.NULL; }
	    Node	node = metric.getLargerNode(code);
	    if (node == Node.NULL) MathPrim.charWarning(metric, code);
	    return node;
	}

	public Box		fetchFittingWidthBox(byte fam, CharCode code,
						      Dimen desired) {
	    FontMetric	metric = fetchFontMetric(fam);
	    if (metric.isNull()) { metricError(fam, code); return Box.NULL; }
	    Box		box = metric.getFittingWidthBox(code, desired);
	    if (box == Box.NULL) MathPrim.charWarning(metric, code);
	    return box;
	}

	/* STRANGE
	 * why is there no single char warning?
	 */
	/* TeXtp[706,707] */
	public Node		fetchSufficientNode(Delimiter delimiter,
						    Dimen desired) {
	    Dimen		maxSize = Dimen.ZERO;
	    Node		maxNode = Node.NULL;
	    boolean		large = false;
	    byte		fam = delimiter.getSmallFam();
	    CharCode		code = delimiter.getSmallCode();
	ProbingBothSizes:
	    for (;;) {
		if (code != CharCode.NULL)
		    for (byte s = fontSize; s >= TEXT_SIZE; s--) {
			FontMetric		metric
			    = MathPrim.getMathConfig().familyFont(s, fam);
			Node		node
			    = metric.getSufficientNode(code, desired);
			if (node != Node.NULL) {
			    Dimen		size
				= node.getHeight().plus(node.getDepth());
			    if (size.moreThan(maxSize)) {
				maxSize = size; maxNode = node;
				if (!size.lessThan(desired))
				    break ProbingBothSizes;
			    }
			}
		    }
		if (large) break;
		large = true;
		fam = delimiter.getLargeFam();
		code = delimiter.getLargeCode();
	    }
	    return (maxNode != Node.NULL) ? maxNode
		 : new HBoxNode(new BoxSizes(Dimen.ZERO,
			getConfig().getDimParam(DIMP_NULL_DELIMITER_SPACE),
					     Dimen.ZERO, Dimen.ZERO),
				GlueSetting.NATURAL, NodeList.EMPTY);
	}

	public Dimen		getXHeight(byte fam) {
	    return fetchFontMetric(fam)
			.getDimenParam(FontMetric.DIMEN_PARAM_X_HEIGHT);
	}

	/* TeXtp[741] */
	public Dimen		skewAmount(byte fam, CharCode code) {
	    FontMetric		metric = fetchFontMetric(fam);
	    Num		skewCharNum
		= metric.getNumParam(FontMetric.NUM_PARAM_SKEW_CHAR);
	    if (skewCharNum != Num.NULL) {
		CharCode	skewCode
		    = Token.makeCharCode(skewCharNum.intVal());
		if (skewCode != CharCode.NULL) {
		    Dimen	skew = metric.getKernBetween(code, skewCode);
		    if (skew != Dimen.NULL) return skew;
		}
	    }
	    return Dimen.ZERO;
	}

	/* STRANGE
	 * why .over(x).times(y) instead of times(y, x) ?
	 */
	/* TeXtp[762] */
	public Dimen		delimiterSize(Dimen height, Dimen depth) {
	    Dimen	max = height.max(depth);
	    Dimen	size = max.over(500)
		    .times(getConfig().getIntParam(INTP_DELIMITER_FACTOR));
	    max = max.times(2)
		    .minus(getConfig().getDimParam(DIMP_DELIMITER_SHORTFALL));
	    return max.max(size);
	}

	public Dimen		scriptSpace()
	    { return getConfig().getDimParam(DIMP_SCRIPT_SPACE); }

	public Node		getSpacing(byte left, byte right) {
	    MathPrim.MathSpacer		spacer
		= MathPrim.getMathConfig().mathSpacing(left, right);
	    Glue	space = spacer.getGlue(fontSize);
	    return (space.isZero()) ? Node.NULL
		 : new NamedHSkipNode(muToPt(space), spacer.getName());
	}

	public Num		getPenalty(byte left, byte right) {
	    return (penalties)
	         ? MathPrim.getMathConfig().mathPenalty(left, right)
		 : Num.NULL;
	}

	public boolean		isScript() { return isScriptSize(fontSize); }

	public ConvStyle	makeNew(byte style)
	    { return new FormulaStyle(style, false, penalties); }

	protected ConvStyle	deriveNew(byte style, boolean cramped)
	    { return new FormulaStyle(style, cramped, false); }

	public MathWordBuilder	getWordBuilder(byte fam, TreatNode proc) {
	    FontMetric	metric = fetchFontMetric(fam);
	    return (metric.isNull()) ? MathWordBuilder.NULL
				     : metric.getMathWordBuilder(proc);
	}

	public boolean		forcedItalCorr(byte fam) {
	    return fetchFontMetric(fam)
			.getDimenParam(FontMetric.DIMEN_PARAM_SPACE).isZero();
	}

    }

}
