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
// Filename: nts/tfm/TeXFm.java
// $Id: TeXFm.java,v 1.1.1.1 2001/02/22 03:30:07 ksk Exp $
package	nts.tfm;

import	java.io.InputStream;
import	java.io.IOException;
import	java.io.Serializable;
import	nts.base.BoolPar;
import	nts.base.IntPar;

/*
 * This class is not intended to be used directly by typesetting system, it
 * should be rather wraped by an adaptor which provides unified interface
 * for typesetting purposes and shades out the methods for operations special
 * to this particular type (TeX) of font metric.
 *
 * The organization of data corresponds to traditional |tfm| athough it is not
 * limited to only 256 characters or any traditional limit of different values
 * of particular character dimension.
 */

/**
 * Internal representation of TeX font metric.
 */
public class	TeXFm	implements Serializable {

    /*
     * The primitive type |short| is used to represent character codes.
     * It is also the only purpose for which the |short| is used.
     * It is only internal representation not dependent on representation of
     * character codes in the typesetting system. The only way this type of
     * font metric (derived from tfm) provide for accesing information about
     * character dimensions are the numbers of characters. Any smarter way
     * (character names) must be provided on different level (wraper object).
     */

    /** Symbolic constant for nonexistent character code */
    public static final short	NO_CHAR_CODE = -1;

    /*
     * All indexes to any tables are represented by type |int|.
     * Folowing constant is used for situations where we want to indicate
     * that no data corresponding to the index are available.
     */

    /** Symbolic constant for index which is not valid */
    public static final int	NO_INDEX = -1;

    /*
     * There are two kinds of instructions for a character in a font --- the
     * |Ligature| and the |Kerning| steps. The information about the next
     * instruction of the lig/kern program (SKIP or STOP) is handled by their
     * common base class |LigKern|. Athough in property list representation of
     * the ligtable the instruction and the step are printed separately, it is
     * more natural to represent them in one object. It's easier for counting
     * the skip amounts because only lig/kern steps counts and the STEP
     * or STOP must follow lig/kern step by definition anyway.
     *
     * The base class also maintains the next character code which is common
     * to both types of instructions. It tells the code of the character which
     * must be next to the current one in the text to activate current
     * lig/kern instruction.
     *
     * For the original data structures see TFtoPL[13].
     */

    /**
     * Base class for |Ligature|/|Kerning| instructions.
     * It handles the skip amount to the next instruction in the kern/lig
     * program and the character code for the next character.
     */
    public abstract static class	LigKern	implements Serializable {

	public static final LigKern	NULL = null;

	/** The skip amount */
        private int		skip;

	/**
	 * Character code representing the character which must be next to the
	 * current one to activate this instruction.
	 */
        protected short		nextChar;

	/**
	 * Constructs a lig/kern instruction with given skip amount to the
	 * next instruction in the lig/kern program.
	 * @param	skip the skip amount to the next instruction.
	 *		|0| means the folowing instruction is the next,
	 *		a number |< 0| means that there is no next instruction
	 *		(this is the last).
	 * @param	next the code of the next character.
	 */
	public LigKern(int skip, short next)
	    { this.skip = skip; nextChar = next; }

	public short	getLig(BoolPar left, BoolPar right, IntPar over)
	    { return NO_CHAR_CODE; }

	public FixWord	getKern() { return FixWord.NULL; }

	/**
	 * Tells the index to the ligtable of the next instruction of lig/kern
	 * program for given index of this instruction.
	 * @param	pos the index of this instruction.
	 * @return	the index of the next instruction or |NO_INDEX|
	 *		if this is the last instruction of
	 *		the lig/kern program.
	 */
	public int	nextIndex(int pos)
	    { return (skip < 0) ? NO_INDEX : pos + skip + 1; }

