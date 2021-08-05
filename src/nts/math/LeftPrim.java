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
// Filename: nts/math/LeftPrim.java
// $Id: LeftPrim.java,v 1.1.1.1 2000/08/05 16:20:27 ksk Exp $
package	nts.math;

import	nts.noad.LeftNoad;
import	nts.noad.RightNoad;
import	nts.noad.InnerNoad;
import	nts.noad.NoadList;
import	nts.noad.TreatNoadList;
import	nts.noad.NoadListField;
import	nts.command.Token;
import	nts.command.FrozenToken;
import	nts.command.Primitive;
import	nts.command.Group;
import	nts.command.Closing;

public class	LeftPrim	extends MathPrim {

    public /* final */ Token		rightTok;

    public	LeftPrim(String name, Primitive right)
	{ super(name); rightTok = new FrozenToken(right); }

    public MathAction	mathAction() { return NORMAL; }

    /* TeXtp[1191,1184] */
    public final MathAction	NORMAL = new MathAction() {
	public void		exec(final MathBuilder bld, Token src) {
	    final LeftNoad	left = new LeftNoad(scanDelimiter());
	    pushLevel(new LeftGroup(rightTok, new TreatNoadList() {
		public void	execute(NoadList list) {
		    RightNoad	right = new RightNoad(scanDelimiter());
		    NoadList	whole = new NoadList(left);
		    whole.append(list).append(right);
		    bld.addNoad(new InnerNoad(new NoadListField(whole)));
		}
	    }, left));
	}
    };

    /* TeXtp[1192] */
    public static final Closing		EXTRA_RIGHT = new Closing() {
	public void	exec(Group grp, Token src)
	    { MathPrim.scanDelimiter(); error("ExtraRight", src); }
    };

}
