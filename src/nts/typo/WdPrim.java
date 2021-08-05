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
// Filename: nts/typo/WdPrim.java
// $Id: WdPrim.java,v 1.1.1.1 1999/08/02 12:18:24 ksk Exp $
package	nts.typo;

import	nts.base.Dimen;
import	nts.node.BoxSizes;

public class	WdPrim	extends BoxDimenPrim {

    public WdPrim(String name, SetBoxPrim reg) { super(name, reg); }

    protected BoxSizes	changeSizes(BoxSizes sizes, Dimen dimen) {
        return new BoxSizes(sizes.getHeight(), dimen,
			    sizes.getDepth(), sizes.getLeftX());
    }

    protected Dimen	selectSize(BoxSizes sizes)
	{ return sizes.getWidth(); }

}
