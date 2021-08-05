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
// Filename: nts/command/MuGlueParam.java
// $Id: MuGlueParam.java,v 1.1.1.1 2000/04/06 20:21:33 ksk Exp $
package	nts.command;

import	nts.base.Glue;

/**
 * Setting muglue parameter primitive.
 */
public class	MuGlueParam extends AnyGlueParam {

    /**
     * Creates a new MuGlueParam with given name and value and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the MuGlueParam
     * @param	val the value of the MuGlueParam
     */
    public	MuGlueParam(String name, Glue val) { super(name, val); }

    /**
     * Creates a new MuGlueParam with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the MuGlueParam
     */
    public	MuGlueParam(String name) { super(name); }

    protected void	scanValue(Token src, boolean glob) {
    	set(scanMuGlue(), glob);
	// System.err.println("= Assignment \\" + getName() + " = " + get());
    }

    protected void	perform(int operation, boolean glob)
	{ set(performFor(get(), operation, true), glob); }

    public String	getUnit() { return "mu"; }
    public boolean	hasMuGlueValue() { return true; }
    public Glue		getMuGlueValue() { return get(); }

}
