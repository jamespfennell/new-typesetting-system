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
// Filename: nts/command/Token.java
// $Id: Token.java,v 1.1.1.1 2000/04/29 05:34:26 ksk Exp $
package	nts.command;

import	java.io.Serializable;
import	nts.io.Log;
import	nts.io.Loggable;
import	nts.io.CharCode;
import	nts.io.Name;

/*
 * Base class |Token| defines rather complicated interface because it is used
 * in many different situations (thanks to TeX). It tries to provide
 * reasonable default behavior for all methods.
 */
/**
 * Base class of all kinds of tokens.
 */
public abstract class	Token	implements Serializable, Loggable {

    /** Symbolic constant for non-existing |Token| */
    public static final Token		NULL = null;

    /*
     * In many situations we are interested in |Command| currently associated
     * as the meaning of a |Token|. For some kinds of (non-definable) |Token|s
     * the meaning is hardwired (most of character tokens), for some it is
     * stored in table of equivalents --- therefore the parameter |eqtab|.
     *
     * Note that the class |Command| is used only as the output type of this
     * method in this class (no member is ever referenced) and therefore makes
     * unnecessary dependencies between classes.
     */

    /**
     * Tells whether the meaning of the |Token| can be redefined.
     * @return	|true| if the token is redefinable, |false| otherwise.
     */
    public boolean	definable() { return false; }

    /**
     * Tells whether this |Token| was not created from the input but was
     * inserted during some error recovery.
     * @return	|true| if the token is from error recovery.
     */
    public boolean	frozen() { return false; }

    /**
     * Gives object (potentially) associated in table of equivalents.
     * @param	eqtab table of equivalents.
     * @return	the Command to interpret this token.
     */
    public Command	meaning() { return Command.NULL; }

    /**
     * Define given |Command| to be equivalent in table of equivalents.
     * @param	eqtab table of equivalents.
     * @param	cmd the object to interpret this token.
     * @param	glob if |true| the equivalent is defined globaly.
     */
    public void		define(Command cmd, boolean glob) {
    	throw new RuntimeException(
	    "Attempt to redefine meaning of a non-definable Token");
    }

    public abstract boolean	match(Token tok);

    public boolean		sameCatAs(Token tok)
	{ return (getClass() == tok.getClass()); }

    public Token		category() { return Token.NULL; }

    /*
     * The following few methods test if this |Token| matches in TeX terms a
     * character token of some category:
     * |matchLeftBrace|:	category 1
     * |matchRightBrace|:	category 2
     * |matchMacroParam|:	category 6
     * |matchSpace|:		category 10
     * |matchLetter|:		category 11 and given 7 bit ascii character
     * |matchOther|:		category 12 and given 7 bit ascii character
     */

    /**
     * Tests whether this |Token| matches left brace.
     * @return	|true| if it matches.
     */
    public boolean	matchLeftBrace() { return false; }

    /**
     * Tests whether this |Token| matches right brace.
     * @return	|true| if it matches.
     */
    public boolean	matchRightBrace() { return false; }

    /**
     * Tests whether this |Token| matches macro parameter indicator.
     * @return	|true| if it matches.
     */
//    public boolean	matchMacroParam() { return false; }

    /**
     * Tests whether this |Token| matches spacer.
     * @return	|true| if it matches.
     */
    public boolean	matchSpace() { return false; }

    /**
     * Tests whether this |Token| matches letter of particular character code.
     * @param	letter the character code to be matched.
     * @return	|true| if it matches.
     */
//    public boolean	matchLetter(char c) { return false; }

    /**
     * Tests whether this |Token| matches other char of particular char code.
     * @param	other the character code to be matched.
     * @return	|true| if it matches.
     */
    public boolean	matchOther(char c) { return false; }

    /**
     * Tests whether this |Token| matches normal (letter or other) char of
     * particular char code.
     * @param	normal the character code to be matched.
     * @return	|true| if it matches.
     */
//    public boolean	matchNormal(char c) { return false; }

    /**
     * Gives the 7 bit ascii character code of letter character |Token|.
     * The result is |CharCode.NO_CHAR| if the |Token| is not a letter
     * or if its character code is not a 7 bit ascii.
     * @return	the 7 bit ascii code if the category is matched and the
     *		code is defined, |CharCode.NO_CHAR| otherwise.
     */
    public char		letterChar() { return CharCode.NO_CHAR; }

    /**
     * Gives the 7 bit ascii character code of other character |Token|.
     * The result is |CharCode.NO_CHAR| if the |Token| is not an other
     * or if its character code is not a 7 bit ascii.
     * @return	the 7 bit ascii code if the category is matched and the
     *		code is defined, |CharCode.NO_CHAR| otherwise.
     */
    public char		otherChar() { return CharCode.NO_CHAR; }

    /**
     * Gives the 7 bit ascii character code of normal (letter or other)
     * character |Token|.
     * The result is |CharCode.NO_CHAR| if the |Token| is not normal
     * (letter or other) or if its character code is not a 7 bit ascii.
     * @return	the 7 bit ascii code if the category is matched and the
     *		code is defined, |CharCode.NO_CHAR| otherwise.
     */
//    public char		normalChar() { return CharCode.NO_CHAR; }

    /**
     * Gives the |CharCode| of non active character |Token|.
     * The result is |CharCode.NULL| if the |Token| is not a character
     * or is an active character,
     * @return	the |CharCode| if the category is matched and the
     *		code is defined, |CharCode.NULL| otherwise.
     */
    public CharCode	nonActiveCharCode() { return CharCode.NULL; }

    /**
     * Gives the 7 bit ascii character code of non active character |Token|.
     * The result is |CharCode.NO_CHAR| if the |Token| is active character,
     * is not a character or if its character code is not a 7 bit ascii.
     * @return	the 7 bit ascii code if the category is matched and the
     *		code is defined, |CharCode.NO_CHAR| otherwise.
     */
//    public final char	nonActiveChar() {
//	CharCode	code = nonActiveCharCode();
//	return (code != CharCode.NULL) ? code.toChar()
//					  : CharCode.NO_CHAR;
//    }

    public int		numValue() { return -1; }

    public CharCode	charCode() { return CharCode.NULL; }
    public Token	makeCharToken(CharCode code) { return Token.NULL; }
    public Name		controlName() { return Name.NULL; }

    public boolean	isMacroParameter() { return false; } //XXX better name
    public int		macroParameterNumber() { return -1; }

    public void		addProperlyOn(Log log) { addOn(log); }

    private static CharCode.Maker	maker;

    public static void		setCharCodeMaker(CharCode.Maker mak)
	{ maker = mak; }

    public static CharCode	makeCharCode(char chr)
	{ return maker.make(chr); }

    public static CharCode	makeCharCode(int num)
	{ return maker.make(num); }

    public static Name		makeName(String str) {
        CharCode[]	codes = new CharCode[str.length()];
	for (int i = 0; i < codes.length; i++)
	    codes[i] = maker.make(str.charAt(i));
	return new Name(codes);
    }

    public interface	CharHandler {
    	void	handle(CharCode code, Token src);
    }

}
