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
// Filename: nts/tfm/TeXFmLoader.java
// $Id: TeXFmLoader.java,v 1.1.1.1 2001/02/22 01:53:10 ksk Exp $
package	nts.tfm;

import	java.io.IOException;
import	java.io.InputStream;
import	java.io.FileInputStream;
import	java.io.BufferedInputStream;
import	nts.tfm.TeXFm.*;

/*
 * The only purpose of this class is to load a tfm file and construct an
 * |TeXFm| instance of it. The reason of writing this class
 * is that it takes out the task of loading from the |TeXFm|
 * class --- otherwise it would be too large. The loading is however
 * independent on the rest of the functionality of the metric class so it
 * seems quite natural. This class is the only class which reads the tfm file.
 *
 * Note that all inner classes of |TeXFm| are imported (in the
 * last import statement) as they are used extensively.
 */

/**
 * This class loads tfm file when instantiated and creates an
 * |TeXFm| (or its subtype). The path name of tfm file is
 * provided as parameter to the constructor and the resulting metric object
 * can be retrieved by its |getMetric| method.
 */
class	TeXFmLoader {

    /*
     * The form (and even presence) of error messages is independent
     * on loading and not included here. The same loader can be used
     * in |TFtoPL| with error messaging provided as well as in \TeX\
     * counterpart where only the overall status is interesting.
     */

    /** The object for reporting error messages (may be |null|) */
    TeXFmDiagnostic		diagnostic;

    /**
     * Reports a fatal error and throws |BadTeXFmException|.
     * @param	s the text of error message.
     * @param	dg object for reporting the message.
     * @exception	BadTeXFmException always.
     */
    protected static void	abort(String s, TeXFmDiagnostic dg)
    					throws BadTeXFmException {
        if (dg != null) dg.fatal(s);
        throw new BadTeXFmException(s);
    }

    /**
     * Reports a fatal error and throws |BadTeXFmException|.
     * @param	s the text of the error message.
     * @exception	BadTeXFmException always.
     */
    protected void	abort(String s) throws BadTeXFmException
        { abort(s, diagnostic); }

    /**
     * Reports a recoverable error but throws |BadTeXFmException|
     * if there is no interest to continue loading --- i.e. the |diagnostic|
     * is not set or the |abortOnError()| is true.
     * @param	s the text of the error message.
     * @exception	BadTeXFmException if necessary.
     */
    protected void	bad(String s) throws BadTeXFmException {
        if (diagnostic != null) diagnostic.error(s);
	if (diagnostic == null || diagnostic.abortOnError())
	    throw new BadTeXFmException(s);
    }

    /**
     * Reports a warning message.
     * @param	s the text of the warning message.
     */
    protected void	warning(String s)
        { if (diagnostic != null) diagnostic.warning(s); }

    /** Byte input stream for reading the contens of tfm file */
    protected InputStream		input;

    /**
     * Creates an |TeXFmLoader| by loading the tfm file and
     * creating a |TeXFm| object of it.
     * @param	in	InputStream to read the tfm data from.
     * @param	dg	object to use for error messages.
     * @exception	IOException if an I/O error occurs or if the tfm file
     *			is malformed.
     */
     /* See TFtoPL[96..99] */
    protected TeXFmLoader(InputStream in, TeXFmDiagnostic dg)
    						throws IOException {
	input = in;
	diagnostic = dg;
	try {
	    readLengths();
	    readHeader();
	    readTables();
	    checkTables();
	    makeLigTable();
	    checkExtens();
	    makeCharTable();
	    makeMetric();
	} finally { input.close(); }
    }

    /**
     * Creates an |TeXFmLoader| by loading the tfm file and
     * creating a |TeXFm| object of it.
     * @param	in	InputStream to read the tfm data from.
     * @exception	IOException if an I/O error occurs or if the tfm file
     *			is malformed.
     */
     /* See TFtoPL[96..99] */
    protected TeXFmLoader(InputStream in) throws IOException
	{ this(in, null); }

    /**
     * Creates an |TeXFmLoader| by loading the tfm file and
     * creating a |TeXFm| object of it.
     * @param	path	path name of the tfm file.
     * @param	dg	object to use for error messages.
     * @exception	IOException if an I/O error occurs or if the tfm file
     *			is malformed.
     */
     /* See TFtoPL[96..99] */
    protected TeXFmLoader(String path, TeXFmDiagnostic dg) throws IOException
	{ this(new BufferedInputStream(new FileInputStream(path)), dg); }

    /**
     * Creates an |TeXFmLoader| by loading the tfm file and
     * creating a |TeXFm| object of it.
     * @param	path	path name of the tfm file.
     * @exception	IOException if an I/O error occurs or if the tfm file
     *			is malformed.
     */
    protected TeXFmLoader(String path) throws IOException
        { this(path, null); }

    /*
     * Following 4 constants logically belong to the |AuxCharInfo| inner class
     * but if they are inside, Java compiler does not treat them as constant
     * outside. I really do not know why!
     *
     * For the original source see TFtoPL[12].
     */

    /** Value of |AuxCharInfo.tag()| tagging normal character */
    protected static final byte		NO_TAG = 0;

    /**
     * Value of |AuxCharInfo.tag()| tagging character with lig/kern program
     */
    protected static final byte		LIG_TAG = 1;

    /**
     * Value of |AuxCharInfo.tag()| tagging non last character in a chain
     * of larger characters.
     */
    protected static final byte		LIST_TAG = 2;

    /** Value of |AuxCharInfo.tag()| which tagging with extensible recipe. */
    protected static final byte		EXT_TAG = 3;

    /*
     * |AuxCharInfo| reflect the raw data structure used in tfm file for
     * character information. It provides methods which interpret the data
     * structure acording the definition from tftopl. Because the Java bytes
     * are signed it masks most of the values with apropriate hexadecimal
     * mask.
     *
     * For description of original data structure see TFtoPL[11].
     */

    /**
     * Data structure for raw character information from tfm file.
     */
    protected static class	AuxCharInfo {

	/** Index to the width table */
	private byte	_width_index;

	/** Indexes to the height and depth tables. */
	private byte	_height_depth_index;

	/** Index to the italic correction table and the tag */
	private byte	_italic_index_tag;

	/** Remainder which meaning is determined by value of tag */
	private byte	_remainder;

	/**
	 * Index to newly created |ligKernTable| which is set during
	 * translation of the original raw lig/kern table in the tfm file.
	 */
	int		lig_kern_start;

