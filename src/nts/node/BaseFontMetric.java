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
// Filename: nts/node/BaseFontMetric.java
// $Id: BaseFontMetric.java,v 1.1.1.1 2001/02/09 12:53:15 ksk Exp $
package	nts.node;

import	nts.base.Num;
import	nts.base.Dimen;
import	nts.base.Glue;

public abstract class	BaseFontMetric	implements FontMetric {

    public boolean	isNull() { return false; }

    private Num[]		numPars = new Num[NUMBER_OF_NUM_PARAMS];
    { for (int i = 0; i < numPars.length; numPars[i++] = Num.NULL); }

    public Num	getNumParam(int idx) {
	return (0 <= idx && idx < numPars.length)
	     ? numPars[idx] : Num.NULL;
    }

    public Num	setNumParam(int idx, Num val) {
	return (0 <= idx && idx < numPars.length)
	     ? (numPars[idx] = val) : Num.NULL;
    }

    public boolean	definesNumParams(int[] idxs) {
	for (int i = 0; i < idxs.length; i++) {
	    int		idx = idxs[i];
	    if (  idx < 0 || idx >= numPars.length
	       || numPars[idx] == Num.NULL  ) return false;
	}
	return true;
    }

    /*
     * We make no assumptions about the order of DIMEN_PARAM constants
     * therefore we simply allocate the array for all possible parameters and
     * initialize each element to Dimen.NULL. Subclasses of this class can
     * assign appropriate values to those parameters which they really provide
     * during construction.
     */

    private Dimen[]		dimPars = new Dimen[NUMBER_OF_DIMEN_PARAMS];
    {
	for (int i = 0; i < dimPars.length; dimPars[i++] = Dimen.NULL);
	final int[]	params = ALL_TEXT_DIMEN_PARAMS;
	for (int i = 0; i < params.length; dimPars[params[i++]] = Dimen.ZERO);
    }

    public Dimen	getDimenParam(int idx) {
	return (0 <= idx && idx < dimPars.length)
	     ? dimPars[idx] : Dimen.NULL;
    }

    public Dimen	setDimenParam(int idx, Dimen val) {
	if (0 <= idx && idx < dimPars.length) {
	    normalSpace = Glue.NULL;
	    return dimPars[idx] = val;
	}
	return Dimen.NULL;
    }

    public boolean	definesDimenParams(int[] idxs) {
	for (int i = 0; i < idxs.length; i++) {
	    int		idx = idxs[i];
	    if (  idx < 0 || idx >= dimPars.length
	       || dimPars[idx] == Dimen.NULL  ) return false;
	}
	return true;
    }

    private transient Glue	normalSpace = Glue.NULL;

    public Glue		getNormalSpace() {
        if (normalSpace == Glue.NULL)
	    normalSpace = Glue.valueOf( dimPars[DIMEN_PARAM_SPACE],
					dimPars[DIMEN_PARAM_STRETCH],
					dimPars[DIMEN_PARAM_SHRINK] );
	return normalSpace;
    }

}