	/*
	 * There are two situations where an lig/kern instruction is dumped.
	 * One is during listing the ligtble where SKIPs and STOPs are listed;
	 * other are during listing lig/kern program for particular char or
	 * during listing inaccesible parts of ligtable. In later cases only
	 * pure lig/kern instrictuions are printed in side a COMMENT.
	 *
	 * There are two methods for this two kinds of situations with
	 * overloaded name. The first (simpler) one dumps only pure lig/kern
	 * instructions, the second dumps SKIPs and STOPs as well using
	 * other necessary information about activity of surrounding
	 * instructions provided as aditional two parameters. The parameters
	 * are |activity| array which indicates for surrounding instructions
	 * if they are reachable by any lig/kern program and |pos| which
	 * determines the index of current instruction flag in |active|.
	 * That is because only active (reachable) instructions counts for
	 * SKIPs, the unreachable instructions are COMMENTed out.
	 *
	 * The base class provides the SKIP/STOP version which in turn use the
	 * simpler one. The simpler one is abstract in the base class and is
	 * the only version provided by the subclasses.
	 *
	 * For original version of symbolic output of lig/kerns see
	 * TFtoPL[74..77].
	 */

	/**
	 * Dumps the pure lig/kern instruction (no SKIP or STOP).
	 * @param	dmp the property list dumper to dump on.
	 * @return	the property list dumper for subsequent dumps.
	 */
	public abstract PLDumper	dump(PLDumper dmp);

	/**
	 * Dumps the lig/kern instruction (including SKIP or STOP).
	 * @param	dmp	the property list dumper to dump on.
	 * @param	active	array with activity flag for surrounding
	 *			instructions
	 * @param	pos	the index of this instruction activity flag in
	 *			the |active| array.
	 * @return	the property list dumper for subsequent dumps.
	 */
	public PLDumper	dump(PLDumper dmp, boolean[] active, int pos) {
	    dump(dmp);
	    if (skip > 0) {
		int		count = 0;
		int		next = pos + skip + 1;
		while (++pos < next)
		    if (active[pos]) count++;
		dmp.open("SKIP").addDec(count).close();
	    } else if (skip < 0)
		dmp.open("STOP").close();
	    return dmp;
	}

    }

    /** Ligature lig/kern instruction */
    public static class	Ligature	extends LigKern {

	/**
	 * Character code representing the ligature character to be added
	 * between the current and next character in the text if this
	 * instruction is activated.
	 */
	private short		addingChar;

	/**
	 * If some of the following flags are not set, the corresponding
	 * character in the text is removed after inserting the ligature
	 * character (in the process of constituing of ligatures).
	 */

	/** Indication that the current character should not be removed */
	private boolean		keepLeft;

	/** Indication that the next character should not be removed */
	private boolean		keepRight;

	/**
	 * Tells how many characters from the current position in the text
	 * should be skiped over after performing this instruction.
	 */
	private byte		stepOver;

	/**
	 * Makes new ligature instruction.
	 * See |LigKern| constructor for the details of the two first
	 * parameters.
	 * @param	skip the skip amount to the next instruction.
	 * @param	next the code of the next character.
	 * @param	a character code of ligature character to be inserted.
	 * @param	l indication that the current character should not be
	 *		  removed.
	 * @param	r indication that the next character should not be
	 *		  removed.
	 * @param	s number of characters from the current one to be
	 *		  stepped over after performing of this instruction.
	 */
        public Ligature(int skip, short next, short a,
			boolean l, boolean r, byte s) {
	    super(skip, next);
	    addingChar = a;
	    keepLeft = l; keepRight = r;
	    stepOver = s;
	}

	public short	getLig(BoolPar left, BoolPar right, IntPar over) {
	    left.set(keepLeft); right.set(keepRight);
	    over.set(stepOver); return addingChar;
	}

	/**
	 * Dumps the pure ligature instruction (no SKIP or STOP).
	 * @param	dmp the property list dumper to dump on.
	 * @return	the property list dumper for subsequent dumps.
	 */
	public PLDumper	dump(PLDumper dmp) {
	    StringBuffer	buf = new StringBuffer(7);
	    if (keepLeft) buf.append('/');
	    buf.append("LIG");
	    if (keepRight) buf.append('/');
	    for (byte i = stepOver; i-- > 0; buf.append('>'));
	    dmp.open(buf.toString()).addChar(nextChar);
	    return dmp.addChar(addingChar).close();
	}

    }

    /** Kerning lig/kern instruction */
    public static class	Kerning	extends LigKern {

	/** The amount of kerning */
	private FixWord		kern;

	/**
	 * Makes new ligature instruction.
	 * See |LigKern| constructor for the details of the two first
	 * parameters.
	 * @param	skip the skip amount to the next instruction.
	 * @param	next the code of the next character.
	 * @param	k the amount of kerning between the current and
	 *		  the next characters.
	 */
	public Kerning(int skip, short next, FixWord k)
	    { super(skip, next); kern = k; }

