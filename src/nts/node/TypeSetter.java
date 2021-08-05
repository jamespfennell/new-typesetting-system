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
// Filename: nts/node/TypeSetter.java
// $Id: TypeSetter.java,v 1.1.1.1 1999/12/16 17:07:29 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.io.CharCode;

public interface	TypeSetter {

    TypeSetter		NULL = null;

    public interface	Mark {
	void		move();
	Dimen		xDiff();
	Dimen		yDiff();
    }

    void		set(char ch, FontMetric metric);
    void		set(CharCode code, FontMetric metric);
    void		setRule(Dimen h, Dimen w);
    void		setSpecial(byte[] spec);
    void		moveLeft(Dimen x);
    void		moveRight(Dimen x);
    void		moveUp(Dimen y);
    void		moveDown(Dimen y);
    Mark		mark();
    void		syncHoriz();
    void		syncVert();
    void		push();
    void		pop();
    void		startPage(Dimen yOffset, Dimen xOffset,
    				  Dimen height, Dimen width, int[] nums);
    void		endPage();
    void		close();

}
