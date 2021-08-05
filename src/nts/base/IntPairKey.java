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
// Filename: nts/base/IntPairKey.java
// $Id: IntPairKey.java,v 1.1.1.1 2001/05/16 21:40:27 ksk Exp $
package	nts.base;

import	java.io.Serializable;

/** Hashable pair of integers.
 * Used as a key to hash tables where association to pair of integers is
 * needed (e.g. sparse matrix).
 *
 * @author	Karel Skoupy
 * @version	${VERSION}
 * @since	NTS1.0
 */
public final class	IntPairKey	implements Serializable {

    /** first item of the pair */
    public final int		first;
    /** second item of the pair */
    public final int		second;

    /** Constructor with for a given couple of integers.
     * @param	first	first item of the pair
     * @param	second	second item of the pair
     * @return	the pair [|first|, |second|]
     */
    public IntPairKey(int first, int second)
	{ this.first = first; this.second = second; }

    /** Hash code for this |IntPairKey| object
     * @return	hash code
     */
    public int		hashCode()
	{ return 1009 * first + second; }

    /** Comparison of this object against another object.
     * @param	o	object to compare to
     * @return	|true| if |o| is an |IntPairKey| and the first resp.
     *		the second items have the same value, |false| otherwise.
     */
    public boolean		equals(Object o) {
	if (o != null && o instanceof IntPairKey) {
	    IntPairKey	k = (IntPairKey) o;
	    return (first == first && k.second == second);
	}
	return false;
    }

}
