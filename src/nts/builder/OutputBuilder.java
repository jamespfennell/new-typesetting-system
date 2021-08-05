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
// Filename: nts/builder/OutputBuilder.java
// $Id: OutputBuilder.java,v 1.1.1.1 2000/02/08 14:22:30 ksk Exp $
package	nts.builder;

import	nts.io.Log;

public class	OutputBuilder extends VBoxBuilder {

    public OutputBuilder(int line) { super(line); }

    /* TeXtp[218] */
    protected void	specialShow(Log log, int depth, int breadth) {
	log.add(" (\\output routine)");
	super.specialShow(log, depth, breadth);
    }

}
