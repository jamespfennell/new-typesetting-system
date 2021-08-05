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
// Filename: nts/command/Prim.java
// $Id: Prim.java,v 1.1.1.1 2001/03/13 09:24:23 ksk Exp $
package	nts.command;

import	nts.base.BytePar;
import	nts.base.IntPar;
import	nts.base.BoolPar;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.Loggable;
import	nts.io.CharCode;

/**
 * Prim command of the macro language. It can be invoked
 * by its name using control sequence Token.
 */
public abstract class	Prim	extends Command
				implements Primitive {

    /** The name of the primitive */
    private String		name;

    /**
     * Creates a new Prim with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the Prim
     */
    protected	Prim(String name) { this.name = name; }

    public final String		getName() { return name; }

    public final Command	getCommand() { return this; }

    public final void		addOn(Log log) { log.addEsc(name); }

    public final String		toString() { return "@" + name; }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    public static final int		ADVANCE = 0;
    public static final int		MULTIPLY = 1;
    public static final int		DIVIDE = 2;

    protected static Num	performFor(Num val, int operation) {
	scanKeyword("by");
	int	i = scanInt();
	try {
	    switch (operation) {
		case ADVANCE:	val = val.plus(i);	break;
		case MULTIPLY:	val = val.times(i);	break;
		case DIVIDE:	val = val.over(i);	break;
	    }
	} catch (ArithmeticException e)
	    { val = Num.ZERO; error("ArithOverflow"); }
	//XXXX arith_error
	return val;
    }

    protected static Dimen	performFor(Dimen val, int operation) {
	scanKeyword("by");
	try {
	    switch (operation) {
		case ADVANCE:	val = val.plus(scanDimen());	break;
		case MULTIPLY:	val = val.times(scanInt());	break;
		case DIVIDE:	val = val.over(scanInt());	break;
	    }
	} catch (ArithmeticException e)
	    { val = Dimen.ZERO; error("ArithOverflow"); }
	//XXXX arith_error
	return val;
    }

    protected static Glue	performFor(Glue val, int operation,
    					   boolean mu) {
	scanKeyword("by");
	try {
	    switch (operation) {
		case ADVANCE:	val = val.plus(scanGlue(mu));	break;
		case MULTIPLY:	val = val.times(scanInt());	break;
		case DIVIDE:	val = val.over(scanInt());	break;
	    }
	} catch (ArithmeticException e)
	    { val = Glue.ZERO; error("ArithOverflow"); }
	//XXXX arith_error
	return val;
    }

    public static int	scanAnyCode(int par, String err) {
        int	max = getConfig().getIntParam(par);
        int	i = scanInt();
	if (i < 0 || i > max)
	    { error(err, num(i), num(0), num(max)); return 0; }
	return i;
    }

    public static final int	INTP_MAX_REG_CODE = newIntParam();
    public static final int	INTP_MAX_FILE_CODE = newIntParam();
    public static final int	INTP_MAX_CHAR_CODE = newIntParam();

    /* TeXtp[433] */
    public static int	scanRegisterCode()
	{ return scanAnyCode(INTP_MAX_REG_CODE, "BadRegister"); }

    /* TeXtp[435] */
    public static int	scanFileCode()
	{ return scanAnyCode(INTP_MAX_FILE_CODE, "BadFileNum"); }

    /* TeXtp[434] */
    public static int	scanCharacterCode()
	{ return scanAnyCode(INTP_MAX_CHAR_CODE, "BadCharCode"); }

    public static TokenList	scanTokenList(Loggable src, boolean xpand) {
        TokenList.Buffer	buf = new TokenList.Buffer(30);
	InpTokChecker	savedChk = setTokenChecker(
	    new ScanToksChecker("OuterInToks", "EOFinToks", "text", buf, src));
	scanLeftBrace();
	for (int balance = 1;;) {
	    Token	tok = nextScannedToken(xpand, buf);
	    if (tok != Token.NULL) {
		if (tok.matchLeftBrace()) ++balance;
		else if (tok.matchRightBrace() && --balance == 0) break;
		buf.append(tok);
	    }
	}
	setTokenChecker(savedChk);
	return buf.toTokenList();
    }

    public static Token	nextScannedToken(boolean xpand, TokenList.Buffer buf)
	{ return (xpand) ? nextExpToken(buf) : nextRawToken(); }

}
