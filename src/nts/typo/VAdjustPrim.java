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
// Filename: nts/typo/VAdjustPrim.java
// $Id: VAdjustPrim.java,v 1.1.1.1 2000/04/29 18:14:33 ksk Exp $
package	nts.typo;

import	nts.node.AdjustNode;
import	nts.builder.Builder;
import	nts.command.Token;

public class	VAdjustPrim	extends BuilderPrim {

    public VAdjustPrim(String name) { super(name); }

    /* TeXtp[1099] */
    public final Action		NORMAL = new Action() {
	public void	exec(final Builder bld, Token src)
	    { pushLevel(new VAdjustGroup()); scanLeftBrace(); }
    };

    /* TeXtp[1100] */
    public static class	VAdjustGroup	extends VertGroup {

	protected VAdjustGroup() { super(); }

	public void		close() {
	    super.close();
	    Builder	bld = getBld();
	    bld.addNode(new AdjustNode(builder.getList()));
	    // bld.buildPage(); // impossible
	}

    }

}
