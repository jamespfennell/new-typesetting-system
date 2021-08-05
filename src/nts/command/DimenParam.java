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
// Filename: nts/command/DimenParam.java
// $Id: DimenParam.java,v 1.1.1.1 1999/06/24 10:43:51 ksk Exp $
package	nts.command;

import	nts.base.Dimen;

/**
 * Setting dimension parameter primitive.
 */
public class	DimenParam	extends AnyDimenParam
				implements Dimen.Provider {

    /**
     * Creates a new DimenParam with given name and value and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the DimenParam
     * @param	val the value of the DimenParam
     */
    public	DimenParam(String name, Dimen val) { super(name, val); }

    /**
     * Creates a new DimenParam with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the DimenParam
     */
    public	DimenParam(String name) { super(name); }

    protected void	scanValue(Token src, boolean glob) {
        set(scanDimen(), glob);
	// System.err.println("= Assignment \\" + getName() + " = " + get());
    }

    protected void	perform(int operation, boolean glob)
	{ set(performFor(get(), operation), glob); }

    public String	getUnit() { return "pt"; }
    public boolean	hasDimenValue() { return true; }
    public Dimen	getDimenValue() { return get(); }

}
