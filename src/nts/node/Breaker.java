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
// Filename: nts/node/Breaker.java
// $Id: Breaker.java,v 1.1.1.1 2000/05/27 16:47:25 ksk Exp $
package	nts.node;

import	java.util.List;
import	java.util.ListIterator;
import	java.util.LinkedList;
import	java.util.Enumeration;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.Loggable;

public abstract class	Breaker {

    protected static final Break	NULL_BREAK = null;
    protected static final Fitness	NULL_FITNESS = null;
    protected static final int		INF_BAD = Dimen.INF_BAD;
    protected static final int		AWFUL_BAD = Dimen.AWFUL_BAD;

    /* TeXtp[819,821] */
    protected static class	Break {

	public final int		index;
	public final int		count;
	public final int		lineNo;
	public final Fitness		fitness;
	public final boolean		hyphenated;
	public final int		demerits;
	public final int		serial;
	public final Break		prev;
	public NetDimen			delta;

	public Break(int index, int count, int lineNo,
		     Fitness fitness, boolean hyphenated, int demerits,
		     int serial, Break prev, NetDimen delta) {
	    this.index = index; this.count = count;
	    this.lineNo = lineNo; this.fitness = fitness;
	    this.hyphenated = hyphenated; this.demerits = demerits;
	    this.serial = serial; this.prev = prev;
	    this.delta = delta;
	}

    }

    protected class	Fitness	implements Loggable {

	private final int	code;
	public final Fitness	next;
	private int		minDem = AWFUL_BAD;
	private Break		best = NULL_BREAK;

	public Fitness(int code, Fitness next)
	    { this.code = code; this.next = next; }

	public void	addOn(Log log) { log.add(code); }

	public boolean	adjoins(Fitness other) {
	    return (  this.equals(other) || other.equals(next)
	    	   || this.equals(other.next)  );
	}

	public void	update(int dem, Break brk)
	    { if (minDem >= dem) { minDem = dem; best = brk; } }

	public void	reset()
	    { minDem = AWFUL_BAD; best = NULL_BREAK; }

	public boolean	fits(int limit) { return (minDem <= limit); }

	public Break	makeFirst(int lineNo) {
	    return new Break(0, 0, lineNo, this, false,
			     0, maxSerial++, NULL_BREAK, new NetDimen());
	}

	public Break	makeBest(int idx, int cnt, boolean hyphenated,
				 NetDimen delta) {
	    if (best == NULL_BREAK)
		throw new RuntimeException("no best break");
	    return new Break(idx, cnt, best.lineNo + 1, this, hyphenated,
			     minDem, maxSerial++, best, delta);
	}

	public String	toString() { return Integer.toString(code); }

    }

    protected int		minDem = AWFUL_BAD;
    protected final Fitness	TIGHT		= new Fitness(3, NULL_FITNESS);
    protected final Fitness	DECENT		= new Fitness(2, TIGHT);
    protected final Fitness	LOOSE		= new Fitness(1, DECENT);
    protected final Fitness	VERY_LOOSE	= new Fitness(0, LOOSE);
    protected final Fitness	FITNESS_HEAD	= VERY_LOOSE;

    protected Fitness	getFitness(int badness, boolean stretching) {
	if (stretching) {
	    if (badness > 99) return VERY_LOOSE;
	    if (badness > 12) return LOOSE;
	} else if (badness > 12) return TIGHT;
	return DECENT;
    }

    protected void	resetDemerits() {
	minDem = AWFUL_BAD;
	for (Fitness fit = FITNESS_HEAD; fit != NULL_FITNESS; fit = fit.next)
	    fit.reset();
    }

    // private Log		debug;

/*
    public static Loggable	nod(final Node node) {
        return new Loggable() {
	    public void		addOn(Log log)
		{ CntxLog.addOn(log, node, 100, 10000); }
	};
    }
*/
    
    private NodeEnum		nodeEnum;
    private int			nodeCount = 0;
    protected Node[]		nodeList = new Node[32];

