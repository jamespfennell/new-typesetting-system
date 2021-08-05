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
// Filename: nts/command/CommandBase.java
// $Id: CommandBase.java,v 1.1.1.1 2001/03/22 13:47:30 ksk Exp $
package	nts.command;

import	java.io.Serializable;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.base.BytePar;
import	nts.base.IntPar;
import	nts.base.BoolPar;
import	nts.base.EqTable;
import	nts.base.LevelEqTable;
import	nts.io.Log;
import	nts.io.NullLog;
import	nts.io.Loggable;
import	nts.io.MaxLoggable;
import	nts.io.EqTraceable;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.io.InputLine;
import	nts.io.LineInput;
import	nts.io.LineOutput;
import	nts.io.DoubleLineOutput;

/*
 * The language interpreter
 */
public abstract class	CommandBase {

    /** The |EqTable| of the macro language interpreter */
    private static LevelEqTable		eqTable;

    /**
     * Assigns a table of equivalents for the macro language interpreter.
     * @param	eqtab the table of equivalents.
     */
    public static void	setEqt(LevelEqTable eqtab)
	{ eqTable = eqtab; }

    /**
     * Gives the table of equivalents of the macro language interpreter.
     * @return	the current table of equivalents.
     */
    public static EqTable	getEqt() { return eqTable; }

    public static Command	meaningOf(Token tok) {
        Command		cmd = tok.meaning();
	return (cmd != Command.NULL) ? cmd : Undefined.getUndefined();
    }

    public static Command	meaningOf(Token tok, boolean expOK)
	{ return meaningOf(tok).meaning(expOK); }

    /** The input tokenizer stack of the macro language interpreter */
    private static TokenizerStack	tokStack;

    /**
     * Assigns a tokenizer stack for the macro language interpreter.
     * @param	tokStack the input tokenizer stack.
     */
    public static void		setTokStack(TokenizerStack tokstack)
	{ tokStack = tokstack; }

    /**
     * Gives the tokenizer stack of the macro language interpreter.
     * @return	the current input tokenizer stack.
     */
    public static TokenizerStack	getTokStack() { return tokStack; }

    public static InpTokChecker		setTokenChecker(InpTokChecker chk)
	{ return tokStack.setChecker(chk); }

    public static int		currLineNumber()
	{ return tokStack.lineNumber(); }

    public static int		braceNestingDifference(Token tok) {
	return (tok.matchLeftBrace()) ? 1
	     : (tok.matchRightBrace()) ? -1 : 0;
    }

    /**
     * Gives the next unexpanded input |Token| of macro language interpreter.
     * @param	canExpand boolean output parameter querying whether the
     *			  acquired |Token| can be expanded (e.g. was not
     *			  preceded by \noexpand).
     * @return	next |Token|.
     */
    public static Token		nextRawToken(BoolPar canExpand) {
	Token		tok;
	for (;;) {
	    tok = tokStack.nextToken(canExpand);
	    Command	cmd = meaningOf(tok);
	    if (cmd.explosive()) cmd.detonate(tok);
	    else break;
	}
	adjustBraceNesting(braceNestingDifference(tok));
	return tok;
    }

    /**
     * Gives the next unexpanded input |Token| of macro language interpreter.
     * @return	next |Token|.
     */
    public static Token		nextRawToken()
	{ return nextRawToken(BoolPar.NULL); }
	//XXX make dummy here instead of in TokenizerStack

    public static Token		nextUncheckedRawToken(BoolPar canExpand) {
	InpTokChecker	savedChk = setTokenChecker(InpTokChecker.NULL);
	Token		tok =  nextRawToken(canExpand);
	setTokenChecker(savedChk);
	return tok;
    }

    public static Token		nextUncheckedRawToken()
	{ return nextUncheckedRawToken(BoolPar.NULL); }

    /**
     * Pushes a |Token| back to be read again by macro language interpreter.
     * @param	tok the |Token| to be pushed back.
     */
    public static void		backToken(Token tok) {
        tokStack.cleanFinishedLists();
	backTokenWithoutCleaning(tok);
    }

    /* STRNGE why we don't simply always clean the stack? */
    public static void		backTokenWithoutCleaning(Token tok) {
	tokStack.backUp(tok, true);
	adjustBraceNesting(-braceNestingDifference(tok));
    }

    public static void		backList(TokenList list)
	{ tokStack.backUp(list); }

    public static void		pushToken(Token tok, String desc)
	{ tokStack.push(tok, '<' + desc + "> "); }

    public static void		pushList(TokenList list, String desc)
	{ tokStack.push(list, '<' + desc + "> "); }

    public static void		insertToken(Token tok) {
        tokStack.cleanFinishedLists();
	tokStack.push(tok, "<inserted text> ");
    }

