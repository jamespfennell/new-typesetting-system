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
// Filename: nts/typo/NullFontMetric.java
// $Id: NullFontMetric.java,v 1.1.1.1 2001/01/28 17:57:21 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.node.Box;
import	nts.node.Node;
import	nts.node.TreatNode;
import	nts.node.WordBuilder;
import	nts.node.WordRebuilder;
import	nts.node.MathWordBuilder;
import	nts.node.BaseFontMetric;
import	nts.command.Token;

public class	NullFontMetric	extends BaseFontMetric {

    public static final NullFontMetric	METRIC = new NullFontMetric();
    public static final SetFont		COMMAND = new SetFont(METRIC);
    public static final Name		NAME = Token.makeName("nullfont");

    public NullFontMetric() {
        setNumParam(NUM_PARAM_HYPHEN_CHAR, Num.valueOf('-'));
        setNumParam(NUM_PARAM_SKEW_CHAR, Num.valueOf(-1));
    }

    public Name		getIdent() { return NAME; }
    public Name		getName() { return NAME; }
    public void		addDescOn(Log log) { NAME.addOn(log); }
    public boolean	isNull() { return true; }
    public Node		getCharNode(CharCode code) { return Node.NULL; }
    public Node		getLargerNode(CharCode code) { return Node.NULL; }

    public Node		getSufficientNode(CharCode code, Dimen desired)
	{ return Node.NULL; }

    public Box		getFittingWidthBox(CharCode code, Dimen desired)
	{ return Box.NULL; }

    public Dimen	getKernBetween(CharCode left, CharCode right)
	{ return Dimen.NULL; }

    					//CCC jikes rejects boundary
    public WordBuilder	getWordBuilder(TreatNode proc, boolean bound,
				       boolean discretionaries)
	{ return new NullWordBuilder(); }

    public WordRebuilder	getWordRebuilder(TreatNode proc,
					         boolean bound)
	{ return new NullWordBuilder(); }

    public MathWordBuilder	getMathWordBuilder(TreatNode proc)
	{ return new NullWordBuilder(); }

    private class	NullWordBuilder
		    implements WordRebuilder, MathWordBuilder {

	public boolean	add(CharCode code) { return false; }
	public byte	addIfBelongsToCut(CharCode code) { return INDEPENDENT; }
	public boolean	prolongsCut(CharCode code) { return false; }
	public void	close(CharCode code) { }
	public void	close(boolean boundary) { }
	public void	close() { }
	public boolean	lastHasCollapsed() { return false; }
	public Node	takeLastNode() { return Node.NULL; }
	public Node	takeLastLargerNode() { return Node.NULL; }

    }

}