	/**
	 * Tells if the character of this |AuxCharInfo| exists in the font.
	 * @return	|true| if the character exists.
	 */
	boolean		exists()
			    { return _width_index != 0; }

	/**
	 * Gives the index to the width table from the tfm file.
	 * @return	the index to |widthTable|.
	 */
	int		widthIndex()
			    { return _width_index & 0xff; }

	/**
	 * Gives the index to the height table from the tfm file.
	 * @return	the index to |heightTable|.
	 */
	int		heightIndex()
			    { return _height_depth_index >> 4 & 0x0f; }

	/**
	 * Gives the index to the depth table from the tfm file.
	 * @return	the index to |depthTable|.
	 */
	int		depthIndex()
			    { return _height_depth_index & 0x0f; }

	/**
	 * Gives the index to the italic correction table from the tfm file.
	 * @return	the index to |italicTable|.
	 */
	int		italicIndex()
			    { return _italic_index_tag >> 2 & 0x3f; }

	/**
	 * Gives the tag field of the character information data.
	 * @return	the tag value.
	 */
	byte		tag()
			    { return (byte) (_italic_index_tag & 0x03); }

	/**
	 * Resets the tag field to NO_TAG (zero) value.
	 */
	void		resetTag()
			    { _italic_index_tag &= ~0x03; }

	/**
	 * Gives the value of remainder which meaning is dependent on the tag
	 * field value.
	 * @return	the uninterpreted tag.
	 */
	private short	remainder()
			    { return (short) (_remainder & 0xff); }

	/**
	 * Gives the remainder value interpreted as the index to the raw
	 * lig/kern table from tfm file.
	 * @return	starting of the lig/kern program in |ligAuxTab|.
	 */
	int		ligStart() { return remainder(); }

	/**
	 * Gives the remainder value interpreted as the code of next character
	 * in the chain of larger characters.
	 * @return	the next larger character.
	 */
	short		biggerChar() { return remainder(); }

	/**
	 * Gives the remainder value interpreted as the index to the table of
	 * extensible recipes from tfm file.
	 * @return	the index to the |extAuxTab|.
	 */
	int		extenIndex() { return remainder(); }

	/**
	 * Creates |AuxCharInfo| by reading four bytes from the tfm file.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
        AuxCharInfo(InputStream in, TeXFmDiagnostic dg) throws IOException {
	    _width_index	= (byte) readByte(in, dg);
	    _height_depth_index	= (byte) readByte(in, dg);
	    _italic_index_tag	= (byte) readByte(in, dg);
	    _remainder		= (byte) readByte(in, dg);
	}

	/**
	 * Reads a table of |AuxCharInfo|.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @param	count the number of character information to read.
	 * @return	the array of character information read.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
	static final AuxCharInfo[]
		    readTable(InputStream in, TeXFmDiagnostic dg, int count)
		    				throws IOException {
	    AuxCharInfo[]	table = new AuxCharInfo[count];
	    for (int i = 0; i < count; i++)
		table[i] = new AuxCharInfo(in, dg);
	    return table;
	}

    }

    /*
     * |AuxLigKern| reflect the data structure for lig/kern step in tfm file
     * in similar way as |AuxCharInfo|.
     * For description of original data structure see TFtoPL[13].
     */

    /**
     * The data structure for lig/kern instruction from tfm file.
     */
    protected static class	AuxLigKern {

	/** Value of |skip_byte()| which indicates the boundary information */
	private static final short	BOUNDARY_FLAG = 255;

	/**
	 * Value of |skip_byte()| which indicates the last instruction in a
	 * lig/kern program.
	 */
	private static final short	STOP_FLAG = 128;

	/** Value of |op_byte()| which indicates the kerning instruction */
	private static final short	KERN_FLAG = 128;

	/** Amount of skip or a stop or boundary flag */
        private byte		_skip_byte;

	/**
	 * Code of character which must be next to the current one to
	 * activate instruction.
	 */
	private byte		_next_char;

	/** Encoded ligature or kerning operation. */
	private byte		_op_byte;

	/** Remainder which meaning depends on the value of |op_byte|. */
	private byte		_remainder;

	/**
	 * Gives the unsigned value of the |_skip_byte|.
	 * @return	the amount of skip or the stop or boundary flag.
	 */
	private short	skip_byte()
				{ return (short) (_skip_byte & 0xff); }

 	/**
	 * Gives the unsigned value of the |_op_byte|.
	 * @return	the encoded ligature or kern operation.
	 */
	private short	op_byte()
				{ return (short) (_op_byte & 0xff); }

 	/**
	 * Gives the unsigned value of uninterpreted remainder.
	 * @return	the remainder which meaning depends on the value
	 *		of |op_byte()|.
	 */
	private short	remainder()
				{ return (short) (_remainder & 0xff); }

	/**
	 * Tells whether this |AuxLigKern| contains information about boundary
	 * (it must be also first or last in the lig/kern table).
	 * @return	|true| if it contains boundary information.
	 */
	boolean		meansBoundary()
			    { return (skip_byte() == BOUNDARY_FLAG); }

	/**
	 * Tells whether this |AuxLigKern| redirects the actual start of
	 * a lig/kern program to some other instruction (it must be also the
	 * first instruction of some lig/kern program).
	 * @return	|true| if it is a restart instruction.
	 */
	boolean		meansRestart()
			    { return (skip_byte() > STOP_FLAG); }

	/**
	 * Tells whether this |AuxLigKern| is the last instruction of a
	 * lig/kern program.
	 * @return	|true| if this is the last instruction of a lig/kern
	 *		program.
	 */
	boolean		meansStop()
			    { return (skip_byte() >= STOP_FLAG); }

	/**
	 * Tells the position of the next lig/kern program instruction given
	 * the position of this |AuxLigKern| in the lig/kern table.
	 * @return	index to the |ligAuxTab| of the next lig/kern
	 *		instruction.
	 */
        int		nextIndex(int pos)
			    { return pos + skip_byte() + 1; }

	/**
	 * Forces this |AuxLigKern| to be the last instruction in a lig/kern
	 * program.
	 */
	void		makeStop()
			    { _skip_byte = (byte) STOP_FLAG; }

	/**
	 * Gives the code of the character which must be next to the current
	 * character if this instruction has to be activated.
	 * @return	the next character code.
	 */
	short		nextChar()
				{ return (short) (_next_char & 0xff); }