    public void			refeed(NodeEnum nodeEnum)
	{ this.nodeEnum = nodeEnum; nodeCount = 0; }

    protected final Node	nodeAt(int i) { return nodeList[i]; }

    protected final boolean	stillNodeAt(int i) {
	while (nodeCount <= i) {
	    if (!nodeEnum.hasMoreNodes()) return false;
	    if (nodeList.length <= nodeCount) {
		int		newLength = nodeList.length * 2;
		while (newLength <= nodeCount) newLength *= 2;
		Node[]		oldList = nodeList;
		nodeList = new Node[newLength];
		System.arraycopy(oldList, 0, nodeList, 0, nodeCount);
	    }
	    nodeList[nodeCount++] = nodeEnum.nextNode();
	}
	return true;
    }

    protected /* final */ LinesShape	shape;
    protected /* final */ int		firstLineNo;
    protected /* final */ int		looseness;
    protected /* final */ int		linePen;
    protected /* final */ int		hyphPen;
    protected /* final */ int		exHyphPen;
    protected /* final */ int		adjDem;
    protected /* final */ int		dblHyphDem;
    protected /* final */ int		finHyphDem;

    public Breaker(NodeEnum nodeEnum, LinesShape shape,
		   int firstLineNo, int looseness,
		   int linePen, int hyphPen, int exHyphPen,
		   int adjDem, int dblHyphDem, int finHyphDem) {
	this.nodeEnum = nodeEnum;
	this.shape = shape;
	this.firstLineNo = firstLineNo;
	this.looseness = looseness;
	this.linePen = linePen;
	this.hyphPen = hyphPen;
	this.exHyphPen = exHyphPen;
	this.adjDem = adjDem;
	this.dblHyphDem = dblHyphDem;
	this.finHyphDem = finHyphDem;
	// debug = nts.command.Command.normLog;
    }

    protected NetDimen		background;
    protected int		threshold;
    protected boolean		finPass;
    protected List		breakList;
    private int			maxSerial;
    protected Break[]		lineBreaks;
    protected int		currLineIndex = 0;
    protected NodeEnum		lastPostBreak = NodeEnum.NULL;

    protected void		reset() {
	breakList = new LinkedList(); maxSerial = 0;
	lineBreaks = null; currLineIndex = 0;
	lastPostBreak = NodeEnum.NULL;
	resetDemerits();
    }

    /* STRANGE
     * Why is the last break hyphenated?
     * the only difference is '-' traced by trace_break
     * The reason is probably only the more efficient test in TeXtp[859].
     */
    public void		breakToLines(NetDimen background, int threshold,
				     boolean finPass) {
	this.background = background;
	this.threshold = (threshold > INF_BAD) ? INF_BAD : threshold;
	this.finPass = finPass;
	reset(); breakList.add(DECENT.makeFirst(firstLineNo));
	if (passNodes()) tryBreak(nodeCount, Node.EJECT_PENALTY,
				  true, Dimen.ZERO, true);
	Break		best = bestBreak();
	if (looseness != 0 && best != NULL_BREAK)
	    best = bestBreak(best.lineNo);
        int		n = 0;
        for (Break brk = best; brk != NULL_BREAK; brk = brk.prev) n++;
	if (n > 0) {
	    lineBreaks = new Break[n]; currLineIndex = 1;
	    for (Break brk = best; brk != NULL_BREAK; brk = brk.prev)
		lineBreaks[--n] = brk;
	}
    }

    public boolean	successfullyBroken() { return (currLineIndex > 0); }

    public boolean	hasMoreLines()
	{ return (currLineIndex > 0 && currLineIndex < lineBreaks.length); }

    public boolean	nextLineWasHyphenated() {
	Break		brk = lineBreaks[currLineIndex];
	return (brk.hyphenated && brk.count > 0);
    }

