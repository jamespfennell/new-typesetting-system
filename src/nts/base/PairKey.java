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
// Filename: nts/base/PairKey.java
// $Id: PairKey.java,v 1.1.1.1 2001/05/16 21:40:47 ksk Exp $
package	nts.base;

import	java.io.Serializable;

/** Hashable pair of objects.
 * Used as a key to hash tables where association to pair of objects is
 * needed.
 *
 * @author	Karel Skoupy
 * @version	${VERSION}
 * @since	NTS1.0
 */
public final class	PairKey	implements Serializable {

    /** first item of the pair */
    public final Object		first;
    /** second item of the pair */
    public final Object		second;

    /** Constructor with for a given couple of objects.
     * @param	first	first item of the pair
     * @param	second	second item of the pair
     * @return	the pair [|first|, |second|]
     */
    public PairKey(Object first, Object second)
	{ this.first = first; this.second = second; }

    /** Hash code for this |PairKey| object
     * @return	hash code
     */
    public int		hashCode()
	{ return 1009 * first.hashCode() + second.hashCode(); }

    /** Comparison of this object against another object.
     * @param	o	object to compare to
     * @return	|true| if |o| is a |PairKey| and the first resp.
     *		the second items are equal, |false| otherwise.
     */
    public boolean		equals(Object o) {
	if (o != null && o instanceof PairKey) {
	    PairKey	k = (PairKey) o;
	    return (k.first.equals(first) && k.second.equals(second));
	}
	return false;
    }

}
