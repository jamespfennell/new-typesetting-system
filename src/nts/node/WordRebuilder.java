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
// Filename: nts/node/WordRebuilder.java
// $Id: WordRebuilder.java,v 1.1.1.1 2001/01/28 17:57:04 ksk Exp $
package	nts.node;

import	nts.io.CharCode;

public interface	WordRebuilder	extends WordBuilder {

    WordRebuilder	NULL = null;

    byte	INDEPENDENT = 0;
    byte	AFFECTING = 1;
    byte	BELONGING = 2;

    byte	addIfBelongsToCut(CharCode code);
    boolean	prolongsCut(CharCode code);
    void	close(CharCode code);

}
