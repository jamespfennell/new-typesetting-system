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
// Filename: nts/node/MigratingNode.java
// $Id: MigratingNode.java,v 1.1.1.1 2000/05/27 02:27:39 ksk Exp $
package	nts.node;

public abstract class	MigratingNode	extends BaseNode {
    /* corresponding to adjust_node, ins_node, mark_node */

    public boolean	sizeIgnored() { return true; }
    public boolean	isMigrating() { return true; }
    public NodeEnum	getMigration() { return NodeList.nodes(this); }
    public byte		afterWord() { return SUCCESS; }

}
