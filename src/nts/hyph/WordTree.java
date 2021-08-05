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
// Filename: nts/hyph/WordTree.java
// $Id: WordTree.java,v 1.1.1.1 2000/06/13 12:40:09 ksk Exp $
package	nts.hyph;

import	java.io.Serializable;
import	java.util.NoSuchElementException;
import	java.util.Enumeration;
import	java.util.HashMap;
import	java.util.BitSet;
import	java.util.Stack;
import	java.util.Map;

public class	WordTree	implements WordMap {

    public static final WordTree	NULL = null;
    public static final Entry		NULL_ENTRY = null;

    private static class	Entry	implements Serializable {

	final char		code;
	Object			value;
	Entry			below;
	Entry			next;

	public Entry(char code, Object value, Entry below, Entry next) {
	    this.code = code; this.value = value;
	    this.below = below; this.next = next;
	}

	public Entry(char code, Entry next) {
	    this.code = code; this.value = null;
	    this.below = NULL_ENTRY; this.next = next;
	}

    }

    private final Entry		root = new Entry('\000', NULL_ENTRY);
    private int			count = 0;
    private char		maxCode = 0;

    private static boolean	sameObjects(Object x, Object y)
	{ return (x == null) ? (y == null) : x.equals(y); }

    private Entry	find(Entry from, char code) {
	for (Entry ent = from.below; ent != NULL_ENTRY; ent = ent.next)
	    if (code == ent.code) return ent;
	    else if (code < ent.code) break;
	return NULL_ENTRY;
    }

    private Entry	get(Entry from, char code) {
	Entry		ent;
	boolean		first = true;
	for (ent = from.below; ent != NULL_ENTRY; ent = from.next) {
	    if (code == ent.code) return ent;
	    else if (code < ent.code) break;
	    first = false; from = ent;
	}
	ent = new Entry(code, ent);
	if (first) from.below = ent;
	else from.next = ent;
	if (maxCode < code) maxCode = code;
	count++;
	return ent;
    }

    private Entry	find(Entry from, String word) {
	for (int i = 0; i < word.length() && from != NULL_ENTRY; i++)
	    from = find(from, word.charAt(i));
	return from;
    }

    private Entry	get(Entry from, String word) {
	for (int i = 0; i < word.length(); i++)
	    from = get(from, word.charAt(i));
	return from;
    }

    public Object	get(String word) {
	Entry		ent = find(root, word);
	return (ent != NULL_ENTRY) ? ent.value : null;
    }

    public Object	put(String word, Object value) {
	Entry		ent = get(root, word);
	Object		old = ent.value;
	ent.value = value;
	return old;
    }

    public class	Seeker	implements WordMap.Seeker {

	private Entry		curr = root;

	public void		reset() { curr = root; }
	public boolean		isValid() { return (curr != NULL_ENTRY); }

	public void		seek(char code)
	    { if (curr != NULL_ENTRY) curr = find(curr, code); }

	public Object		get()
	    { return (curr != NULL_ENTRY) ? curr.value : null; }

    }

    public WordMap.Seeker	seeker() { return new Seeker(); }
    public Enumeration		entries() { return new Enum(); }

    private class	Enum	implements Enumeration {

	private Stack		stack = new Stack();
	private Entry		curr = root.below;

	private void	step() {
	    if (curr.below != NULL_ENTRY)
		{ stack.push(curr); curr = curr.below; }
	    else if (curr.next != NULL_ENTRY) curr = curr.next;
	    else for (;;) {
		if (stack.empty())
		    { curr = NULL_ENTRY; break; }
		curr = (Entry) stack.pop();
		if (curr.next != NULL_ENTRY)
		    { curr = curr.next; break; }
	    }
	}

	public boolean	hasMoreElements() { return (curr != NULL_ENTRY); }

	public Object	nextElement() {
	    if (curr == NULL_ENTRY)
		throw new NoSuchElementException("WordTree is finished");
	    Object	mapEnt = new Map.Entry() {

		private Entry		ent = curr;
		private String		key = makeKey();

		private String	makeKey() {
		    char[]	codes = new char[stack.size() + 1];
		    int		i;
		    for (i = 0; i < stack.size(); i++)
			codes[i] = ((Entry) stack.get(i)).code;
		    codes[i] = curr.code;
		    return new String(codes);
		}

		public Object	getKey() { return key; }
		public Object	getValue() { return ent.value; }

		public Object	setValue(Object value) {
		    Object	old = ent.value;
		    ent.value = value; return old;
		}

	    };
	    step(); return mapEnt;
	}

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
     */

