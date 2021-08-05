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
// Filename: nts/node/Box.java
// $Id: Box.java,v 1.1.1.1 2000/05/02 21:17:23 ksk Exp $
package	nts.node;

import	nts.base.Dimen;
import	nts.io.Log;

public interface	Box	extends Node {

    Box		NULL = null;

    public interface	Provider { Box	getBoxValue(); }
    //CCC jikes requires public here

    boolean	isVoid();
    boolean	isHBox();
    boolean	isVBox();

    BoxSizes	getSizes();
    Box		pretendSizesCopy(BoxSizes sizes);
    Box		pretendingWidth(Dimen width);
    void	addOn(Log log, int maxDepth, int maxCount);
    void	typeSet(TypeSetter setter);

    NodeEnum	getHorizList();
    NodeEnum	getVertList();

    /* STRANGE
     * box.addOn(log, maxDepth, maxCount) could be just:
     * CntxLog.addItem(log, box, maxDepth, maxCount)
     * (as in case of AnyBoxNode) but VoidBoxNode is an exception
     */

}