	/**
	 * Forces this |AuxLigKern| to have particular value of |nextChar()|.
	 * @param	c the forced value of |nextChar()|.
	 */
	void		setNextChar(int c)
				{ _next_char = (byte) c; }

	/**
	 * Gives actual starting index of the lig/kern program for restart
	 * instruction.
	 * @return	the actual start of lig/kern program.
	 */
	int		restartIndex()
			    { return (op_byte() << 8) + remainder(); }

	/**
	 * Tells whether this |AuxLigKern| is a kerning instruction.
	 * @return	|true| for kerning instruction.
	 */
	boolean		meansKern()
			    { return op_byte() >= KERN_FLAG; }

	/**
	 * Gives the index to the kern table from tfm file for kerning
	 * instruction.
	 * @return	the index to the |kernTable|.
	 */
	int		kernIndex()
	    { return (op_byte() - KERN_FLAG << 8) + remainder(); }

	/**
	 * Tells whether the current character should be left in place when
	 * executing this ligature instructions.
	 * @return	|true| if the current character should be left.
	 */
	boolean		leaveLeft()
			    { return (op_byte() & 0x02) != 0; }

	/**
	 * Tells whether the next character should be left in place when
	 * executing this ligature instructions.
	 * @return	|true| if the next character should be left.
	 */
	boolean		leaveRight()
			    { return (op_byte() & 0x01) != 0; }

	/**
	 * Tells how many character should be skipped over after executing
	 * this ligature instruction.
	 * @return the number of characters to be skipped.
	 */
	byte		stepOver()
			    { return (byte) (op_byte() >>> 2); }

	/**
	 * Gives the code of charcter which should be inserted between the
	 * current and the next characters when executing this ligature
	 * instruction.
	 * @return	the code of the character to be inserted.
	 */
	short		ligChar()
			    { return remainder(); }

	/**
	 * Forces the |ligChar()| to have particular value.
	 * @param	c the forced value of |ligChar()|.
	 */
	void		setLigChar(short c)
			    { _remainder = (byte) c; }

	/**
	 * Creates |AuxLigKern| by reading four bytes from the tfm file.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
	AuxLigKern(InputStream in, TeXFmDiagnostic dg) throws IOException {
	    _skip_byte = (byte) readByte(in, dg);
	    _next_char = (byte) readByte(in, dg);
	    _op_byte   = (byte) readByte(in, dg);
	    _remainder = (byte) readByte(in, dg);
	}

	/**
	 * Reads a table of |AuxLigKern|.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @param	count the number of lig/kern instructions to read.
	 * @return	the array of lig/kern instructions read.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
	static final AuxLigKern[]
		    readTable(InputStream in, TeXFmDiagnostic dg, int count)
		    				throws IOException {
	    AuxLigKern[]	table = new AuxLigKern[count];
	    for (int i = 0; i < count; i++)
		table[i] = new AuxLigKern(in, dg);
	    return table;
	}

	/**
	 * The value of |activity| field which means that this lig/kern
	 * instruction is not a part of lig/kern program for any character.
	 */
        static final byte	UNREACHABLE = 0;

	/**
	 * The value of |activity| field which means that this is restart
	 * instruction or the boundary information which was processed.
	 */
        static final byte	PASS_THROUGH = 1;

	/**
	 * The value of |activity| field which means that this lig/kern
	 * instruction is a part of lig/kern program for some character.
	 */
        static final byte	ACCESSIBLE = 2;

	/** The flag determining the status of this lig/kern instruction. */
	byte		activity = UNREACHABLE;

    }

    /*
     * |AuxExtRecipe| reflect the data structure for extensible recipe in tfm
     * file in similar way as |AuxCharInfo|.
     * For description of original data structure see TFtoPL[14].
     */

    /**
     * The data structure for extensible recipe from tfm file.
     */
    protected static class AuxExtRecipe {

	/** Character code of the top part of extensible character. */
	short		top;

	/** Character code of the middle part of extensible character. */
	short		mid;

	/** Character code of the bottom part of extensible character. */
	short		bot;

	/** Character code of the repeatable part of extensible character. */
	short		rep;

	/**
	 * Creates |AuxExtRecipe| by reading four bytes from the tfm file.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
	AuxExtRecipe(InputStream in, TeXFmDiagnostic dg) throws IOException {
	    top = readByte(in, dg);
	    mid = readByte(in, dg);
	    bot = readByte(in, dg);
	    rep = readByte(in, dg);
	}

	/**
	 * Reads a table of |AuxExtRecipe|.
	 * @param	in the tfm byte input stream.
	 * @param	dg the object for reporting error messages.
	 * @param	count the number of extensible recipes to read.
	 * @return	the array of extensible recipes read.
	 * @exception	IOException if an I/O error occures or the end of file
	 *		is reached.
	 */
	static final AuxExtRecipe[]
		    readTable(InputStream in, TeXFmDiagnostic dg, int count)
		    				throws IOException {
	    AuxExtRecipe[]	table = new AuxExtRecipe[count];
	    for (int i = 0; i < count; i++)
		table[i] = new AuxExtRecipe(in, dg);
	    return table;
	}

    }

    /*
     * The following member variables are set to the lengths of particular tfm
     * file areas. All lengths are in words (4 byte units).
     * For the description of original data structures see TFtoPL[8].
     */

    /** The length of the whole tfm file (in words). */
    protected int		fileLength;

    /** The length of the tfm file header. */
    protected int		headerLength;

    /** Code of the first charactre present in this font */
    protected short		firstCharCode;

    /** The number of character information structures in this tfm file */
    protected int		charCount;

    /** The length of the width array (|widthTable|). */
    protected int		widthCount;

    /** The length of the height array (|heightTable|). */
    protected int		heightCount;

    /** The length of the depth array (|depthTable|). */
    protected int		depthCount;

    /** The length of the italic correction array (|italicTable|). */
    protected int		italicCount;

    /** The length of the raw lig/kern instruction table in tfm file */
    protected int		ligAuxLen;

    /** The length of the kern amounts array (|kernTable|). */
    protected int		kernCount;

    /** The length of the raw extensible recipe table in tfm file */
    protected int		extAuxCnt;

    /** The number of font dimension parameters (length of |paramTable|). */
    protected int		paramCount;

