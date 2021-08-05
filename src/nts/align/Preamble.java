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
// Filename: nts/align/Preamble.java
// $Id: Preamble.java,v 1.1.1.1 2000/08/07 03:04:04 ksk Exp $
package	nts.align;

import	nts.base.Glue;
import	nts.command.Token;
import	nts.command.TokenList;

public class	Preamble {

    public static final Entry	NULL_ENTRY = null;

    public static class	Entry {
	private TokenList	uPart;
	private TokenList	vPart;
	private Glue		skip;

	public Entry(TokenList uPart, TokenList vPart, Glue skip)
	    { this.uPart = uPart; this.vPart = vPart; this.skip = skip; }

    }

    public final Glue		firstSkip;
    public final String		skipName;
    public final Token		endTemplate;
    private final Entry[]	records;
    private final int		loopIndex;

    public Preamble(Glue firstSkip, String skipName, Token endTemplate,
		    Entry[] records, int loopIndex) {
	this.firstSkip = firstSkip; this.skipName = skipName;
	this.endTemplate = endTemplate;
	this.records = records; this.loopIndex = loopIndex;
	if (loopIndex >= records.length)
	    throw new RuntimeException("invalid Preamble loopIndex");
    }

    public int		length() { return records.length; }
    public boolean	periodic() { return (loopIndex >= 0); }

    public boolean	hasRecord(int i)
	{ return (i >= 0 && (i < records.length || loopIndex >= 0)); }

    public TokenList	getUPart(int i) {
	Entry		ent = getEntry(i);
	return (ent != NULL_ENTRY) ? ent.uPart : TokenList.NULL;
    }

    public TokenList	getVPart(int i) {
	Entry		ent = getEntry(i);
	return (ent != NULL_ENTRY) ? ent.vPart : TokenList.NULL;
    }

    public Glue		getSkip(int i) {
	Entry		ent = getEntry(i);
	return (ent != NULL_ENTRY) ? ent.skip : Glue.NULL;
    }

    private Entry	getEntry(int i) {
	if (i >= 0) {
	    if (i < records.length) return records[i];
	    else if (loopIndex >= 0)
		return records[loopIndex + (i - loopIndex)
					 % (records.length - loopIndex)];
	}
	return NULL_ENTRY;
    }

/*
    public void		trace() {
	System.err.println("=== Preamble ===");
	System.err.println("firstSkip = " + firstSkip);
	System.err.println("skipName = " + skipName);
	System.err.println("endTemplate = " + endTemplate);
	for (int i = 0; i < records.length; i++) {
	    System.err.println("index = " + i);
	    System.err.println("uPart = " + records[i].uPart);
	    System.err.println("vPart = " + records[i].vPart);
	    System.err.println("skip = " + records[i].skip);
	}
	System.err.println("loopIndex = " + loopIndex);
	System.err.println();
    }
*/

}
