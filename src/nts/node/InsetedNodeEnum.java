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
// Filename: nts/node/InsetedNodeEnum.java
// $Id: InsetedNodeEnum.java,v 1.1.1.1 2000/05/25 22:02:34 ksk Exp $
package	nts.node;

public abstract class	InsetedNodeEnum	extends NodeEnum {

    private final NodeEnum		in;
    protected NodeEnum			inseted;

    public InsetedNodeEnum(NodeEnum in) { this.in = in; }

    public Node		nextNode() {
	if (inseted != NULL) {
	    if (inseted.hasMoreNodes()) return inseted.nextNode();
	    inseted = NULL;
	}
	return in.nextNode();
    }

    public boolean	hasMoreNodes() {
	if (inseted != NULL) {
	    if (inseted.hasMoreNodes()) return true;
	    inseted = NULL;
	}
	return in.hasMoreNodes();
    }

}
