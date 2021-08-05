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
// Filename: nts/typo/Paragraph.java
// $Id: Paragraph.java,v 1.1.1.1 2001/03/20 09:41:50 ksk Exp $
package	nts.typo;

import	java.util.Enumeration;
import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CharCode;
import	nts.node.Language;
import	nts.node.FontMetric;
import	nts.node.GlueSetting;
import	nts.node.BoxSizes;
import	nts.node.Node;
import	nts.node.NodeList;
import	nts.node.NodeEnum;
import	nts.node.HyphenNodeEnum;
import	nts.node.NetDimen;
import	nts.node.Breaker;
import	nts.node.LinesShape;
import	nts.node.HBoxNode;
import	nts.node.PenaltyNode;
import	nts.node.NamedHSkipNode;
import	nts.node.VShiftNode;
import	nts.builder.Builder;
import	nts.builder.ParBuilder;
import	nts.command.Token;
import	nts.command.Command;

public abstract class	Paragraph	extends TypoCommand {

    public static final int	DIMP_PAR_INDENT = newDimParam();
    public static final int	TOKSP_EVERY_PAR = newToksParam();
    public static final int	INTP_INTER_LINE_PENALTY = newIntParam();
    public static final int	INTP_BROKEN_PENALTY = newIntParam();
    public static final int	INTP_CLUB_PENALTY = newIntParam();
    public static final int	INTP_WIDOW_PENALTY = newIntParam();

    public static void		makeIndent(Builder bld) {
	bld.addBox(new HBoxNode(
	    new BoxSizes(Dimen.ZERO,
	    		 getConfig().getDimParam(DIMP_PAR_INDENT),
			 Dimen.ZERO, Dimen.ZERO),
	    GlueSetting.NATURAL, NodeList.EMPTY));
    }

    public static final int	GLUEP_PAR_SKIP = newGlueParam();

    /* TeXtp[1091] */
    public static void		start(boolean indent) {
	Command.Config	cfg = getConfig();
	Builder		old = getBld();
	old.setPrevGraf(0);
	if (old.needsParSkip())
	    old.addSkip(cfg.getGlueParam(GLUEP_PAR_SKIP),
			cfg.getGlueName(GLUEP_PAR_SKIP));
	Builder		par
	    = new ParBuilder(currLineNumber(), getTypoConfig().getLanguage());
	Builder.push(par);
	if (indent) makeIndent(par);
	cfg.getToksInserter(TOKSP_EVERY_PAR).insertToks();
	old.buildPage(); //XXX is this order unavoidable?
    }

    public static void		finish() {
	Builder		parBld = getBld();
	NodeList	list = parBld.getParagraph();
	if (list != NodeList.NULL) {
	    lineBreak(list, parBld.getStartLine(),
		      getConfig().getIntParam(INTP_WIDOW_PENALTY),
		      parBld.getInitLang(), Dimen.NULL_PAR);
	    getTypoConfig().resetParagraph();
	    getIOHandler().resetErrorCount();
	}
    }

    public static final int	BOOLP_TRACING_PARAGRAPHS = newBoolParam();
    public static final int	BOOLP_UC_HYPH = newBoolParam();
    public static final int	INTP_PRETOLERANCE = newIntParam();
    public static final int	INTP_TOLERANCE = newIntParam();
    public static final int	INTP_LOOSENESS = newIntParam();
    public static final int	INTP_LINE_PENALTY = newIntParam();
    public static final int	INTP_HYPHEN_PENALTY = newIntParam();
    public static final int	INTP_EX_HYPHEN_PENALTY = newIntParam();
    public static final int	INTP_ADJ_DEMERITS = newIntParam();
    public static final int	INTP_DOUBLE_HYPHEN_DEMERITS = newIntParam();
    public static final int	INTP_FINAL_HYPHEN_DEMERITS = newIntParam();
    public static final int	DIMP_EMERGENCY_STRETCH = newDimParam();
    public static final int	GLUEP_LEFT_SKIP = newGlueParam();
    public static final int	GLUEP_RIGHT_SKIP = newGlueParam();
    public static final int	GLUEP_PAR_FILL_SKIP = newGlueParam();