	public FixWord	getKern() { return kern; }

	/**
	 * Dumps the pure kerning instruction (no SKIP or STOP).
	 * @param	dmp the property list dumper to dump on.
	 * @return	the property list dumper for subsequent dumps.
	 */
	public PLDumper	dump(PLDumper dmp) {
	    dmp.open("KRN").addChar(nextChar);
	    return dmp.addReal(kern).close();
	}

    }

    /*
     * All character infos have basic dimension information about width,
     * height, depth and italic rorrection. Each info can potentialy have
     * associated lig/kern program, next larger character in the chain and
     * an extensible recipe. In traditional |tfm| these three options are
     * mutually exclusive but this aproach is more general. The methods for
     * additional information in base class return the default values which
     * mean that the special information is not associated. The methods are
     * overriden in more specialised classes.
     *
     * For the original data structures see TFtoPL[11,12,14].
     */

    /**
     * Base class for character information.
     */
    public static class CharInfo	implements Serializable {

	/**
	 * Symbolic constatnt for null |CharInfo| reference which is used for
	 * better distinguishing.
	 */
	public static final CharInfo	NULL = null;

	/** Character width */
	private FixWord		width;

	/** Character height */
	private FixWord		height;

	/** Character depth */
	private FixWord		depth;

	/** Character italic correction */
	private FixWord		italic;

	/**
	 * Makes new |CharInfo| with given dimensions.
	 * @param	w character width.
	 * @param	h character height.
	 * @param	d character depth.
	 * @param	i character italic correction.
	 */
	public	CharInfo(FixWord w, FixWord h, FixWord d, FixWord i)
	    { width = w; height = h; depth = d;	italic = i; }

	/**
	 * Gets the width of the character.
	 * @return	the character width.
	 */
	public final FixWord	getWidth() { return width; }

	/**
	 * Gets the height of the character.
	 * @return	the character height.
	 */
	public final FixWord	getHeight() { return height; }

	/**
	 * Gets the depth of the character.
	 * @return	the character depth.
	 */
	public final FixWord	getDepth() { return depth; }

	/**
	 * Gets the italic correction of the character.
	 * @return	the character italic correction.
	 */
	public final FixWord	getItalic() { return italic; }

	/**
	 * Gets the index to the |ligKernTable| of the start of lig/kern
	 * program for this character.
	 * @return	start index of lig/kern program or |NO_INDEX|
	 *		if it has no lig/kern program associated.
	 */
	public int	ligKernStart() { return NO_INDEX; }

	/**
	 * Gets the code of next larger character in the list.
	 * @return	next larger character code or |NO_CHAR_CODE|
	 *		if there is no larger character.
	 */
	public short	nextChar() { return NO_CHAR_CODE; }

	/**
	 * Gets the character code for top part of extensible character.
	 * @return	code of top part character or |NO_CHAR_CODE|
	 *		if there is no top part or the character
	 *		is not extensible.
	 */
	public short	extTop() { return NO_CHAR_CODE; }

	/**
	 * Gets the character code for middle part of extensible character.
	 * @return	code of middle part character or |NO_CHAR_CODE|
	 *		if there is no middle part or the character
	 *		is not extensible.
	 */
	public short	extMid() { return NO_CHAR_CODE; }

	/**
	 * Gets the character code for bottom part of extensible character.
	 * @return	code of bottom part character or |NO_CHAR_CODE|
	 *		if there is no bottom part or the character
	 *		is not extensible.
	 */
	public short	extBot() { return NO_CHAR_CODE; }

	/**
	 * Gets the character code for repeatable part of extensible
	 * character.
	 * @return	code of repeatable part character or |NO_CHAR_CODE|
	 *		if there is no repeatable part or the character
	 *		is not extensible.
	 */
	public short	extRep() { return NO_CHAR_CODE; }

