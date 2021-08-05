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
// Filename: nts/command/Command.java
// $Id: Command.java,v 1.1.1.1 2001/03/22 13:31:36 ksk Exp $
package	nts.command;

import	java.io.Serializable;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.Loggable;
import	nts.io.CharCode;
import	nts.node.Box;			//DDD
import	nts.node.BoxSizes;		//DDD
import	nts.node.TreatBox;		//DDD
import	nts.node.FontMetric;		//DDD

/**
 * Command of the macro language.
 */
public abstract class	Command	extends CommandBase
				implements Serializable, Loggable {

    /** Symbolic constant for nonexisting |Command| */
    public static final Command		NULL = null;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Each TeX primitive is implemented as a subclass of |Command| class.
     * So are all the user defined macros. The |Command|s are assigned as a
     * meaning of input |Token|s. Some of them can be expanded (macros and
     * primitives of expand processor) and they cause some tokens to be pushed
     * on the token input. The others can be executed.
     *
     * The main loop of the system reads the |Token|s from the input, gets the
     * associated |Command|s and tries to expand them. If they are not
     * expandable they are executed then. The |Token|s and associated
     * |Command|s can be fetched not only in the main loop but also some
     * commands can read them as parameters. In some of such cases there is no
     * attempt to expand them.
     *
     * First come the non-static methods which define the interface for every
     * piece of command.
     */

    /**
     * Tells whether this |Command| is expandable.
     * @return	|true| if it is expandable.
     */
    public boolean	expandable() { return false; }

    /**
     * Expand itself in the process of macro expansion.
     * @param	src source |Token| for diagnostic output.
     */
    public void		doExpansion(Token src)
	{ throw new RuntimeException("not expandable"); }

    public void		addExpandable(Log log) { addExpandable(log, false); }

    public void		addExpandable(Log log, boolean full) { addOn(log); }

    public void		addExpandable(Log log, int maxCount)
	{ addExpandable(log, true); }

    public Command	meaning(boolean expOK) { return this; }

    public boolean	sameAs(Command cmd) { return (cmd == this); }

    public Token	origin() { return Token.NULL; }

    public static final int	BOOLP_TRACING_COMMANDS = newBoolParam();

    /* TeXtp[1031] */
    public final void		execute(Token src) {
	if (getConfig().getBoolParam(BOOLP_TRACING_COMMANDS))
	    traceCommand(this);
	exec(src);
    }

    /* TeXtp[1031] */
    public final void		execute(Token src, int prefixes) {
	// if (getConfig().getBoolParam(BOOLP_TRACING_COMMANDS))
	    // traceCommand(this);
	exec(src, prefixes);
    }

    /**
     * Performs itself in the process of interpretation of the macro language.
     * @param	src source |Token| for diagnostic output.
     */
    public abstract void	exec(Token src);

    /**
     * Performs itself in the process of interpretation of the macro language
     * after sequence of prefix commands.
     * @param	src source |Token| for diagnostic output.
     * @param	prefixes accumulated prefixes.
     */
    public void		exec(Token src, int prefixes) {
	/* See TeXtp[1212]. */
	backToken(src); error("NonPrefixCommand", this);
    }

    public boolean	immedExec(Token src) { return false; }

    public void		perform(int operation, boolean glob, Command after)
	{ error("CantAfterThe", this, after); }

    public boolean	assignable() { return false; }
    public void		doAssignment(Token src, int prefixes)
	{ throw new RuntimeException("not assignable"); }

    public boolean	isSpacer() { return false; }
    public boolean	isRelax() { return false; }
    public boolean	isLeftBrace() { return false; }
    public boolean	isRightBrace() { return false; }
    public boolean	isMacroParam() { return false; }
    public boolean	isEndCsName() { return false; }
    public boolean	isOuter() { return false; }
    public boolean	isConditional() { return false; }
    public int		endBranchLevel() { return 0; }
    public boolean	isNoBoundary() { return false; }
    public boolean	isMathShift() { return false; }
    //XXX only for alignment preamble
    public boolean	isTabSkip() { return false; }
    //XXX only for alignment body
    public boolean	isNoAlign() { return false; }
    public boolean	isOmit() { return false; }
    public boolean	isCrCr() { return false; }
    //XXX only for alignment (preamble & body)
    public boolean	isCarRet() { return false; }
    public boolean	isTabMark() { return false; }
    public boolean	isSpan() { return false; }
    //XXX only for alignment body
    public boolean	explosive() { return false; }
    public void		detonate(Token src)
	{ throw new RuntimeException("not explosive"); }

    public CharCode	charCode() { return CharCode.NULL; }
    public CharCode	charCodeToAdd() { return CharCode.NULL; }
    public boolean	appendToks(TokenList.Buffer buf) { return false; }

    public boolean	hasNumValue()
			    { return (hasDimenValue() || hasMuDimenValue()); }
    public boolean	hasDimenValue() { return hasGlueValue(); }
    public boolean	hasMuDimenValue() { return hasMuGlueValue(); }
    public boolean	hasGlueValue() { return false; }
    public boolean	hasMuGlueValue() { return false; }
    public boolean	hasToksValue() { return false; }
    public boolean	hasFontTokenValue() { return false; }
    public boolean	hasFontMetricValue() { return false; }
    public boolean	hasRuleValue() { return false; }
    public boolean	hasBoxValue() { return false; }
    public boolean	canMakeBoxValue() { return false; }
    public boolean	hasCrazyValue() { return false; }
    public boolean	hasMathCodeValue() { return false; }
    public boolean	hasDelCodeValue() { return false; }

    public Num		getNumValue() {
	if (hasDimenValue())
	    return Num.valueOf(getDimenValue().toInt(DIMEN_DENOMINATOR));
	if (hasMuDimenValue()) {
	    Num	n = Num.valueOf(getMuDimenValue().toInt(DIMEN_DENOMINATOR));
	    muError();	/* STRANGE but compatible with TeX */
	    return n;
	}
	return Num.NULL;
    }

    public Dimen	getDimenValue() {
	return (hasGlueValue()) ? getGlueValue().getDimen()
				: Dimen.NULL;
    }

    public Dimen	getMuDimenValue() {
	return (hasMuGlueValue()) ? getMuGlueValue().getDimen()
				  : Dimen.NULL;
    }

    public Glue		getGlueValue() { return Glue.NULL; }

    public Glue		getMuGlueValue() { return Glue.NULL; }

    public TokenList	getToksValue() { return TokenList.NULL; }

    public Token	getFontTokenValue() { return Token.NULL; }

    public FontMetric	getFontMetricValue() { return FontMetric.NULL; }

    /* STRANGE only for \leaders */
    public BoxSizes	getRuleValue() { return BoxSizes.NULL; }

    public Box		getBoxValue() { return Box.NULL; }

    public void		makeBoxValue(TreatBox proc)
	{ throw new RuntimeException("Cannot make a box"); }

    /* STRANGE only for \leaders */
    public Glue		getSkipForLeaders() { return Glue.NULL; }

    public int		getMathCodeValue() { return 0; }
    public int		getDelCodeValue() { return 0; }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * The language interpreter
     */

    public static final int	TOKSP_EVERY_JOB = newToksParam();

    private static boolean	working;
    private static boolean	dumping;

    public static boolean	mainLoop() {
	working = true;
	getConfig().getToksInserter(TOKSP_EVERY_JOB).insertToks();
	do {
	    Token	tok = nextExpToken();
	    meaningOf(tok).execute(tok);
	} while (working);
	return dumping;
    }

    public static void		endMainLoop(boolean dump)
	{ dumping = dump; working = false; }

    public static void		cleanUp() {
	ensureOpenLog();
	getTokStack().close();
	finishGroups();
	//XXX TeXtp[1335]
    }

}