    public static final Pack		NULL_PACK = null;
    
    private static class	Pack {

	final char		code;
	final Object		value;
	final Pack		below;
	final Pack		next;
	int			start = 0;

	public Pack(char code, Object value, Pack below, Pack next) {
	    this.code = code; this.value = value;
	    this.below = below; this.next = next;
	}

	public boolean	equals(Object obj) {
	    if (obj == this) return true;
	    if (!(obj instanceof Pack)) return false;
	    Pack	that = (Pack) obj;
	    return (  code == that.code && sameObjects(value, that.value)
		   && below == that.below && next == that.next  );
	}

	public int		hashCode() {
	    int		h = 191 * code;
	    if (value != null) h += 313 * value.hashCode();
	    if (below != NULL_PACK) h += 1009 * below.hashCode();
	    if (next != NULL_PACK) h += 1571 * next.hashCode();
	    return h;
	}

    }

    public WordMap	packed() {
    /*
	Compressor	comp = new Compressor();
	Pack		compressed = comp.compress(root.below);
	int		shared = comp.sharedCount();
	comp = null;
	if (count > 0) {
	    System.err.println("count = " + count);
	    System.err.println("shared = " + shared
			     + " (" + (100 * shared / count) + "%)");
	}
	long	time = System.currentTimeMillis();
	WordTrie.Entry[]	tab = (new Packer()).packed(compressed);
	time = System.currentTimeMillis() - time;
	System.err.println("packed = " + tab.length + " (" + time + "ms)");
    */
	Pack		compressed = (new Compressor()).compress(root.below);
	WordTrie.Entry[]	tab = (new Packer()).packed(compressed);
	return new WordTrie(tab, maxCode);
    }

    private static class	Compressor {

	private HashMap		shareMap = new HashMap();
	private int		shared = 0;

	public int	sharedCount() { return shared; }

	public Pack	compress(Entry ent) {
	    if (ent != NULL_ENTRY) {
		Pack	pack = new Pack(ent.code, ent.value,
					compress(ent.below),
					compress(ent.next));
		Pack	hashed = (Pack) shareMap.get(pack);
		if (hashed == NULL_PACK) shareMap.put(pack, pack);
		else { pack = hashed; shared++; }
		return pack;
	    }
	    return NULL_PACK;
	}

    }

/*
    private static class	Free {
	int		prev;
	int		next;
	Free(int prev, int next)
	    { this.prev = prev; this.next = next; }
    }
*/

    private class	Packer {

	private BitSet			taken;
	private BitSet			used;
	private WordTrie.Entry[]	table;

	public WordTrie.Entry[]		packed(Pack pack) {
	    taken = new BitSet(count); used = new BitSet(count);
	    taken.set(maxCode); used.set(0);
	    int		start = (pack != NULL_PACK) ? allocate(pack) : 0;
	    int		size = used.size();
	    while (!used.get(size - 1)) size--;
	    taken = used = null;
	    WordTrie.Entry[]	tab = new WordTrie.Entry[size];
	    tab[0] = new WordTrie.Entry('\000', start, null);
	    if (start != 0)
		{ table = tab; distribute(pack, start); table = null; }
	    return tab;
	}

	private void	distribute(Pack pack, int start) {
	    do {
		table[start + pack.code]
		    = new WordTrie.Entry(pack.code, pack.start, pack.value);
		if (pack.start != 0)
		     distribute(pack.below, pack.start);
		pack = pack.next;
	    } while (pack != NULL_PACK);
	}

	private int	allocate(Pack pack) {
	    int		start = firstFit(pack);
	    do {
		if (pack.below != NULL_PACK && pack.start == 0)
		    pack.start = allocate(pack.below);
		pack = pack.next;
	    } while (pack != NULL_PACK);
	    return start;
	}

	private int	firstFit(Pack pack) {
	    int		start = 1 - pack.code;
	    for (;;) {
		while (taken.get(start + maxCode)) start++;
		for (Pack p = pack;;) {
		    if (used.get(start + p.code)) break;
		    p = p.next;
		    if (p == NULL_PACK) {
			taken.set(start + maxCode);
			Pack	q = pack;
			do { used.set(start + q.code); q = q.next; }
			while (q != NULL_PACK);
			return start;
		    }
		}
		start++;
	    }
	}

    }

}
