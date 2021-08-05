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
// Filename: nts/tfm/IndexMultimap.java
// $Id: IndexMultimap.java,v 1.1.1.1 1999/06/05 04:18:58 ksk Exp $
package	nts.tfm;

import	java.util.Vector;

/**
 * |IndexMultimap| can store and retrieve |int| values associated to
 * particular |int| key. There can be more values associated to the same key.
 * This class can be replaced by any generic associative container which
 * provides one to many mapping.
 */
public class	IndexMultimap {

    /**
     * The internal representation of (key, value) pair.
     */
    protected static class	Pair {

        /** The key */
    	int		key;

	/** The value */
	int		val;

	/**
	 * Makes new |Pair| with given key and value.
	 * @param	k the key
	 * @param	v the value
	 */
	Pair(int k, int v) { key = k; val = v; }

    }

    /*
     * The (key, value) pairs are kept in |Vector| sorted acording the key.
     */

    /** Internal storage of (key, value) pairs */
    private Vector	data = new Vector();

    /**
     * The number of (key, value) pairs kept.
     * @return	the number of stored pairs.
     */
    protected final int		size()
	{ return data.size(); }

    /**
     * (key, value) pair at given position.
     * @param	i the position of pair to be examined.
     * @return	the pair at given position.
     */
    protected final Pair	at(int i)
	{ return (Pair) data.elementAt(i); }

    /**
     * Insert a (key, value) pair at the given position.
     * @param	i the pair to be inserted.
     * @param	i the position to be inserted to.
     */
    protected final void	insert(Pair p, int i)
	{ data.insertElementAt(p, i); }

    /*
     * Searching is implemented by binary search algorithm. If any
     * (key, value) pair withe given key is present in the sorted sequence, it
     * returns the position of one of such pairs. If not it returns the
     * position where a new pair with given key should be inserted to keep the
     * the sequence sorted.
     */

    /**
     * Gives the position where a (key, value) pair with given key is stored
     * or where it should be stored if there is no such pair.
     * @param	key the key searched for.
     * @return	the position.
     */
    protected final int		search(int key) {
        int		beg = 0;
	int		end = size();
        while (beg < end) {
	    int		med = (beg + end) / 2;
	    Pair	p = at(med);
	    if (key < p.key) end = med;
	    else if (key > p.key) beg = med + 1;
	    else return med;
	}
	return beg;
    }

    /*
     * When adding a new (key, value) pair the position of some pair with
     * given key is found first, then it is inserted after all such pairs
     * (or to founded position if there is none such pair).
     */

    /**
     * Adds a new (key, value) pair.
     * @param	key the key of the new pair.
     * @param	val the value of the new pair.
     */
    public void	add(int key, int val) {
        synchronized (data) {
	    int	pos = search(key);
	    while (pos < size() && at(pos).key == key) pos++;
	    insert(new Pair(key, val), pos);
	}
    }

    /*
     * The class |Enum| is similar to interface |Enumeration| in the sense
     * that it provides methods for geting next value in a sequnce and for
     * testing the availability of the next value. But instead of |Object|s
     * it works with |ints|.
     */

    /**
     * Class |Enum| provides the sequence of all values associated to
     * particular key.
     */
    public class	Enum {

        /** the current position in the sequence of pairs */
        private	int		pos;

	/** the key for which the values are required */
	private final int	key;

        /**
	 * Makes new |Enum| for given key.
	 * @param	k the key for which the values are required.
	 */
	/*
	 * The constructor is private so only the enclosing class can
	 * instantiate it.
	 */
	private Enum(int k) {
	    synchronized (data) {
		key = k; pos = search(key);
		while (pos > 0 && at(pos - 1).key == key) pos--;
	    }
	}

	/**
	 * Tests if there is another associated value.
	 * @return	|true| if next value is available, |false| otherwise.
	 */
    	public final boolean	hasMore()
	    { return (pos < size() && at(pos).key == key); }

	/**
	 * Gives the next value from the sequence of associated values.
	 * @return	the next value.
	 */
	public final int	next()
	    { return at(pos++).val; }

    }

    /**
     * Gives the sequence of all keys associated to the given key.
     * @param	key the given key.
     * @return	the object representing the sequence of associated values.
     */
    public Enum		forKey(int key) { return new Enum(key); }

}
