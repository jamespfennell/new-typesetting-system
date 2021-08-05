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
// Filename: nts/builder/VertBuilder.java
// $Id: VertBuilder.java,v 1.1.1.1 2000/08/08 04:44:04 ksk Exp $
package	nts.builder;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.NodeEnum;
import	nts.node.BoxSizes;
import	nts.node.Box;
import	nts.node.RuleNode;
import	nts.node.VKernNode;
import	nts.node.VSkipNode;
import	nts.node.NamedVSkipNode;
import	nts.node.Leaders;
import	nts.node.BoxLeaders;
import	nts.node.VLeadersNode;
import	nts.node.VLeadRuleNode;

public abstract class	VertBuilder extends ListBuilder {

    public static final Dimen	IGNORE_DEPTH = Dimen.valueOf(-1000);

    private int		prevGraf = 0;
    private Dimen	prevDepth = IGNORE_DEPTH;

    protected VertBuilder(int line, NodeList list) { super(line, list); }
    protected VertBuilder(int line) { super(line); }

    public boolean	isVertical() { return true; }
    public boolean	wantsMigrations() { return true; }
    public void		addKern(Dimen kern) { addNode(new VKernNode(kern)); }
    public void		addSkip(Glue skip) { addNode(new VSkipNode(skip)); }

    public void		addNamedSkip(Glue skip, String name)
    	{ addNode(new NamedVSkipNode(skip, name)); }

    /* TeXtp[1056] */
    public void		addRule(BoxSizes sizes) {
	addNode(new RuleNode(sizes));
	uncheckedSetPrevDepth(IGNORE_DEPTH);
    }

    public void		addLeaders(Glue skip, Leaders lead)
	{ addNode(new VLeadersNode(skip, lead)); }

    public void		addLeadRule(Glue skip, BoxSizes sizes, String desc)
        { addNode(new VLeadRuleNode(skip, sizes, desc)); }

    public BoxLeaders.Mover	getBoxLeadMover()
	{ return VLeadersNode.BOX_MOVER; }

    /* TeXtp[679] */
    public void		addBox(Node box)
	{ super.addBox(box); uncheckedSetPrevDepth(box.getDepth()); }

    protected NodeEnum	unBoxList(Box box) { return box.getVertList(); }

    public int		getPrevGraf() { return prevGraf; }
    public void		setPrevGraf(int pg) { prevGraf = pg; }

    public Dimen	getPrevDepth() { return prevDepth; }
    protected void	uncheckedSetPrevDepth(Dimen pd) { prevDepth = pd; }
    public Dimen	nearestValidPrevDepth() { return prevDepth; }

    public void		setPrevDepth(Dimen pd)
	{ if (pd != Dimen.NULL) prevDepth = pd; }

    public boolean	needsParSkip() { return (!list.isEmpty()); }

    /* TeXtp[219] */
    protected void	specialShow(Log log) {
	log.startLine().add("prevdepth ");
	if (getPrevDepth().moreThan(IGNORE_DEPTH))
	    log.add(getPrevDepth().toString());
	else log.add("ignored");
	if (prevGraf != 0)
	    log.add(", prevgraf ").add(prevGraf)
	       .add((prevGraf == 1) ? " line" : " lines");
    }

}
