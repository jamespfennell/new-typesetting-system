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
// Filename: nts/math/MathCharDefPrim.java
// $Id: MathCharDefPrim.java,v 1.1.1.1 2001/03/22 15:52:06 ksk Exp $
package	nts.math;

import	nts.base.Num;
import	nts.io.Log;
import	nts.command.Token;
import	nts.command.Command;
import	nts.command.Relax;
import	nts.command.AssignPrim;
import	nts.command.Primitive;

public class	MathCharDefPrim	extends AssignPrim {

    private Primitive		prim;

    public MathCharDefPrim(String name, Primitive prim)
	{ super(name); this.prim = prim; }

    protected void	assign(Token src, boolean glob) {
	Token	tok = definableToken();
	tok.define(Relax.getRelax(), glob);
	skipOptEquals();
	int	code = MathPrim.scanMathCharCode();	//XXX int
	tok.define(new MathGiven(code, prim.getName()), glob);

	// CharCode	code = Token.makeCharCode(scanMathCharCode());
	// if (code != CharCode.NULL)
	    // tok.define(new MathGiven(code, prim.getName()), glob);
	// else throw new RuntimeException("no mathchar number scanned");
    }

}

class	MathGiven	extends Command	implements Num.Provider {

    private int			code;	//XXX int
    private String		name;

    public MathGiven(int code, String name)
	{ this.code = code; this.name = name; }

    public void		exec(Token src)
	{ MathPrim.handleMathCode(code, src); }

    public boolean	hasMathCodeValue() { return true; }
    public int		getMathCodeValue() { return code; }

    public boolean	sameAs(Command cmd) {
	return (  cmd instanceof MathGiven
	       && code == ((MathGiven) cmd).code  );
    }

    public void		addOn(Log log) {
        log.addEsc(name).add('"')
	   .add(Integer.toHexString(code).toUpperCase());
    }

    public boolean  hasNumValue() { return true; }
    public Num      getNumValue() { return Num.valueOf(code); }

    public final String	toString()
	{ return "[mathchar given: " + code + ']'; }

}