    /**
     * Reads the lengths from tfm file and gives the error diagnostic (and/or
     * throws an exception) if necessary.
     * @exception	IOException if an I/O error ocurrs or if the tfm file
     *			is malformed.
     */
     /* See TFtoPL[21..23] */
    protected void	readLengths() throws IOException {

	fileLength	= readFileLength();
	headerLength	= readLength();
	firstCharCode	= readLength();
	short lastChar	= readLength();
	widthCount	= readLength();
	heightCount	= readLength();
	depthCount	= readLength();
	italicCount	= readLength();
	ligAuxLen	= readLength();
	kernCount	= readLength();
	extAuxCnt	= readLength();
	paramCount	= readLength();

	if (headerLength < 2)
	    abort("The header length is only " + headerLength + '!');

	if (firstCharCode > lastChar + 1 || lastChar > 255)
	    abort("The character code range " + firstCharCode
		  + ".." + lastChar + "is illegal!");

	charCount	= lastChar + 1 - firstCharCode;

	if (charCount == 0) firstCharCode = 0;

	if (  widthCount == 0 || heightCount == 0
	   || depthCount == 0 || italicCount == 0  )
	    abort("Incomplete subfiles for character dimensions!");

	if (extAuxCnt > 256)
	    abort("There are " + extAuxCnt + " extensible recipes!");

	if (fileLength != 6 + headerLength + charCount
			    + widthCount + heightCount
			    + depthCount + italicCount
			    + ligAuxLen + kernCount
			    + extAuxCnt + paramCount)
	    abort("Subfile sizes don't add up to the stated total!");
    }

    /**
     * Reads the length of the whole tfm file (first 2 bytes).
     * @exception	IOException if an I/O error ocurrs or if the tfm file
     *			is malformed.
     */
     /* See TFtoPL[20] */
    protected int	readFileLength() throws IOException {
	int		i = readByte();
	if (i < 0)
	    abort("The input file is empty!");
	if (i > 127)
	    abort("The first byte of the input file exceeds 127!");

	int		len = i << 8;
	i = readByte();
	if (i < 0)
	    abort("The input file is only one byte long!");
	len += i;
	if (len == 0)
	    abort("The file claims to have length zero,"
		  + " but that's impossible!");
	if (len < 6)
	    abort("The file claims to have length " + len
		  + " words, but it must be at least 6 words long!");
	return len;
    }

    /*
     * Following member variables (|checkSum| to |restIndex|) are derived from
     * the tfm header. They are used to construct (copied to) the metric
     * object.
     */

    /** 32 bit checksum of |tfm| file */
    protected int		checkSum;

    /** Design size of |tfm| file */
    protected FixWord		designSize;

    /** Font coding scheme */
    protected String		codingScheme = null;

    /** Font family name */
    protected String		family = null;

    /** Font Xerox face code */
    protected int		face = -1;

    /** True if only 7 bit character codes are used. */
    protected boolean		sevenBitSafe = false;

    /** Uninterpreted rest of the header if any, |null| if there is not. */
    protected int[]		headerRest = null;

    /** Starting index of uninterpreted rest of the header */
    protected int		restIndex = 0;

    /*
     * The value of |fontType| is determined from header information too.
     * There are three known types --- each of them cause constructing of
     * different subtype of font metric object:
     * |VANILLA|:	|TeXFm|
     * |MATHSY|:	|TeXMathSymFm|
     * |MATHEX|:	|TeXMathExtFm|
     */

    /** Normal TeX font metric type */
    protected static final byte	VANILLA = 0;

    /** TeX Math Symbols font metric type */
    protected static final byte	MATHSY  = 1;

    /** TeX Math Extension font metric type */
    protected static final byte	MATHEX  = 2;

    /**
     * The actual font metric type
     * @see #VANILLA
     * @see #MATHSY
     * @see #MATHEX
     */
    protected byte		fontType;

    /** Size of coding scheme header information in 4 byte words */
    protected static final int	CODING_SIZE = 10;

    /** Size of family header information in 4 byte words */
    protected static final int	FAMILY_SIZE = 5;

    /**
     * Reads the header information and sets the actual font metric type.
     * @exception	IOException if an I/O error ocurrs or if the tfm file
     *			is malformed.
     */
    /* For original header procesing see TFtoPL[48..57] */
    protected void	readHeader() throws IOException {

	int	rest = headerLength;
	checkSum	= readWord();
	FixWord	dSize	= readFixWord();

	if ((rest -= 2) >= CODING_SIZE) {
	    codingScheme = readBCPL(4 * CODING_SIZE);
	    fontType = getFontType(codingScheme);
	    if ((rest -= CODING_SIZE) >= FAMILY_SIZE) {
	        family = readBCPL(4 * FAMILY_SIZE);
		if ((rest -= FAMILY_SIZE) >= 1) {
		    sevenBitSafe = (readByte() > 127);
		    input.skip(2);
		    face = readByte();
		    if (--rest > 0) {
		        headerRest = new int[rest];
			restIndex = headerLength - rest;
			for (int i = 0; i < rest; i++)
			    headerRest[i] = readWord();
		    }
		}
	    }
	}

	if (dSize.lessThan(0))
	    dSize = badDesignSize(dSize, "negative");
	else if (dSize.lessThan(1))
	    dSize = badDesignSize(dSize, "too small");
	designSize	= dSize;
    }

    /**
     * Determines the actual font metric type based on character string value
     * of coding scheme.
     * @param	s the coding scheme.
     * @return	the font metric type.
     */
    protected static byte	getFontType(String s) {
        if (s.startsWith("TEX MATH SY")) return MATHSY;
        else if (s.startsWith("TEX MATH EX")) return MATHEX;
	return VANILLA;
    }

    /**
     * Reads a character string from the header given the size of appropriate
     * area. The string is stored as its length in first byte then the string
     * and the rest of area is not used.
     * @param	size the size of string area in the header.
     * @return	the read character string.
     * @exception	IOException if an I/O error ocurrs or if the tfm file
     *			is malformed.
     */
    protected String	readBCPL(int size) throws IOException {
        int	len = readByte();
	if (len >= size) {
	    bad("String is too long; I've shortened it drastically.");
	    len = 1;
	}
	size -= len + 1;
	StringBuffer	buf = new StringBuffer(len);
	while (len-- > 0) {
	    char	c = (char) readByte();
	    if (c == '(' || c == ')') {
	        bad("Parenthesis in string has been changed to slash.");
	        c = '/';
	    } else if (!(' ' <= c && c <= '~')) {
	        bad("Nonstandard ASCII code has been blotted out.");
	        c = '?';
	    } else c = Character.toUpperCase(c);
	    buf.append(c);
	}
	input.skip(size);
	return buf.toString();
    }

