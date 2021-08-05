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
// Filename: nts/node/AnyBoxNode.java
// $Id: AnyBoxNode.java,v 1.1.1.1 2001/04/27 14:40:21 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;

public abstract class	AnyBoxNode	extends AnyBoxedNode
					implements Box {
    /* corresponding to hlist_node, vlist_node */

    protected final GlueSetting		setting;
    protected NodeList			list;

    public AnyBoxNode(BoxSizes sizes, GlueSetting setting, NodeList list)
	{ super(sizes); this.setting = setting; this.list = list; }

    public GlueSetting	getSetting() { return setting; }
    public NodeList	getList() { return list; }

    public boolean	isVoid() { return false; }
    public boolean	isHBox() { return false; }
    public boolean	isVBox() { return false; }

    public NodeEnum	getHorizList()
	{ return (isHBox()) ? list.nodes(): NodeEnum.NULL; }

    public NodeEnum	getVertList()
	{ return (isVBox()) ? list.nodes(): NodeEnum.NULL; }

    public boolean	isBox() { return true; }
    public Box		getBox() { return this; }
    public boolean	isCleanBox() { return true; }
    public boolean	canBePartOfDiscretionary() { return true; }
    protected boolean	allegedlyVisible() { return true; }

    public Box		pretendingWidth(Dimen width) {
	return (getWidth().equals(width)) ? this
	     : pretendSizesCopy(sizes.withWidth(width));
    }

    public void		typeSet(TypeSetter setter)
	{ typeSet(setter, true); }

    public void		typeSet(TypeSetter setter, SettingContext sctx) {
	if (!list.isEmpty())
	    { setter.push(); typeSet(setter, sctx.allowIO); setter.pop(); }
    }

    private void	typeSet(TypeSetter setter, boolean allowIO) {
        TypeSetter.Mark		here = setter.mark();
	NodeEnum		nodes = list.nodes();
        moveStart(setter);
	SettingContext		sctx
	    = new SettingContext(sizes, setting, setter.mark(), allowIO);
	while (nodes.hasMoreNodes()) {
	    Node	node = nodes.nextNode();
	    movePrev(setter, node);
	    node.typeSet(setter, sctx);
	    movePast(setter, node);
	}
	here.move();
    }

    public void		syncVertIfBox(TypeSetter setter)
	{  if (!list.isEmpty()) setter.syncVert(); }

    protected abstract void	moveStart(TypeSetter setter);
    protected abstract void	movePrev(TypeSetter setter, Node node);
    protected abstract void	movePast(TypeSetter setter, Node node);

    public void		addOn(Log log, CntxLog cntx, Dimen shift) {
	log.addEsc(getDesc()).add(sizes).add(setting);
	if (shift != Dimen.NULL && !shift.isZero())
	    log.add(", shifted ").add(shift.toString());
	cntx.addOn(log, list.nodes());
    }

    public void		addOn(Log log, CntxLog cntx)
	{ addOn(log, cntx, Dimen.NULL); }

    public void		addOn(Log log, int maxDepth, int maxCount)
	{ CntxLog.addItem(log, this, maxDepth, maxCount); }

    public void		addListShortlyOn(Log log) { list.addShortlyOn(log); }

    public abstract String	getDesc();

    /* TeXtp[715] */
    public Node		reboxedToWidth(Dimen width) {
	return (list.isEmpty()) ? pretendingWidth(width)
	     : super.reboxedToWidth(width);
    }

}
