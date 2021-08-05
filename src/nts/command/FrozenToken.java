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
// Filename: nts/command/FrozenToken.java
// $Id: FrozenToken.java,v 1.1.1.1 2000/03/03 11:29:04 ksk Exp $
package	nts.command;

import	nts.io.Log;
import	nts.io.Name;

/**
 * Frozen control sequence Token.
 */
public class	FrozenToken extends Token {

    /** The name of the control sequence */
    private final Name		name;

    private final Command	cmd;

    /**
     * Creates a frozen control sequence token of the given name.
     * @param	name the name of the frozencontrol sequence
     * @param	cmd the meaning of the frozen control sequence
     */
    public FrozenToken(Name name, Command cmd)
	{ this.name = name; this.cmd = cmd; }

    public FrozenToken(String name, Command cmd)
	{ this.name = makeName(name); this.cmd = cmd; }

    public FrozenToken(Primitive prim) {
	this.name = makeName(prim.getName());
	this.cmd = prim.getCommand();
    }

    /**
     * Gives object (potentially) associated in table of equivalents.
     * @param	eqtab table of equivalents.
     * @return	the Command to interpret this token.
     */
    public Command	meaning() { return cmd; }

    public boolean	frozen() { return true; }

    public boolean	match(Token tok) {
    	//XXX does it match anything at all?
	//XXXX it should probably match CtrlSeqToken of the same name
	//XXXX is there any case, where it is tried to match?
	//XXXX (scanning mac params?)
        // return (  tok instanceof FrozenToken
	  //     && ((FrozenToken) tok).name.match(name)  );
	return false;
    }

    public void		addOn(Log log) { name.addEscapedOn(log); }

    public void		addProperlyOn(Log log)
	{ name.addProperlyEscapedOn(log); }

    // is the rest OK ?

    public int		numValue()
	{ return (name.length() == 1) ? name.codeAt(0).numValue() : -1; }

    public Name		controlName() { return name; }

    public String	toString() { return "<Frozen: " + name + '>'; }

}