    /* TeXtp[815] */
    public static void		lineBreak(NodeList list, int startingLine,
					  int widowPenalty, Language initLang,
					  Dimen.Par lastVisibleWidth) {
        if (list.isEmpty()) Builder.pop();
	else {
	    Command.Config	cfg = getConfig();
	    Config		tcfg = getTypoConfig();
	    Node		last = list.lastNode();
	    if (last.isSkip()) list.removeLastNode();
	    list.append(new PenaltyNode(Num.valueOf(Node.INF_PENALTY)));
	    list.append(new NamedHSkipNode(
	    			cfg.getGlueParam(GLUEP_PAR_FILL_SKIP),
				cfg.getGlueName(GLUEP_PAR_FILL_SKIP)));
	    Builder.pop();
	    boolean	marginSkipHadInfiniteShrink = false;
	    boolean	tracing = cfg.getBoolParam(BOOLP_TRACING_PARAGRAPHS);
	    Glue	left = cfg.getGlueParam(GLUEP_LEFT_SKIP);
	    Glue	right = cfg.getGlueParam(GLUEP_RIGHT_SKIP);
	    if (  left.getShrOrder() != Glue.NORMAL
	       && !left.getShrink().isZero()
	       || right.getShrOrder() != Glue.NORMAL
	       && !right.getShrink().isZero()  ) {
		marginSkipHadInfiniteShrink = true;
		tcfg.setMarginSkipsShrinkFinite();
		left = cfg.getGlueParam(GLUEP_LEFT_SKIP);
		right = cfg.getGlueParam(GLUEP_RIGHT_SKIP);
	    }
	    Node	leftSkip = (left.isZero()) ? Node.NULL
				 : new NamedHSkipNode(left,
					cfg.getGlueName(GLUEP_LEFT_SKIP));
	    Node	rightSkip = new NamedHSkipNode(right,
					cfg.getGlueName(GLUEP_RIGHT_SKIP));
	    NetDimen	background
		= new NetDimen(cfg.getGlueParam(GLUEP_LEFT_SKIP));
	    background.add(cfg.getGlueParam(GLUEP_RIGHT_SKIP));
	    LinesShape	shape = tcfg.linesShape();
	    Builder	bld = getBld();
	    int		lineNo = bld.getPrevGraf();
	    ParBreaker	breaker
		= new ParBreaker(list.nodes(), shape, lineNo,
				cfg.getIntParam(INTP_LOOSENESS),
				cfg.getIntParam(INTP_LINE_PENALTY),
				cfg.getIntParam(INTP_HYPHEN_PENALTY),
				cfg.getIntParam(INTP_EX_HYPHEN_PENALTY),
				cfg.getIntParam(INTP_ADJ_DEMERITS),
				cfg.getIntParam(INTP_DOUBLE_HYPHEN_DEMERITS),
				cfg.getIntParam(INTP_FINAL_HYPHEN_DEMERITS),
				cfg.getBoolParam(BOOLP_TRACING_PARAGRAPHS));
	    if (marginSkipHadInfiniteShrink)
		breaker.infiniteShrinkageError();
	    int		threshold = cfg.getIntParam(INTP_PRETOLERANCE);
	    if (threshold >=0) {
	        if (tracing) diagLog.startLine().add("@firstpass");
		breaker.breakToLines(background, threshold, false);
		if (!breaker.successfullyBroken() && tracing)
		    diagLog.startLine().add("@secondpass");
	    }
	    if (!breaker.successfullyBroken()) {
		Dimen	emergStr = cfg.getDimParam(DIMP_EMERGENCY_STRETCH);
	        threshold = cfg.getIntParam(INTP_TOLERANCE);
		tcfg.preparePatterns();
		breaker.refeed(new HyphNodeEnum(list.nodes(), initLang,
					    cfg.getBoolParam(BOOLP_UC_HYPH)));
		breaker.breakToLines(background, threshold,
				     !emergStr.moreThan(0));
		if (!breaker.successfullyBroken()) {
		    if (tracing) diagLog.startLine().add("@emergencypass");
		    background.addStretch(Glue.NORMAL, emergStr);
		    breaker.breakToLines(background, threshold, true);
		}
	    }
	    if (tracing) diagLog.startLine().endLine();
	    if (breaker.hasMoreLines()) {
		LinePacker	packer = new LinePacker(startingLine);
	        boolean		club = true;
		boolean		hyph = breaker.nextLineWasHyphenated();
		NodeList	line = breaker.getNextLine();
		HBoxNode	lastHBox;
		Dimen		lastIndent;
	        for (;;) {
		    line.append(rightSkip);
		    if (leftSkip != Node.NULL) {
		        NodeList	old = line;
			line = new NodeList(leftSkip);
			line.append(old);	//XXX awful
		    }
		    NodeEnum	mig = (bld.wantsMigrations())
				    ? line.extractedMigrations().nodes()
				    : NodeList.EMPTY_ENUM;
		    lastHBox = packer.packHBox(line, shape.getWidth(lineNo),
					       true);
		    Node	box = lastHBox;
		    lastIndent = shape.getIndent(lineNo);
		    box = VShiftNode.shiftingRight(box, lastIndent);
		    appendBox(bld, box, mig, false); lineNo++;
		    if (!breaker.hasMoreLines()) break;
		    int		pen = cfg.getIntParam(INTP_INTER_LINE_PENALTY);
		    if (club) pen += cfg.getIntParam(INTP_CLUB_PENALTY);
		    if (hyph) pen += cfg.getIntParam(INTP_BROKEN_PENALTY);
		    club = false;
		    hyph = breaker.nextLineWasHyphenated();
		    line = breaker.getNextLine();
		    if (!breaker.hasMoreLines()) pen += widowPenalty;
		    if (pen != 0) bld.addPenalty(Num.valueOf(pen));
		}
		bld.setPrevGraf(lineNo);
		if (lastVisibleWidth != Dimen.NULL_PAR) {
		    Dimen	visible = lastHBox.allegedlyVisibleWidth();
		    if (visible != Dimen.NULL)
			visible = visible.plus(lastIndent);
		    lastVisibleWidth.set(visible);
		}
	    }
	}
    }