    public static void		insertTokenWithoutCleaning(Token tok)
	{ tokStack.push(tok, "<inserted text> "); }

    public static void		insertList(TokenList list)
	{ tokStack.push(list, "<inserted text> "); }

    /**
     * Gives the next expanded |Token| for macro language interpreter.
     * @return	next |Token|
     */
    /* See TeXtp[370]. */
    public static Token		nextExpToken() {
        Token		tok;
	do tok = onlyRawToken();
	while (tok == Token.NULL);
	return tok;
    }

    public static Token		onlyRawToken() {
	BoolPar		exp = new BoolPar();
	Token		tok = nextRawToken(exp);
	Command		cmd = meaningOf(tok);
	if (cmd.expandable() && exp.get())
	    { cmd.doExpansion(tok); return Token.NULL; }
	return tok;
    }

    public static Token		nextExpToken(TokenList.Buffer buf) {
	BoolPar		exp = new BoolPar();
	Token		tok = nextRawToken(exp);
	Command		cmd = meaningOf(tok);
	if (exp.get()) {
	    if (cmd.appendToks(buf)) return Token.NULL;
	    if (cmd.expandable()) { cmd.doExpansion(tok); return Token.NULL; }
	}
	return tok;
    }

    /* STRANGE
     * the track of brace nesting is kept only for alignment which
     * might be otherwise completely independent
     */
    private static BraceNesting		braceNesting = new BraceNesting() {
	public void	adjust(int count) { }
    };

    public static BraceNesting	setBraceNesting(BraceNesting nesting) {
	BraceNesting	old = braceNesting;
	braceNesting = nesting;
	return old;
    }

    public static void		adjustBraceNesting(int count)
	{ braceNesting.adjust(count); }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    private static int		MAX_INT_PARAM = 0;
    private static int		MAX_DIM_PARAM = 0;
    private static int		MAX_GLUE_PARAM = 0;
    private static int		MAX_TOKS_PARAM = 0;
    private static int		MAX_NODE_PARAM = 0;
    private static int		MAX_BOOL_PARAM = 0;

    public static int		maxIntParam() { return MAX_INT_PARAM; }
    public static int		maxDimParam() { return MAX_DIM_PARAM; }
    public static int		maxGlueParam() { return MAX_GLUE_PARAM; }
    public static int		maxToksParam() { return MAX_TOKS_PARAM; }
    public static int		maxBoolParam() { return MAX_BOOL_PARAM; }

    protected static int	newIntParam() { return MAX_INT_PARAM++; }
    protected static int	newDimParam() { return MAX_DIM_PARAM++; }
    protected static int	newGlueParam() { return MAX_GLUE_PARAM++; }
    protected static int	newToksParam() { return MAX_TOKS_PARAM++; }
    protected static int	newBoolParam() { return MAX_BOOL_PARAM++; }

    public interface	Config {
	int			getIntParam(int param);
	Dimen			getDimParam(int param);
	Glue			getGlueParam(int param);
	TokenList		getToksParam(int param);
	boolean			getBoolParam(int param);
	String			getGlueName(int param);
	TokenList.Inserter	getToksInserter(int param);
	boolean			enableInput(boolean val);
	boolean			enableAfterAssignment(boolean val);
	void			afterAssignment();
	boolean			formatLoaded();
	Token			frozenFi();
    }

    private static Config	config;
    public static void		setConfig(Config conf) { config = conf; }
    public static Config	getConfig() { return config; }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    public interface	IOHandler {

	Name		getJobName();
	void		ensureOpenLog();
	void		openInput(FileName name);
	ReadInput	openRead(FileName name, int num);
	ReadInput	defaultRead(int num);
	Log		makeLog(LineOutput out);
	Log		makeStringLog();
	Log		openWrite(FileName name, int num);
	FileName	makeFileName();

	void	resetErrorCount();
	void	error(String ident, Loggable[] params, boolean delAllowed);
	void	fatalError(String ident);
	void	errMessage(TokenList message);
	void	logMode();
	void	completeShow();
	void	illegalCommand(Command cmd);

    }

    /** The input handler of the macro language interpreter */
    private static IOHandler	ioHandler;

    /**
     * Assigns an input handler for the macro language interpreter.
     * @param	ioHandler the input handler.
     */
    public static void	setIOHandler(IOHandler ioHand)
	{ ioHandler = ioHand; }

    public static IOHandler	getIOHandler() { return ioHandler; }

    public static Log		makeLog(LineOutput out)
	{ return ioHandler.makeLog(out); }

    public static void		ensureOpenLog()
	{ ioHandler.ensureOpenLog(); }

    public static void		startInput()
	{ ioHandler.openInput(scanFileName()); }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    private static LineOutput		terminal   = LineOutput.NULL;
    private static LineOutput		logFile    = LineOutput.NULL;
    private static LineOutput		termAndLog = LineOutput.NULL;