    /**
     * Reports bad font metric design size and returns an acceptable
     * substitution.
     * @exception	BadTeXFmException if recoverable errors
     *			stop loading.
     */
    protected FixWord	badDesignSize(FixWord dSize, String s)
    					throws BadTeXFmException {
        bad("Design size " + s + "!\nI've set it to 10 points.");
	return FixWord.valueOf(10);
    }

    /*
     * Following tables correspond to tables stored in tfm file. |paramTable|
     * is directly used when constructing the metric object, |charAuxTab|
     * and |ligAuxTab| are translated to diffrent format and also used, the
     * rest is distrubuted to the two tables just stated.
     */

    /** Character information table in format close to tfm file */
    protected AuxCharInfo[]	charAuxTab;

    /** The widths of characters in |charAuxTab| */
    protected FixWord[]		widthTable;

    /** The heights of characters in |charAuxTab| */
    protected FixWord[]		heightTable;

    /** The depths of characters in |charAuxTab| */
    protected FixWord[]		depthTable;

    /** The italic corrections of characters in |charAuxTab| */
    protected FixWord[]		italicTable;

    /** The instructions of lig/kern programs of characters in |charAuxTab| */
    protected AuxLigKern[]	ligAuxTab;

    /** The kerning amounts of kerning instructions in |ligAuxTab| */
    protected FixWord[]		kernTable;

    /** The extensible recipes of characters in |charAuxTab| */
    protected AuxExtRecipe[]	extAuxTab;

    /** The font dimension parameters */
    protected FixWord[]		paramTable;

    /**
     * Check the existence of particular character in the font.
     * @param	c the checked character code.
     * @return	|true| if the character is present.
     */
    protected boolean	charExists(short c) {
        return (  (c -= firstCharCode) >= 0
	       && c < charCount && charAuxTab[c].exists()  );
    }

    /*
     * The following |String| constants are used in two places and we want
     * them to be the same and they are also much shorter than the string
     * literals.
     */
    private static final String		WD = "Width";
    private static final String		HT = "Height";
    private static final String		DP = "Depth";
    private static final String		IC = "Italic correction";
    private static final String		KR = "Kern";

    /*
     * For replacing invalid dimension we use a zero dimension constructed
     * specialy for this purpose. We do not use the |FixWord.ZERO| zero
     * because it has influence on property list dumping for compatibility
     * with original tftopl.
     */
    /** Zero dimension used for replacing the invalid dimensions */
    protected static final FixWord	zeroFixWord = FixWord.valueOf(0);

    /**
     * Reads all the tables from tfm file.
     * @exception	IOException if an I/O error ocurrs or if the tfm file
     *			is malformed.
     */
    protected void	readTables() throws IOException {

	charAuxTab	= AuxCharInfo.readTable(input, diagnostic, charCount);
	widthTable	= readFixWords(widthCount);
	heightTable	= readFixWords(heightCount);
	depthTable	= readFixWords(depthCount);
	italicTable	= readFixWords(italicCount);
	ligAuxTab	= AuxLigKern.readTable(input, diagnostic, ligAuxLen);
	kernTable	= readFixWords(kernCount);
	extAuxTab	= AuxExtRecipe.readTable(input, diagnostic, extAuxCnt);
	paramTable	= readFixWords(paramCount);

	if (input.read() >= 0)
	    warning("There's some extra junk at the end of the TFM file,\n"
		    + "but I'll proceed as if it weren't there.");

    }

    /**
     * Checks all the tables readed from tfm file for inconsistency or
     * malformation.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected void	checkTables() throws BadTeXFmException {

        checkParams();

	checkZeroDimen(widthTable,  "width");
	checkZeroDimen(heightTable, "height");
	checkZeroDimen(depthTable,  "depth");
	checkZeroDimen(italicTable, "italic");

	checkDimens(widthTable,  0, widthCount,  WD);
	checkDimens(heightTable, 0, heightCount, HT);
	checkDimens(depthTable,  0, depthCount,  DP);
	checkDimens(italicTable, 0, italicCount, IC);
	checkDimens(kernTable,   0, kernCount,   KR);

    }

    /**
     * Checks the font dimension parameter table for malformation.
     * @exception	BadTeXFmException when some error stops loading.
     */
    /* See TFtoPL[58..60] */
    protected void	checkParams() throws BadTeXFmException {

	checkDimens(paramTable, 1, paramCount, "Parameter");

	switch (fontType) {
	    case MATHSY:
	        if (paramCount != 22)
		    warning("Unusual number of fontdimen parameters"
			    + " for a math symbols font ("
			    + paramCount + " not 22).");
		break;
	    case MATHEX:
	        if (paramCount != 13)
		    warning("Unusual number of fontdimen parameters"
		    	    + " for an extension font ("
			    + paramCount + " not 13).");
		break;
	}

    }

    /*
     * Almost all dimension in tfm file must be less than 16 in its absolute
     * value. The only two exceptions are |designSize| and |parmTable[0]| ---
     * the |slant| parameter. See TFtoPL[62].
     */
    /**
     * Checks a portion of a dimension table for malformation.
     * @param	table the table of dimensions.
     * @param	beg the starting index of checked dimensions.
     * @param	end the index after the checked dimensions.
     * @param	what identification for error messages.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected void
    		checkDimens(FixWord[] table, int beg, int end, String what)
					throws BadTeXFmException {
	for (; beg < end; beg++)
	    if (!(table[beg].lessThan(16) && table[beg].moreThan(-16))) {
	        bad(what + ' ' + beg + " is too big;\nI have set it to zero.");
		table[beg] = zeroFixWord;
	    }
    }

    /**
     * Checks whether the first element of dimension table is zero.
     * @param	table the checked dimension table.
     * @param	what identification for error messages.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[62] */
    protected void	checkZeroDimen(FixWord[] table, String what)
    					throws BadTeXFmException {
	if (!table[0].isZero())
	    bad(what + "[0] should be zero.");
	else table[0] = FixWord.ZERO;
    }

    /**
     * Converts the lig/kern table information read from tfm file to a form
     * suitable for metric object and check for errors.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[66] */
    protected void	makeLigTable() throws BadTeXFmException {
	if (ligAuxLen > 0)
	    setupBoundary();
	buildLabels();
	promoteActivity();
	buildLigKernTable();
    }

