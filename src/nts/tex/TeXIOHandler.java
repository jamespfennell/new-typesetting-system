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
// Filename: nts/tex/TeXIOHandler.java
// $Id: TeXIOHandler.java,v 1.1.1.1 2001/08/31 15:58:02 ksk Exp $
package	nts.tex;

import	java.io.InputStream;
import	java.io.FileInputStream;
import	java.io.BufferedInputStream;
import	java.io.OutputStream;
import	java.io.FileOutputStream;
import	java.io.BufferedOutputStream;
import	java.io.ObjectOutputStream;
import	java.io.Reader;
import	java.io.Writer;
import	java.io.InputStreamReader;
import	java.io.OutputStreamWriter;
import	java.io.StringWriter;
import	java.io.OutputStream;
import	java.io.FileOutputStream;
import	java.io.IOException;
import	java.io.FileNotFoundException;
import	java.io.UnsupportedEncodingException;
import	java.text.DateFormat;
import	java.text.SimpleDateFormat;
import	java.util.Calendar;
import	java.util.Locale;
import	nts.io.*;
import	nts.command.*;
import	nts.typo.TypoCommand;
import	nts.node.TypeSetter;
import	nts.dvi.DviTypeSetter;


public class	TeXIOHandler	implements
					Command.IOHandler,
					LineInputTokenizer.InputHandler,
					LineInputReadInput.InputHandler,
					TeXTokenMaker.ErrHandler {

    public interface	Config	extends TeXTokenMaker.Categorizer {
        Calendar		date();
	TokenList		errHelp();
	int			errContextLines();
	boolean			confirmingLines();
	int			magnification();
    }

    private Config		config;
    private InputLine		firstLine;
    private TeXCharMapper	mapper;
    private FileOpener		opener;
    private TeXTokenMaker	maker;
    private ContextDisplay	contextDisplay;
    private TeXErrorPool	errPool;

    public TeXIOHandler(Config config, InputLine firstLine,
			TeXCharMapper mapper, FileOpener opener) {
	this.config = config;
	this.firstLine = firstLine;
	this.mapper = mapper;
	this.opener = opener;
	this.maker = new TeXTokenMaker(config, this);
	contextDisplay = new TeXContextDisplay(config, mapper, mapper);
	errPool = new TeXErrorPool();
    }

    public TeXTokenMaker	getTokenMaker() { return maker; }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * implementation of |IOHandler|
     */

    private Name		jobName = Name.NULL;

    public Name		getJobName() {
	if (jobName == Name.NULL)
	    { jobName = Token.makeName("texput"); openLogFile(); }
	return jobName;
    }

    public void		ensureOpenLog() { getJobName(); }

    /* TeXtp[537] */
    public void		openInput(FileName name) {
	Reader		in;
	FileName	fullName;
	for (;;) {
	    fullName = name.copy();
	    try { in = openReader(fullName, TeXConfig.INPUT_EXT, true); break; }
	    catch (IOException e) {	//XXX distinguish IO and FileNotFound
		cantOpen(name, true, true);
		name = promptFileName("input file name");
	    }
	}
	if (jobName == Name.NULL)
	    { jobName = name.baseName(); openLogFile(); }
	int	len = Command.normLog.voidCounter().add(fullName).getCount();
	Command.normLog.sepRoom(len + 2).add('(').add(fullName).flush();
	Command.getTokStack().push(
	    new LineInputTokenizer(new LineInput(in, mapper), this, name));
    }

    private boolean		afterEnd = false;

    public void			setAfterEnd() { afterEnd = true; }

    /* STRANGE
     * After finishing main loop, the space is printed before ')'
     */
    public void		closeInput() {
	if (afterEnd) Command.normLog.add(' ');
	Command.normLog.add(')').flush();
    }

    /* TeXtp[1275] */
    public ReadInput	openRead(FileName name, int num) {
	Reader	in;
	try { in = openReader(name, TeXConfig.READ_EXT, false); }
	catch (IOException e) { return ReadInput.NULL; }
	return new LineInputReadInput(new LineInput(in, mapper), this, num);
    }

    public ReadInput	defaultRead(int num)
	{ return new StdinReadInput(this, num); }

    public Log		openWrite(FileName name, int num) {
	Writer		wr;
	for (;;) {
	    try { wr = openWriter(name, TeXConfig.WRITE_EXT); break; }
	    catch (IOException e) {
		cantOpen(name, false, true);
		name = promptFileName("output file name");
	    }
	}
	if (jobName != Name.NULL)
	    Command.diagLog.startLine().addEsc("openout").add(num)
			   .add(" = `").add(name).add("'.")
			   .startLine().endLine();
	return new StandardLog(new WriterLineOutput(wr, mapper), mapper);
    }

    public Log		makeLog(LineOutput out)
	{ return new StandardLog(out, mapper); }

    public Log		makeStringLog()
	{ return new StringLog(mapper); }

    public FileName	makeFileName()
	{ return new TeXCharMapper.TeXFileName(); }

    public void		error(String ident, Loggable[] params,
			      boolean delAllowed)
	{ error(errPool.get(ident), params, true, delAllowed); }

    public void		fatalError(String ident) {
        ensureOpenLog();
	InteractionPrim.setScroll();
	TeXError	err = errPool.get(ident);
	if (Command.isFileLogActive()) error(err, null, true, false);
	else err.addText(Command.normLog.startLine().add("! "), null);
	throw new FatalError(ident);
    }

    public void		errMessage(TokenList message)
	{ error(errPool.get(message, config.errHelp()), null, true, true); }

    private transient String	shownBuilderName = null;

    public void		logMode() {
        String		name = TypoCommand.getBld().modeName();
	if (!name.equals(shownBuilderName)) {
	    Command.diagLog.add(TypoCommand.getBld()).add(": ");
	    shownBuilderName = name;
	}
    }

    public void		completeShow() {
        String	ident = (!InteractionPrim.isErrStopping()) ? "Void"
		      : (Command.termDiagActive()) ? "ShortShow" : "LongShow";
	error(errPool.get(ident), null, false, true);
    }

    public void		illegalCommand(Command cmd)
	{ TypoCommand.illegalCase(cmd, TypoCommand.getBld()); }

    private int		errorCount = 0;

    public void		resetErrorCount() { errorCount = 0; }

    private void	error(TeXError err, Loggable[] params,
			      boolean counting, boolean delAllowed) {
	Log		log = Command.normLog;
	if (counting) {
	    log.startLine().add("! ");
	    err.addText(log, params);
	}
	log.add('.');
	Command.getTokStack().show(contextDisplay);
	if (InteractionPrim.isErrStopping()) {
	    int		currHelp = 0;
	    for (;;) {
		log.endLine();
		Command.getTokStack().cleanFinishedInserts();
		InputLine	line = Command.promptInput("? ");
		CharCode	cmd = line.getNextRawCode();
		if (cmd == InputLine.EOL) break;
		char		c = cmd.toChar();
		FilePos		pos = Command.getTokStack().filePos();
		switch (Character.toUpperCase(c)) {
		    case 'S':	setInteraction("scrollmode");	return;
		    case 'R':	setInteraction("nonstopmode");	return;
		    case 'Q':	setInteraction("batchmode");	return;
		    case 'I':
		        String		desc;
		        if (line.empty()) {
			    line = Command.promptInput("insert>");
			    desc = "<insert>  ";
			} else {
			    line = line.pureRest();
			    desc = "<insert>   ";
			}
			InputLineTokenizer	tokenizer
			    = new InputLineTokenizer(line, maker, desc);
			tokenizer.setMidLine();
			Command.getTokStack().push(tokenizer);
			return;
		    case 'E':
			if (pos != FilePos.NULL) {
			    InteractionPrim.setScroll();
			    throw new EditException(pos);
			}
			break;
		    default:
			if (delAllowed && Character.isDigit(c)) {
			    int		count = Character.digit(c, 10);
			    cmd = line.getNextRawCode();
			    if (cmd != InputLine.EOL) {
				c = cmd.toChar();
				if (Character.isDigit(c))
				    count = count * 10 + Character.digit(c, 10);
			    }
			    while (count-- > 0) Command.nextRawToken();
			    Command.getTokStack().show(contextDisplay);
			    currHelp = 2;
			    continue;
			}
			break;
		    case 'H':
		        switch (currHelp) {
			    case 0:	err.addHelp(log, params);	break;
			    case 1:	errPool.addHelpAfterHelp(log);	break;
			    case 2:	errPool.addHelpAfterDel(log);	break;
			}
			currHelp = 1;
			continue;
		    case 'X':
			InteractionPrim.setScroll();
			throw new FatalError("User stop");
			// break;
		}
		log.add("Type <return> to proceed, ")
		.add("S to scroll future error messages,").endLine()
		.add("R to run without stopping, ")
		.add("Q to run quietly,").endLine()
		.add("I to insert something, ");
		if (pos != FilePos.NULL)
		    log.add("E to edit your file,");
		if (delAllowed)
		    log.startLine().add("1 or ... or 9 to ignore ")
				   .add("the next 1 to 9 tokens of input,");
		log.startLine().add("H for help, X to quit.");
	    }
	} else {
	    if (counting && ++errorCount >= TeXConfig.MAX_ERROR_COUNT) {
	        log.startLine().add("(That makes " + errorCount
		        + " errors; please try again.)");
		throw new FatalError("Too many errors");
	    }
	    err.addDesc(Command.fileLog, params);
	    Command.fileLog.endLine();
	    log.endLine();
	}
    }

    private void		setInteraction(String mode) {
        errorCount = 0;
	InteractionPrim.set(mode);
	Command.normLog.add("OK, entering ")
		       .add(mode).add("...").endLine();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Dvi File
     */

    private FileName		dviName = FileName.NULL;
    private DviTypeSetter	dviSetter = DviTypeSetter.NULL;

    public TypeSetter	getTypeSetter(DviTypeSetter.FontInformator fontInf) {
	if (dviSetter == DviTypeSetter.NULL) {
	    OutputStream	out;
	    FileName	name = new TeXCharMapper.TeXFileName(getJobName());
	    String	ext = System.getProperty("nts.dvi.extension",
						 TeXConfig.DVI_EXT);
	    name.append('.'); name.append(ext);
	    for (;;) {
		try {
		    out = new BufferedOutputStream(
			    opener.openForWriting(name, ext));
		    break;
		} catch (IOException e) {
		    cantOpen(name, false, false);
		    name = promptFileName("file name for output");
		    name.addDefaultExt(ext);
		}
	    }
	    dviName = name;
	    DateFormat	fmt
		= new SimpleDateFormat("yyyy.MM.dd:HHmm", Locale.ENGLISH);
	    Calendar	cal = config.date();
	    cal.setLenient(true); fmt.setCalendar(cal);
	    String	comment = " TeX output " + fmt.format(cal.getTime());
	    dviSetter = new DviTypeSetter(out, fontInf,
					config.magnification(),
					comment.getBytes());
	}
	return dviSetter;
    }

    /* TeXtp[642] */
    public void		finishDvi() {
        if (dviSetter != DviTypeSetter.NULL) {
	    config.magnification();
	    dviSetter.close();
	    int		pages = dviSetter.pageCount();
	    Command.normLog.startLine().add("Output written on ")
			   .add(dviName).add(" (").add(pages).add(' ')
			   .add((pages != 1) ? "pages" : "page")
			   .add(", ").add(dviSetter.byteCount())
			   .add(" bytes).");
	    dviName = FileName.NULL;
	    dviSetter = DviTypeSetter.NULL;
	} else Command.normLog.startLine().add("No pages of output.");
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Format File
     */

    private FileName		fmtName = FileName.NULL;
    private ObjectOutputStream	dumper = null;

    public ObjectOutputStream	getDumper() throws IOException {
	if (dumper == null) {
	    OutputStream	out;
	    FileName	name = new TeXCharMapper.TeXFileName(getJobName());
	    name.append('.'); name.append(TeXConfig.FMT_EXT);
	    for (;;) {
		try {
		    out = new BufferedOutputStream(
			    opener.openForWriting(name, "fmt"));	//XXX
		    break;
		} catch (IOException e) {
		    cantOpen(name, false, false);
		    name = promptFileName("format file name");
		    name.addDefaultExt(TeXConfig.FMT_EXT);
		}
	    }
	    fmtName = name;
	    dumper = new ObjectOutputStream(out);
	}
	return dumper;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Log File
     */

    private FileName		logName = FileName.NULL;
    private LineOutput		logFile = LineOutput.NULL;

    public FileName		getLogName() { return logName; }

    public void		closeLogFile() {
	if (logFile != LineOutput.NULL)
	    { logFile.endLine(); logFile.close(); }
	logName = FileName.NULL;
	logFile = LineOutput.NULL;
    }

    private void		openLogFile() {
	Writer		wr;
	FileName	name = new TeXCharMapper.TeXFileName(jobName);
	String		ext = System.getProperty("nts.log.extension",
						 TeXConfig.LOG_EXT);
	name.append('.'); name.append(ext);
	for (;;) {
	    try { wr = openWriter(name, ext); break; }
	    catch (IOException e) {
		//XXX enable term [535]
		cantOpen(name, false, false);
		name = promptFileName("transcript file name");
		name.addDefaultExt(ext);
	    }
	}
	WriterLineOutput	out = new WriterLineOutput(wr, mapper, false,
						    TeXConfig.MAX_PRINT_LINE);
	out.addRaw(TeXConfig.BANNER);
	DateFormat	fmt
	    = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
	Calendar	cal = config.date();
	cal.setLenient(true); fmt.setCalendar(cal);
	out.add("  " + fmt.format(cal.getTime()).toUpperCase());
	out.startLine(); out.add("**");
	Log	log = new StandardLog(out, mapper);
	firstLine.addContext(log, log, true);
	out.endLine();
	logName = name; logFile = out;
	Command.setLogFile(logFile);
    }

    private FileName	promptFileName(String prompt) {
	Command.normLog.startLine().add("Please type another ").add(prompt);
	if (!InteractionPrim.isInteractive())
	    fatalError("MissingFile");
	InputLine	line = Command.promptInput(": ");
	FileName	name = new TeXCharMapper.TeXFileName();
	CharCode	code;
	line.skipSpaces();
	do code = line.getNextRawCode();
	while (code != InputLine.EOL && name.accept(code) > 0);
	return name;
    }

    private void	cantOpen(FileName name, boolean inp, boolean cntx) {
	Command.normLog.startLine()
		       .add((inp) ? "! I can't find file `"
				  : "! I can't write on file `");
	Command.normLog.add(name).add("'.");
	if (cntx) Command.getTokStack().show(contextDisplay);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * implementation of |LineInputTokenizer.InputHandler|
     */

    public InputLine	emptyLine() { return new InputLine(mapper); }

    public InputLine	confirmLine(InputLine line) {
	if (  config.confirmingLines()
	   && InteractionPrim.isInteractive()  ) {
	    Command.normLog.endLine().add(line);
	    InputLine	instead = Command.promptInput("=>");
	    if (instead != InputLine.NULL && !instead.empty())
		return instead;
	}
	return line;
    }

    public Tokenizer
	    makeTokenizer(InputLine line, String desc, boolean addEolc) {
	if (addEolc) line = line.addEndOfLineChar();
	return new InputLineTokenizer(line, maker, desc);
    }

    public static Reader	makeReader(InputStream in) {
        try { return new InputStreamReader(in, "8859_1"); }
	catch (UnsupportedEncodingException e)
	    { throw new RuntimeException("Can't happen"); }
    }

    private Reader	openReader(FileName name, String format,
				   boolean mustExist) throws IOException {
	return makeReader(new BufferedInputStream(
		    opener.openForReading(name, format, mustExist)));
    }

    public static Writer	makeWriter(OutputStream out) {
        try { return new OutputStreamWriter(out, "8859_1"); }
	catch (UnsupportedEncodingException e)
	    { throw new RuntimeException("Can't happen"); }
    }

    private Writer	openWriter(FileName name, String format)
						throws IOException {
	return makeWriter(new BufferedOutputStream(
		    opener.openForWriting(name, format)));
    }

    public InputStream	openTeXFm(FileName name) throws IOException {
	return new BufferedInputStream(
	    opener.openForReading(name, TeXConfig.TFM_EXT, true));
    }

}

class	TeXContextDisplay	implements ContextDisplay {

    private TeXIOHandler.Config		config;
    private BufferLineOutput		leftOut;
    private BufferLineOutput		rightOut;
    private Log				leftLog;
    private Log				rightLog;
    private int				origin = 0;

    public TeXContextDisplay(TeXIOHandler.Config config,
			     CharCode.Maker maker, StandardLog.Escape esc) {
	this.config = config;
	leftOut = new BufferLineOutput(TeXConfig.HALF_ERROR_LINE, 0, maker);
	rightOut = new BufferLineOutput(TeXConfig.ERROR_LINE,
					TeXConfig.ERROR_LINE, maker);
	leftLog = new StandardLog(leftOut, esc);
	rightLog = new StandardLog(rightOut, esc);
	if (normal() != Log.NULL) origin = normal().getCount();
    }

    public void		reset() {
	leftOut.reset(); rightOut.reset();
	origin = normal().getCount();
    }

    public int		lines() { return config.errContextLines(); }
    public Log		normal() { return Command.normLog; }
    public Log		left() { return leftLog; }
    public Log		right() { return rightLog; }

    public void		show() {
	int		intro = normal().getCount() - origin;
	int		half = intro + leftOut.size();
	if (half <= TeXConfig.HALF_ERROR_LINE) leftOut.addOn(normal());
	else {
	    normal().add("...");
	    half = TeXConfig.HALF_ERROR_LINE;
	    int	n = half - intro - 3;
	    if (n > 0) leftOut.addOn(normal(), leftOut.size() - n,
					leftOut.size());
	}
	normal().endLine().add(' ', half);
	if (half + rightOut.size() <= TeXConfig.ERROR_LINE)
	    rightOut.addOn(normal());
	else {
	    int	n = TeXConfig.ERROR_LINE - half - 3;
	    if (n > 0) rightOut.addOn(normal(), 0, n);
	    normal().add("...");
	}
    }

}

class	StdinReadInput	implements ReadInput {

    private TeXIOHandler	handler;
    private int			num;
    private String		desc = "<read *> ";

    public StdinReadInput(TeXIOHandler handler, int num)
	{ this.handler = handler; this.num = num; }

    public Tokenizer	nextTokenizer(Token def, int ln) {
	if (!InteractionPrim.isInteractive())
	    Command.fatalError("NoTermRead");
	InputLine	line;
	if (num > 0 && ln == 0) {
	    Command.normLog.endLine().add(def);
	    line = Command.promptInput("=");
	} else line = Command.promptInput("");
	return (line == LineInput.EOF) ? Tokenizer.NULL
	     : handler.makeTokenizer(line, desc, true);
    }

    public Tokenizer	emptyLineTokenizer()
	{ return handler.makeTokenizer(handler.emptyLine(), desc, true); }

    public void		close() { }

}
