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
// Filename: nts/math/MathBuilder.java
// $Id: MathBuilder.java,v 1.1.1.1 2001/03/22 22:31:41 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.BoxSizes;
import	nts.node.Node;
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
import	nts.noad.Noad;
import	nts.noad.NoadList;
import	nts.noad.OrdNoad;
import	nts.noad.FractionNoad;
import	nts.noad.DelFractionNoad;
import	nts.noad.Field;
import	nts.noad.NodeField;
import	nts.noad.EmptyField;
import	nts.noad.NoadListField;
import	nts.noad.Delimiter;
import	nts.builder.Builder;

public abstract class	MathBuilder extends Builder {

    protected NoadList		list;
    protected final int		startLine;
    protected final Noad	leftDelim;

    protected MathBuilder(int startLine, NoadList list, Noad leftDelim) {
	this.startLine = startLine;
	this.list = list; this.leftDelim = leftDelim;
    }

    protected MathBuilder(int startLine, NoadList list)
	{ this(startLine, list, Noad.NULL); }

    protected MathBuilder(int startLine, Noad leftNoad)
	{ this(startLine, new NoadList(), leftNoad); }

    protected MathBuilder(int startLine)
	{ this(startLine, new NoadList()); }

    public void		addNoad(Noad noad) { list.append(noad); }
    public Noad		lastNoad() { return list.lastNoad(); }
    public void		removeLastNoad() { list.removeLastNoad(); }
    public void		replaceLastNoad(Noad noad)
			    { list.replaceLastNoad(noad); }

    public void		addNode(Node node) { list.append(node); }
    public void		addNodes(NodeEnum nodes) { list.append(nodes); }

    public Node		lastNode() { return list.lastNode(); }
    public void		removeLastNode() { list.removeLastNode(); }
    public Node		lastSpecialNode() { return list.lastSpecialNode(); }

    public int		getStartLine() { return startLine; }

    public boolean	unBox(Box box) { return false; }

    /* ****************************************************************** */

    public boolean	isMath() { return true; }
    public boolean	isCharAllowed() { return true; }
    public boolean	forbidsThirdPartOfDiscretionary() { return true; }

    public void		addKern(Dimen kern) { addNode(new HKernNode(kern)); }
    public void		addSkip(Glue skip) { addNode(new HSkipNode(skip)); }

    public void		addNamedSkip(Glue skip, String name)
    	{ addNode(new NamedHSkipNode(skip, name)); }

    /* TeXtp[1056] */
    public void		addRule(BoxSizes sizes)
	{ addNode(new RuleNode(sizes)); }

    /* STRANGE
     * \leaders<box>\hskip #pt and \leaders<box>\mskip #mu
     * silently produce the same result.
     * Isn't it a bug?
     */
    public void		addLeaders(Glue skip, Leaders lead)
	{ addNode(new HLeadersNode(skip, lead)); }

    public void		addLeadRule(Glue skip, BoxSizes sizes, String desc)
        { addNode(new HLeadRuleNode(skip, sizes, desc)); }

    public BoxLeaders.Mover	getBoxLeadMover()
	{ return HLeadersNode.BOX_MOVER; }

    /* TeXtp[1076] */
    public void		addBox(Node box)
	{ list.append(new OrdNoad(new NodeField(box))); }

    public boolean      canTakeLastBox() { return false; }

    /* ****************************************************************** */

    private NoadList		numerator = NoadList.NULL;
    private Dimen		thickness;
    private Delimiter		fractLeft = Delimiter.NULL;
    private Delimiter		fractRight = Delimiter.NULL;

    private FractionNoad	makeFraction(NoadList num, Field den) {
	return (fractLeft != Delimiter.NULL)
	     ? new DelFractionNoad(new NoadListField(num), den,
				   thickness, fractLeft, fractRight)
	     : new FractionNoad(new NoadListField(num), den, thickness);
    }

    public boolean	alreadyFractioned()
	{ return (numerator != NoadList.NULL); }

    public boolean	isEmpty()
	{ return (list.isEmpty() && !alreadyFractioned()); }
	//XXX check in TeXtp that it's correct

    /* TeXtp[1181] */
    public void		fractione(Dimen thickness) {
	numerator = getList(); list = new NoadList();
	this.thickness = thickness;
    }

    /* TeXtp[1181] */
    public void		fractione(Dimen thickness, Delimiter left,
						   Delimiter right)
	{ fractione(thickness); fractLeft = left; fractRight = right; }

    /* TeXtp[1185] */
    public NoadList	getList() {
	return (numerator == NoadList.NULL) ? list
	     : new NoadList(makeFraction(numerator, new NoadListField(list)));
    }

    /* STRANGE
     * isn't this faking of left delimiter crazy?
     */
    /* TeXtp[219] */
    protected void	specialShow(Log log, int depth, int breadth) {
	CntxLog.addItems(log, ( (alreadyFractioned())
			      ? list : fakeLeftDelim(list) ).noads(),
			 depth, breadth);
	log.endLine();
	if (alreadyFractioned()) {
	    log.add("this will be denominator of:");
	    CntxLog.addItem(log, makeFraction(fakeLeftDelim(numerator),
					      EmptyField.FIELD),
			    depth, breadth);
	}
    }

    private NoadList	fakeLeftDelim(NoadList list) {
	if (leftDelim == Noad.NULL) return list;
	NoadList	faked =  new NoadList(list.length() + 1);
	return faked.append(leftDelim).append(list);
    }

    public void		setEqNo(Node node, boolean left) { }

}
