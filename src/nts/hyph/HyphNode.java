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
// Filename: nts/hyph/HyphNode.java
// $Id: HyphNode.java,v 1.1.1.1 2000/11/10 16:25:59 ksk Exp $
package	nts.hyph;

import	java.io.Serializable;

public class	HyphNode	implements Serializable {

    public static final HyphNode	NULL = null;
    public static final HyphNode	ZERO = new HyphNode(0, 0, NULL);

    public final int		offset;
    public final int		value;
    public final HyphNode	next;

    public HyphNode(int offset, int value, HyphNode next)
	{ this.offset = offset; this.value = value; this.next = next; }

    public boolean	equals(Object obj) {
	if (obj == this) return true;
	if (!(obj instanceof HyphNode)) return false;
	HyphNode	that = (HyphNode) obj;
	return (  offset == that.offset
	       && value == that.value
	       && next == that.next  );
    }

    public boolean	isZero()
	{ return (offset == 0 && value == 0 && next == NULL); }

    public int		hashCode() {
	int		code = 0;
	HyphNode	hyph = this;
	do {
	    code += 313 * (1009 * hyph.offset + hyph.value);
	    hyph = hyph.next;
	} while (hyph != NULL);
	return code;
    }

    public String	toString() {
	StringBuffer	buf = new StringBuffer();
	HyphNode	hyph = this;
	do {
	    buf.append(" ->").append(hyph.offset)
	       .append(": ").append(hyph.value);
	    hyph = hyph.next;
	} while (hyph != NULL);
	return buf.toString();
    }

}
