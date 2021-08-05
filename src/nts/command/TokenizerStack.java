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
// Filename: nts/command/TokenizerStack.java
// $Id: TokenizerStack.java,v 1.1.1.1 2000/08/04 04:51:07 ksk Exp $
package	nts.command;

import	nts.base.BoolPar;

/**
 * Stack of |Tokenizer|s. It provides methods |push| for |Tokenizer|,
 * array of |Token|s and single |Token|. Method |nextToken| calls the same
 * method of the |Tokenizer| on the top of stack.
 */
public class TokenizerStack {

    /*
     * There are at least two possibilities how to implement stack of
     * tokenizers. First is to use some generic container and second is to
     * make base class for tokenizers which can chain itself into an linked
     * list. I chose the second method because the operations are so simple.
     * It contains another member related to pushing and poping anyway --- see
     * the paragraph below. The generic stack in Java on the other hand needs
     * casting for accesing elements.
     *
     * Each tokenizer contains the reference to the stack where it is pushed.
     * The reason is that some tokenizers might want to push another tokenizer
     * on the top of stack (for example a macro expansion needs to push a
     * parameter --- it is much simplier than treating two levels of expansion
     * in single object) and then it needs to know the reference to the stack.
     * But after some thinking it seems quite logical that one tokenizer is
     * used in only one stack at the some time so the binding to one stack is
     * OK from this point of view too.
     *
     * The base tokenizer is implemented as an inner class. This guarantees
     * that only the class itself and the |TokenizerStack| have access to its
     * private members which maintain chaining. The method |nextToken| is
     * protected so the subclasses can override it but beside them only
     * |TokenizerStack| can call it. Provided that the |Token|s are retrieved
     * from a |Tokenizer| only by |TokenizerStack| where it belongs to and
     * also the synchronizations seems to be sufficient on the level of
     * |nextToken| method of the |TokenizerStack|.
     *
     * For maintaining of input stack in TeX see TeXtp[301,321,322].
     */

    /** The current |Tokenizer| - top of the stack */
    private Tokenizer		top = Tokenizer.NULL;

    public Tokenizer		getTop() { return top; }

    /**
     * Pushes a |Tokenizer| on the top of the stack.
     * @param	tokenizer the |Tokenizer| to be pushed.
     * @exception	RuntimeException if the |tokenizer| is already
     *			pushed somewhere.
     */
    public synchronized void	push(Tokenizer tokenizer) {
        Tokenizer	oldTop = top;
	top = tokenizer;
        top.pushTo(this, oldTop);
    }

    /**
     * Pops one |Tokenizer| from the top of the stack.
     */
    private synchronized Tokenizer	pop() {
        if (top != Tokenizer.NULL) {
	    Tokenizer	oldTop = top;
	    top = oldTop.getNext();
	    oldTop.popFrom(this);
	    return oldTop;
	}
	return Tokenizer.NULL;
    }

    public synchronized void		dropPop()
	{ if (top != Tokenizer.NULL) pop().close(); }

    public synchronized void		dropFinishedPop()
	{ if (top != Tokenizer.NULL && top.finished()) pop().close(); }

    /**
     * Creates a |Tokenizer| for a portion of a |TokenList| and
     * pushes it on the top of the stack.
     * @param	list the |TokenList|.
     * @param	start position of the first |Token| in the sequence.
     * @param	end position after the last |Token| in the sequence.
     */
//	    public void		push(TokenList list, int start, int end)
//		{ push(new TokenListTokenizer(list, start, end)); }

    /**
     * Creates a Tokenizer for a |TokenList| and pushes it
     * on the top of the stack.
     * @param	list the |TokenList|.
     */
    public void		push(TokenList list, String desc)
	{ push(new InsertedTokenList(list, desc)); }

    public void		backUp(TokenList list)
	{ push(new BackedTokenList(list)); }

    public void		push(Token tok, String desc)
	{ push(new InsertedToken(tok, desc)); }

    /**
     * Creates a |Tokenizer| for a single |Token| and pushes it
     * on the top of the stack.
     * @param	tok the single |Token|.
     * @param	exp tells whether the backed |Token| can be expanded
     *		    (e.g. was not preceded by \noexpand).
     */
    public void		backUp(Token tok, boolean exp)
	{ push(new BackedToken(tok, exp)); }

    private InpTokChecker		checker = InpTokChecker.NULL;

    public InpTokChecker	setChecker(InpTokChecker chk) {
        InpTokChecker	old = checker;
	checker = chk; return old;
    }

    private final BoolPar		CAN_EXPAND = new BoolPar();

    /**
     * Gives the next |Token| from the |Tokenizer| stack. It calls the
     * |nextToken| method of the |Tokenizer| on the top of
     * the stack. If the |Tokenizer| on top finishes (returns |Token.NULL|)
     * it is poped out and the next stack top |Tokenizer| is asked.
     * When the stack is empty it returns |Token.NULL| to indicate
     * that the whole stack is finished.
     * @param	canExpand boolean output parameter querying whether the
     *			  acquired |Token| can be expanded (e.g. was not
     *			  preceded by \noexpand).
     * @return	the next |Token| or |Token.NULL| if the stack is finished.
     */
    public synchronized Token	nextToken(BoolPar canExpand) {
        Token		tok;
	if (canExpand == BoolPar.NULL) canExpand = CAN_EXPAND;
        while (top != Tokenizer.NULL) {
	    if ((tok = top.nextToken(canExpand)) != Token.NULL)
	        return (checker != InpTokChecker.NULL)
		     ? checker.checkToken(tok, canExpand) : tok;
	    if (pop().close() && checker != InpTokChecker.NULL)
	        checker.checkEndOfFile();
	}
        canExpand.set(false);
    	return Token.NULL;
    }

    public synchronized void	close()
	{ while (top != Tokenizer.NULL) pop().close(); }

    public synchronized void	cleanFinishedLists()
	{ while (top != Tokenizer.NULL && top.finishedList()) pop().close(); }

    public synchronized void	cleanFinishedInserts()
	{ while (top != Tokenizer.NULL && top.finishedInsert()) pop().close(); }

    public synchronized void	endTopmostInput() {
	for (Tokenizer curr = top; curr != Tokenizer.NULL;
	     curr = curr.getNext()) if (curr.endInput()) break;
    }

    public synchronized FilePos		filePos() {
        FilePos		pos = FilePos.NULL;
	for (Tokenizer curr = top; curr != Tokenizer.NULL;
	     curr = curr.getNext()) {
	    pos = curr.filePos();
	    if (pos != FilePos.NULL) break;
	}
	return pos;
    }

    public int		lineNumber() {
        FilePos		pos = filePos();
	return (pos != FilePos.NULL) ? pos.line : 0;
    }

    public synchronized void	show(ContextDisplay disp) {
        int		lines = disp.lines();
        boolean		dots = (lines > 0);
	Tokenizer	curr = top;
	if (curr != Tokenizer.NULL) for (;; curr = curr.getNext()) {
	    boolean	bottom = (  curr.enoughContext()
	    			 || curr.getNext() == Tokenizer.NULL  );
	    boolean	force = (bottom || curr == top);	//SSS
	    if (force || lines > 0) {
	        disp.reset();
		lines -= curr.show(disp, force, lines);
	    } else if (lines <= 0 && dots)
		{ disp.normal().startLine().add("..."); dots = false; }
	    if (bottom) break;
	}
    }

}
