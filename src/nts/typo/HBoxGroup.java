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
// Filename: nts/typo/HBoxGroup.java
// $Id: HBoxGroup.java,v 1.1.1.1 2001/04/27 16:01:50 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.Box;
import	nts.node.TreatBox;
import	nts.node.NodeList;
import	nts.node.NodeEnum;

public class	HBoxGroup	extends HorizGroup {

    protected Dimen		size;
    protected boolean		exactly;
    protected TreatBox		proc;

    public HBoxGroup(Dimen size, boolean exactly, TreatBox proc)
	{ this.size = size; this.exactly = exactly; this.proc = proc; }

    public void		close() {
        super.close();
	NodeList	list = builder.getList();
	NodeEnum	mig = (proc.wantsMig())
			    ? list.extractedMigrations().nodes()
			    : NodeList.EMPTY_ENUM;
	proc.execute(makeBox(list), mig);
    }

    protected Box	makeBox(NodeList list)
	{ return TypoCommand.packHBox(list, size, exactly); }

}
