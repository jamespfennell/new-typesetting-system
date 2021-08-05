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
// Filename: nts/command/DefCodePrim.java
// $Id: DefCodePrim.java,v 1.1.1.1 2000/01/28 04:58:19 ksk Exp $
package	nts.command;

import	nts.base.Num;
import	nts.base.EqTable;
import	nts.io.Log;

/**
 */
public class	DefCodePrim	extends AssignPrim
				implements Num.Provider {

    protected final NumKind	tabKind = new NumKind();

    protected final Num		defVal;

    protected final int		maxVal;

    /**
     * Creates a new |DefCodePrim| with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the |DefCodePrim|
     */
    public DefCodePrim(String name, int defVal, int maxVal) {
	super(name);
	this.defVal = (defVal != 0) ? Num.valueOf(defVal) : Num.ZERO;
	this.maxVal = maxVal;
    }

    protected final void	set(int idx, Num val, boolean glob) {
	if (glob) getEqt().gput(tabKind, idx, val);
	else getEqt().put(tabKind, idx, val);
    }

    public Num			get(int idx) {
        Num	val = (Num) getEqt().get(tabKind, idx);
	return (val != Num.NULL) ? val : defVal;
    }

    public void		addEqValueOn(int idx, Log log)
	{ log.add(get(idx).toString()); }

    /**
     * Performs the assignment.
     * @param	src source token for diagnostic output.
     * @param	glob indication that the assignment is global.
     */
    /* TeXtp[1232] */
    protected void	assign(Token src, boolean glob) {
	int	idx = scanCharacterCode();
        skipOptEquals();
	int	val = scanInt();
	if (val < 0 || val > maxVal) {
	    error("CodeOutOfRange", num(val), num(maxVal));
	    val = 0;
	}
	set(idx, Num.valueOf(val), glob);
    }

    public final boolean	hasNumValue() { return true; }
    public final Num		getNumValue()
	{ return get(scanCharacterCode()); }

    public final int		intVal(int idx) { return get(idx).intVal(); }

    public final void		init(int idx, int val)
        { set(idx, Num.valueOf(val), true); }

}
