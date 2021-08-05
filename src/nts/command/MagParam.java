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
// Filename: nts/command/MagParam.java
// $Id: MagParam.java,v 1.1.1.1 1999/07/12 11:04:07 ksk Exp $
package	nts.command;

import	nts.base.Num;

/**
 * Setting number parameter primitive.
 */
public class	MagParam extends NumParam {

    /**
     * Creates a new MagParam with given name and value and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the MagParam
     * @param	val the value of the MagParam
     */
    public	MagParam(String name, Num val) { super(name, val); }
    public	MagParam(String name, int val) { super(name, val); }

    /**
     * Creates a new MagParam with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the MagParam
     */
    public	MagParam(String name) { super(name); }

    private Num		magSet = Num.NULL;

    public Num		get() {
        Num		mag = super.get();
        if (magSet != Num.NULL && !magSet.equals(mag)) {
	    error("IncompatMag", str(mag), str(magSet));
	    mag = magSet; set(mag, true);
	}
	if (!mag.moreThan(0) || mag.moreThan(32768)) {
	    error("IllegalMag", str(mag));
	    mag = Num.valueOf(1000); set(mag, true);
	}
	magSet = mag;
	return mag;
    }

}
