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
// Filename: nts/node/AdjustLeaders.java
// $Id: AdjustLeaders.java,v 1.1.1.1 1999/09/06 08:06:45 ksk Exp $
package	nts.node;

import	nts.base.Dimen;

public class	AdjustLeaders	extends BoxLeaders {

    public static final String		DESCRIPTOR = "leaders";

    public AdjustLeaders(Node node, Mover mover) { super(node, mover); }

    protected String	getDesc() { return DESCRIPTOR; }

    protected void	typeSet(TypeSetter setter, SettingContext sctx,
				Dimen size, Dimen nodeSize) {
	Dimen		start = mover.offset(sctx.start).modulo(nodeSize);
	if (!start.isZero()) start = nodeSize.minus(start);
	int		count = size.minus(start).divide(nodeSize);
	typeSet(setter, sctx, count, start, Dimen.ZERO);
    }

}