	/*
	 * For some subclasses we also dump some related information (e.g.
	 * lig/kern instructions) so we must supply the |tfm| parameter to
	 * give the context.
	 */
	/**
	 * Dumps one character information.
	 * @param	dmp the property list dumper to dump on.
	 * @param	tfm the reference to the metric object which
	 *		the character info belongs to.
	 * @return	the property list dumper for subsequent dumps.
	 */
        public PLDumper	dump(PLDumper dmp, TeXFm tfm) {
	    dumpDimen(dmp, width,  "CHARWD");
	    dumpDimen(dmp, height, "CHARHT");
	    dumpDimen(dmp, depth,  "CHARDP");
	    dumpDimen(dmp, italic, "CHARIC");
	    return dmp;
	}

	/**
	 * Dumps one dimension property.
	 * @param	dmp the property list dumper to dump on.
	 * @param	dim the dimension which is the value of the property.
	 * @param	what the property name.
	 */
	protected void	dumpDimen(PLDumper dmp, FixWord dim, String what) {
	    if (dim != FixWord.ZERO)	//SSS
		dmp.open(what).addReal(dim).close();
	}

    }

    /**
     * Character information for character with associated lig/kern program.
     */
    public static class LigCharInfo extends CharInfo {

	/**
	 * Index of the starting instruction of lig/kern program in the
	 * |ligKernTable|.
	 */
	private int		start;

	/**
	 * Makes new |LigCharInfo| with given dimensions and lig/kern program
	 * starting index.
	 * @param	w character width.
	 * @param	h character height.
	 * @param	d character depth.
	 * @param	i character italic correction.
	 * @param	s lig/kern program starting index.
	 */
	public	LigCharInfo(FixWord w, FixWord h, FixWord d, FixWord i,
			    int s) { super(w, h, d, i); start = s; }

	/**
	 * Gets the index to the |ligKernTable| of the start of lig/kern
	 * program for this character.
	 * @return	start index of lig/kern program.
	 */
	public final int	ligKernStart() { return start; }

	/**
	 * Dumps one character information with associated lig/kern commands.
	 * @param	dmp the property list dumper to dump on.
	 * @param	tfm the reference to the metric object which
	 *		the character info belongs to.
	 * @return	the property list dumper for subsequent dumps.
	 */
        public PLDumper	dump(PLDumper dmp, TeXFm tfm) {
	    super.dump(dmp, tfm);
	    dmp.open("COMMENT");
	    for (int i = start; i != NO_INDEX;
	         i = tfm.ligKernTable[i].nextIndex(i))
	        tfm.ligKernTable[i].dump(dmp);
	    return dmp.close();
	}

    }

    /**
     * Character information for character which has next larger character
     * associated.
     */
    public static class ListCharInfo extends CharInfo {

	/** Next larger character code */
	private short		next;

	/**
	 * Makes new |ListCharInfo| with given dimensions and next larger
	 * character code.
	 * @param	w character width.
	 * @param	h character height.
	 * @param	d character depth.
	 * @param	i character italic correction.
	 * @param	n character code of the next larger character.
	 */
	public	ListCharInfo(FixWord w, FixWord h, FixWord d, FixWord i,
			     short n) { super(w, h, d, i); next = n; }

	/**
	 * Gets the code of next larger character in the list.
	 * @return	next larger character code.
	 */
	public final short	nextChar() { return next; }

	/**
	 * Dumps one character information with information about next larger
	 * character.
	 * @param	dmp the property list dumper to dump on.
	 * @param	tfm the reference to the metric object which
	 *		the character info belongs to.
	 * @return	the property list dumper for subsequent dumps.
	 */
        public PLDumper	dump(PLDumper dmp, TeXFm tfm) {
	    super.dump(dmp, tfm);
	    return dmp.open("NEXTLARGER").addChar(next).close();
	}

    }

    /**
     * Character information for character which has extensible recipe.
     */
    public static class ExtCharInfo extends CharInfo {

	/** top part chracter code */
	private short		top;

	/** middle part chracter code */
	private short		mid;

	/** bottom part chracter code */
	private short		bot;

	/** repeatable part chracter code */
	private short		rep;

	/**
	 * Makes new |ListCharInfo| with given dimensions and character codes
	 * for extensible parts.
	 * @param	w character width.
	 * @param	h character height.
	 * @param	d character depth.
	 * @param	i character italic correction.
	 * @param	t top part character code.
	 * @param	m middle part character code.
	 * @param	b bottom part character code.
	 * @param	r repeatable part character code.
	 */
	public	ExtCharInfo(FixWord w, FixWord h, FixWord d, FixWord i,
			    short t, short m, short b, short r)
	    { super(w, h, d, i); top = t; mid = m; bot = b; rep = r; }

