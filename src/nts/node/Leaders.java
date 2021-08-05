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
// Filename: nts/node/Leaders.java
// $Id: Leaders.java,v 1.1.1.1 2000/02/19 01:43:45 ksk Exp $
package	nts.node;

import	java.io.Serializable;
import	nts.base.Dimen;
import	nts.base.Glue;
import	nts.io.Log;
import	nts.io.CntxLog;

public interface	Leaders	extends Serializable {

    Leaders	NULL = null;

    Dimen	getHeight();
    Dimen	getWidth();
    Dimen	getDepth();
    Dimen	getLeftX();
    void	addOn(Log log, CntxLog cntx, Glue skip);
    void	typeSet(TypeSetter setter, Dimen size, SettingContext sctx);

}
