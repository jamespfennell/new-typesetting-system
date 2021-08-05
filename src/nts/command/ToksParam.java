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
// Filename: nts/command/ToksParam.java
// $Id: ToksParam.java,v 1.1.1.1 2000/01/05 00:41:15 ksk Exp $
package	nts.command;

import	nts.io.Log;

/**
 * Setting tokens parameter primitive.
 */
public class	ToksParam	extends ParamPrim
				implements TokenList.Provider,
					   TokenList.Inserter {

    private TokenList		value;

    /**
     * Creates a new ToksParam with given name and value and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the ToksParam
     * @param	val the value of the ToksParam
     */
    public	ToksParam(String name, TokenList val)
	{ super(name); value = val; }

    /**
     * Creates a new ToksParam with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the ToksParam
     */
    public	ToksParam(String name) { this(name, TokenList.EMPTY); }

    public final Object		getEqValue() { return value; }

    public final void	setEqValue(Object val) { value = (TokenList) val; }

    public final void	addEqValueOn(Log log)
	{ value.addOn(log, getConfig().getIntParam(INTP_MAX_TLRES_TRACE)); }

    public final TokenList	get() { return value; }

    public void			set(TokenList val, boolean glob)
	{ beforeSetting(glob); value = val; }

    protected void	scanValue(Token src, boolean glob)
        { set(scanToksValue(src), glob); }

    protected TokenList	scanToksValue(Token src)
	{ return scanToks(src, false); }

    public boolean	hasToksValue() { return true; }
    public TokenList	getToksValue() { return value; }

    public void		insertToks()
	{ tracedPushList(value, getName()); }

    public boolean	isEmpty() { return (value.isEmpty()); }

}
