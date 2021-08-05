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
// Filename: nts/command/TracingOnlineParam.java
// $Id: TracingOnlineParam.java,v 1.1.1.1 1999/06/08 22:05:48 ksk Exp $
package	nts.command;

import	nts.base.Num;

public class	TracingOnlineParam	extends NumParam {

    public	TracingOnlineParam(String name, Num val) { super(name, val); }
    public	TracingOnlineParam(String name, int val) { super(name, val); }
    public	TracingOnlineParam(String name) { super(name); }

    public void		set(Num val, boolean glob)
	{ super.set(val, glob); setDiagOnTerm(intVal() > 0); }

}
