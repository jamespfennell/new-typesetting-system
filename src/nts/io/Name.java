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
// Filename: nts/io/Name.java
// $Id: Name.java,v 1.1.1.1 2000/06/10 05:20:32 ksk Exp $
package	nts.io;

import	java.io.Serializable;
import	java.util.Vector;

/**
 * This class represents a name consisting of internal characer codes. It is
 * used for control sequence names, names of primitive commands, etc.
 */
public class	Name	implements Serializable, Loggable {

    public static final	Name	NULL = null;
    public static final	Name	EMPTY = new Name();

    /**
     * |Buffer| is used for incremental building of |Name|.
     */
    public static class	Buffer	implements Serializable {

	/** Internal representaion of internal character string */
        private Vector		data;

	/**
	 * Makes a |Buffer| with default initial size.
	 */
    	public Buffer()
	    { data = new Vector(); }

	/**
	 * Makes a |Buffer| with given initial size.
	 * @param	size the initial size.
	 */
    	public Buffer(int size)
	    { data = new Vector(size); }

	/**
	 * Appends one internal character code on the end of the internal
	 * character string.
	 * @param	code the internal character code to be appended.
	 * @return	the |Buffer| for subsequent appends.
	 */
	public Buffer	append(CharCode code)
	    { data.addElement(code); return this; }

	/**
	 * Appends sequence of internal character codes on the end
	 * of the internal character string.
	 * @param	codes the array of internal character codes
	 * 		to be appended.
	 * @param	offset the starting offset of the sequence.
	 * @param	count the number of codes to be appended.
	 * @return	the |Buffer| for subsequent appends.
	 */
	public Buffer	append(CharCode[] codes, int offset, int count) {
	    data.ensureCapacity(data.size() + count);
	    while (count-- > 0) data.addElement(codes[offset++]);
	    return this;
	}

	/**
	 * Appends sequence of internal character codes on the end
	 * of the internal character string.
	 * @param	codes the array of internal character codes
	 * 		to be appended.
	 * @return	the |Buffer| for subsequent appends.
	 */
	public Buffer	append(CharCode[] codes)
	    { return append(codes, 0, codes.length); }

	/**
	 * Appends internal character string on the end of the internal
	 * character string.
	 * @param	name the internal character string to be appended.
	 * @return	the |Buffer| for subsequent appends.
	 */
	public Buffer	append(Name name)
	    { return append(name.codes); }

	public void	clear() { data.clear(); }

	public int	length() { return data.size(); }

	public CharCode	codeAt(int index)
	    { return (CharCode) data.elementAt(index); }

	public void	getCodes(int beg, int end, CharCode[] dst, int offset)
	    { while (beg < end) dst[offset++] = codeAt(beg++); }

	/**
	 * Gives a hash code for an internal character string.
	 * @return  the hash code for this object.
	 */
	public int	hashCode() {
	    int	h = 0;
	    for (int i = 0; i < length(); i++)
		h = h * 39 + codeAt(i).hashCode();
	    return h;
	}

	/**
	 * Compares this internal character string to the specified object.
	 * The result is |true| if and only if the the argument is not |null| and
	 * is the |Name| object representing the same internal character string.
	 * @param   o the object to compare this internal character string against.
	 * @return  |true| if the argument is equal, |false| otherwise.
	 */
	public final boolean	equals(Object o) {
	    if (o != null && o instanceof Name.Buffer) {
		Name.Buffer	other = (Name.Buffer) o;
		if (length() == other.length()) {
		    for (int i = 0; i < length(); i++)
			if (!codeAt(i).equals(other.codeAt(i)))
			    return false;
		    return true;
		}
	    }
	    return false;
	}


	/**
	 * Gives the |Name| corresponding to the contens of the |Buffer|.
	 * @return	the corresponding |Name|.
	 */
	public Name	toName() {
	    CharCode[]	codes = new CharCode[length()];
	    data.copyInto(codes);
	    return new Name(codes);
	}

    }

    /** Representation of internal character string */
    private CharCode[]		codes;

    /**
     * Makes a |Name| of given sequence of internal character codes.
     * @param	codes the internal character code array.
     */
    public Name(CharCode[] codes)
        { this.codes = codes; }

    /**
     * Makes a |Name| of given single internal character code.
     * @param	code the single internal character code.
     */
    public Name(CharCode code) {
	codes = new CharCode[1];
	codes[0] = code;
    }

    /**
     * Makes an empty |Name| (of length 0).
     */
    public Name()
	{ codes = new CharCode[0]; }

    /**
     * Tells the length of the |Name|.
     * @return	the length of this |Name|.
     */
    public final int	length() { return codes.length; }

    /**
     * Gives an internal character code at the given index.
     * An index ranges from |0| to |length() - 1|.
     * @param	index the index of the internal character code.
     * @return	the code at the specified index of this |Name|.
     * @exception	ArrayIndexOutOfBoundsException for invalid index.
     */
    public CharCode	codeAt(int index) { return codes[index]; }

    public void	getCodes(int beg, int end, CharCode[] dst, int offset)
	{ while (beg < end) dst[offset++] = codes[beg++]; }

    public void	getCodes(CharCode[] dst, int offset)
	{ getCodes(0, codes.length, dst, offset); }

    /**
     * Gives a hash code for an internal character string.
     * @return  the hash code for this object.
     */
    public int	hashCode() {
	int	h = 0;
	for (int i = 0; i < length(); i++)
	    h = h * 39 + codeAt(i).hashCode();
	return h;
    }

    /**
     * Compares this internal character string to the specified object.
     * The result is |true| if and only if the the argument is not |null| and
     * is the |Name| object representing the same internal character string.
     * @param   o the object to compare this internal character string against.
     * @return  |true| if the argument is equal, |false| otherwise.
     */
    public final boolean	equals(Object o) {
        if (o != null && o instanceof Name) {
	    Name	other = (Name) o;
	    if (length() == other.length()) {
	        for (int i = 0; i < length(); i++)
		    if (!codeAt(i).equals(other.codeAt(i)))
			return false;
		return true;
	    }
	}
	return false;
    }

    public boolean	match(Name x) {
        if (length() == x.length()) {
	    for (int i = 0; i < length(); i++)
	        if (!codeAt(i).match(x.codeAt(i))) return false;
	    return true;
	}
	return false;
    }

    public void		addOn(Log log) { log.add(codes); }
    public void		addEscapedOn(Log log) { log.addEsc().add(codes); }

    public void		addProperlyEscapedOn(Log log) {
        switch (codes.length) {
	    case 0:	log.addEsc("csname").addEsc("endcsname");	break;
	    case 1:	log.addEsc().add(codes[0]);
	    		if (codes[0].isLetter()) log.add(' ');		break;
	    default:	log.addEsc().add(codes).add(' ');		break;
	}
    }

    public String	toString() {
	StringBuffer	buf = new StringBuffer();
	for (int i = 0; i < length(); buf.append(codeAt(i++)));
	return buf.toString();
    }

}
