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
// Filename: nts/math/DisplayGroup.java
// $Id: DisplayGroup.java,v 1.1.1.1 2001/03/22 12:11:04 ksk Exp $
package	nts.math;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.BoolPar;
import	nts.builder.Builder;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.NodeEnum;
import	nts.node.HBoxNode;
import	nts.node.VShiftNode;
import	nts.node.ChrKernNode;
import	nts.node.SizesEvaluator;
import	nts.node.HorizIterator;
import	nts.node.GlueSetting;
import	nts.node.BoxSizes;
import	nts.noad.Noad;
import	nts.noad.ConvStyle;
import	nts.noad.Conversion;
import	nts.builder.ParBuilder;
import	nts.command.Token;
import	nts.typo.TypoCommand;

public class	DisplayGroup	extends FormulaGroup {

    public DisplayGroup(DisplayBuilder builder)
	{ super((MathBuilder) builder); }

    public DisplayGroup() { super(new DisplayBuilder(currLineNumber())); }

    public static final int	INTP_PRE_DISPLAY_PENALTY = newIntParam();
    public static final int	INTP_POST_DISPLAY_PENALTY = newIntParam();
    public static final int	DIMP_PRE_DISPLAY_SIZE = newDimParam();
    public static final int	DIMP_DISPLAY_WIDTH = newDimParam();
    public static final int	DIMP_DISPLAY_INDENT = newDimParam();
    public static final int	GLUEP_ABOVE_DISPLAY_SKIP = newGlueParam();
    public static final int	GLUEP_BELOW_DISPLAY_SKIP = newGlueParam();
    public static final int	GLUEP_ABOVE_DISPLAY_SHORT_SKIP = newGlueParam();
    public static final int	GLUEP_BELOW_DISPLAY_SHORT_SKIP = newGlueParam();

    /* TeXtp[1194,1199] */
    public void		stop() {
	Builder.pop();
	Builder		bld = TypoCommand.getBld();
	Config		cfg = getConfig();
	boolean		success = necessaryParamsDefined();
	Node		eqNoBox = ((DisplayBuilder) builder).getEqNoBox();
	boolean		eqNoLeft = ((DisplayBuilder) builder).getEqNoLeft();
	if (eqNoBox == Node.NULL)
	    { eqNoLeft = false; expectAnotherMathShift(); }
	NodeList	list = NodeList.EMPTY;
	Dimen		eqNoWidth = Dimen.ZERO;
	Dimen		eqNoAdd = Dimen.ZERO;
	boolean		sideBySide = false;
	if (success) {
	    ConvStyle	style = new FormulaStyle(Noad.DISPLAY_STYLE, false);
	    list = Conversion.madeOf(builder.getList().noads(), style);
	    if (eqNoBox != Node.NULL) {
		eqNoWidth = eqNoBox.getWidth().plus(eqNoBox.getLeftX());
		if (!eqNoWidth.isZero()) {
		    eqNoAdd = style.getDimPar(ConvStyle.DP_MATH_QUAD)
			    .plus(eqNoWidth);
		    sideBySide = true;
		}
	    }
	}
	NodeEnum	mig = (bld.wantsMigrations())
			    ? list.extractedMigrations().nodes()
			    : NodeList.EMPTY_ENUM;
	Dimen		lastVis = cfg.getDimParam(DIMP_PRE_DISPLAY_SIZE);
	Dimen		width = cfg.getDimParam(DIMP_DISPLAY_WIDTH);
	Dimen		indent = cfg.getDimParam(DIMP_DISPLAY_INDENT);
	BoolPar		parBroken = new BoolPar();
	HBoxNode	box = packer.packHBox(list, eqNoAdd, width, parBroken);
	sideBySide = (sideBySide && !parBroken.get());
	Dimen		boxWidth = box.getWidth().plus(box.getLeftX());
	Dimen		delta = width.minus(boxWidth).halved();
	if (sideBySide && delta.lessThan(eqNoWidth.times(2)))
	    delta = (!list.isEmpty() && list.nodeAt(0).isSkip()) ? Dimen.ZERO
		  : width.minus(boxWidth).minus(eqNoWidth).halved();
	bld.addPenalty(Num.valueOf(
			cfg.getIntParam(INTP_PRE_DISPLAY_PENALTY)));
	Node		kernBetween = (sideBySide)
			    ? new ChrKernNode(width.minus(boxWidth)
					      .minus(eqNoWidth).minus(delta))
				    : Node.NULL;
	int		belowSkipParam;
	if (eqNoLeft) {
	    belowSkipParam = GLUEP_BELOW_DISPLAY_SKIP;
	    if (sideBySide) {
		int	aboveSkipParam = GLUEP_ABOVE_DISPLAY_SKIP;
		bld.addSkip(cfg.getGlueParam(aboveSkipParam),
			    cfg.getGlueName(aboveSkipParam));
		NodeList	together = new NodeList(3);
		together.append(eqNoBox).append(kernBetween).append(box);
		box = HBoxNode.packedOf(together);
		appendBox(bld, box, indent);
	    } else {
		appendBox(bld, eqNoBox, indent);
		bld.addPenalty(Num.valueOf(Node.INF_PENALTY));
		appendBox(bld, box, indent.plus(delta));
	    }
	} else {
	    boolean	biggerSkips = !delta.plus(indent).moreThan(lastVis);
	    belowSkipParam = (biggerSkips) ? GLUEP_BELOW_DISPLAY_SKIP
					   : GLUEP_BELOW_DISPLAY_SHORT_SKIP;
	    int		aboveSkipParam = (biggerSkips)
				       ? GLUEP_ABOVE_DISPLAY_SKIP
				       : GLUEP_ABOVE_DISPLAY_SHORT_SKIP;
	    bld.addSkip(cfg.getGlueParam(aboveSkipParam),
			cfg.getGlueName(aboveSkipParam));
	    if (sideBySide) {
		NodeList	together = new NodeList(3);
		together.append(box).append(kernBetween).append(eqNoBox);
		box = HBoxNode.packedOf(together);
		appendBox(bld, box, indent.plus(delta));
	    } else {
		appendBox(bld, box, indent.plus(delta));
		if (eqNoBox != Node.NULL) {
		    bld.addPenalty(Num.valueOf(Node.INF_PENALTY));
		    appendBox(bld, eqNoBox,
			      indent.plus(width).minus(eqNoWidth));
		    belowSkipParam = -1;
		}
	    }
	}
	bld.addNodes(mig);
	bld.addPenalty(Num.valueOf(
			cfg.getIntParam(INTP_POST_DISPLAY_PENALTY)));
	if (belowSkipParam != -1)
	    bld.addSkip(cfg.getGlueParam(belowSkipParam),
			cfg.getGlueName(belowSkipParam));
    }

