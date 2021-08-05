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
// Filename: nts/math/DisplayBuilder.java
// $Id: DisplayBuilder.java,v 1.1.1.1 2001/03/20 11:21:57 ksk Exp $
package	nts.math;

import	nts.node.Node;

public class	DisplayBuilder	extends MathBuilder {

    public DisplayBuilder(int line) { super(line); }
    public boolean	isInner() { return false; }
    public String	modeName() { return "display math"; }

    private Node	eqNoBox = Node.NULL;
    private boolean	eqNoLeft = false;

    public void		setEqNo(Node node, boolean left)
	{ eqNoBox = node; eqNoLeft = left; }

    public Node		getEqNoBox() { return eqNoBox; }
    public boolean	getEqNoLeft() { return eqNoLeft; }

}