    public NodeList	getNextLine() {
	int		i = currLineIndex++;
	return makeList(lineBreaks[i - 1], lineBreaks[i]);
    }
    
    protected NodeList		makeList(Break before, Break after) {
	int		beg = before.index + before.count;
	int		end = after.index;
	NodeList	list;
	if (lastPostBreak != NodeEnum.NULL)
	    list = new NodeList(lastPostBreak);
	else list = new NodeList(end - beg);
	list.append(nodeList, beg, end - beg);
	if (after.count > 0) {
	    Node	node = nodeList[end];
	    list.append(node.atBreakReplacement());
	    lastPostBreak = node.postBreakNodes();
	} else lastPostBreak = NodeEnum.NULL;
	return list;
    }

    /* TeXtp[874] */
    protected Break	bestBreak() {
        int		fewestDem = AWFUL_BAD;
	Break		best = NULL_BREAK;
	ListIterator	iterator = breakList.listIterator();
	while (iterator.hasNext()) {
	    Break	brk = (Break) iterator.next();
	    if (brk.demerits < fewestDem)
		{ best = brk; fewestDem = brk.demerits; }
	}
	return best;
    }

    /* TeXtp[875] */
    protected Break	bestBreak(int bestLineNo) {
        final int	desiredLineNo = bestLineNo + looseness;
        int		fewestDem = AWFUL_BAD;
	Break		best = NULL_BREAK;
	ListIterator	iterator = breakList.listIterator();
	while (iterator.hasNext()) {
	    Break	brk = (Break) iterator.next();
	    if (  bestLineNo < brk.lineNo && brk.lineNo <= desiredLineNo
	       || bestLineNo > brk.lineNo && brk.lineNo >= desiredLineNo  ) {
	    	best = brk; fewestDem = brk.demerits;
		bestLineNo = brk.lineNo;
	    } else if (brk.lineNo == bestLineNo && brk.demerits < fewestDem)
		{ best = brk; fewestDem = brk.demerits; }
	}
	return (bestLineNo == desiredLineNo || finPass) ? best : NULL_BREAK;
    }

    protected class	BreakingContext	implements BreakingCntx {

	public boolean		space = true;
	public boolean		atSkip = true;

	public boolean		spaceBreaking() { return space; }
	public boolean		allowedAtSkip() { return atSkip; }
	public int		hyphenPenalty() { return hyphPen; }
	public int		exHyphenPenalty() { return exHyphPen; }

    }

    protected boolean	passNodes() {
	BreakingContext		brkContext = new BreakingContext();
	for (int i = 0; stillNodeAt(i); i++) {
	    Node	node = nodeAt(i);
	    if (node.allowsSpaceBreaking()) brkContext.space = true;
	    else if (node.forbidsSpaceBreaking()) brkContext.space = false;
	    if (  !node.isKernBreak()
	       || stillNodeAt(i + 1)
	       && nodeAt(i + 1).canFollowKernBreak()  ) {
		brkContext.atSkip
		    = (i > 0 && nodeAt(i - 1).canPrecedeSkipBreak());
		int	pen = node.breakPenalty(brkContext);
		if (pen < Node.INF_PENALTY) {
		    Dimen	preWidth = node.preBreakWidth();
		    actWidth().add(preWidth);
		    tryBreak(i, (pen < Node.EJECT_PENALTY)
			      ? Node.EJECT_PENALTY : pen,
			     node.isHyphenBreak(), preWidth, false);
		    //XXX add preWidth localy in tryBreak
		    if (breakList.isEmpty()) return false;
		    actWidth().sub(preWidth);
		}
	    }
	    checkShrinkage(node);
	    add(actWidth(), node);
/*
	    debug.startLine().add("node = ").add(nod(node)).endLine();
	    debug.add("active width = ")
	         .add(((Break) breakList.get(0)).delta.toString())
		 .endLine();
*/
	}
	return true;
    }

    private NetDimen		actWidth()
	{ return ((Break) breakList.get(0)).delta; }