    public static Log			termLog = NullLog.LOG;
    public static Log			fileLog = NullLog.LOG;
    public static Log			normLog = NullLog.LOG;
    public static Log			diagLog = NullLog.LOG;

    private static boolean		termEnable = false;
    private static boolean		diagOnTerm = false;

    public static boolean	isTermLogActive()
	{ return (termLog != NullLog.LOG); }	//SSS

    public static boolean	isFileLogActive()
	{ return (fileLog != NullLog.LOG); }	//SSS

    public static boolean	termDiagActive() { return diagOnTerm; }

    private static void		setupLogs(LineOutput term, LineOutput file,
    					  boolean ten) {
	LineOutput	both = termAndLog;
	if (term != terminal || file != logFile)
	    both = (term != LineOutput.NULL && file != LineOutput.NULL)
		 ? new DoubleLineOutput(term, file)
		 : (term != LineOutput.NULL) ? term : file;
	if (ten != termEnable || term != terminal)
	    termLog = (ten && term != LineOutput.NULL)
	    	    ? makeLog(term) : NullLog.LOG;
	if (file != logFile)
	    fileLog = (file != LineOutput.NULL)
	    	    ? makeLog(file) : NullLog.LOG;
	if (ten != termEnable || both != termAndLog)
	    normLog = (!ten) ? fileLog
		    : (both != LineOutput.NULL)
		    ? makeLog(both) : NullLog.LOG;
	diagLog = (diagOnTerm) ? normLog : fileLog;
	terminal = term; logFile = file;
	termEnable = ten; termAndLog = both;
    }

    public static void	setTermEnable(boolean ten)
	{ if (ten != termEnable) setupLogs(terminal, logFile, ten); }

    public static void	setDiagOnTerm(boolean dot)
	{ diagLog = (diagOnTerm = dot) ? normLog : fileLog; }

    public static void		setTerminal(LineOutput term)
	{ setupLogs(term, logFile, termEnable); }

    public static void		setLogFile(LineOutput file)
	{ setupLogs(terminal, file, termEnable); }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    private static LineInput	input;

    public static void		setInput(LineInput in) { input = in; }

