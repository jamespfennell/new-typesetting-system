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
// Filename: nts/align/AlignPrim.java
// $Id: AlignPrim.java,v 1.1.1.1 2001/03/21 08:24:12 ksk Exp $
package	nts.align;

import	nts.base.Dimen;
import	nts.builder.Builder;
import	nts.builder.ListBuilder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.FrozenToken;
import	nts.command.Group;
import	nts.command.Closing;
import	nts.command.Command;
import	nts.command.Primitive;
import	nts.typo.Action;
import	nts.typo.BuilderPrim;
import	nts.math.DisplayBuilder;

public abstract class	AlignPrim	extends BuilderPrim {

    private final TokenList.Inserter	everyCr;
    private final Token			frozenCr;
    private final Token			frozenEndTemplate;

    public AlignPrim(String name, TokenList.Inserter everyCr,
		     Primitive carRet, Command endv) {
	super(name); this.everyCr = everyCr;
	frozenCr = new FrozenToken(carRet);
	frozenEndTemplate = new FrozenToken("endtemplate",
		new EndTemplate(new FrozenToken("endtemplate", endv)));
    }

    /* STRANGE
     * Why is the outer builder pushed before scanning the alignment spec?
     * It is inconsistent with \hbox, etc. and makes the code more complicated.
     */
    public final Action		NORMAL = new Action() {
	public void	exec(Builder bld, Token src) {
	    //XXX[774] first align_group (scan_spec(align_group, false);)
	    //XXX[774] pushLevel(new SimpleGroup()); ???
	    ListBuilder		builder = makeOuterBuilder(currLineNumber());
	    Builder.push(builder);
	    Alignment	align = scanAlignSpec(builder);
	    pushLevel(new AlignGroup(align));
	    align.start(src);
	}
    };

    protected class	DisplayedClosing	extends Closing {
	
	/* TeXtp[774,776] */
	public void		exec(Group grp, Token src) {
	    if (!(getBld() instanceof DisplayBuilder))
		throw new RuntimeException(
			    "non-display builder for displayed halign");
	    Builder	bld = getBld();
	    if (!bld.isEmpty()) {
		error("ImproperAlignInFormula", AlignPrim.this);
		Builder.pop();
		Builder.push(new DisplayBuilder(bld.getStartLine()));
	    }
	    ListBuilder		builder = makeOuterBuilder(currLineNumber());
	    Builder.push(builder);
	    Alignment	align = scanAlignSpec(builder);
	    pushLevel(new DispAlignGroup(align));
	    align.start(src);
	}

    }

    public Closing	makeDisplayedClosing()
	{ return new DisplayedClosing(); }

    /* STRANGE
     * Why is the starting line number (for outer builder) before
     * size specification and left brace?
     */
    /* TeXtp[774] */
    protected Alignment		scanAlignSpec(ListBuilder builder) {
	Dimen		size = Dimen.ZERO;
	boolean		exactly = false;
	if (scanKeyword("to")) { size = scanDimen(); exactly = true; }
	else if (scanKeyword("spread")) size = scanDimen();
	scanLeftBrace();
	return makeAlignment(size, exactly, everyCr, frozenCr,
			     frozenEndTemplate, builder);
    }

    protected abstract ListBuilder	makeOuterBuilder(int lineNo);
    protected abstract Alignment	makeAlignment(
						Dimen size, boolean exactly,
						TokenList.Inserter everyCr,
						Token frzCr, Token frzEndt,
						ListBuilder builder);

}
