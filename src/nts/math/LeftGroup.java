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
// Filename: nts/math/LeftGroup.java
// $Id: LeftGroup.java,v 1.1.1.1 2001/03/22 16:13:36 ksk Exp $
package	nts.math;

import	nts.noad.Noad;
import	nts.noad.TreatNoadList;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.OtherToken;
import	nts.command.SimpleGroup;

public class	LeftGroup	extends SimpleGroup {

    private final Token			expected;
    private final TreatNoadList		proc;
    protected final FormulaBuilder	builder;

    public LeftGroup(Token expected, TreatNoadList proc,
		     FormulaBuilder builder) {
	this.expected = expected; this.proc = proc;
	this.builder = builder;
    }

    public LeftGroup(Token expected, TreatNoadList proc, Noad leftDelim) {
	this.expected = expected; this.proc = proc;
	builder = new FormulaBuilder(currLineNumber(), leftDelim);
    }

    public void		start() { Builder.push(builder); }

    /* TeXtp[1191] */
    public void		stop()
	{ Builder.pop(); proc.execute(builder.getList()); }

    public Token	expectedToken() { return expected; }

    /* TeXtp[1065] */
    public void		reject(Token tok) {
        backToken(tok);
	TokenList.Buffer	buf = new TokenList.Buffer(2);
	buf.append(expected);	//XXX make ins on demand and keep it
	buf.append(new OtherToken(Token.makeCharCode('.')));
	TokenList	ins = buf.toTokenList();
	insertList(ins); error("MissingInserted", ins);
    }

}
