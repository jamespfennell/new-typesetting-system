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
// Filename: nts/node/SizesIterator.java
// $Id: SizesIterator.java,v 1.1.1.1 2000/08/09 06:05:31 ksk Exp $
package	nts.node;

import	nts.base.Dimen;

public abstract class	SizesIterator {

    public abstract boolean		hasMoreElements();
    public abstract void		takeNextElement();
    public abstract boolean		sizeIgnored();
    public abstract Dimen		currHeight();
    public abstract Dimen		currWidth();
    public abstract Dimen		currDepth();
    public abstract Dimen		currLeftX();
    public abstract Dimen		currStr();
    public abstract Dimen		currShr();
    public abstract byte		currStrOrd();
    public abstract byte		currShrOrd();

    /* TeXtp[668] */
    public void		summarize(SizesSummarizer pack) {
	boolean		empty = true;
	while (hasMoreElements()) {
	    takeNextElement();
	    if (!sizeIgnored()) {
		if (empty) pack.setHeight(currHeight());
		else pack.add(currHeight());
	        pack.setDepth(currDepth());
		pack.addStretch(currStr(), currStrOrd());
		pack.addShrink(currShr(), currShrOrd());
		pack.setMaxWidth(currWidth());
		pack.setMaxLeftX(currLeftX());
	    }
	    empty = false;
	}
    }

}
