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
// Filename: nts/align/VertAlignment.java
// $Id: VertAlignment.java,v 1.1.1.1 2001/03/21 08:16:51 ksk Exp $
package	nts.align;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.node.GlueSetting;
import	nts.node.BoxSizes;
import	nts.node.NodeList;
import	nts.node.NodeEnum;
import	nts.node.SizesSummarizer;
import	nts.node.VertIterator;
import	nts.node.AnyBoxNode;
import	nts.node.VBoxNode;
import	nts.node.AnySkipNode;
import	nts.node.VSkipNode;
import	nts.node.NamedVSkipNode;
import	nts.builder.Builder;
import	nts.builder.ListBuilder;
import	nts.builder.VBoxBuilder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.typo.TypoCommand;

public class	VertAlignment	extends Alignment {

    protected final ListBuilder		builder;

    public VertAlignment(Dimen size, boolean exactly,
			 TokenList.Inserter everyCr, Token frzCr,
			 Token frzEndt, ListBuilder builder) {
	super(size, exactly, everyCr, frzCr, frzEndt);
	this.builder = builder;
    }

    /* TeXtp[785] */
    protected void	startNonAligned() {
	super.startNonAligned();
	TypoCommand.getTypoConfig().resetParagraph();
    }

    /* TeXtp[787] */
    protected void	startSpan(int index) {
	super.startSpan(index);
	TypoCommand.getTypoConfig().resetParagraph();
    }

    protected VBoxBuilder	rowBuilder;
    protected VBoxBuilder	spanBuilder;

    /* TeXtp[786] */
    protected void	pushNewRowBuilder() {
	//XXX[786] prev_depth = 0
	rowBuilder = new VAlignBuilder(currLineNumber());
	Builder.push(rowBuilder);
    }

    /* TeXtp[787] */
    protected void	pushNewSpanBuilder() {
	spanBuilder = new VBoxBuilder(currLineNumber());
	Builder.push(spanBuilder);
    }

    protected void	addNamedSkipToRow(Glue skip, String name)
	{ rowBuilder.addNamedSkip(skip, name); }

    /* TeXtp[796] */
    protected Dimen	packedSpanSize(int spanCount) {
	NodeList	list = spanBuilder.getList();
	Builder.pop(); spanBuilder = null;
	SizesSummarizer	pack = new SizesSummarizer();
	VertIterator.summarize(list.nodes(), pack);
	Dimen	size = pack.getBody().plus(pack.getHeight())
				     .plus(pack.getDepth());
	BoxSizes	sizes = new BoxSizes(size, pack.getWidth(),
					     Dimen.ZERO, pack.getLeftX());
	byte		strOrder = pack.maxTotalStr();
	byte		shrOrder = pack.maxTotalShr();
	rowBuilder.addNode(new AnyUnsetNode(sizes, list, spanCount,
				pack.getTotalStr(strOrder), strOrder,
				pack.getTotalShr(shrOrder), shrOrder));
	return size;
    }

    /* TeXtp[799] */
    protected void	packRow() {
	NodeList	list = rowBuilder.getList();
	Builder.pop(); rowBuilder = null;
	TypoCommand.appendBox(builder,
	    new AnyUnsetNode(VertIterator.naturalSizes(list.nodes(),
						       Dimen.NULL), list));
    }

    protected NodeEnum		getUnsetNodes()
	{ return builder.getList().nodes(); }

    protected Dimen		getRelevantSize(BoxSizes sizes)
	{ return sizes.getHeight(); }

    protected BoxSizes		transformSizes(BoxSizes sizes, Dimen dim)
	{ return sizes.withHeight(dim); }

    protected AnyBoxNode	makeBox(BoxSizes sizes, GlueSetting setting,
					NodeList list)
	{ return new VBoxNode(sizes, setting, list); }

    protected AnySkipNode	makeSkip(Glue skip)
	{ return new VSkipNode(skip); }

    protected AnySkipNode	makeSkip(Glue skip, String name)
	{ return new NamedVSkipNode(skip, name); }

    protected TypoCommand.AnyBoxPacker	makeBoxPacker() {
	return new TypoCommand.VBoxPacker() {
	    /* TeXtp[675] */
	    protected void		reportLocation(Log log) {
		log.add("in alignment at lines ")
		.add(builder.getStartLine()).add("--")
		.add(currLineNumber());
	    }
	};
    }

    public void		copyPrevParameters(Builder bld) {
	bld.setSpaceFactor(builder.getSpaceFactor());
	//XXX[812] language
    }

}
