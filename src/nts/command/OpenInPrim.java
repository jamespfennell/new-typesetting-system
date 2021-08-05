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
// Filename: nts/command/OpenInPrim.java
// $Id: OpenInPrim.java,v 1.1.1.1 1999/07/28 08:07:12 ksk Exp $
package	nts.command;

public class	OpenInPrim	extends Prim {

    private ReadPrim		read;

    public OpenInPrim(String name, ReadPrim read)
	{ super(name); this.read = read; }

    /* TeXtp[1275] */
    public void		exec(Token src) {
        int		num = scanFileCode();
	skipOptEquals();
	read.set(num, getIOHandler().openRead(scanFileName(), num));
    }

}