	/**
	 * Gets the character code for top part of extensible character.
	 * @return	code of top part character or |NO_CHAR_CODE|
	 *		if there is no top part.
	 */
	public final short	extTop() { return top; }

	/**
	 * Gets the character code for middle part of extensible character.
	 * @return	code of middle part character or |NO_CHAR_CODE|
	 *		if there is no middle part.
	 */
	public final short	extMid() { return mid; }

	/**
	 * Gets the character code for bottom part of extensible character.
	 * @return	code of bottom part character or |NO_CHAR_CODE|
	 *		if there is no bottom part.
	 */
	public final short	extBot() { return bot; }

	/**
	 * Gets the character code for repeatable part of extensible
	 * character.
	 * @return	code of repeatable part character or |NO_CHAR_CODE|
	 *		if there is no repeatable part.
	 */
	public final short	extRep() { return rep; }

	/**
	 * Dumps one character information with extensible recipe.
	 * @param	dmp the property list dumper to dump on.
	 * @param	tfm the reference to the metric object which
	 *		the character info belongs to.
	 * @return	the property list dumper for subsequent dumps.
	 */
        public PLDumper	dump(PLDumper dmp, TeXFm tfm) {
	    super.dump(dmp, tfm);
	    dmp.open("VARCHAR");
	    dumpPart(dmp, top, "TOP");
	    dumpPart(dmp, mid, "MID");
	    dumpPart(dmp, bot, "BOT");
	    dumpPart(dmp, rep, "REP");
	    return dmp.close();
	}

	/**
	 * Dumps one part of an extensible recipe.
	 * @param	dmp the property list dumper to dump on.
	 * @param	c dumped part character code which is the value of the
	 *		property.
	 * @param	what the dumped part property name.
	 */
	protected void	dumpPart(PLDumper dmp, short c, String what) {
	    if (c != NO_CHAR_CODE)
		dmp.open(what).addChar(c).close();
	}

    }

    /*
     * The following member variables correspond to font parameters.
     * For corresponding structures in the |tfm| file see TFtoPL[9,10]
     */

    /** 32 bit checksum of |tfm| file */
    protected int		checkSum;

    /** Design size of |tfm| file */
    protected FixWord		designSize;

    /** Code of the first character present in the font. */
    protected short		firstCharCode;

    /**
     * Table of character information. The first member (|charTable[0]|)
     * correspons to the first character in the font (|firtsCharCode|).
     * A character of code |c| is present in the font if:
     * (1) |c >= firstCharCode|
     * (2) |c - firstCharCode < charTable.length|
     * (3) |charTable[c - firstCharCode] != CharInfo.NULL|
     */
    protected CharInfo[]	charTable;

    /*
     * Left and right boundary of character sequence can have influence on
     * ligatures and kerning.
     *
     * Right boundary is represented as invisible boundary character. If
     * the last character in the sequence has lig/kern program and if
     * it has an instructions in that program which has the same value
     * of member |nextChar| as |boundaryChar| then this instruction is
     * applicable to this case. Value |NO_CHAR_CODE| of |boundaryChar|
     * means that there are no ligatures or kernings for the right boundary.
     *
     * The ligatures and kernings for the left boundary of the sequence
     * are contained in lig/kern program which starts on position
     * |boundaryStart| in |ligKernTable|. A value |NO_INDEX|
     * of |boundaryStart| indicates that there is no such program.
     *
     * See TFtoPL[13].
     */

    /** Invisible right boundary character code. */
    protected short		boundaryChar = NO_CHAR_CODE;

    /**
     * Starting index of lig/kern program for invisible left boundary
     * character or |NO_INDEX| if there is no such program.
     */
    protected int		boundaryStart = NO_INDEX;

    /** Table of lig/kern instructions for character lig/kern programs. */
    protected LigKern[]		ligKernTable;

    /*
     * The meaning of parameters in the following table is dependent on type
     * of font. Only the first 7 parameters have always the same meaning:
     * |paramTable[0]|: slant
     * |paramTable[1]|: space
     * |paramTable[2]|: space_stretch
     * |paramTable[3]|: space_shrink
     * |paramTable[4]|: x_height
     * |paramTable[5]|: quad
     * |paramTable[6]|: extra_space
     * For detailed meaning see TFtoPL[15].
     */

