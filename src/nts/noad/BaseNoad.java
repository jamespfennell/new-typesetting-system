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
// Filename: nts/noad/BaseNoad.java
// $Id: BaseNoad.java,v 1.1.1.1 2000/10/18 10:33:22 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.Box;
import	nts.node.HBoxNode;
import	nts.node.VBoxNode;
import	nts.node.HShiftNode;
import	nts.node.VShiftNode;
import	nts.node.IntVKernNode;
import	nts.node.TreatNode;
import	nts.node.MathWordBuilder;

public abstract class	BaseNoad	implements Noad {

    public boolean	acceptsLimits() { return false; }
    public boolean	isScriptable() { return false; }
    public boolean	isOrdinary() { return false; }
    public boolean	isJustChar() { return false; }
    public boolean	alreadySuperScripted() { return false; }
    public boolean	alreadySubScripted() { return false; }
    public boolean	canPrecedeBin() { return true; }
    public boolean	canFollowBin() { return true; }

    public Noad		withLimits(byte limits) { return this; }
    public Field	ordinaryField() { return Field.NULL; }

    /* TeXtp[696] */
    public void		addOnWithScripts(Log log, CntxLog cntx,
					 Field sup, Field sub) {
	addOn(log, cntx);
	sup.addOn(log, cntx, '^');
	sub.addOn(log, cntx, '_');
    }

    /* TeXtp[756] */
    public Egg		convertWithScripts(Converter conv,
					   Field sup, Field sub) {
	return makeScriptsTo(convert(conv), !isJustChar(), conv, sup, sub);
    }

    /* TeXtp[756] */
    protected static Egg	makeScriptsTo(Egg body, boolean notJustChar,
				    Converter conv, Field sup, Field sub) {
	Node		supNode = Node.NULL;
	Node		subNode = Node.NULL;
	Dimen		supShift = Dimen.ZERO;
	Dimen		subShift = Dimen.ZERO;
	Dimen		delta = Dimen.ZERO;
	if (!sup.isEmpty()) {
	    supNode = makeScriptBox(sup, conv, SUPER_SCRIPT);
	    if (notJustChar) {
		supShift = body.getHeight().max(Dimen.ZERO)
			   .minus(conv.getDimPar(DP_SUP_DROP, SUPER_SCRIPT));
	    }
	}
	if (!sub.isEmpty()) {
	    subNode = makeScriptBox(sub, conv, SUB_SCRIPT);
	    if (notJustChar) {
		subShift = body.getDepth().max(Dimen.ZERO)
			   .plus(conv.getDimPar(DP_SUB_DROP, SUB_SCRIPT));
	    }
	    Dimen	italCorr = body.getItalCorr();
	    if (italCorr != Dimen.NULL)
		{ delta = italCorr; body.suppressItalCorr(); }
	}
	Node		scripts;
	Dimen		xHeight = conv.getDimPar(DP_MATH_X_HEIGHT).absolute();
	if (supNode == Node.NULL) {
	    if (subNode == Node.NULL) return body;
	    subShift = subShift.max(conv.getDimPar(DP_SUB1));
	    subShift = subShift.max(subNode.getHeight()
				    .minus(xHeight.times(4, 5)));
	    scripts = HShiftNode.shiftingDown(subNode, subShift);
	} else {
	    supShift = supShift.max(conv.getDimPar(
			      (conv.isCramped()) ? DP_SUP3
			    : (conv.getStyle() == Noad.DISPLAY_STYLE)
			    ? DP_SUP1 : DP_SUP2));
	    supShift = supShift.max(supNode.getDepth().plus(xHeight.over(4)));
	    if (subNode == Node.NULL)
		scripts = HShiftNode.shiftingUp(supNode, supShift);
	    else {
		subShift = subShift.max(conv.getDimPar(DP_SUB2));
		Dimen		clr
		    = conv.getDimPar(DP_DEFAULT_RULE_THICKNESS).times(4)
		      .minus(supShift.minus(supNode.getDepth())
			     .minus(subNode.getHeight().minus(subShift)));
		if (clr.moreThan(0)) {
		    subShift = subShift.plus(clr);
		    clr = xHeight.times(4, 5)
			  .minus(supShift.minus(supNode.getDepth()));
		    if (clr.moreThan(0)) {
			supShift = supShift.plus(clr);
			subShift = subShift.minus(clr);
		    }
		}
		NodeList	list = new NodeList(3);
		list.append(VShiftNode.shiftingRight(supNode, delta))
		    .append(new IntVKernNode(
				supShift.minus(supNode.getDepth())
				.minus(subNode.getHeight().minus(subShift))))
		    .append(subNode);
		scripts = VBoxNode.packedOf(list);
		scripts = HShiftNode.shiftingDown(scripts, subShift);
	    }
	}
	return new FollowNodeEgg(body, scripts);
    }

    private static Node	makeScriptBox(Field field, Converter conv, byte how) {
	Node		node = field.cleanBox(conv, how);
	Dimen		dim = conv.scriptSpace();
	Box		box = (node.isBox()) ? node.getBox()
					     : HBoxNode.packedOf(node);
	return box.pretendingWidth(box.getWidth().plus(dim));
    }

    public boolean	startsWord() { return false; }
    public boolean	canBePartOfWord() { return false; }
    public boolean	finishesWord() { return true; }

    public MathWordBuilder	getMathWordBuilder(Converter conv,
						   TreatNode proc)
	{ throw new RuntimeException("not start of math word"); }

    public byte		wordFamily() { return -1; }

    public void		contributeToWord(MathWordBuilder word)
	{ throw new RuntimeException("cannot contribute to math word"); }

    public Egg	wordFinishingEgg(MathWordBuilder word, Converter conv)
	{ throw new RuntimeException("not part of math word"); }

    public Egg	wordFinishingEggWithScripts(MathWordBuilder word,
					    Converter conv,
					    Field sup, Field sub)
	{ return wordFinishingEgg(word, conv); }

}
