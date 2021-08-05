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
// Filename: nts/io/InputLine.java
// $Id: InputLine.java,v 1.1.1.1 1999/07/28 08:07:13 ksk Exp $
package	nts.io;

/**
 * The representation of one input line.
 */
public class	InputLine	implements Loggable {

    public static final InputLine	NULL = null;

    /** Value returned by |getNext| and |peekNext| when the line is finished */
    public static final CharCode	EOL = CharCode.NULL;

    public interface	Mapper {

	CharCode	map(char chr);
	CharCode	map(int num);
	CharCode	endLine();
	boolean		ignoreTrailing(char chr);

    }

    /** Array of internal character codes forming the input line */
    private CharCode[]		codes;

    private Mapper		mapper;

    /** Position of current internal character code */
    private int			pos;

    /**
     * Creates |InputLine| for given array of internal character codes.
     * @param	codes the array of internal character codes.
     */
    private InputLine(CharCode[] codes, Mapper mapper, int pos)
	{ this.codes = codes; this.mapper = mapper; this.pos = pos; }

    /**
     * Creates |InputLine| for given array of internal character codes.
     * @param	codes the array of internal character codes.
     */
    public InputLine(CharCode[] codes, Mapper mapper)
	{ this(codes, mapper, 0); }

    public InputLine(Mapper mapper)
	{ this(new CharCode[0], mapper); }

    /* For TeX algorithm of reading input line see TeXtp[31]. */
    /**
     * Creates |InputLine| for given |String| using an |Mapper|.
     * It ignores trailing characters which should be ignored
     * (usually spaces) and appends end line character if defined.
     * @param	str the string of characters.
     * @param	mapper the input mapper.
     */
    public InputLine(String str, Mapper mapper) {
	int		end = str.length();
	while (end > 0 && mapper.ignoreTrailing(str.charAt(end - 1))) end--;
	codes = new CharCode[end];
	this.mapper = mapper; pos = 0;
	for (int i = 0; i < end; i++)
	    codes[i] = mapper.map(str.charAt(i));
    }

    public InputLine(InputLine line)
	{ this(line.codes, line.mapper, line.pos); }

    public InputLine	addEndOfLineChar() {
	CharCode	elc = mapper.endLine();
	if (elc != CharCode.NULL) {
	    CharCode[]	newCodes = new CharCode[codes.length + 1];
	    System.arraycopy(codes, 0, newCodes, 0, codes.length);
	    newCodes[codes.length] = elc;
	    return new InputLine(newCodes, mapper, pos);
	}
	return this;
    }

    public InputLine	pureRest() {
	CharCode[]	newCodes = new CharCode[codes.length - pos];
	System.arraycopy(codes, pos, newCodes, 0, codes.length - pos);
	return new InputLine(newCodes, mapper, 0);
    }

    public boolean	wasEmpty(boolean addEolc) {
	if (addEolc) {
	    CharCode	elc = mapper.endLine();
	    if (elc != CharCode.NULL)
		return (codes.length == 1 && codes[0].match(elc));
	}
	return (codes.length == 0);
    }

    public boolean	empty()
        { return (pos >= codes.length); }

    public synchronized void		skipSpaces()
	{ while (pos < codes.length && codes[pos].match(' ')) pos++; }

    public synchronized CharCode	getNextRawCode()
	{ return (pos < codes.length) ? codes[pos++] : EOL; }

    public synchronized CharCode	peekNextRawCode()
	{ return (pos < codes.length) ? codes[pos] : EOL; }

    /**
     * Gives the next internal character code in the line.
     * Interprets the expanded chracter constructions ('^^').
     * @return	the next internal character code or |EOL|
     *		if the line is finished.
     */
     /* TeXtp[352,355] */
    public synchronized CharCode	getNext() {
	if (pos < codes.length) {
	    CharCode	code = codes[pos++];
	    char	c1, c2;	/* first and second |char| after  '^^' */
	    while (  pos + 1 < codes.length
		  && code.startsExpand() && code.match(codes[pos])
		  && (c1 = codes[pos + 1].toChar()) != CharCode.NO_CHAR
		  && c1 < 0200  ) {
		int	numCode;
		pos += 2;
		if (  pos < codes.length && isHexDigit(c1)
		   && (c2 = codes[pos].toChar()) != CharCode.NO_CHAR
		   && isHexDigit(c2)  ) {
		    pos++;
		    numCode = (digitForHex(c1) << 4) + digitForHex(c2);
		} else numCode = (c1 < 0100) ? c1 + 0100 : c1 - 0100;
		code = mapper.map(numCode);
	    }
	    return code;
	}
	return EOL;
    }

    /**
     * Tests whether given character is a lowercase hexadecimal digit which
     * can be part of hexadecimal expanded character construction.
     * @param	c the tested character.
     * @return	|true| if the tested character is lowercase hexadecimal digit.
     */
    private static boolean	isHexDigit(char c)
	{ return ('0' <= c && c <= '9' || 'a' <= c && c <= 'f'); }

    /**
     * Gives the number corresponding to lowercase hexadecimal digit.
     * The parameter must be tested by method |isHexDigit|, this method does
     * not check the validity.
     * @param	c the lowercase hexadecimal digit character.
     * @return	the number corresponding to digit.
     */
    private static int		digitForHex(char c)
	{ return (c <= '9') ? c - '0' : c - 'a' + 10; }

    /**
     * Gives the internal character code on the current position.
     * It does not advance the current position.
     * @return	the next internal character code or |EOL|
     *		if the line is finished.
     */
    /* STRANGE
     * This method has a horrible sideefect, it shufles the already readed
     * character codes. It's so because the result can be seen in context
     * trace and it has to be compatible with TeX.
     */
    public synchronized CharCode	peekNext() {
        int		oldPos = pos;
	CharCode	code = getNext();
	if (pos > oldPos + 1) {
	    CharCode[]	newCodes
		= new CharCode[oldPos + 1 + codes.length - pos];
	    System.arraycopy(codes, 0, newCodes, 0, oldPos);
	    newCodes[oldPos] = code;
	    System.arraycopy(codes, pos, newCodes, oldPos + 1,
			     codes.length - pos);
	    codes = newCodes;
	}
	pos = oldPos;
	return code;
    }

    public synchronized void	skipAll()
	{ pos = codes.length; }

    public void		addOn(Log log)
	{ log.add(codes, pos, codes.length - pos); }

    public int		addContext(Log left, Log right, boolean addEolc) {
	int		end = codes.length;
	if (addEolc && end > 0 && codes[end - 1].isEndLine()) end--;
	Log		log = left;
	for (int i = 0; i < end; i++) {
	    if (i == pos) log = right;
	    log.add(codes[i]);
	}
	return 1;
    }

}