    private void	traceBreakList() {	//DDD
	ListIterator	iterator = breakList.listIterator();
/*
	if (iterator.hasNext()) for (;;) {
	    Break	brk = (Break) iterator.next();
	    System.out.print("> " + brk.delta + " .. @" + brk.serial
	    		   + " (" + brk.lineNo + ')');
	    if (iterator.hasNext()) System.out.println(" ..");
	    else { System.out.println(); break; }
	} else System.out.println("> No Breaks ...");
*/
	System.out.print(">");
	if (iterator.hasNext()) for (;;) {
	    Break	brk = (Break) iterator.next();
	    System.out.print(" @" + brk.serial);
	    if (iterator.hasNext()) System.out.print(" ..");
	    else { System.out.println(); break; }
	}
    }

    protected void	tryBreak(int idx, int pen, boolean hyphen,
				 Dimen preWidth, boolean last) {
	NetDimen	currWidth = new NetDimen(background);
	ListIterator	iterator = breakList.listIterator();
	int		oldLineNo = 0;
	//D*/ System.out.println(">\n> tryBreak(" + idx + ", " + pen + "):");
	while (iterator.hasNext()) {
	    Break	brk = (Break) iterator.next();
	    //D*/ System.out.println("> trying from @" + brk.serial
			     //D*/ + " (" + brk.lineNo + "):");
	    if (brk.lineNo > oldLineNo) {
	        if (looseness != 0 || !shape.isFinal(brk.lineNo)) {
		    oldLineNo = brk.lineNo;
		    if (minDem < AWFUL_BAD) {
		    	iterator.previous();
			createActive(idx, iterator, currWidth,
				     hyphen, preWidth);
		    	iterator.next();
		    }
		} else oldLineNo = Integer.MAX_VALUE;
	    }
	    currWidth.add(brk.delta);
	    //D*/ System.out.println("> currWidth = " + currWidth);
	    int		badness;
	    Fitness	fitness;
	    Dimen	diff = shape.getWidth(brk.lineNo)
				    .minus(currWidth.getNatural());
	    //D*/ System.out.print("> diff = " + diff);
	    if (diff.moreThan(0)) {
		badness = (currWidth.getMaxStrOrder() > Glue.NORMAL) ? 0
			: diff.badness(currWidth.getStretch(Glue.NORMAL));
		fitness = getFitness(badness, true);
	    } else {
		diff = diff.negative();
		badness = (diff.moreThan(currWidth.getShrink())) ? INF_BAD + 1
			: diff.badness(currWidth.getShrink());
		fitness = getFitness(badness, false);
	    }
	    //D*/ System.out.println(", badness = " + badness
			     //D*/ + ", fitness = " + fitness);
	    if (badness > INF_BAD || pen == Node.EJECT_PENALTY) {
	        if (finPass && minDem == AWFUL_BAD && breakList.size() == 1) {
		    traceBreak(idx, brk.serial, badness, pen, 0, true);
		    recordFeasible(brk, fitness, 0);
		} else if (badness <= threshold) {
		    int		dem = demerits(pen, badness)
				    + demerits(brk, fitness, hyphen, last);
		    traceBreak(idx, brk.serial, badness, pen, dem, false);
		    recordFeasible(brk, fitness, dem);
		}
		//D*/ System.out.println("> *** removing @" + brk.serial);
		iterator.remove();
		if (iterator.hasNext()) {
		    ((Break) iterator.next()).delta.add(brk.delta);
		    iterator.previous();
		}
		currWidth.sub(brk.delta);
		brk.delta = NetDimen.NULL;
		//D*/ traceBreakList();
	    } else if (badness <= threshold) {
		int		dem = demerits(pen, badness)
				    + demerits(brk, fitness, hyphen, last);
		traceBreak(idx, brk.serial, badness, pen, dem, false);
		recordFeasible(brk, fitness, dem);
	    }
	}
	if (minDem < AWFUL_BAD)
	    createActive(idx, iterator, currWidth, hyphen, preWidth);
    }

