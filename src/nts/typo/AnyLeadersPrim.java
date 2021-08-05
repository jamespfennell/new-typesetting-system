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
// Filename: nts/typo/AnyLeadersPrim.java
// $Id: AnyLeadersPrim.java,v 1.1.1.1 2000/02/15 18:47:21 ksk Exp $
package	nts.typo;

import	nts.base.Glue;
import	nts.node.Node;
import	nts.node.Box;
import	nts.node.NodeEnum;
import	nts.node.TreatBox;
import	nts.node.Leaders;
import	nts.node.BoxLeaders;
import	nts.node.BoxSizes;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.Command;

public abstract class	AnyLeadersPrim	extends BuilderPrim {

    public AnyLeadersPrim(String name) { super(name); }

    public void		exec(final Builder bld, Token src) {
    	Token			tok = nextNonRelax();
	Command			cmd = meaningOf(tok);
	if (cmd.hasBoxValue())
	    addBoxLeaders(bld, cmd.getBoxValue());
	else if (cmd.canMakeBoxValue())
	    cmd.makeBoxValue(new TreatBox() {
		public void	execute(Box box, NodeEnum mig)
		    { addBoxLeaders(bld, box); }
	    });
	else if (cmd.hasRuleValue()) {
	    BoxSizes		sizes = cmd.getRuleValue();
	    Glue		skip = getSkip();
	    if (skip != Glue.NULL)
		bld.addLeadRule(skip, sizes, getDesc());
	} else { backToken(tok); error("BoxExpected"); }
    }

    protected void	addBoxLeaders(Builder bld, Box box) {
        if (!box.isVoid()) {
	    Glue			skip = getSkip();
	    if (skip != Glue.NULL) {
		BoxLeaders.Mover	mover = bld.getBoxLeadMover();
		if (mover != BoxLeaders.NULL_MOVER)
		    bld.addLeaders(skip, makeLeaders(box, mover));
	    }
	}
    }

    private Glue	getSkip() {
    	Token			tok = nextNonRelax();
	Command			cmd = meaningOf(tok);
	Glue			skip = cmd.getSkipForLeaders();
	if (skip == Glue.NULL)
	    { backToken(tok); error("BadGlueAfterLeaders"); }
	return skip;
    }

    protected abstract String		getDesc();
    protected abstract Leaders		makeLeaders(Node node,
						    BoxLeaders.Mover mover);

}
