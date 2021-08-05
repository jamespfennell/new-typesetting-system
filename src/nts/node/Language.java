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
// Filename: nts/node/Language.java
// $Id: Language.java,v 1.1.1.1 2000/10/18 21:45:31 ksk Exp $
package	nts.node;

import	java.io.Serializable;
import	nts.io.Log;
import	nts.io.Loggable;

public abstract class	Language	implements Serializable, Loggable {

    public static final Language	NULL = null;
    public static final char		WORD_BOUNDARY = 0;

    protected final int		languageNumber;
    protected final int		leftHyphenMin;
    protected final int		rightHyphenMin;

    public Language(int ln, int lhm, int rhm) {
	languageNumber = ln;
	leftHyphenMin = lhm;
	rightHyphenMin = rhm;
    }

    public boolean	isCommon() {
        return (  languageNumber == 0
	       && leftHyphenMin == 2
	       && rightHyphenMin == 3  );
    }

    /* TeXtp[218] */
    public void		addOn(Log log) {
	log.add("language").add(languageNumber)
	   .add(":hyphenmin").add(leftHyphenMin)
	   .add(',').add(rightHyphenMin);
    }

    /* STRANGE
     * Why two so similar forms?
     */
    /* TeXtp[1356] */
    public void		addShortlyOn(Log log) {
	log.add(languageNumber)
	   .add(" (hyphenmin ").add(leftHyphenMin)
	   .add(',').add(rightHyphenMin).add(')');
    }

    public boolean	isZero() { return (languageNumber == 0); }
    public void		addNumberOn(Log log) { log.add(languageNumber); }

    public boolean	sameNumberAs(int number)
	{ return (languageNumber == number); }

    public abstract Hyphens	getHyphens(String word);
    public abstract void	setHyphException(String word, int[] positions);
    public abstract boolean	setHyphPattern(String pattern, int[] values);

}