    protected void	createActive(int idx, ListIterator iterator,
				     NetDimen width,
				     boolean hyphen, Dimen preWidth) {
	int		absAdjDem = Math.abs(adjDem);
	int		limit = (AWFUL_BAD - minDem <= absAdjDem)
			    ? AWFUL_BAD - 1 : minDem + absAdjDem;
	int		cnt = breakCount(idx);
	NetDimen	delta = breakWidth(idx, cnt);
	//D*/ System.out.println("> createActive(" + idx
		         //D*/ + "), breakWidth = " + delta);
	delta.add(preWidth);
	delta.sub(width);
	//D*/ System.out.println("> delta = " + delta);
	for (Fitness fit = FITNESS_HEAD;
		fit != NULL_FITNESS; fit = fit.next) {
	    if (fit.fits(limit)) {
		if (delta != NetDimen.NULL) {
		    Break	best = fit.makeBest(idx, cnt, hyphen, delta);
		    iterator.add(best); traceBreak(best);
		    if (iterator.hasNext()) {
			//D*/ System.out.println("> Inserting @" + best.serial);
			((Break) iterator.next()).delta.sub(best.delta);
			iterator.previous();
		    }
		    width.add(delta); delta = NetDimen.NULL;
		} else {
		    Break	best = fit.makeBest(idx, cnt, hyphen,
						    new NetDimen());
		    iterator.add(best); traceBreak(best);
		}
		//D*/ traceBreakList();
	    }
	    fit.reset();
	}
	minDem = AWFUL_BAD;
    }

    protected int	demerits(int pen, int badness) {
	int		dem = Math.abs(linePen + badness);
	if (dem > Node.INF_PENALTY) dem = Node.INF_PENALTY;
	dem = dem * dem;
	if (pen != 0) {
	    if (pen > 0) dem += pen * pen;
	    else if (pen > Node.EJECT_PENALTY) dem -= pen * pen;
	}
	return dem;
    }

    protected int	demerits(Break brk, Fitness fitness,
				 boolean hyphen, boolean last) {
	int		dem = 0;
	if (brk.hyphenated)
	    if (last) dem += finHyphDem;
	    else if (hyphen) dem += dblHyphDem;
	if (!fitness.adjoins(brk.fitness)) dem += adjDem;
	return dem;
    }

    protected void	recordFeasible(Break brk, Fitness fitness, int dem) {
	dem += brk.demerits;
	fitness.update(dem, brk);
	if (minDem > dem) minDem = dem;
    }

    protected int		breakCount(int idx) {
	int	j = idx;
	if (stillNodeAt(j) && nodeAt(j++).discardsAfter())
	    while (stillNodeAt(j) && nodeAt(j).discardable()) j++;
	return j - idx;
    }

    protected NetDimen		breakWidth(int idx, int cnt) {
	NetDimen	netDim = new NetDimen(background);
	if (cnt > 0) netDim.add(nodeAt(idx).postBreakWidth());
	for (int j = 0; j < cnt; j++) sub(netDim, nodeAt(idx + j));
	return netDim;
    }

    protected static void	add(NetDimen netDim, Node node) {
	netDim.add(node.getLeftX()); netDim.add(node.getWidth());
	netDim.addShrink(node.getWshr());
	netDim.addStretch(node.getWstrOrd(), node.getWstr());
    }

    protected static void	sub(NetDimen netDim, Node node) {
	netDim.sub(node.getLeftX()); netDim.sub(node.getWidth());
	netDim.subShrink(node.getWshr());
	netDim.subStretch(node.getWstrOrd(), node.getWstr());
    }

    protected void		checkShrinkage(Node node) { }	//XXX[825]

    abstract protected void	traceBreak(int idx, int serial,
    					   int bad, int pen, int dem,
    					   boolean artificial);

    abstract protected void	traceBreak(Break brk);

}
