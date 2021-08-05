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
// Filename: nts/typo/IndentPrim.java
// $Id: IndentPrim.java,v 1.1.1.1 1999/11/27 22:27:57 ksk Exp $
package	nts.typo;

import	nts.builder.Builder;
import	nts.command.Token;

public class	IndentPrim	extends BuilderPrim {

    private boolean		indenting;

    public IndentPrim(String name, boolean indenting)
	{ super(name); this.indenting = indenting; }

    public boolean	isIndenting() { return indenting; }

    /* TeXtp[1090] */
    public final Action		NORMAL = new Action() {
	public void	exec(Builder bld, Token src)
	    { Paragraph.start(indenting); }
    };

    /* TeXtp[1093] */
    public final Action		INSERT = new Action() {
	public void	exec(Builder bld, Token src)
	    { Paragraph.makeIndent(bld); }
    };

}