    /*
     * Next two constants are imported from |TeXFm|, otherwise
     * we would use the long prefix everywhere.
     */

    /** Symbolic constant for nonexistent character code */
    protected static final short	NO_CHAR_CODE
    		= TeXFm.NO_CHAR_CODE;

    /** Symbolic constant for index which is not valid */
    protected static final int		NO_INDEX
    		= TeXFm.NO_INDEX;

    /** Invisible right boundary character code. */
    protected short	boundaryChar  = NO_CHAR_CODE;

    /**
     * Starting index of lig/kern program for invisible left boundary
     * character or |NO_INDEX| if there is no such program.
     */
    protected int	boundaryStart = NO_INDEX;

    /*
     * The final version of lig/kern table can have different number of
     * instructions than the table in tfm file. That means that also the
     * starting indexes of lig/kern programs for particular characters may
     * differ. To remap the starting indexes we use |IndexMultimap| in very
     * similar as for dumping labels in |dumpLigKernTable| method of
     * |TeXFm| and the member variable |lig_kern_start| of
     * |AuxCharInfo|.
     */

    /** The associative table of lig/kern program starts in |ligAuxTab| */
    protected IndexMultimap	labels = new IndexMultimap();

    /** Code for left boundary lig/kern program in |labels| table */
    protected static final int		BOUNDARY_LABEL = NO_CHAR_CODE;

    /**
     * Tries to find the information about lig/kerns for word boundaries
     * in tfm lig/kern table and checks for errors.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[69] */
    protected void	setupBoundary() throws BadTeXFmException {

	    AuxLigKern		alk = ligAuxTab[0];
	    if (alk.meansBoundary()) {
		boundaryChar = alk.nextChar();
		alk.activity = AuxLigKern.PASS_THROUGH;
	    }

	    alk = ligAuxTab[ligAuxLen - 1];
	    if (alk.meansBoundary()) {
		int	start = alk.restartIndex();
		alk.activity = AuxLigKern.PASS_THROUGH;
		if (start < ligAuxLen) {
		    ligAuxTab[start].activity = AuxLigKern.ACCESSIBLE;
		    labels.add(start, BOUNDARY_LABEL);
		} else {
		    bad(" Ligature/kern starting index for boundarychar "
			+ "is too large;\nso I removed it.");
		}
	    }

    }

    /**
     * Builds associative table |labels| which maps the character codes to
     * lig/kern program starting indexes in |ligAuxTab| for remapping later.
     * It also marks the starting instructions of lig/kern programs as active
     * (using the |ctivity| field of |AuxLigKern|).
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[67] */
    protected void	buildLabels() throws BadTeXFmException {
	for (int i = 0; i < charCount; i++) {
	    if (charAuxTab[i].tag() == LIG_TAG) {
		int	start = ligAuxStart(charAuxTab[i].ligStart());
		if (start < ligAuxLen) {
		    labels.add(start, i);
		    ligAuxTab[start].activity = AuxLigKern.ACCESSIBLE;
		} else {
		    bad(" Ligature/kern starting index for character '"
			+ octCharNum(i) + "\n is too large;\n"
			+ "so I removed it.");
		    charAuxTab[i].resetTag();
		}
	    }
	}
    }

    /**
     * Finds out the actual starting index of lig/kern program in case there
     * is a restart instructions and checks for validity.
     * @param	start the starting index of lig/kern program given in a
     *		character info.
     * @return	the actual starting index.
     */
     /* See TFtoPL[67] */
    protected int	ligAuxStart(int start) {
	if (start < ligAuxLen) {
	    AuxLigKern		alk = ligAuxTab[start];
	    if (alk.meansRestart()) {
		start = alk.restartIndex();
		if (  start < ligAuxLen
		   && alk.activity == AuxLigKern.UNREACHABLE  )
		    alk.activity = AuxLigKern.PASS_THROUGH;
	    }
	}
	return start;
    }

    /** Lig/kern programs in the final format */
    protected LigKern[]		ligKernTable;

    /**
     * Marks the lig/kern instructions which are really a part of some
     * lig/kern program (active), counts the final number of lig/kern
     * instructions, creates the blank final lig/kern table and checks for
     * errors. Uses |activity| field of |AuxLigKern| for marking the activity.
     * It supposes that the first instructions of programs are already marked
     * active.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[70] */
    protected void	promoteActivity() throws BadTeXFmException {
	int		ligKernLength = 0;
	for (int i = 0; i < ligAuxLen; i++) {
	    AuxLigKern		alk = ligAuxTab[i];
	    if (alk.activity == AuxLigKern.ACCESSIBLE) {
		if (!alk.meansStop()) {
		    int		next = alk.nextIndex(i);
		    if (next < ligAuxLen)
			ligAuxTab[next].activity = AuxLigKern.ACCESSIBLE;
		    else {
			bad("Ligature/kern step " + i
			    + " skips too far;\nI made it stop.");
			alk.makeStop();
		    }
		}
	    }
	    if (alk.activity != AuxLigKern.PASS_THROUGH) ligKernLength++;
	}
	ligKernTable = new LigKern[ligKernLength];
    }

    /**
     * Fills in the blank |ligKernTable| by the final version of lig/kern
     * instructions.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected void	buildLigKernTable() throws BadTeXFmException {
	int		currIns = 0;
	for (int i = 0; i < ligAuxLen; i++) {
	    setLigStarts(i, currIns);
	    AuxLigKern		alk = ligAuxTab[i];
	    if (alk.activity != AuxLigKern.PASS_THROUGH) {
		if (!alk.meansRestart()) {
		    checkLigKern(alk);
		    int	skip = getSkip(i);
		    ligKernTable[currIns++]
		        = (alk.meansKern()) ? makeKern(alk, skip)
					    : makeLig(alk, skip);
		} else if (alk.restartIndex() > ligAuxLen)
		    bad("Ligature unconditional stop command"
			+ " address is too big.");
	    }
	}
    }

    /**
     * Records the starting indexes of final lig/kern program in
     * |ligKernTable| to auxiliary character information field
     * |lig_kern_start| of |AuxCharInfo|.
     * @param	pos	the position of currently processed instruction in
     *			original tfm lig/kern table |ligAuxTab|.
     * @param	start	the position of corresponding instruction in final
     *			lig/kern table |LigKernTable|.
     */
    protected void	setLigStarts(int pos, int start) {
	IndexMultimap.Enum  lab = labels.forKey(pos);
	while (lab.hasMore()) {
	    int		c = lab.next();
	    if (c == BOUNDARY_LABEL) boundaryStart = start;
	    else charAuxTab[c].lig_kern_start = start;
	}
    }

