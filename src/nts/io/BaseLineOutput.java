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
// Filename: nts/io/BaseLineOutput.java
// $Id: BaseLineOutput.java,v 1.1.1.1 1999/04/06 22:36:40 ksk Exp $
package	nts.io;

public abstract class	BaseLineOutput	extends LineOutput {

    protected int		charCount = 0;

    public final void		resetCount() { charCount = 0; }

    public final int		getCount() { return charCount; }

    public final void		add(String str)
	{ for (int i = 0; i < str.length(); i++) add(str.charAt(i)); }

    public void			endLine() { }

    public void			setStartLine() { }

    public boolean		isStartLine() { return false; }

    public boolean		stillFits(int count) { return true; }

    public LineOutput		voidCounter() { return new CountLineOutput(); }

}
