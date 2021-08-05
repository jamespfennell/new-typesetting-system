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
// Filename: nts/noad/Egg.java
// $Id: Egg.java,v 1.1.1.1 2000/10/05 00:17:09 ksk Exp $
package	nts.noad;

import	nts.base.Dimen;

public abstract class	Egg	implements TransfConstants {

    public static final	Egg	NULL = null;

    public abstract Dimen	getHeight();
    public abstract Dimen	getDepth();
    public abstract void	chipShell(Nodery nodery);
    public abstract byte	spacingType();

    public boolean	isBin() { return false; }
    public void		dontBeBin() { }
    public boolean	isPenalty() { return false; }
    public boolean	ignoreNextScriptSpace() { return false; }
    public Dimen	getItalCorr() { return Dimen.NULL; }
    public void		suppressItalCorr() { }

}
