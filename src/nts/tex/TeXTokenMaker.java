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
// Filename: nts/tex/TeXTokenMaker.java
// $Id: TeXTokenMaker.java,v 1.1.1.1 2000/08/06 18:12:37 ksk Exp $
package	nts.tex;

import	nts.io.Loggable;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.command.Token;
import	nts.command.CharToken;
import	nts.command.CtrlSeqToken;
import	nts.command.LeftBraceToken;
import	nts.command.RightBraceToken;
import	nts.command.MacroParamToken;
import	nts.command.SpaceToken;
import	nts.command.LetterToken;
import	nts.command.ActiveCharToken;
import	nts.command.OtherToken;
import	nts.command.DefCodePrim;
import	nts.command.InputLineTokenizer;
import	nts.math.MathShiftToken;
import	nts.math.SuperMarkToken;
import	nts.math.SubMarkToken;
import	nts.align.TabMarkToken;

public class	TeXTokenMaker	implements InputLineTokenizer.TokenMaker {

    public interface	Categorizer { int	catCode(int c); }

    public interface	ErrHandler {
	void	error(String ident, Loggable[] params, boolean delAllowed);
	void	fatalError(String ident);
    }

    private Categorizer		categorizer;
    private ErrHandler		errHandler;

    private final CharToken.Maker	TEMP_MAKER = OtherToken.MAKER;

    private final CharToken.Maker	INVALID_MAKER
        = new CharToken.Maker() {
	    public Token	make(CharCode code) {
	        errHandler.error("InvalidChar", null, false);
	        return Token.NULL;
	    }
	};

    private byte[]		scanCats
	= new byte[TeXConfig.MAX_CATEGORY + 1];

    private CharToken.Maker[]	charTokMakers
	= new CharToken.Maker[TeXConfig.MAX_CATEGORY + 1];

    public TeXTokenMaker(Categorizer categ, ErrHandler errHand) {
	categorizer = categ; errHandler = errHand;

        for (int i = 0; i <= TeXConfig.MAX_CATEGORY; i++) {
	    scanCats[i] = InputLineTokenizer.OTHER;
	    charTokMakers[i] = TEMP_MAKER;
	}

	initScanCat(TeXConfig.CAT_ESCAPE,  InputLineTokenizer.ESCAPE);
	initScanCat(TeXConfig.CAT_CAR_RET, InputLineTokenizer.ENDLINE);
	initScanCat(TeXConfig.CAT_IGNORE,  InputLineTokenizer.IGNORE);
	initScanCat(TeXConfig.CAT_SPACER,  InputLineTokenizer.SPACER);
	initScanCat(TeXConfig.CAT_COMMENT, InputLineTokenizer.COMMENT);

	scanCats[TeXConfig.CAT_LETTER] = InputLineTokenizer.LETTER;

	charTokMakers[TeXConfig.CAT_LEFT_BRACE] = LeftBraceToken.MAKER;
	charTokMakers[TeXConfig.CAT_RIGHT_BRACE] = RightBraceToken.MAKER;
	charTokMakers[TeXConfig.CAT_MATH_SHIFT] = MathShiftToken.MAKER;
	charTokMakers[TeXConfig.CAT_TAB_MARK] = TabMarkToken.MAKER;
	charTokMakers[TeXConfig.CAT_MAC_PARAM] = MacroParamToken.MAKER;
	charTokMakers[TeXConfig.CAT_SUP_MARK] = SuperMarkToken.MAKER;
	charTokMakers[TeXConfig.CAT_SUB_MARK] = SubMarkToken.MAKER;
	charTokMakers[TeXConfig.CAT_LETTER] = LetterToken.MAKER;
	charTokMakers[TeXConfig.CAT_OTHER_CHAR] = OtherToken.MAKER;
	charTokMakers[TeXConfig.CAT_ACTIVE_CHAR] = ActiveCharToken.MAKER;
	charTokMakers[TeXConfig.CAT_INVALID_CHAR] = INVALID_MAKER;

    }

    private void	initScanCat(int idx, byte cat)
	{ scanCats[idx] = cat; charTokMakers[idx] = null; }

    /**
     * Gives the scanning category of given internal character code.
     * The return value must be one of: |ESCAPE|, |LETTER|,
     * |SPACER|, |ENDLINE|, |COMMENT|, |IGNORE|, |OTHER|.
     * @param	code internal character code.
     * @return	scanning category of |code|.
     */
    public byte		scanCat(CharCode code)
	{ return scanCats[categorizer.catCode(code.toChar())]; }

    /**
     * Makes a control sequence |Token| with given name.
     * @param	name the name of the control sequence.
     * @return	the control sequence |Token|.
     */
    public Token		make(Name name)
	{ return new CtrlSeqToken(name); }

    /**
     * Makes a character |Token| for given internal character code.
     * @param	code the internal character code.
     * @return	the character |Token|.
     */
    public Token		make(CharCode code) {
	CharToken.Maker		maker
	    = charTokMakers[categorizer.catCode(code.toChar())];
	if (maker != null)
	    return maker.make(code);
	throw new RuntimeException("Invalid category for making a Token");
    }

    /**
     * Makes a |Token| corresponding to space.
     * @return	the space |Token|.
     */
    public Token		makeSpace() { return SpaceToken.TOKEN; }

    private static final Token	PAR_TOKEN = new CtrlSeqToken("par");

    /**
     * makes a |Token| corresponding to blank line (paragraph end).
     * @return	the paragraph |Token|.
     */
    public Token		makePar() { return PAR_TOKEN; }

}
