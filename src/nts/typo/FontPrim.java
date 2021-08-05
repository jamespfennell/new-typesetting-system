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
// Filename: nts/typo/FontPrim.java
// $Id: FontPrim.java,v 1.1.1.1 2001/02/26 00:01:31 ksk Exp $
package	nts.typo;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.node.FontMetric;
import	nts.command.FileName;
import	nts.command.Token;
import	nts.command.CtrlSeqToken;
import	nts.command.TokenList;
import	nts.command.PrefixPrim;

public class	FontPrim	extends TypoAssignPrim
				implements TokenList.Provider {

    public FontPrim(String name) { super(name); }

    /* STRANGE
     * ensureOpenLog() is in TeX to prevent the font file
     * name from becoming a job name. Is it possible?
     * First line of the form \font\tenrm=cmr10 \input document.tex
     * assigns texput to logname now, but would it assign cmr10 instead of
     * document otherwise?
     */
    /* TeXtp[1257] */
    protected void		assign(Token src, boolean glob) {
        ensureOpenLog();
        Token		tok = PrefixPrim.definableToken();
	Name		ident = makeIdent(tok);
	tok.define(NullFontMetric.COMMAND, glob); skipOptEquals();
	boolean		inpEnbl = getConfig().enableInput(false);
	FileName	name = scanFileName();
	Dimen		size = scanSizeSpec();
	Num		scale = (size != Dimen.NULL)
			      ? Num.NULL : scanScaleSpec();
	getConfig().enableInput(inpEnbl);
	FontMetric	metric = getTypoHandler()
				    .getMetric(name, size, scale, ident, tok);
	if (metric != FontMetric.NULL)
	    tok.define(new SetFont(metric), glob);
    }

    private static final Name		NAME_FONT = Token.makeName("FONT");

    private Name	makeIdent(Token tok) {
        Name		ident = tok.controlName();
	if (ident == Name.NULL) {
	    CharCode	code = tok.charCode();
	    if (code != CharCode.NULL) {
		Name.Buffer	buf = new Name.Buffer(NAME_FONT.length() + 1);
		buf.append(NAME_FONT).append(code);
		ident = buf.toName();
	    }
	} else if (ident.length() == 0) ident = NAME_FONT;
	return ident;
    }

    private Dimen	scanSizeSpec() {
        Dimen		size = Dimen.NULL;
        if (scanKeyword("at")) {
	    size = scanDimen();
	    if (!size.moreThan(0) || !size.lessThan(2048)) {
		error("ImproperAt", str(size));
		size = Dimen.valueOf(10);	//XXX literals, err strings
	    }
	}
	return size;
    }

    private Num		scanScaleSpec() {
        Num		scale = Num.NULL;
        if (scanKeyword("scaled")) {
	    scale = scanNum();
	    if (!scale.moreThan(0) || scale.moreThan(32768)) {
		error("IllegalMag", str(scale));
		scale = Num.valueOf(1000);	//XXX literals, err strings
	    }
	}
	return scale;
    }

    public boolean	hasFontTokenValue() { return true; }
    public boolean	hasFontMetricValue() { return true; }

    /* TeXtp[415,465] */
    public Token	getFontTokenValue()
	{ return new CtrlSeqToken(getCurrFontMetric().getIdent()); }

    /* TeXtp[577] */
    public FontMetric	getFontMetricValue() { return getCurrFontMetric(); }

}
