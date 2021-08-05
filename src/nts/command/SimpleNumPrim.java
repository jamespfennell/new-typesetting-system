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
// Filename: nts/command/SimpleNumPrim.java
// $Id: SimpleNumPrim.java,v 1.1.1.1 1999/07/23 07:27:06 ksk Exp $
package	nts.command;

import	nts.base.Num;

public class	SimpleNumPrim	extends Prim
				implements Num.Provider {

    private Num		value;

    public SimpleNumPrim(String name, Num val)
	{ super(name); value = val; }

    public SimpleNumPrim(String name, int val)
	{ this(name, Num.valueOf(val)); }

    public SimpleNumPrim(String name) { this(name, Num.ZERO); }

    /* STRANGE
     * why does it talk about curren mode?
     */
    public void		exec(Token src) { illegalCommand(this); }

    public Num		get() { return value; }
    public void		set(Num val) { value = val; }
    public final int	intVal() { return get().intVal(); }
    public final void	set(int val) { set(Num.valueOf(val)); }
    public boolean	hasNumValue() { return true; }
    public Num		getNumValue() { return get(); }

}
