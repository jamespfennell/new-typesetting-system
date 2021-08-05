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
// Filename: nts/node/RuleNode.java
// $Id: RuleNode.java,v 1.1.1.1 2000/06/06 08:28:11 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.io.Log;
import	nts.io.CntxLog;

public class	RuleNode	extends AnyBoxedNode {
    /* root corresponding to rule_node */

    public RuleNode(BoxSizes sizes) { super(sizes); }

    public boolean	canBePartOfDiscretionary() { return true; }
    protected boolean	allegedlyVisible() { return true; }

    public void		addOn(Log log, CntxLog cntx)
	{ log.addEsc("rule").add(sizes); }

    public FontMetric	addShortlyOn(Log log, FontMetric metric)
	{ log.add('|'); return metric; }

    public void		typeSet(TypeSetter setter, SettingContext sctx)
	{ typeSet(setter, sizes.replenished(sctx.around)); }

    public static void	typeSet(TypeSetter setter, BoxSizes s) {
        setter.moveDown(s.getDepth());
        setter.moveLeft(s.getLeftX());
	setter.setRule(s.getHeight().plus(s.getDepth()),
		       s.getWidth().plus(s.getLeftX()));
        setter.moveRight(s.getLeftX());
        setter.moveUp(s.getDepth());
    }

    public String	toString() { return "Rule(" + sizes + ')'; }

}
