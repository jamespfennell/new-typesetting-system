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
// Filename: nts/command/AnyMarkPrim.java
// $Id: AnyMarkPrim.java,v 1.1.1.1 2000/01/31 21:42:34 ksk Exp $
package	nts.command;

import	java.io.ObjectInputStream;
import	java.io.IOException;
import	nts.io.Log;

public class	AnyMarkPrim	extends ExpandablePrim
				implements TokenList.Maintainer {

    private transient TokenList		value;

    protected AnyMarkPrim(String name, TokenList val)
	{ super(name); value = val; }

    public AnyMarkPrim(String name) { this(name, TokenList.EMPTY); }

    private void	readObject(ObjectInputStream input)
			    throws IOException, ClassNotFoundException
	{ input.defaultReadObject(); value = TokenList.EMPTY; }

    public TokenList	getToksValue() { return value; }
    public void		setToksValue(TokenList val) { value = val; }
    public boolean	isEmpty() { return value.isEmpty(); }

    public void		expand(Token src)
	{ tracedPushList(value, "mark"); }

    /* TeXtp[296] */
    public void		addExpandable(Log log, boolean full) {
        super.addExpandable(log, full);
	if (full) log.add(':').endLine().add(value);
    }

    /* TeXtp[223] */
    public void		addExpandable(Log log, int maxCount)
	{ addExpandable(log, false); }

}
