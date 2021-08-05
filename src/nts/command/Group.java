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
// Filename: nts/command/Group.java
// $Id: Group.java,v 1.1.1.1 1999/09/17 23:28:01 ksk Exp $
package	nts.command;

public abstract class	Group	extends CommandBase {

    /* TeXtp[1064] */
    public void		reject(Token tok) {
        backToken(tok);
	Token		ins = expectedToken();
	insertTokenWithoutCleaning(ins);
	error("MissingInserted", ins);
    }

    public Token	expectedToken() { return RightBraceToken.TOKEN; }

    public void		open() { }
    public void		start() { }
    public void		stop() { }
    public void		close() { }

    public abstract void	saveForAfter(Token tok);
    public abstract void	unsaveAfter();

}
