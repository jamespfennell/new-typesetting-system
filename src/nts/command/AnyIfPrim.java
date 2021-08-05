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
// Filename: nts/command/AnyIfPrim.java
// $Id: AnyIfPrim.java,v 1.1.1.1 2001/03/13 10:14:11 ksk Exp $
package	nts.command;

import	nts.io.CharCode;

public abstract class	AnyIfPrim	extends CondPrim {

    public AnyIfPrim(String name) { super(name); }

    /* TeXtp[498,500] */
    public void		expand(Token src) {
	CondEntry	currEntry = pushCond(WAIT, this);
	boolean		val = holds();
	if (getConfig().getBoolParam(BOOLP_TRACING_ALL_COMMANDS))
	    diagLog.add('{').add(val).add('}').startLine();
	if (val) currEntry.limit = ELSE;
	else for (;;) {
	    int		endLev = skipBranch();
	    if (topCond() == currEntry) {	//SSS
		if (endLev == OR) error("ExtraOrElseFi", esc("or"));
		else {
		    if (endLev == FI) popCond();
		    else topCond().limit = FI;
		    return;
		}
	    } else if (endLev == FI) popCond();
	}
    }

    public final boolean		isConditional() { return true; }

    protected abstract boolean		holds();

    protected char		relOp() {
	Token	tok = nextExpNonSpacer();
	char	op = tok.otherChar();
	if (op == CharCode.NO_CHAR || "<=>".indexOf(op) < 0) {
	    backToken(tok); op = '=';
	    error("IllegalIfnumOp", exp(this));
	}
	return op;
    }

}
