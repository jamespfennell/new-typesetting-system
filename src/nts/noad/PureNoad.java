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
// Filename: nts/noad/PureNoad.java
// $Id: PureNoad.java,v 1.1.1.1 2000/08/09 06:15:49 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;
import	nts.node.GlueSetting;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.BoxSizes;
import	nts.node.RuleNode;
import	nts.node.IntVKernNode;
import	nts.node.HShiftNode;
import	nts.node.VBoxNode;
import	nts.node.SizesSummarizer;
import	nts.node.VertIterator;

public abstract class	PureNoad	extends BaseNoad {

    public final boolean	isNode() { return false; }
    public Node			getNode() { return Node.NULL; }

    /* TeXtp[704] */
    public static Node		makeRule(Dimen height) {
	return new RuleNode(new BoxSizes(height, Dimen.NULL,
					 Dimen.ZERO, Dimen.NULL));
    }

    /* TeXtp[705] */
    public static Node		makeOverBar(Node node, Dimen clearence,
					    Dimen thickness, Dimen extra) {
	NodeList	list = new NodeList(4);
	list.append(new IntVKernNode(extra));
	list.append(makeRule(thickness));
	list.append(new IntVKernNode(clearence));
	list.append(node);
	return VBoxNode.packedOf(list);
    }

    /* TeXtp[735] */
    public static Node		makeUnderBar(Node node, Dimen clearence,
					     Dimen thickness, Dimen extra) {
	NodeList	list = new NodeList(3);
	list.append(node);
	list.append(new IntVKernNode(clearence));
	list.append(makeRule(thickness));
	SizesSummarizer		pack = new SizesSummarizer();
	VertIterator.summarize(list.nodes(), pack);
	BoxSizes		sizes = new BoxSizes(
			    pack.getHeight(), pack.getWidth(),
			    pack.getBody().plus(pack.getDepth()).plus(extra),
			    pack.getLeftX());
	return new VBoxNode(sizes, GlueSetting.NATURAL, list);
    }

    /* TeXtp[706] */
    public static Node	varDelimiter(Delimiter del, Dimen size,
				     Transformer transf) {
	Node		node = transf.fetchSufficientNode(del, size);
	Dimen		shift
	    = node.getHeight().minus(node.getDepth()).halved()
	     .minus(transf.getDimPar(DP_AXIS_HEIGHT));
	return HShiftNode.shiftingDown(node, shift);
    }

}
