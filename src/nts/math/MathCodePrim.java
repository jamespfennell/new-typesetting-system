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
// Filename: nts/math/MathCodePrim.java
// $Id: MathCodePrim.java,v 1.1.1.1 2000/02/18 10:08:23 ksk Exp $
package	nts.math;

import	nts.base.Num;
import	nts.command.DefCodePrim;

//XXX defVal from DefCodePrim is not used. Make common ancestor for them!

public class	MathCodePrim	extends DefCodePrim {

    /**
     * Creates a new |MathCodePrim| with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the |MathCodePrim|
     */
    public MathCodePrim(String name, int maxVal)
	{ super(name, 0, maxVal); }

    public final Num		get(int idx) {
        Num	val = (Num) getEqt().get(tabKind, idx);
	return (val != Num.NULL) ? val : Num.valueOf(idx);
    }

}
