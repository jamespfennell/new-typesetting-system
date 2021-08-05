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
// Filename: nts/tex/TeXCharMapper.java
// $Id: TeXCharMapper.java,v 1.1.1.1 2001/03/06 20:56:23 ksk Exp $
package	nts.tex;

import	java.io.File;
import	java.io.Writer;
import	java.io.StringWriter;
import	nts.base.Num;
import	nts.io.Log;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.io.InputLine;
import	nts.io.StandardLog;
import	nts.command.Token;
import	nts.command.FileName;

public final class	TeXCharMapper
	implements CharCode.Maker, InputLine.Mapper, StandardLog.Escape {

    public interface	Config {
	int		catCode(int c);
	int		lcNumCode(int c);
	int		ucNumCode(int c);
	int		spaceFactor(int c);
	int		mathCode(int c);
	int		delCode(int c);
	int		escapeNumCode();
	int		newLineNumCode();
	int		endLineNumCode();
    }

    private static Config	config;

    public static void		setConfig(Config conf) { config = conf; }

    private static CharCode	forNum(int n) {
	return (0 <= n && n <= TeXConfig.MAX_TEX_CHAR)
	     ? new Char((char) n) : CharCode.NULL;
    }

    private static CharCode	forNum(Num num)
	{ return forNum(num.intVal()); }

    private static boolean	matchNum(char code, int n)
	{ return (0 <= n && n <= TeXConfig.MAX_TEX_CHAR && n == code); }

    private static boolean	matchNum(char code, Num num)
	{ return matchNum(code, num.intVal()); }

    private static void		putExpCodes(char chr,
					    CharCode.CodeWriter out) {
	writeCode('^', out); writeCode('^', out);
	if (chr < '\100') writeCode((char) (chr + '\100'), out);
	else if (chr < '\200') writeCode((char) (chr - '\100'), out);
	else {
	    writeCode(lcHexDig((chr >> 4) & 15), out);
	    writeCode(lcHexDig(chr & 15), out);
	}
    }

    private static void		putExpChars(char chr,
					    CharCode.CharWriter out) {
	out.writeChar('^'); out.writeChar('^');
	if (chr < '\100') out.writeChar((char) (chr + '\100'));
	else if (chr < '\200') out.writeChar((char) (chr - '\100'));
	else {
	    out.writeChar(lcHexDig((chr >> 4) & 15));
	    out.writeChar(lcHexDig(chr & 15));
	}
    }

/*	//XXX
    private static boolean	isPrintable(char chr) {
	return (  chr >= ' ' && chr < '\177'
	       || chr >= '\200' && Character.isDefined(chr)  );
    }
*/

    private static boolean	isPrintable(char chr)
	{ return (chr >= ' ' && chr < '\177' || chr > '\240'); }

    private static void		writeCode(char chr, CharCode.CodeWriter out)
	{ out.writeCode(new Char(chr)); }

    private static char		lcHexDig(int d)
	{ return (char) ((d < 10) ? '0' + d : 'a' + d - 10); }

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public CharCode	make(char chr) { return new Char(chr); }

    public CharCode	make(int num) { return forNum(num); }

    public CharCode	getEscape() { return forNum(config.escapeNumCode()); }

    public boolean	isNewLine(char chr)
	{ return matchNum(chr, config.newLineNumCode()); }

    public void		writeExpCodes(char chr, CharCode.CodeWriter out) {
	if (isPrintable(chr)) writeCode(chr, out);
	else putExpCodes(chr, out);
    }

    public void		writeExpChars(char chr, CharCode.CharWriter out) {
	if (isPrintable(chr)) out.writeChar(chr);
	else putExpChars(chr, out);
    }

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public CharCode	map(char chr) { return new Char(chr); }

    public CharCode	map(int num) { return forNum(num); }

    public CharCode	endLine() { return forNum(config.endLineNumCode()); }

    /* '\t' is ignored in web2c too */
    public boolean	ignoreTrailing(char chr)
	{ return (chr == ' ' || chr == '\t'); }

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final class Char	implements CharCode {

	private /* final */ char		code;	//XXX JavaC error?

	public Char(char chr) { code = chr; }

	public char		toChar()
	    { return (code <= TeXConfig.MAX_TEX_CHAR) ? code : NO_CHAR; }

	public char		toCanonicalLetter() {
	    int		n = config.lcNumCode(code);
	    return (0 < n && n <= TeXConfig.MAX_TEX_CHAR)
		 ? (char) n : NO_CHAR;
	}

	public int		numValue()
	    { return (code <= TeXConfig.MAX_TEX_CHAR) ? code : -1; }

	public boolean	match(CharCode x) { return (x.match(code)); }
	public boolean	match(char c) { return (c == code); }
	public boolean	match(int n) { return matchNum(code, n); }
	public boolean	match(Num num) { return matchNum(code, num); }

	public CharCode	toLowerCase() {
	    int		n = config.lcNumCode(code);
	    return (0 < n && n <= TeXConfig.MAX_TEX_CHAR)
		 ? new Char((char) n) : this;
	}

	public CharCode	toUpperCase() {
	    int		n = config.ucNumCode(code);
	    return (0 < n && n <= TeXConfig.MAX_TEX_CHAR)
		 ? new Char((char) n) : this;
	}

	public int	spaceFactor() { return config.spaceFactor(code); }
	public int	mathCode() { return config.mathCode(code); }
	public int	delCode() { return config.delCode(code); }

	public boolean	isEscape()
	    { return (config.catCode(code) == TeXConfig.CAT_ESCAPE); }

	public boolean	startsExpand()
	    { return (config.catCode(code) == TeXConfig.CAT_SUP_MARK); }

	public boolean	isLetter()
	    { return (config.catCode(code) == TeXConfig.CAT_LETTER); }

	public boolean	isEndLine() { return match(config.endLineNumCode()); }

	public boolean	isNewLine() { return match(config.newLineNumCode()); }

	public boolean	startsFileExt()	{ return (code == '.'); }

	public void		writeExpCodes(CharCode.CodeWriter out) {
	    if (isPrintable(code)) out.writeCode(this);
	    else putExpCodes(code, out);
	}

	public void		writeExpChars(CharCode.CharWriter out) {
	    if (isPrintable(code)) out.writeChar(code);
	    else putExpChars(code, out);
	}

	public void		writeRawChars(CharCode.CharWriter out)
	    { out.writeChar(code); }

	public void	addOn(Log log) { log.add(this); }

	/**
	* Gives a hash code for an internal character code.
	* @return	the hash code for this object.
	*/
	public int	hashCode() { return (int) code; }

	/**
	* Compares this internal character code to the specified object.
	* The result is |true| if and only if the the argument is not |null| and
	* is the |CharCode| object representing the same internal character code.
	* @param	o the object to compare this internal character code against.
	* @return	|true| if the argument is equal, |false| otherwise.
	*/
	public boolean	equals(Object o) {
	    return (  o != null && o instanceof CharCode
		   && ((CharCode) o).match(code)  );
	}

	public String	toString() { return String.valueOf(code); }

    }

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static class	TeXFileName	implements FileName {

	private Name.Buffer		data = new Name.Buffer();

	public TeXFileName() { }
	public TeXFileName(Name name) { data.append(name); }

	public int		accept(CharCode code) {
	    if (code.toChar() == CharCode.NO_CHAR) return -1;
	    if (code.match(' ')) return 0;
	    data.append(code); return 1;
	}

	public String	getPath() {
	    StringWriter	out = new StringWriter();
	    for (int i = 0; i < data.length(); i++) {
	        CharCode	code = data.codeAt(i);
		if (code instanceof Char) out.write(((Char) code).code);
		// XXX else code.writeExpanded(out);
	    }
	    return out.toString();
	}

	public void	setPath(String path) { data.clear(); append(path); }

	public Name		baseName() {
	    int	beg = indexOfName();
	    int	end = indexOfExt(beg);
	    CharCode[]	codes = new CharCode[end - beg];
	    data.getCodes(beg, end, codes, 0);
	    return new Name(codes);
	}

	public boolean	addDefaultExt(String ext) {
	    int	i = indexOfExt(indexOfName());
	    if (i < data.length()) return false;
	    data.append(new Char('.')); append(ext);
	    return true;
	}

	public void	append(char chr) { data.append(new Char(chr)); }

	public void	append(String str) {
	    int		len = str.length();
	    for (int i = 0; i < len; i++) append(str.charAt(i));
	}

	private int		indexOfName() {
	    String		path = getPath();
	    String		name = (new File(path)).getName();
	    int		i = path.lastIndexOf(name);
	    return (i > 0) ? i : 0;
	}

	private int		indexOfExt(int start) {
	    int		i = data.length();
	    while (--i > start)
		if (data.codeAt(i).startsFileExt()) return i;
	    return data.length();
	}

	public FileName		copy()
	    { return new TeXFileName(data.toName()); }

	public void		addOn(Log log) { log.add(data.toName()); }

	public int		hashCode() { return data.hashCode(); }

	public boolean 		equals(Object o) {
	    return (  o != null && o instanceof TeXFileName
		   && data.equals(((TeXFileName) o).data)  );
	}

	public String		toString() { return getPath(); }

    }

}