    public static InputLine	promptInput(String prompt) {
        if (terminal != LineOutput.NULL) {
	    termAndLog.add(prompt); terminal.flush();
	    InputLine	line = input.readLine();
	    if (line != LineInput.EOF) {
		terminal.setStartLine();
		fileLog.add(line).endLine();
	    } else fatalError("EOFonTerm");
	    return line;
	}
	return LineInput.EOF;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    public static Loggable	num(final int n) {
        return new Loggable() {
	    public void		addOn(Log log) { log.add(n); }
	};
    }

    public static Loggable	str(final String s) {
        return new Loggable() {
	    public void		addOn(Log log) { log.add(s); }
	};
    }

    public static Loggable	str(final Object o) {
        return new Loggable() {
	    public void		addOn(Log log) { log.add(o.toString()); }
	};
    }

    public static Loggable	esc(final String s) {
        return new Loggable() {
	    public void		addOn(Log log) { log.addEsc(s); }
	};
    }

    public static Loggable	esc(final Name n) {
        return new Loggable() {
	    public void		addOn(Log log) { n.addEscapedOn(log); }
	};
    }

    public static void		error(String ident)
	{ ioHandler.error(ident, null, true); }

    public static void		error(String ident, Loggable p1) {
        Loggable[]	params = { p1 };
	ioHandler.error(ident, params, true);
    }

    public static void		error(String ident, Loggable p1, Loggable p2) {
        Loggable[]	params = { p1, p2 };
	ioHandler.error(ident, params, true);
    }

    public static void		error(String ident, Loggable p1, Loggable p2,
						    Loggable p3) {
        Loggable[]	params = { p1, p2, p3 };
	ioHandler.error(ident, params, true);
    }

    public static void		error(String ident, Loggable p1, Loggable p2,
						    Loggable p3, Loggable p4) {
        Loggable[]	params = { p1, p2, p3, p4 };
	ioHandler.error(ident, params, true);
    }

    public static void		error(String ident, Loggable[] params)
	{ ioHandler.error(ident, params, true); }

    public static void		nonDelError(String ident, Loggable[] params)
	{ ioHandler.error(ident, params, false); }

    public static void		fatalError(String ident)
	{ ioHandler.fatalError(ident); }

    public static void		errMessage(TokenList message)
	{ ioHandler.errMessage(message); }

    public static void		completeShow()
	{ ioHandler.completeShow(); }

    public static void		illegalCommand(Command cmd)
	{ ioHandler.illegalCommand(cmd); }

    public static final int	INTP_MAX_RUNAWAY_WIDTH = newIntParam();

    public static void		runAway(String desc, MaxLoggable list) {
	normLog.startLine().add("Runaway ").add(desc).add('?').endLine();
	list.addOn(normLog, getConfig().getIntParam(INTP_MAX_RUNAWAY_WIDTH));
    }

    /* TeXtp[299] */
    public static void		traceCommand(Command cmd) {
        diagLog.startLine().add('{'); ioHandler.logMode();
	cmd.addOn(diagLog); diagLog.add('}').startLine();
    }

    /* TeXtp[299] */
    public static void		traceExpandable(Command cmd) {
        diagLog.startLine().add('{'); ioHandler.logMode();
	cmd.addExpandable(diagLog); diagLog.add('}').startLine();
    }

    public static final int	BOOLP_TRACING_TOKEN_LISTS = newBoolParam();

    public static void		tracedPushXList(TokenList list, String desc) {
	if (getConfig().getBoolParam(BOOLP_TRACING_TOKEN_LISTS))
	    diagLog.startLine().addEsc(desc)
		.add("->").add(list).startLine();
	pushList(list, desc);
    }

    public static void		tracedPushList(TokenList list, String desc)
	{ if (!list.isEmpty()) tracedPushXList(list, desc); }

    public static final int	INTP_MAX_TLRES_TRACE = newIntParam();
    public static final int	BOOLP_TRACING_RESTORES = newBoolParam();

    public static abstract class	NumKind	extends EqTable.NumKind
    						implements Serializable {

	public final void	restored(int key, Object oldVal)
	    { trace("restoring", key); }

	public final void	retained(int key)
	    { trace("retaining", key); }

	private void	trace(String action, int key) {
	    if (getConfig().getBoolParam(BOOLP_TRACING_RESTORES)) {
		diagLog.add('{').add(action).add(' ');
		addDescOn(key, diagLog); diagLog.add('=');
		addValueOn(key, diagLog); diagLog.add('}').startLine();
	    }
	}

	protected abstract void		addDescOn(int key, Log log);
	protected abstract void		addValueOn(int key, Log log);

    }

    public static abstract class	TokKind	extends EqTable.ObjKind
    						implements Serializable {

	public final void	restored(Object key, Object oldVal)
	    { trace("restoring", key); }

	public final void	retained(Object key)
	    { trace("retaining", key); }

	private void	trace(String action, Object key) {
	    if (getConfig().getBoolParam(BOOLP_TRACING_RESTORES)) {
	        Token		tok = getToken(key);
		diagLog.add('{').add(action).add(' ');
		diagLog.add(tok).add('=');
		meaningOf(tok).addExpandable(diagLog,
			    getConfig().getIntParam(INTP_MAX_TLRES_TRACE));
		diagLog.add('}').startLine();
	    }
	}

	protected abstract Token	getToken(Object key);

    }

    public static final int
	RESTORING	= 0,
	RETAINING	= 1;

    public static void		traceRestore(int action, EqTraceable eqt) {
	if (getConfig().getBoolParam(BOOLP_TRACING_RESTORES)) {
	    String	s;
	    switch (action) {
	        case RESTORING:	s = "restoring";	break;
	        case RETAINING:	s = "retaining";	break;
		default:
		    throw new RuntimeException(
		    	"invalid action for traceRestore (" + action + ')');
	    }
	    diagLog.add('{').add(s).add(' ');
	    eqt.addEqDescOn(diagLog);
	    diagLog.add('=');
	    eqt.addEqValueOn(diagLog);
	    diagLog.add('}').startLine();
	}
    }

    public static abstract class	ExtEquiv
				implements EqTable.ExtEquiv, EqTraceable {

	private int		eqLevel = 0;

	public final int	getEqLevel() { return eqLevel; }
	public final void	setEqLevel(int lev) { eqLevel = lev; }

	public final void	retainEqValue()
	    { traceRestore(RETAINING, this); }

	public final void	restoreEqValue(Object val) {
	    setEqValue(val);
	    traceRestore(RESTORING, this);
	}

	protected final void	beforeSetting(boolean glob)
	    { getEqt().beforeSetting(this, glob); }

	public abstract Object	getEqValue();
	public abstract void	setEqValue(Object val);
	public abstract void	addEqDescOn(Log log);
	public abstract void	addEqValueOn(Log log);

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    private static class	CurrGroup	implements EqTable.ExtEquiv {

	private int		eqLevel = 0;

	public final int	getEqLevel() { return eqLevel; }
	public final void	setEqLevel(int lev) { eqLevel = lev; }

	private Group		group = new BottomGroup();

	public final Group	get() { return group; }

	public void		push(Group grp) {
	    grp.open(); eqTable.pushLevel();
	    eqTable.save(this); grp.start();
	    group = grp;
	}

	public void		pop() {
	    Group	grp = group;
	    grp.stop(); eqTable.popLevel(); grp.close();
	}

	public void		kill() { eqTable.popLevel(); }

	public final Object	getEqValue() { return group; }
	public final void	retainEqValue() { }
	public final void	restoreEqValue(Object val)
	    { group.unsaveAfter(); group = (Group) val; }

    }

    private static CurrGroup	currGroup = new CurrGroup();

    public static Group		getGrp() { return currGroup.get(); }
    public static void		pushLevel(Group grp) { currGroup.push(grp); }
    public static void		popLevel() { currGroup.pop(); }

    /* STRANGE
     * killLevel() is there only for killing the DisplayGroup
     * when $$\halign{...}$$ occurs
     */
    public static void		killLevel() { currGroup.kill(); }

    public static void		finishGroups() {
    	if (eqTable.getLevel() > 0)
	    normLog.startLine().add('(').addEsc("end")
	    	   .add(" occurred inside a group at level ")
		   .add(eqTable.getLevel()).add(')');
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    /* STRANGE
     * In case of font identifier the token is backed and read immediately to
     * mimmic TeX log output.
     */
    /* TeXtp[465] */
    protected static TokenList	theToks() {
        Token		tok = nextExpToken();
	Command		cmd = meaningOf(tok);
        if (cmd.hasToksValue()) return cmd.getToksValue();
	if (cmd.hasFontTokenValue()) {
	    backToken(tok); nextRawToken();
	    return new TokenList(cmd.getFontTokenValue());
	}
	String			s;
	if (cmd.hasCrazyValue()) s = "0";
	else if (cmd.hasMuGlueValue()) s = cmd.getMuGlueValue().toString("mu");
	else if (cmd.hasGlueValue()) s = cmd.getGlueValue().toString("pt");
	else if (cmd.hasMuDimenValue()) s = cmd.getMuDimenValue().toString("mu");
	else if (cmd.hasDimenValue()) s = cmd.getDimenValue().toString("pt");
	else if (cmd.hasNumValue()) s = cmd.getNumValue().toString();
	else { s = "0"; error("CantAfterThe", cmd, esc("the")); }
	return new TokenList(s);
    }

    public static int	scanInt() { return scanInt(IntPar.NULL); }
    public static Num	scanNum() { return Num.valueOf(scanInt(IntPar.NULL)); }
    public static Dimen	scanDimen() { return scanDimen(false, BytePar.NULL); }
    public static Dimen	scanMuDimen() { return scanDimen(true, BytePar.NULL); }
    public static Glue	scanGlue() { return scanGlue(false); }
    public static Glue	scanMuGlue() { return scanGlue(true); }

    protected static int	scanInt(IntPar radix) {
	int		rdx = 0;
	long		val = 0;
	Command		cmd;
	BoolPar		negative = new BoolPar(false);
	Token		tok = scanSign(negative);
	if (tok.matchOther('`')) {
	    tok = nextRawToken();
	    if ((val = tok.numValue()) < 0)
		{ val = '0'; backToken(tok); error("NonNumericToken"); }
	    else {
		adjustBraceNesting(-braceNestingDifference(tok));
		skipOptExpSpacer();
	    }
	} else if ((cmd = meaningOf(tok)).hasNumValue()) {
	  val = cmd.getNumValue().intVal();
	} else {
	    if (tok.matchOther('\''))
	        { rdx = 8; tok = nextExpToken(); }
	    else if (tok.matchOther('\"'))
	        { rdx = 16; tok = nextExpToken(); }
	    else rdx = 10;
	    boolean	empty = true;
	    for (boolean ok = true;; tok = nextExpToken()) {
		int	digit = -1;
	        char	dig = tok.otherChar();
		if (dig != CharCode.NO_CHAR) {
		    if (rdx > 10 && dig >= 'A')
			digit = (dig - 'A') + 10;
		    else if (dig >= '0' && dig <= '9')
			digit = dig - '0';
		} else if (rdx > 10) {
		    dig = tok.letterChar();
		    if (dig != CharCode.NO_CHAR && dig >= 'A')
			digit = (dig - 'A') + 10;
		}
		if (digit < 0 || digit >= rdx) break;
		if (ok) {
		    empty = false;
		    val = val * rdx + digit;
		    if (val > Num.MAX_INT_VALUE) {
			ok = false;
		        error("NumberTooBig");
			val = Num.MAX_INT_VALUE;
		    }
		}
	    }
	    if (empty) { backToken(tok); error("MissingNumber"); }
	    else skipOptSpacer(tok);
	}
	IntPar.set(radix, rdx);
	return (negative.get()) ? - (int) val : (int) val;
    }

    protected static final int		DIMEN_DENOMINATOR = 0x10000;

    /* TeXtp[448] */
    protected static Dimen	scanDimen(boolean mu, BytePar glueOrder) {
	Dimen		val = Dimen.NULL;
	BytePar.set(glueOrder, Glue.NORMAL);
	BoolPar		negative = new BoolPar(false);
	Token		tok = scanSign(negative);
	Command		cmd = meaningOf(tok);
	if (mu) {
	    /* STRANGE
	     * This branch is really ugly (but compatible with TeX).
	     * Note the asymetry with the else (!mu) branch.
	     */
	    if (cmd.hasMuDimenValue()) val = cmd.getMuDimenValue();
	    else if (cmd.hasDimenValue()) {
		int	n = cmd.getDimenValue().toInt(DIMEN_DENOMINATOR);
		muError();
		val = convToDimenUnit(n, Dimen.ZERO, mu, glueOrder);
	    } else if (hasOtherValue(cmd)) {
		backToken(tok); error("MissingNumber");
		muError();
		val = convToDimenUnit(0, Dimen.ZERO, mu, glueOrder);
	    }
	} else {
	    if (cmd.hasDimenValue()) val = cmd.getDimenValue();
	    else if (cmd.hasMuDimenValue())
		{ val = cmd.getMuDimenValue(); muError(); }
	    else if (hasOtherValue(cmd))
		{ val = Dimen.ZERO; backToken(tok); error("MissingNumber"); }
	}
	if (val == Dimen.NULL) {
	    if (cmd.hasNumValue()) {
		int		n = cmd.getNumValue().intVal();
		if (n < 0) { negative.negate(); n = -n; }
		val = convToDimenUnit(n, Dimen.ZERO, mu, glueOrder);
	    } else {
		IntPar		radix = new IntPar(10);
		boolean		point = isPoint(tok);
		int			whole = 0;
		Dimen		fract = Dimen.ZERO;
		if (!point) {
		    backToken(tok);
		    whole = scanInt(radix);
		    tok = nextExpToken();
		    point = isPoint(tok);
		}
		if (point && radix.get() == 10) {
		    StringBuffer	buf = new StringBuffer();
		    buf.append('0').append('.');
		    for (;;) {
			tok = nextExpToken();
			char	dig = tok.otherChar();
			if (  dig != CharCode.NO_CHAR
			   && dig >= '0' && dig <= '9'  ) buf.append(dig);
			else break;
		    }
		    try { fract = Dimen.valueOf(buf.toString()); }
		    catch (NumberFormatException e)
			{ whole = Num.MAX_INT_VALUE + 1; }
		}
		skipOptSpacer(tok);
		val = convToDimenUnit(whole, fract, mu, glueOrder);
	    }
	}
	val = checkDimen(val);
	return (negative.get()) ? val.negative() : val;
    }

    private static Dimen	convToDimenUnit(int whole, Dimen fract,
					    boolean mu, BytePar glueOrder) {
	if (glueOrder != BytePar.NULL) {
	    glueOrder.set(scanGlueOrderUnit());
	    if (glueOrder.get() != Glue.NORMAL)
	        return fract.plus(whole);
	}
	return convToDimenUnit(whole, fract, mu);
    }

    private static byte		scanGlueOrderUnit() {
	if (scanKeyword("fil")) {
	    byte	glord = Glue.FIL;
	    while (scanKeyword("l")) {
		if (glord < Glue.MAX_ORDER) glord++;
		else error("IllegalFil");
	    }
	    skipOptExpSpacer();
	    return glord;
	}
	return Glue.NORMAL;
    }

    public static final int	DIMP_EM = newDimParam();
    public static final int	DIMP_EX = newDimParam();

    private static Dimen	convToDimenUnit(int whole, Dimen fract,
						boolean mu) {
        Dimen		val = Dimen.NULL;
	Token		tok = nextExpNonSpacer();
	Command		cmd = meaningOf(tok);
	if (mu) {
	    if (cmd.hasMuDimenValue()) val = cmd.getMuDimenValue();
	    else if (cmd.hasDimenValue())
		{ val = cmd.getDimenValue(); muError(); }
	    else if (hasOtherValue(cmd)) {
		backToken(tok); error("MissingNumber");
		val = Dimen.ZERO; muError();
	    } else if (cmd.hasNumValue()) {
		val = Dimen.valueOf(cmd.getNumValue().intVal(),
				    DIMEN_DENOMINATOR);
		muError();
	    }
	} else {
	    if (cmd.hasDimenValue()) val = cmd.getDimenValue();
	    else if (cmd.hasMuDimenValue())
		{ val = cmd.getMuDimenValue(); muError(); }
	    else if (hasOtherValue(cmd))
		{ val = Dimen.ZERO; backToken(tok); error("MissingNumber"); }
	    else if (cmd.hasNumValue())
		val = Dimen.valueOf(cmd.getNumValue().intVal(),
				    DIMEN_DENOMINATOR);
	}
	if (val == Dimen.NULL) {
	    backToken(tok);
	    if (mu) {
		if (!scanKeyword("mu")) error("IllegalMu");
		skipOptExpSpacer();
		return fract.plus(whole);
	    }
	    else if (scanKeyword("em"))
		val = getConfig().getDimParam(DIMP_EM);
	    else if (scanKeyword("ex"))
		val = getConfig().getDimParam(DIMP_EX);
	    else {
		val = convToDimenUnit(whole, fract);
		skipOptExpSpacer();
		return val;
	    }
	    skipOptExpSpacer();
	}
	return val.times(whole).plus(val.times(fract));
    }

    private static class	DimUnitDesc {
	String		id;	/* identifier */
	int		num;	/* numerator */
	int		den;	/* denominator */
	DimUnitDesc(String i, int n, int d) { id = i; num = n; den = d; }
	DimUnitDesc(String i, int n) { id = i; num = n; den = 0; }
    }

    private static DimUnitDesc[]	dimUnits = {
	new DimUnitDesc("pt", 0),
	new DimUnitDesc("in", 7227,  100),
	new DimUnitDesc("pc", 12,    1),
	new DimUnitDesc("cm", 7227,  254),
	new DimUnitDesc("mm", 7227,  2540),
	new DimUnitDesc("bp", 7227,  7200),
	new DimUnitDesc("dd", 1238,  1157),
	new DimUnitDesc("cc", 14856, 1157),
	new DimUnitDesc("sp", -16),
    };

    public static Dimen		makeDimen(int dim, String unit) {
	for (int i = 0; i < dimUnits.length; i++)
	    if (dimUnits[i].id.equals(unit)) {
	        int	num = dimUnits[i].num;
	        int	den = dimUnits[i].den;
		return (den != 0) ? Dimen.valueOf(dim).times(num, den)
				  : Dimen.shiftedValueOf(dim, num);
	    }
	throw new RuntimeException("Illegal unit of measure (" + unit + ")");
    }

    public static final int	INTP_MAGNIFICATION = newIntParam();

    private static Dimen	convToDimenUnit(int whole, Dimen fract) {
	if (scanKeyword("true")) {
	    int		mag = getConfig().getIntParam(INTP_MAGNIFICATION);
	    long	w = whole * 1000L;
	    fract = fract.times(1000).plus((int) (w % mag)).over(mag);
	    int		i = fract.toInt();
	    whole = (int) (w / mag) + i;
	    fract = fract.minus(i);
	}
	for (int i = 0; i < dimUnits.length; i++)
	    if (scanKeyword(dimUnits[i].id)) {
	        int	num = dimUnits[i].num;
	        int	den = dimUnits[i].den;
	    /*
	     *	int	p = whole * num;
	     *	return fract.times(num).plus(p % den).over(den).plus(p / den);
	     */
		return (den != 0) ? fract.plus(whole).times(num, den)
				  : Dimen.shiftedValueOf(whole, num)
					 .plus(fract.shifted(num));
	    }
	error("IllegalUnit");
	return fract.plus(whole);
    }

    protected static void	muError() { error("MixedGlueUnits"); }

    private static Dimen	checkDimen(Dimen val) {
	if (val.moreThan(Dimen.MAX_VALUE))
	    { val = Dimen.MAX_VALUE; error("DimenTooLarge"); }
	return val;
    }

    protected static Glue	scanGlue(boolean mu) {
	Glue		val = Glue.NULL;
	Dimen		width = Dimen.NULL;
	Dimen		plus = Dimen.ZERO;
	Dimen		minus = Dimen.ZERO;
        BytePar		plusOrder = new BytePar(Glue.NORMAL);
        BytePar		minusOrder = new BytePar(Glue.NORMAL);
	BoolPar		negative = new BoolPar(false);
	Token		tok = scanSign(negative);
	Command		cmd = meaningOf(tok);
	if (mu) {
	    if (cmd.hasMuGlueValue()) val = cmd.getMuGlueValue();
	    else if (cmd.hasGlueValue())
		{ val = cmd.getGlueValue(); muError(); }
	    else if (cmd.hasMuDimenValue()) width = cmd.getMuDimenValue();
	    else if (cmd.hasDimenValue())
		{ width = cmd.getDimenValue(); muError(); }
	    else if (hasOtherValue(cmd)) {
		backToken(tok); error("MissingNumber");
		width = Dimen.ZERO; muError();
	    }
	} else {
	    if (cmd.hasGlueValue()) val = cmd.getGlueValue();
	    else if (cmd.hasMuGlueValue())
		{ val = cmd.getMuGlueValue(); muError(); }
	    else if (cmd.hasDimenValue()) width = cmd.getDimenValue();
	    else if (cmd.hasMuDimenValue())
		{ width = cmd.getMuDimenValue(); muError(); }
	    else if (hasOtherValue(cmd))
		{ width = Dimen.ZERO; backToken(tok); error("MissingNumber"); }
	}
	if (val != Glue.NULL)
	    return (negative.get()) ? val.negative() : val;
	if (width == Dimen.NULL) {
	    if (cmd.hasNumValue()) {
		int		n = cmd.getNumValue().intVal();
		if (n < 0) { negative.negate(); n = -n; }
		width = convToDimenUnit(n, Dimen.ZERO, mu);
		width = checkDimen(width);
	    } else {
		backToken(tok);
		width = scanDimen(mu, BytePar.NULL);
	    }
	}
	if (negative.get()) width = width.negative();
	if (scanKeyword("plus"))  plus = scanDimen(mu, plusOrder);
	if (scanKeyword("minus")) minus = scanDimen(mu, minusOrder);
	return Glue.valueOf(width, plus, plusOrder.get(),
				   minus, minusOrder.get());
    }

    private static boolean	isPoint(Token tok)
	{ return (tok.matchOther('.') || tok.matchOther(',')); }

    private static boolean	hasOtherValue(Command cmd)
	{ return (cmd.hasToksValue() || cmd.hasFontTokenValue()); }

    protected static Token	scanSign(BoolPar negative) {
	Token		tok;
	for (;;) {
	    tok = nextExpNonSpacer();
	    if (tok.matchOther('-'))
		negative.set(!negative.get());
	    else if (!tok.matchOther('+'))
		return tok;
	}
    }

    protected static final void		skipOptSpacer(Token tok)
	{ if (!meaningOf(tok).isSpacer()) backToken(tok); }

    protected static final void		skipOptExpSpacer()
	{ skipOptSpacer(nextExpToken()); }

    /* TeXtp[406] */
    protected static final Token	nextExpNonSpacer() {
	Token		tok;
	do tok = nextExpToken();
	while (meaningOf(tok).isSpacer());
	return tok;
    }

    /**
     * Skips an optional equal sign preceded by optional spaces.
     */
    /* TeXtp[405] */
    public static void		skipOptEquals() {
	Token		tok = nextExpNonSpacer();
	if (!tok.matchOther('=')) backToken(tok);
    }

    /* TeXtp[404] */
    protected static final Token	nextNonRelax() {
        Token		tok;
	Command		cmd;
	do {
	    tok = nextExpToken();
	    cmd = meaningOf(tok);
	} while (cmd.isSpacer() || cmd.isRelax());
	return tok;
    }

    /* TeXtp[404] */
    protected static final Token	nextNonAssignment() {
        for (;;) {
	    Token	tok = nextNonRelax();
	    Command	cmd = meaningOf(tok);
	    if (cmd.assignable()) cmd.doAssignment(tok, 0);
	    else return tok;
	}
    }

    /* TeXtp[403] */
    public static void	scanLeftBrace() {
        Token		tok = nextNonRelax();
	if (!meaningOf(tok).isLeftBrace()) {
	    backToken(tok); error("MissingLeftBrace");
	    adjustBraceNesting(1);
	}
    }

    /* TeXtp[407] */
    protected static boolean	scanKeyword(String keyword) {
        Token[]		backup = new Token[keyword.length()];
	Token		tok = nextExpNonSpacer();
	for (int i = 0;;) {
	    CharCode	code = tok.nonActiveCharCode();
	    if (  code == CharCode.NULL
	       || !code.match(keyword.charAt(i))
	       && !code.match(Character.toUpperCase(keyword.charAt(i)))  ) {
		backToken(tok);
		if (i > 0) backList(new TokenList(backup, 0, i));
		return false;
	    }
	    backup[i++] = tok;
	    if (i >= keyword.length()) return true;
	    tok = nextExpToken();
	}
    }

    /* TeXtp[526] */
    protected static FileName	scanFileName() {
	boolean		inpEnbl = getConfig().enableInput(false);
        FileName	name = ioHandler.makeFileName();
	Token		tok;
	Command		cmd;
	int		i;
	do {
	    tok = nextExpToken();
	    cmd = meaningOf(tok);
	} while (cmd.isSpacer());
	for (;;) {
	    CharCode	code = cmd.charCode();
	    i = (code != CharCode.NULL)
	      ? name.accept(code) : -1;
	    if (i <= 0) break;
	    tok = nextExpToken();
	    cmd = meaningOf(tok);
	}
	if (i < 0) backToken(tok);
	getConfig().enableInput(inpEnbl);
	return name;
    }

}
