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
// Filename: nts/hyph/ArrayHyphens.java
// $Id: ArrayHyphens.java,v 1.1.1.1 2000/10/18 21:48:24 ksk Exp $
package	nts.hyph;

import	nts.node.Hyphens;

public class	ArrayHyphens	implements Hyphens {

    public static final ArrayHyphens	EMPTY
	= new ArrayHyphens(new boolean[0]);

    private final boolean[]	array;

    public ArrayHyphens(boolean[] array) { this.array = array; }

    public boolean	isEmpty() { return (array.length == 0); }

    public boolean	hyphenAt(int pos)
	{ return (pos < array.length && array[pos]); }

    public String	toString(String word) {
	StringBuffer	buf = new StringBuffer(2 * word.length() + 1);
	for (int i = 0; i < word.length(); i++) {
	    if (hyphenAt(i)) buf.append('-');
	    buf.append(word.charAt(i));
	}
	if (hyphenAt(word.length())) buf.append('-');
	return buf.toString();
    }

    public static ArrayHyphens	forPositions(int[] positions,
					     int start, int end) {
	if (start <= end) {
	    int		i = 0;
	    while (i < positions.length && positions[i] < start) i++;
	    int		l = positions.length; 
	    while (--l > 0 && positions[l] > end);
	    if (i <= l) {
		boolean[]	array = new boolean[positions[l] + 1];
		while (i <= l) array[positions[i++]] = true;
		return new ArrayHyphens(array);
	    }
	}
	return EMPTY;
    }

}
