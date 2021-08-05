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
// Filename: nts/command/OutputParam.java
// $Id: OutputParam.java,v 1.1.1.1 1999/05/31 11:18:50 ksk Exp $
package	nts.command;

public class	OutputParam	extends ToksParam {

    /**
     * Creates a new OutputParam with given name and value and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the OutputParam
     * @param	val the value of the OutputParam
     */
    public	OutputParam(String name, TokenList val) { super(name, val); }

    /**
     * Creates a new OutputParam with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the OutputParam
     */
    public	OutputParam(String name) { super(name); }

    protected TokenList	scanToksValue(Token src)
	{ return scanToks(src, true); }

}