    /**
     * Table of font dimension parameters.
     * @see #paramName
     */
    protected FixWord[]		paramTable;

    /*
     * The remaining parameters are taken from the tfm header and are not
     * interesting for \TeX. See TFtoPL[10].
     */

    /** Font coding scheme */
    protected String		codingScheme;

    /** Font family name */
    protected String		family;

    /** Font Xerox face code */
    protected int 		face;

    /** True if only 7 bit character codes are used. */
    protected boolean		sevenBitSafe;

    /** Uninterpreted rest of the header if any, |null| if there is not. */
    protected int[]		headerRest;

    /** Starting index of uninterpreted rest of the header */
    protected int		restIndex;

    /**
     * Creates new internal representation of TeX font metric.
     * @param	checkSum	32 bit checksum of tfm file.
     * @param	designSize	design size of the font.
     * @param	firstCharCode	first character code present in the font.
     * @param	charTable	table of character information.
     * @param	boundaryChar	code of invisible boundary character.
     * @param	boundaryStart	index to |ligKernTable| where the
     *				ligature/kern program for he boundary
     *				char starts.
     * @param	ligKernTable	table of ligature/kern instructions.
     * @param	paramTable	table of font dimension parameters.
     * @param	codingScheme	coding scheme.
     * @param	family		family name.
     * @param	face		Xerox face code.
     * @param	sevenBitSafe	indication if only 7 bit character codes
     *				are used.
     * @param	headerRest	uninterpreted rest of tfm header.
     * @param	restIndex	starting index of uninterpreted header rest.
     */
    protected TeXFm(int checkSum, FixWord designSize,
    			  short firstCharCode, CharInfo[] charTable,
			  short boundaryChar, int boundaryStart,
			  LigKern[] ligKernTable, FixWord[] paramTable,
			  String codingScheme, String family,
			  int face, boolean sevenBitSafe,
			  int[] headerRest, int restIndex) {
	this.checkSum = checkSum;
	this.designSize = designSize;
	this.firstCharCode = firstCharCode;
	this.charTable = charTable;
	this.boundaryChar = boundaryChar;
	this.boundaryStart = boundaryStart;
	this.ligKernTable = ligKernTable;
	this.paramTable = paramTable;
	this.codingScheme = codingScheme;
	this.family = family;
	this.face = face;
	this.sevenBitSafe = sevenBitSafe;
	this.headerRest = headerRest;
	this.restIndex = restIndex;
    }

    public static TeXFm		readFrom(InputStream in, TeXFmDiagnostic dg)
						throws IOException
	{ return (new TeXFmLoader(in, dg)).getMetric(); }

    public static TeXFm		readFrom(InputStream in) throws IOException
	{ return (new TeXFmLoader(in)).getMetric(); }

    public static TeXFm		readFrom(String path, TeXFmDiagnostic dg)
						throws IOException
	{ return (new TeXFmLoader(path, dg)).getMetric(); }

    public static TeXFm		readFrom(String path) throws IOException
	{ return (new TeXFmLoader(path)).getMetric(); }


    public int		getCheckSum() { return checkSum; }
    public FixWord	getDesignSize() { return designSize; }
    public int		paramCount() { return paramTable.length; }
    public FixWord	getParam(int idx) { return paramTable[idx]; }

    public CharInfo	getCharInfo(short idx) {
	return (0 <= (idx -= firstCharCode) && idx < charTable.length)
	     ? charTable[idx] : CharInfo.NULL;
    }

    /* TeXtp[1039] */
    public LigKern	getLigKern(short left, short right) {
	int		i;
	if (left == NO_CHAR_CODE) i = boundaryStart;
	else {
	    CharInfo	info = getCharInfo(left);
	    i = (info != CharInfo.NULL) ? info.ligKernStart() : NO_INDEX;
	}
	if (right == NO_CHAR_CODE) right = boundaryChar;
	if (right != NO_CHAR_CODE)
	    while (i != NO_INDEX) {
	        LigKern		lk = ligKernTable[i];
		if (lk.nextChar == right) return lk;
		i = lk.nextIndex(i);
	    }
	return LigKern.NULL;
    }

