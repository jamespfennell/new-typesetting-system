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
// Filename: nts/typo/AnyBoxPrim.java
// $Id: AnyBoxPrim.java,v 1.1.1.1 2000/08/03 14:28:10 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.Box;
import	nts.node.NodeEnum;
import	nts.node.TreatBox;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.Group;

public abstract class	AnyBoxPrim	extends BuilderPrim {

    private final TokenList.Inserter	every;

    public AnyBoxPrim(String name, TokenList.Inserter every)
	{ super(name); this.every = every; }

    public void		exec(final Builder bld, Token src) {
	makeBoxValue(new TreatBox() {
	    public boolean	wantsMig() { return bld.wantsMigrations(); }
	    public void		execute(Box box, NodeEnum mig)
		{ appendBox(bld, box, mig); }
	});
    }

    public boolean	canMakeBoxValue() { return true; }

    /* TeXtp[645] */
    public void		makeBoxValue(TreatBox proc) {
	Dimen		size = Dimen.ZERO;
	boolean		exactly = false;
	if (scanKeyword("to")) { size = scanDimen(); exactly = true; }
	else if (scanKeyword("spread")) size = scanDimen();
	pushLevel(makeGroup(size, exactly, proc));
	scanLeftBrace(); every.insertToks();
    }

    protected abstract Group	makeGroup(Dimen size, boolean exactly,
					  TreatBox proc);

}
