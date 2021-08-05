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
// Filename: nts/math/VCenterGroup.java
// $Id: VCenterGroup.java,v 1.1.1.1 2000/03/19 23:19:56 ksk Exp $
package	nts.math;

import	nts.base.Dimen;
import	nts.node.Box;
import	nts.node.TreatBox;
import	nts.node.NodeList;
import	nts.typo.TypoCommand;
import	nts.typo.VertGroup;

public class	VCenterGroup	extends VertGroup {

    protected Dimen		size;
    protected boolean		exactly;
    private TreatBox		proc;

    public VCenterGroup(Dimen size, boolean exactly, TreatBox proc)
	{ this.size = size; this.exactly = exactly; this.proc = proc; }

    public void		close() {
        super.close();
	proc.execute(makeBox(builder.getList()), NodeList.EMPTY_ENUM);
    }

    protected Box	makeBox(NodeList list)
	{ return TypoCommand.packVBox(list, size, exactly); }

}
