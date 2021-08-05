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
// Filename: nts/noad/OpNoad.java
// $Id: OpNoad.java,v 1.1.1.1 2000/10/05 07:48:36 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.VBoxNode;
import	nts.node.VShiftNode;
import	nts.node.IntVKernNode;
import	nts.node.GlueSetting;
import	nts.node.BoxSizes;
import	nts.node.MathWordBuilder;

public class	OpNoad	extends WordPartNoad {

    public static final byte	SIDE_LIMITS		= 0;
    public static final byte	USUAL_LIMITS		= 1;
    public static final byte	DEFAULT_LIMITS		= 2;

    private final byte		limits;

    public OpNoad(Field nucleus, byte limits)
	{ super(nucleus); this.limits = limits; }

    public OpNoad(Field nucleus)
	{ super(nucleus); this.limits = DEFAULT_LIMITS; }

    protected String	getDesc() { return "mathop"; }
    public boolean	acceptsLimits() { return true; }
    public boolean	canPrecedeBin() { return false; }
    protected byte	spacingType() { return SPACING_TYPE_OP; }

    public Noad		withLimits(byte limits)
	{ return new OpNoad(nucleus, limits); }

    /* TeXtp[696] */
    public void		addOn(Log log, CntxLog cntx) {
	log.addEsc(getDesc());
	switch (limits) {
	    case SIDE_LIMITS:		log.addEsc("nolimits");	break;
	    case USUAL_LIMITS:		log.addEsc("limits");	break;
	}
	nucleus.addOn(log, cntx, '.');
    }

    protected boolean	usingLimits(Converter conv, boolean display) {
	switch (limits) {
	    case SIDE_LIMITS:		return false;
	    case USUAL_LIMITS:		return true;
	    default:			return display;
	}
    }

    public boolean	isJustChar() { return false; }

    public Egg		convert(Converter conv) {
	boolean		display = (conv.getStyle() == DISPLAY_STYLE);
	return makeEgg(nucleus.makeOperator(conv, display), conv, display);
    }

    public Egg		wordFinishingEgg(MathWordBuilder word,
					 Converter conv) {
	if (word.lastHasCollapsed()) return Egg.NULL;
	boolean		display = (conv.getStyle() == DISPLAY_STYLE);
	return makeEgg(nucleus.takeLastOperator(word, conv, display),
		       conv, display);
    }

    private Egg		makeEgg(Operator oper, Converter conv,
				boolean display) {
	return (usingLimits(conv, display))
	     ? new StSimpleNodeEgg(
			VBoxNode.packedOf(oper.getNodeToBeLimited()),
			spacingType())
	     : oper.getEggToBeScripted(spacingType());
    }

    /* TeXtp[750] */
    public Egg		convertWithScripts(Converter conv,
					   Field sup, Field sub) {
	boolean		display = (conv.getStyle() == DISPLAY_STYLE);
	if (!usingLimits(conv, display))
	    return super.convertWithScripts(conv, sup, sub);
	return makeEggWithScripts(nucleus.makeOperator(conv, display),
				  conv, sup, sub);
    }

    public Egg		wordFinishingEggWithScripts(MathWordBuilder word,
						    Converter conv,
						    Field sup, Field sub) {
	boolean		display = (conv.getStyle() == DISPLAY_STYLE);
	if (word.lastHasCollapsed() || !usingLimits(conv, display))
	    return super.wordFinishingEggWithScripts(word, conv, sup, sub);
	return makeEggWithScripts(
		    nucleus.takeLastOperator(word, conv, display),
		    conv, sup, sub);
    }

    private Egg		makeEggWithScripts(Operator oper, Converter conv,
					   Field sup, Field sub) {
	Node		body = oper.getNodeToBeLimited();
	Dimen		width = body.getWidth();
	Node		supNode = Node.NULL;
	Node		subNode = Node.NULL;
	if (!sup.isEmpty()) {
	    supNode = sup.cleanBox(conv, SUPER_SCRIPT);
	    width = width.max(supNode.getWidth());
	}
	if (!sub.isEmpty()) {
	    subNode = sub.cleanBox(conv, SUB_SCRIPT);
	    width = width.max(subNode.getWidth());
	}
	body = body.reboxedToWidth(width);
	Dimen		height = body.getHeight();
	Dimen		depth = body.getDepth();
	Dimen		leftX = body.getLeftX();
	NodeList	list = new NodeList();
	Dimen		extra = conv.getDimPar(DP_BIG_OP_SPACING5);
	Dimen		delta = oper.getItalCorr();
	if (delta != Dimen.NULL) delta = delta.halved();
	if (supNode != Node.NULL) {
	    supNode = supNode.reboxedToWidth(width);
	    leftX = leftX.max(supNode.getLeftX());
	    Dimen	clr = conv.getDimPar(DP_BIG_OP_SPACING3)
			      .minus(supNode.getDepth());
	    clr = clr.max(conv.getDimPar(DP_BIG_OP_SPACING1));
	    height = height.plus(extra).plus(supNode.getHeight())
			   .plus(supNode.getDepth()).plus(clr);
	    list.append(new IntVKernNode(extra));
	    list.append( (delta == Dimen.NULL) ? supNode
		       : VShiftNode.shiftingRight(supNode, delta) );
	    list.append(new IntVKernNode(clr));
	}
	list.append(body);
	if (subNode != Node.NULL) {
	    subNode = subNode.reboxedToWidth(width);
	    leftX = leftX.max(subNode.getLeftX());
	    Dimen	clr = conv.getDimPar(DP_BIG_OP_SPACING4)
			      .minus(subNode.getHeight());
	    clr = clr.max(conv.getDimPar(DP_BIG_OP_SPACING2));
	    depth = depth.plus(extra).plus(subNode.getHeight())
			 .plus(subNode.getDepth()).plus(clr);
	    list.append(new IntVKernNode(clr));
	    list.append( (delta == Dimen.NULL) ? subNode
		       : VShiftNode.shiftingLeft(subNode, delta) );
	    list.append(new IntVKernNode(extra));
	}
	return new StSimpleNodeEgg(new VBoxNode(
			    new BoxSizes(height, width, depth, leftX),
			    GlueSetting.NATURAL, list), spacingType());
    }

}
