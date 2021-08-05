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
// Filename: nts/command/EndGroupCommand.java
// $Id: EndGroupCommand.java,v 1.1.1.1 2000/08/05 16:07:00 ksk Exp $
package	nts.command;

import	nts.io.Log;

public class	EndGroupCommand	extends GroupCommand {

    private final String		name;

    public EndGroupCommand(String name) { this.name = name; }

    public void		exec(Group grp, Token src) { grp.reject(src); }

    public static final Closing		NORMAL = new Closing() {
	public void	exec(Group grp, Token src) { popLevel(); }
    };

    public final void		addOn(Log log) { log.add(name); }

    public final String		toString() { return "@(" + name + ')'; }

}