    /*
     * The dumping is implemented in such way that it gives the same output as
     * the original tftopl program. Original algorithms are contained (and
     * mixed with checking stuff) in TFtoPL[44-87].
     */

    /**
     * Dumps its content on property list dumper.
     * @param	dmp the property list dumper.
     * @return	the dumper for subsequent dumps.
     */
    public PLDumper	dump(PLDumper dmp) {
        if (family != null)
	    dmp.open("FAMILY").addStr(family).close();
	if (face >= 0)
	    dmp.open("FACE").addFace(face).close();
	if (headerRest != null)
	    dumpHeaderRest(dmp, headerRest, restIndex);
	if (codingScheme != null)
	    dmp.open("CODINGSCHEME").addStr(codingScheme).close();
	dmp.open("DESIGNSIZE").addReal(designSize).close();
	dumpComment(dmp, "DESIGNSIZE IS IN POINTS");
	dumpComment(dmp, "OTHER SIZES ARE MULTIPLES OF DESIGNSIZE");
	dmp.open("CHECKSUM").addOct(checkSum).close();
	if (sevenBitSafe)
	    dmp.open("SEVENBITSAFEFLAG").addBool(sevenBitSafe).close();
	dumpParams(dmp);
	dumpLigKernTable(dmp);
	dumpChars(dmp);
	return dmp;
    }

    /**
     * Dumps the font dimension parameters from |paramTable|.
     * @param	dmp the property list dumper.
     * @return	the dumper for subsequent dumps.
     */
    protected PLDumper	dumpParams(PLDumper dmp) {
	if (paramTable.length > 0) {
	    dmp.open("FONTDIMEN");
	    for (int i = 0; i < paramTable.length; i++) {
		String	name = paramName(i);
		if (name != null) dmp.open(name);
		else dmp.open("PARAMETER").addDec(i + 1);
		dmp.addReal(paramTable[i]).close();
	    }
	    dmp.close();
	}
	return dmp;
    }

    /*
     * When dumping the whole lig/kern table we need to output the character
     * labels in places where the particular lig/kern programs for characters
     * start. This information is not directly included in the |ligKernTable|.
     * Therefore we first make auxiliary |IndexMultimap| which associate the
     * character codes to corresponding program start indexes and then we use
     * this table to dump the labels. The technique is borrowed from tftopl,
     * see TFtoPL[63,67,68,69,72].
     */

    /**
     * Symbolic constant used to represent left boundary program label.
     * Its value cannot be value of any character code.
     */
    protected static final int	BOUNDARY_LABEL_CODE = NO_CHAR_CODE;

    /**
     * Dumps the whole contens of lig/kern table |ligKernTable|.
     * @param	dmp the property list dumper.
     * @return	the dumper for subsequent dumps.
     */
    protected PLDumper	dumpLigKernTable(PLDumper dmp) {
	if (boundaryChar != NO_CHAR_CODE)
	    dmp.open("BOUNDARYCHAR").addChar(boundaryChar).close();
	if (ligKernTable.length > 0) {
	    dmp.open("LIGTABLE");
	    boolean[]	activity = new boolean[ligKernTable.length];
	    IndexMultimap	labels = buildLabels(activity);
	    computeActivity(activity);
	    boolean	commenting = false;
	    for (int i = 0; i < ligKernTable.length; i++) {
	        if (activity[i]) {
		    if (commenting) { dmp.close(); commenting = false; }
		    IndexMultimap.Enum	lab = labels.forKey(i);
		    while (lab.hasMore()) {
			short	charCode = (short) lab.next();
			dmp.open("LABEL");
			if (charCode == BOUNDARY_LABEL_CODE)
			    dmp.addStr("BOUNDARYCHAR");
			else dmp.addChar(charCode);
			dmp.close();
		    }
		    ligKernTable[i].dump(dmp, activity, i);
		} else {
		    if (!commenting) {
		        dmp.open("COMMENT");
			dmp.addStr("THIS PART OF THE PROGRAM IS NEVER USED!");
			commenting = true;
		    }
		    ligKernTable[i].dump(dmp);
		}
	    }
	    if (commenting) dmp.close();
	    dmp.close();
	}
	return dmp;
    }