    /**
     * Performs validity checks which are common to both (lig and kern) types
     * of lig/kern instructions in tfm file.
     * @param	alk the checked lig/kern instruction.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected void	checkLigKern(AuxLigKern alk)
    					throws BadTeXFmException {
	if (  !charExists(alk.nextChar())
	   && alk.nextChar() != boundaryChar  ) {
	    bad_char(alk.nextChar(),
	    	     ((alk.meansKern()) ? "Kern" : "Ligature") + " step for");
	    alk.setNextChar(firstCharCode);
	}
    }

    /**
     * Reports a reference to a nonexistent character.
     * @param	c the checked character code.
     * @param	s identification for error messages.
     * @exception	BadTeXFmException if recoverable errors
     *			stop loading.
     */
    protected void	bad_char(short c, String s)
    					throws BadTeXFmException {
	bad(s + " nonexistent character '"
	      + Integer.toOctalString(c) + '.');
    }

    /**
     * Gets the offset of next lig/kern instruction in a program based on
     * counting only those intervene instructions which will be converted to
     * final lig/kern program.
     * @param	pos	the position of current lig/kern instruction in
     *			|ligAuxTable|.
     * @return	the skip amount of the next instruction in the final version
     *		of lig/kern program.
     */
    protected int	getSkip(int pos) {
	AuxLigKern	alk = ligAuxTab[pos];
	if (alk.meansStop()) return -1;
	int		skip = 0;
	int		next = alk.nextIndex(pos);
	while (++pos < next)
	    if (ligAuxTab[pos].activity != AuxLigKern.PASS_THROUGH) skip++;
	return skip;
    }

    /**
     * Creates a final version of ligature instruction after validity checks.
     * @param	alk	the original version of lig/kern instruction.
     * @param	skip	the offset of next lig/kern instruction in the final
     *			version of the lig/kern program.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected LigKern	makeLig(AuxLigKern alk, int skip)
    						throws BadTeXFmException {
	if (!charExists(alk.ligChar())) {
	    bad_char(alk.ligChar(), "Ligature step produces the");
	    alk.setLigChar(firstCharCode);
	}
	boolean		left =  alk.leaveLeft();
	boolean		right = alk.leaveRight();
	byte		step = alk.stepOver();
	if (step > (left ? 1 : 0) + (right ? 1 : 0)) {
	    warning("Ligature step with nonstandard code changed to LIG");
	    left = right = false; step = 0;
	}
	return new Ligature(skip, alk.nextChar(), alk.ligChar(),
			    left, right, step);
    }

    /**
     * Creates a final version of kerning instruction after validity checks.
     * @param	alk	the original version of lig/kern instruction.
     * @param	skip	the offset of next lig/kern instruction in the final
     *			version of the lig/kern program.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected LigKern	makeKern(AuxLigKern alk, int skip)
    						throws BadTeXFmException {
	int		kernIdx = alk.kernIndex();
	FixWord		kern;
	if (kernIdx < kernTable.length) kern = kernTable[kernIdx];
	else { bad("Kern index too large."); kern = zeroFixWord; }
	return new Kerning(skip, alk.nextChar(), kern);
    }

    /**
     * Checks the extensible recepies from tfm file for validity.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[87] */
    protected void	checkExtens() throws BadTeXFmException {
        for (int i = 0; i < extAuxCnt; i++) {
	    AuxExtRecipe	aer = extAuxTab[i];
	    if (aer.top != 0) checkExt(aer.top);
	    if (aer.mid != 0) checkExt(aer.mid);
	    if (aer.bot != 0) checkExt(aer.bot);
	    checkExt(aer.rep);
	}
    }

    /**
     * Checks one piece of extensible recipe for existence of used character.
     * @param	the referenced character code.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected void	checkExt(short c) throws BadTeXFmException {
	if (!charExists(c))
	    bad_char(c, "Extensible recipe involves the");
    }

    /** Character information table in the final format. */
    protected CharInfo[]		charTable;

    /**
     * Converts the original tfm character infos to its final format and
     * checks for validity.
     * @exception	BadTeXFmException when some error stops loading.
     */
     /* See TFtoPL[78] */
    protected void	makeCharTable() throws BadTeXFmException {
        charTable = new CharInfo[charCount];
        for (int i = 0; i < charCount; i++)
	    charTable[i] = (charAuxTab[i].exists())
	    		 ? makeCharInfo(i) : CharInfo.NULL;
    }

    /**
     * Create one piece of character information in the final format for
     * particular character.
     * @param	pos the position of original character info in |charAuxTab|.
     * @return	the final version of character information.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected CharInfo	makeCharInfo(int pos) throws BadTeXFmException {
	AuxCharInfo	aci = charAuxTab[pos];
	FixWord	wd = takeDimen(widthTable,  aci.widthIndex(),  pos, WD);
	FixWord	ht = takeDimen(heightTable, aci.heightIndex(), pos, HT);
	FixWord	dp = takeDimen(depthTable,  aci.depthIndex(),  pos, DP);
	FixWord	ic = takeDimen(italicTable, aci.italicIndex(), pos, IC);
	switch(aci.tag()) {
	    case LIG_TAG:
		return new LigCharInfo(wd, ht, dp, ic, aci.lig_kern_start);
	    case LIST_TAG:
	        if (validCharList(pos))
		    return new ListCharInfo(wd, ht, dp, ic, aci.biggerChar());
		break;
	    case EXT_TAG:
		if (aci.extenIndex() < extAuxCnt) {
		    AuxExtRecipe	aer = extAuxTab[aci.extenIndex()];
		    return new ExtCharInfo(wd, ht, dp, ic,
				    (aer.top != 0) ? aer.top : NO_CHAR_CODE,
				    (aer.mid != 0) ? aer.mid : NO_CHAR_CODE,
				    (aer.bot != 0) ? aer.bot : NO_CHAR_CODE,
					   aer.rep);
		} else range_error(pos, "Extensible");
		break;
	}
	return new CharInfo(wd, ht, dp, ic);
    }

    /**
     * Gets referenced character dimension from apropriate table but checks
     * for consistence first.
     * @param	table	referenced table of dimensions.
     * @param	i	referenced index to the dimension table.
     * @param	pos	the position of character in |charTable|
     *			for error messages.
     * @param	what	identification for error messages.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected FixWord
    		takeDimen(FixWord[] table, int i, int pos, String what)
    						throws BadTeXFmException {
        if (i < table.length) return table[i];
	range_error(pos, what); return zeroFixWord;
    }

    /**
     * Reports an inconsistent index of some character dimension in some
     * table.
     * @param	pos	the position of processed character info in
     *			|charTable|.
     * @param	what	identification for error messages.
     * @exception	BadTeXFmException if recoverable errors
     *			stop loading.
     */
    protected void	range_error(int pos, String what)
				    throws BadTeXFmException {
	bad(what + " index for character '" + octCharNum(pos)
		 + " is too large;\nso I reset it to zero.");
    }