    private static void		appendBox(Builder bld, Node node, Dimen shift) {
	TypoCommand.appendBox(bld,
	    VShiftNode.shiftingRight(node, shift.plus(node.getLeftX())),
			      false);
    }

    public void		close() { resumeAfterDisplay(); }

    /* TeXtp[1200] */
    public static void		resumeAfterDisplay() {
	Builder		bld = Builder.top();
	bld.setPrevGraf(bld.getPrevGraf() + 3);
	Builder		par = new ParBuilder(currLineNumber(),
				TypoCommand.getTypoConfig().getLanguage());
	Builder.push(par);
	skipOptExpSpacer();
	bld.buildPage();
    }

    private static final DisplayPacker	packer = new DisplayPacker();

    public static class	DisplayPacker	extends TypoCommand.HBoxPacker {

	public HBoxNode		packHBox(NodeList list, Dimen additional,
					 Dimen desired, BoolPar broken) {
	    BoolPar.set(broken, false);
	    SizesEvaluator	pack = new SizesEvaluator();
	    HorizIterator.summarize(list.nodes(), pack);
	    Dimen	height = pack.getWidth();
	    Dimen	width = pack.getBody().plus(pack.getDepth());
	    Dimen	depth = pack.getLeftX();
	    Dimen	leftX = pack.getHeight();
	    desired = desired.minus(leftX);
	    Dimen	attempt = width.min(desired.minus(additional));
	    pack.evaluate(attempt.minus(width), list.isEmpty());
	    if (  pack.getReport() == SizesEvaluator.OVERFULL
	       && !additional.isZero()) {
		BoolPar.set(broken, true);
		attempt = width.min(desired);
		pack.evaluate(attempt.minus(width), list.isEmpty());
	    }
	    BoxSizes	sizes = new BoxSizes(height, attempt, depth, leftX);
	    HBoxNode	hbox =  new HBoxNode(sizes, pack.getSetting(), list);
	    if (pack.getReport() == SizesEvaluator.OVERFULL)
		addOverfullRule(list, pack.getOverfull());
	    if (check(pack)) reportBox(hbox);
	    return hbox;
	}

    }

}