    /*
     * Next function builds auxiliary multimap which keeps all the
     * character codes for which the lig/kern program starts on particular
     * position in the |ligKernTable|. The constant |BOUNDARY_LABEL_CODE|
     * is used for start of left boundary lig/kern program (which does not
     * belong to a character).  In the same time it marks the positions
     * in |ligKernTable| which are start of some lig\kern program in
     * a boolean array.
     */

    /**
     * Builds auxiliary table for printing lig/kern starting labels and
     * marks the active starting positions of |ligKernTable| in |active| array.
     * @param	active	array of indicators that the corresponding position in
     *			|ligKernTable| is active.
     * @return	object which keeps for any position of |ligKernTable| the
     *		codes of characters which lig/kern programs start at this
     *		position.
     */
    protected IndexMultimap	buildLabels(boolean[] active) {
        IndexMultimap	labels = new IndexMultimap();
	int		start;
	if (boundaryStart != NO_INDEX) {
	    labels.add(boundaryStart, BOUNDARY_LABEL_CODE);
	    active[boundaryStart] = true;
	}
	for (int i = 0; i < charTable.length; i++)
	    if (  charTable[i] != CharInfo.NULL
	       && (start = charTable[i].ligKernStart()) != NO_INDEX  ) {
	        labels.add(start, i + firstCharCode);
	    	active[start] = true;
	    }
	return labels;
    }

    /**
     * Marks the instructions in |ligKernTable| which are really part of some
     * lig/kern program (reachable). It supposes that the starts of programs
     * are lready marked.
     * @param	active	array of indicators that the corresponding position in
     *			|ligKernTable| is active.
     */
    protected void	computeActivity(boolean[] active) {
        int		next;
        for (int i = 0; i < ligKernTable.length; i++)
	    if (  active[i]
	       && (next = ligKernTable[i].nextIndex(i)) != NO_INDEX  )
	    	active[next] = true;
    }

    /**
     * Dumps the information about all characters in the font metric.
     * @param	dmp the property list dumper.
     * @return	the dumper for subsequent dumps.
     */
    protected PLDumper	dumpChars(PLDumper dmp) {
	for (int i = 0; i < charTable.length; i++)
	    if (charTable[i] != CharInfo.NULL) {
		dmp.open("CHARACTER").addChar((short)(i + firstCharCode));
		charTable[i].dump(dmp, this).close();
	    }
	return dmp;
    }

    /**
     * Symbolicaly dumps the uninterpreted rest of the header.
     * @param	dmp the property list dumper.
     * @param	rest the uninterpreted rest of the header.
     * @param	num the first uninterpreted position of the header.
     * @return	the dumper for subsequent dumps.
     */
    protected static PLDumper
    		dumpHeaderRest(PLDumper dmp, int[] rest, int num) {
	for (int i = 0; i < rest.length; i++)
	    dmp.open("HEADER").addDec(i + num).addOct(rest[i]).close();
	return dmp;
    }

    /**
     * Dumps out a comment.
     * @param	dmp the property list dumper.
     * @param	s the character string of the comment.
     * @return	the dumper for subsequent dumps.
     */
    protected PLDumper	dumpComment(PLDumper dmp, String s)
	{ return dmp.open("COMMENT").addStr(s).close(); }

    /*
     * The property names of first 7 font dimension parameters are common for
     * all types of tfm files and they are stored in the following table.
     * See TFtoPL[60,61].
     */

    private static int		init_fp = 0;
    public static final int
	FP_SLANT	= init_fp++,
    	FP_SPACE	= init_fp++,
    	FP_STRETCH	= init_fp++,
    	FP_SHRINK	= init_fp++,
    	FP_X_HEIGHT	= init_fp++,
    	FP_QUAD		= init_fp++,
    	FP_EXTRA_SPACE	= init_fp++,
    	FP_MAX		= init_fp++;

    /** Table of property names common for all tfm file types */
    protected static final String[]	paramLabel = {
        "SLANT", "SPACE", "STRETCH", "SHRINK",
	"XHEIGHT", "QUAD", "EXTRASPACE"
    };

    /**
     * Gives the property name for font dimension parameter.
     * @param	i the number of the parameter.
     * @return	the property name.
     */
    protected String	paramName(int i)
        { return (i < paramLabel.length) ? paramLabel[i] : null; }

}
