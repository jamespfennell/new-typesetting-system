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
// Filename: nts/command/InteractionPrim.java
// $Id: InteractionPrim.java,v 1.1.1.1 2001/02/28 19:52:43 ksk Exp $
package	nts.command;

public class	InteractionPrim extends AssignPrim {

    public static final int		BATCH = 0;
    public static final int		NONSTOP = 1;
    public static final int		SCROLL = 2;
    public static final int		ERROR_STOP = 3;

    public static final String[]	names =
    	{ "batchmode", "nonstopmode", "scrollmode", "errorstopmode" };

    private static int		value = ERROR_STOP;

    public static int		get() { return value; }

    public static boolean	isSilent() { return (value == BATCH); }
    public static boolean	isInteractive() { return (value > NONSTOP); }
    public static boolean	isErrStopping() { return (value == ERROR_STOP); }

    public static void		setScroll()
	{ if (value > SCROLL) value = SCROLL; }

    public static void		set(int val)
	{ value = val; setTermEnable(value != BATCH); }

    public static void		set(String mnem) {
	for (int i = 0; i < names.length; i++)
	    if (mnem.equals(names[i])) { set(i); return; }
	throw new RuntimeException("Invalid interaction: " + mnem);
    }

    static {	//XXX move the property dependency to nts.tex.TeX
        String	prop = System.getProperty("nts.interaction");
	if (prop != null) set(prop); else set(ERROR_STOP);
	//XXX works only as INITEX (without loading a format)
    }

    private int			mode;

    public InteractionPrim(String name, int mode)
	{ super(name); this.mode = mode; }

    protected void	assign(Token src, boolean glob)
	{ normLog.endLine(); set(mode); }

}