    private static class	ParBreaker	extends Breaker {

	private final boolean		tracing;

	public ParBreaker(NodeEnum nodeEnum, LinesShape shape,
			  int firstLineNo, int looseness,
			  int linePen, int hyphPen, int exHyphPen,
			  int adjDem, int dblHyphDem, int finHyphDem,
			  boolean tracing) {
	    super(nodeEnum, shape, firstLineNo, looseness,
		  linePen, hyphPen, exHyphPen,
		  adjDem, dblHyphDem, finHyphDem);
	    this.tracing = tracing;
	}

	private int		lastPrinted;
	private FontMetric	lastMetric;

	protected void		reset()
	    { super.reset(); lastPrinted = -1; lastMetric = FontMetric.NULL; }

	protected void		traceBreak(int idx, int serial,
					   int bad, int pen, int dem,
					   boolean artificial) {
	    if (tracing) {
		if (lastPrinted < idx) {
		    diagLog.startLine();
		    do  if (stillNodeAt(++lastPrinted))
			    lastMetric = nodeAt(lastPrinted)
					.addShortlyOn(diagLog, lastMetric);
		    while (lastPrinted < idx);
		}
		diagLog.startLine().add('@');
		if (stillNodeAt(idx))
		    nodeAt(idx).addBreakDescOn(diagLog);
		else diagLog.addEsc("par");
		diagLog.add(" via @@").add(serial).add(" b=");
		if (bad > Dimen.INF_BAD) diagLog.add('*');
		else diagLog.add(bad);
		diagLog.add(" p=").add(pen).add(" d=");
		if (artificial) diagLog.add('*');
		else diagLog.add(dem);
		//D*/ diagLog.endLine();
	    }
	}

	protected void		traceBreak(Break brk) {
	    if (tracing) {
		diagLog.startLine().add("@@").add(brk.serial)
		       .add(": line ").add(brk.lineNo)
		       .add('.').add(brk.fitness);
		if (brk.hyphenated) diagLog.add('-');
		diagLog.add(" t=").add(brk.demerits)
		       .add(" -> @@").add(brk.prev.serial);
		//D*/ diagLog.endLine();
	    }
	}
	
	private boolean		infiniteShrinkageSeen;

	protected void		infiniteShrinkageError() {
	    if (!infiniteShrinkageSeen)
		{ error("InfShringInPar"); infiniteShrinkageSeen = true; }
	}

    }

    private static class	LinePacker	extends HBoxPacker {

	protected final int		startLine;

    	public LinePacker(int startLine) { this.startLine = startLine; }

	protected void		reportLocation(Log log) {
	    log.add("in paragraph at lines ")
	       .add(startLine).add("--")
	       .add(currLineNumber());
	}

    }

    private static class	HyphNodeEnum	extends HyphenNodeEnum {

	public HyphNodeEnum(NodeEnum in, Language lang, boolean ucHyph)
	    { super(in, lang, ucHyph); }

	protected CharCode	hyphenChar(FontMetric metric) {
	    Num		num
		= metric.getNumParam(FontMetric.NUM_PARAM_HYPHEN_CHAR);
	    return (num != Num.NULL) ? Token.makeCharCode(num.intVal())
				     : CharCode.NULL;
	}

	protected void		complain(FontMetric metric, CharCode code)
	    { charWarning(metric, code); }

    }

}
