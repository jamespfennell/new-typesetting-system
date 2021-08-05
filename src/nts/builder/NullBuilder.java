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
// Filename: nts/builder/NullBuilder.java
// $Id: NullBuilder.java,v 1.1.1.1 2001/03/20 11:21:34 ksk Exp $
package	nts.builder;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.node.Node;
import	nts.node.NodeEnum;
import	nts.node.BoxSizes;
import	nts.node.Leaders;

public class	NullBuilder	extends Builder	{

    public void		addNode(Node node) { }
    public void		addNodes(NodeEnum nodes) { }
    public void		addKern(Dimen kern) { }
    public void		addSkip(Glue skip) { }
    public void		addNamedSkip(Glue skip, String name) { }
    public void		addRule(BoxSizes sizes) { }
    public void		addLeaders(Glue skip, Leaders lead) { }
    public void		addLeadRule(Glue skip, BoxSizes sizes,
				    String desc) { }
    public boolean	isEmpty() { return true; }
    public Node		lastNode() { return Node.NULL; }
    public void		removeLastNode() { }
    public Node		lastSpecialNode() { return Node.NULL; }
    public int		getPrevGraf() { return 0; }
    public void		setPrevGraf(int pg) { }
    public String	modeName() { return "no"; }

}
