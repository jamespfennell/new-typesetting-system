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
// Filename: nts/builder/HorizBuilder.java
// $Id: HorizBuilder.java,v 1.1.1.1 2000/08/08 04:35:55 ksk Exp $
package	nts.builder;

import	nts.io.Log;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.node.BoxSizes;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.NodeEnum;
import	nts.node.Box;
import	nts.node.RuleNode;
import	nts.node.HKernNode;
import	nts.node.HSkipNode;
import	nts.node.NamedHSkipNode;
import	nts.node.Leaders;
import	nts.node.BoxLeaders;
import	nts.node.HLeadersNode;
import	nts.node.HLeadRuleNode;

public abstract class	HorizBuilder extends ListBuilder {

    public static final int	NORMAL_SPACE_FACTOR = 1000;

    private int		spaceFactor = NORMAL_SPACE_FACTOR;

    protected HorizBuilder(int line, NodeList list) { super(line, list); }
    protected HorizBuilder(int line) { super(line); }

    public boolean	isHorizontal() { return true; }
    public boolean	isCharAllowed() { return true; }

    public void		addKern(Dimen kern) { addNode(new HKernNode(kern)); }
    public void		addSkip(Glue skip) { addNode(new HSkipNode(skip)); }

    public void		addNamedSkip(Glue skip, String name)
    	{ addNode(new NamedHSkipNode(skip, name)); }

    /* TeXtp[1056] */
    public void		addRule(BoxSizes sizes)
	{ addNode(new RuleNode(sizes)); resetSpaceFactor(); }

    public void		addLeaders(Glue skip, Leaders lead)
	{ addNode(new HLeadersNode(skip, lead)); }

    public void		addLeadRule(Glue skip, BoxSizes sizes, String desc)
        { addNode(new HLeadRuleNode(skip, sizes, desc)); }

    public BoxLeaders.Mover	getBoxLeadMover()
	{ return HLeadersNode.BOX_MOVER; }

    public void		addBox(Node box)
	{ super.addBox(box); resetSpaceFactor(); }

    protected NodeEnum	unBoxList(Box box) { return box.getHorizList(); }

    /* TeXtp[1034] */
    public void		adjustSpaceFactor(int sf) {
	if (sf > 0)
	    spaceFactor = (  sf <= NORMAL_SPACE_FACTOR
	    		  || spaceFactor >= NORMAL_SPACE_FACTOR  )
			? sf : NORMAL_SPACE_FACTOR;
    }

    public int		getSpaceFactor() { return spaceFactor; }
    public int		nearestValidSpaceFactor() { return spaceFactor; }

    public void		setSpaceFactor(int sf)
	{ if (sf > 0) spaceFactor = sf; }

    public void		resetSpaceFactor()
	{ spaceFactor = NORMAL_SPACE_FACTOR; }

    /* TeXtp[219] */
    protected void	specialShow(Log log)
	{ log.startLine().add("spacefactor ").add(getSpaceFactor()); }

}
