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
// Filename: nts/typo/DiscretionaryPrim.java
// $Id: DiscretionaryPrim.java,v 1.1.1.1 2000/06/07 04:58:39 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.io.CharCode;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.DiscretionaryNode;
import	nts.node.FontMetric;
import	nts.builder.Builder;
import	nts.builder.HBoxBuilder;
import	nts.command.Token;
import	nts.command.SimpleGroup;

public class	DiscretionaryPrim	extends BuilderPrim {

    public	DiscretionaryPrim(String name) { super(name); }

    /* TeXtp[1117,1119] */
    public final Action		NORMAL = new Action() {
	public void	exec(final Builder bld, Token src) {
	    bld.addNode(DiscretionaryNode.EMPTY);
	    pushLevel(new DiscGroup());
	}
    };

    public class	DiscGroup	extends SimpleGroup {

	private NodeList[]		lists = new NodeList[3];
	private HBoxBuilder		builder;
	private int			index = 0;

	{ lists[0] = lists[1] = lists[2] = NodeList.EMPTY; }

	public void		start() {
	    builder = new HBoxBuilder(currLineNumber());
	    Builder.push(builder); scanLeftBrace();
	}

	/* TeXtp[1119-1121] */
	public void		close() {
	    Builder.pop();
	    NodeList		list = builder.getList();
	    for (int i = 0; i < list.length(); i++)
		if (!list.nodeAt(i).canBePartOfDiscretionary()) {
		    error("ImproperDisc");
		    addItemsOnDiagLog(
		      "The following discretionary sublist has been deleted:",
				      list.nodes(i));
		    list = new NodeList(list.nodes(0, i));
		    break;
		}
	    lists[index] = list;
	    Builder		bld = getBld();
	    if (++index < lists.length) pushLevel(this);
	    else {
		if (bld.forbidsThirdPartOfDiscretionary() && !list.isEmpty()) {
		    error("IllegalMathDisc", DiscretionaryPrim.this);
		    lists[index - 1] = NodeList.EMPTY;
		} else if (list.length() > DiscretionaryNode.MAX_LIST_LENGTH) {
		    error("TooLongDisc");
		    bld.removeLastNode();
		    bld.addNode(new DiscretionaryNode(lists[0], lists[1],
						      NodeList.EMPTY));
		    bld.addNodes(list.nodes());
		    return;
		}
	    }
	    bld.removeLastNode();
	    bld.addNode(new DiscretionaryNode(lists[0], lists[1], lists[2]));
	}

    }

}