    /**
     * Error message identification of character given its position in
     * |charTable|.
     * @param	pos the position of referenced character info in |charTable|.
     * @return	the string representation of character for error messages.
     */
    protected String	octCharNum(int pos)
        { return Integer.toOctalString(pos + firstCharCode); }

    /*
     * Following method checks for cycles in the larger character chains.
     * For original version of the algorithm see TFtoPL[84].
     */

    /**
     * Checks the consistency of larger character chain. It checks only the
     * characters which have less position in |charTable| then the given
     * character position and are supossed to have the corresponding
     * |CharInfo| already created.
     * @param	pos	position of currently processed character in
     *			|charTable|.
     * @return	|true| if the associated chain is consistent.
     * @exception	BadTeXFmException when some error stops loading.
     */
    protected boolean	validCharList(int pos) throws BadTeXFmException {
	AuxCharInfo	aci = charAuxTab[pos];
	short		next = aci.biggerChar();
        if (!charExists(next)) {
	    bad_char(next, "Character list link to");
	    aci.resetTag();
	    return false;
	}
	while (  (next -= firstCharCode) < pos
	      && (aci = charAuxTab[next]).tag() == LIST_TAG  )
	    next = aci.biggerChar();
	if (next == pos) {
	    bad("Cycle in a character list!\nCharacter '"
	        + octCharNum(pos) + " now ends the list.");
	    charAuxTab[pos].resetTag();
	    return false;
	}
	return true;
    }

    /** The metric object created according the loaded tfm file. */
    protected TeXFm	metric;

    /**
     * Gives the |TeXFm| as a result of loading the tfm file.
     * @return	the loaded |TeXFm| (or its subtype).
     */
    public TeXFm	getMetric() { return metric; }

    /**
     * Creates metric object as a result of loading the tfm file.
     * It uses loaded values for final creation.
     */
    protected void	makeMetric() {
        switch (fontType) {
	    case MATHSY:
	        metric = new TeXMathSymFm(
		    checkSum, designSize, firstCharCode, charTable,
		    boundaryChar, boundaryStart, ligKernTable, paramTable,
		    codingScheme, family, face, sevenBitSafe,
		    headerRest, restIndex);
		break;
	    case MATHEX:
	        metric = new TeXMathExtFm(
		    checkSum, designSize, firstCharCode, charTable,
		    boundaryChar, boundaryStart, ligKernTable, paramTable,
		    codingScheme, family, face, sevenBitSafe,
		    headerRest, restIndex);
		break;
	    default:
	        metric = new TeXFm(
		    checkSum, designSize, firstCharCode, charTable,
		    boundaryChar, boundaryStart, ligKernTable, paramTable,
		    codingScheme, family, face, sevenBitSafe,
		    headerRest, restIndex);
		break;
	}
    }

    /**
     * Reads an array of |FixWords| from the tfm file.
     * It reports fatal error message (and throws exception)
     * if the end of file is reached.
     * @param	count the number of fractions to be read.
     * @return	the array of fractions.
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected final FixWord[]	readFixWords(int count) throws IOException {
	FixWord[]	table = new FixWord[count];
	for (int i = 0; i < count; i++)
	    table[i] = readFixWord();
	return table;
    }

    /*
     * Following method uses the knowledge about representation of fractions
     * (|fix_word|) in the tfm file and converts it to the FixWord which has
     * general fraction interface and is independent on file representation.
     * See TFtoPL[9].
     */

    /**
     * Reads four bytes from the tfm file and interpretes them as a |FixWord|
     * fraction.
     * It reports fatal error message (and throws exception)
     * if the end of file is reached.
     * @return	the resulting fraction.
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected final FixWord	readFixWord() throws IOException {
	final int	FIX_WORD_DENOMINATOR = 0x100000;
	return FixWord.valueOf(readWord(), FIX_WORD_DENOMINATOR);
    }

    /**
     * Reads four bytes (32 bits) from the tfm file and returns them in an
     * |int| in BigEndian order.
     * It reports fatal error message (and throws exception)
     * if the end of file is reached.
     * @return	the integer value in BigEndian byte order.
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected final int		readWord() throws IOException {
	int	i = readByte();
	i = (i << 8) + readByte();
	i = (i << 8) + readByte();
	return (i << 8) + readByte();
    }

    /**
     * Reads 16 bit length value from the tfm file. It reports fatal error
     * message (and throws exception) if the end of file is reached and
     * reports recoverable error when the length is negative.
     * @return	the lenght value
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected final short		readLength() throws IOException {
	short	i = readByte();
	if ((i & 0x80) != 0)
	    abort("One of the subfile sizes is negative!");
	return (short) ((i << 8) + readByte());
    }

    /*
     * We need positive values of the byte in a tfm file. Java unfortunatelly
     * does not provide unsigned byte so we must use short instead.
     */

    /**
     * Reads on byte from the tfm file.
     * It reports fatal error message (and throws exception)
     * if the end of file is reached.
     * @return	the positive value of the read byte.
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected final short	readByte() throws IOException
        { return readByte(input, diagnostic); }

    /**
     * Reads on byte from a byte input stream.
     * It takes parameters neccesary when the method is called
     * in static context.
     * It reports fatal error message (and throws exception)
     * if the end of file is reached.
     * @param	in the input stream.
     * @param	dg the object for reporting errors.
     * @return	the positive value of the read byte.
     * @exception	IOException if an I/O error occurs or if the end of
     *			file is reached.
     */
    protected static final short
    				readByte(InputStream in, TeXFmDiagnostic dg)
    						throws IOException {
	int	i = in.read();
	if (i < 0)
	    abort("The file has fewer bytes than it claims!", dg);
	return (short) i;
    }

}
