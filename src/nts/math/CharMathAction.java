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
// Filename: nts/math/CharMathAction.java
// $Id: CharMathAction.java,v 1.1.1.1 2000/03/02 09:28:15 ksk Exp $
package	nts.math;

import	nts.io.CharCode;
import	nts.command.Token;
import	nts.command.Command;

public class	CharMathAction	extends MathAction {

    private Command		cmd;

    public	CharMathAction(Command cmd) { this.cmd = cmd; }

    /* TeXtp[1154] */
    public void		exec(MathBuilder bld, Token src) {
	CharCode	code = cmd.charCodeToAdd();
	if (code != CharCode.NULL) MathPrim.setMathChar(bld, code);
    }

}
