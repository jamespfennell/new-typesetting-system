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
// Filename: nts/node/HorizIterator.java
// $Id: HorizIterator.java,v 1.1.1.1 2000/08/09 06:09:11 ksk Exp $
package	nts.node;

import	nts.base.Dimen;

public class	HorizIterator	extends EnumSizesIterator {

    public HorizIterator(NodeEnum nodes) { super(nodes); }

    public Dimen	currHeight() { return curr.getLeftX(); }
    public Dimen	currWidth() { return curr.getHeight(); }
    public Dimen	currDepth() { return curr.getWidth(); }
    public Dimen	currLeftX() { return curr.getDepth(); }
    public Dimen	currStr() { return curr.getWstr(); }
    public Dimen	currShr() { return curr.getWshr(); }
    public byte		currStrOrd() { return curr.getWstrOrd(); }
    public byte		currShrOrd() { return curr.getWshrOrd(); }

    public static Dimen		totalWidth(NodeEnum nodes) {
	SizesSummarizer		pack = new SizesSummarizer();
	summarize(nodes, pack);
	return pack.getHeight().plus(pack.getBody()).plus(pack.getDepth());
    }

    public static BoxSizes	naturalSizes(NodeEnum nodes) {
	SizesSummarizer		pack = new SizesSummarizer();
	summarize(nodes, pack);
	return new BoxSizes(pack.getWidth(),
			    pack.getBody().plus(pack.getDepth()),
			    pack.getLeftX(),
			    pack.getHeight());
    }

    public static void	summarize(NodeEnum nodes, SizesSummarizer pack)
	{ (new HorizIterator(nodes)).summarize(pack); }

}
