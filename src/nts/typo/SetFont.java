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
// Filename: nts/typo/SetFont.java
// $Id: SetFont.java,v 1.1.1.1 2001/03/22 13:35:51 ksk Exp $
package	nts.typo;

import	nts.io.Log;
import	nts.node.FontMetric;
import	nts.command.Token;
import	nts.command.CtrlSeqToken;
import	nts.command.TokenList;
import	nts.command.Command;
import	nts.command.PrefixPrim;

//XXX maybe create PrefixedCommand and derive PrefixPrim of it
//XXX maybe create AssignCommand and derive AssignPrim of it

public class	SetFont		extends TypoCommand
				implements TokenList.Provider {

    private final FontMetric		metric;

    public SetFont(FontMetric metric) { this.metric = metric; }

    /** Non prefixed version of exec */
    public final void		exec(Token src) { exec(src, 0); }

    public final boolean	assignable() { return true; }
    public final void		doAssignment(Token src, int prefixes)
				    { exec(src, prefixes); }

    /**
     * Performs itself in the process of interpretation of the macro language
     * after sequence of prefix commands.
     * @param	src source token for diagnostic output.
     * @param	prefixes accumulated code of prefixes.
     */
    /* TeXtp[1217] */
    public final void		exec(Token src, int prefixes) {
	PrefixPrim.beforeAssignment(this, prefixes);
	setCurrFontMetric(metric, PrefixPrim.globalAssignment(prefixes));
	PrefixPrim.afterAssignment();
    }

    /* TeXtp[1261] */
    public final void		addOn(Log log)
	{ log.add("select font "); metric.addDescOn(log); }

    public boolean		sameAs(Command cmd) {
	return (  cmd instanceof SetFont
	       && metric.equals(((SetFont) cmd).metric)  );
    }

    public boolean	hasFontTokenValue() { return true; }
    public boolean	hasFontMetricValue() { return true; }

    /* STRANGE
     * What is \FONT~ good for?
     */
    /* TeXtp[415,465] */
    public Token	getFontTokenValue()
	{ return new CtrlSeqToken(metric.getIdent()); }

    /* TeXtp[577] */
    public FontMetric	getFontMetricValue() { return metric; }

}
