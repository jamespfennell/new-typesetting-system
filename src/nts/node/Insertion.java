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
// Filename: nts/node/Insertion.java
// $Id: Insertion.java,v 1.1.1.1 2000/02/19 01:43:42 ksk Exp $
package	nts.node;

import	java.io.Serializable;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;

public class	Insertion	implements Serializable {

    public static final Insertion	NULL = null;

    public final int		num;
    public final NodeList	list;
    public final Dimen		size;
    public final Glue		topSkip;
    public final Dimen		maxDepth;
    public final Num		floatCost;

    public Insertion(int num, NodeList list, Dimen size,
		     Glue topSkip, Dimen maxDepth, Num floatCost) {
	this.num = num; this.list = list; this.size = size;
	this.topSkip = topSkip; this.maxDepth = maxDepth;
	this.floatCost = floatCost;
    }

    public Insertion	makeCopy(NodeList list, Dimen size) {
	return new Insertion(num, list, size, topSkip, maxDepth, floatCost);
    }

    /* TeXtp[188] */
    public void		addOn(Log log, CntxLog cntx) {
	log.addEsc("insert").add(num)
	   .add(", natural size ").add(size.toString())
	   .add("; split(").add(topSkip.toString())
	   .add(',').add(maxDepth.toString())
	   .add("); float cost ").add(floatCost.toString());
	cntx.addOn(log, list.nodes());
    }

}
