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
// Filename: nts/node/HLeadersNode.java
// $Id: HLeadersNode.java,v 1.1.1.1 2001/03/06 14:57:05 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;

public class	HLeadersNode	extends HSkipNode {
    /* corresponding to glue_node */

    protected final Leaders		lead;

    public HLeadersNode(Glue skip, Leaders lead)
	{ super(skip); this.lead = lead; }

    public Dimen	getHeight() { return lead.getHeight(); }
    public Dimen	getDepth() { return lead.getDepth(); }

    protected boolean	allegedlyVisible() { return true; }

    public void		addOn(Log log, CntxLog cntx)
	{ lead.addOn(log, cntx, skip); }

    public void		typeSet(TypeSetter setter, SettingContext sctx) {
	TypeSetter.Mark		here = setter.mark();
	setter.moveLeft(getLeftX(sctx.setting));
	lead.typeSet(setter, sctx.setting.set(skip, true), sctx);
	here.move();
    }

    public static final BoxLeaders.Mover	BOX_MOVER
	= new BoxLeaders.Mover() {

	public Dimen	offset(TypeSetter.Mark start)
	    { return start.xDiff(); }

	public Dimen	size(Node node)
	    { return node.getLeftX().plus(node.getWidth()); }

	public void	back(TypeSetter setter, Dimen gap)
	    { setter.moveLeft(gap); }

	public void	move(TypeSetter setter, Dimen gap)
	    { setter.moveRight(gap); }

	public void	movePrev(TypeSetter setter, Node node) {
	    setter.moveRight(node.getLeftX());
	    setter.syncVert(); setter.syncHoriz();
	}

	public void	movePast(TypeSetter setter, Node node)
	    { setter.moveRight(node.getWidth()); }

    };

    public String	toString()
	{ return "HLeaders(" + skip + "; " + lead + ')'; }

}
