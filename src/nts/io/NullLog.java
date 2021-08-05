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
// Filename: nts/io/NullLog.java
// $Id: NullLog.java,v 1.1.1.1 1999/06/09 10:08:22 ksk Exp $
package	nts.io;

public class	NullLog	implements Log {

    public static final NullLog		LOG = new NullLog();

    public final Log	add(char ch) { return this; }
    public final Log	add(char ch, int count) { return this; }
    public final Log	add(String str) { return this; }
    public final Log	add(CharCode x) { return this; }
    public final Log	endLine() { return this; }
    public final Log	startLine() { return this; }
    public final Log	flush() { return this; }
    public final void	close() { }

    public final Log	add(boolean val) { return this; }
    public final Log	add(int num) { return this; }

    public final Log	addEsc() { return this; }
    public final Log	addEsc(String str) { return this; }
    public final Log	add(Loggable x) { return this; }
    public final Log	add(Loggable[] array) { return this; }
    public final Log	add(Loggable[] array, int offset, int length)
	{ return this; }

    public final Log	resetCount() { return this; }
    public final int	getCount() { return 0; }
    public final Log	voidCounter() { return LOG; }
    public final Log	sepRoom(int count) { return this; }

}
