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
// Filename: nts/typo/SetBoxPrim.java
// $Id: SetBoxPrim.java,v 1.1.1.1 2001/04/18 06:16:27 ksk Exp $
package	nts.typo;

import	nts.io.Log;
import	nts.node.Box;
import	nts.node.VoidBoxNode;
import	nts.node.NodeEnum;
import	nts.node.TreatBox;
import	nts.command.Token;
import	nts.command.RegisterPrim;

public class	SetBoxPrim	extends RegisterPrim {

    private final String	desc;

    /**
     * Creates a new |SetBoxPrim| with given name and stores it
     * in language interpreter |EqTable|.
     * @param	name the name of the |SetBoxPrim|
     */
    public SetBoxPrim(String name, String desc)
	{ super(name); this.desc = desc; }

    public String	getDesc() { return desc; }
    public String	getEqDesc() { return desc; }

    public final void	set(int idx, Box val, boolean glob) {
	if (glob) getEqt().gput(tabKind, idx, val);
	else getEqt().put(tabKind, idx, val);
    }

    public final Box	get(int idx) {
	Box	val =  (Box) getEqt().get(tabKind, idx);
	return (val != Box.NULL) ? val : VoidBoxNode.BOX;
    }

    public final Box	steal(int idx) {
	Box	val =  (Box) getEqt().get(tabKind, idx);
	if (val != Box.NULL) {
	    getEqt().nastyReplace(tabKind, idx, VoidBoxNode.BOX);
	    return val;
	}
	return VoidBoxNode.BOX;
    }

    public final void	foist(int idx, Box val) {
	/* if (getEqt().get(tabKind, idx) != Box.NULL) */
	getEqt().nastyReplace(tabKind, idx, val);
    }

    public void		addEqValueOn(int idx, Log log)
	{ get(idx).addOn(log, 0, 1); }

    /**
     * Performs the assignment.
     * @param	src source token for diagnostic output.
     * @param	glob indication that the assignment is global.
     */
    protected final void	assign(Token src, final boolean glob) {
	final int	idx = scanRegisterCode();
        skipOptEquals();
	TypoCommand.scanBox(new TreatBox() {
	    public void	execute(Box box, NodeEnum mig)
		{ set(idx, box, glob); }
	});
    }

    /* STRANGE
     * Why it scans code and equals when \setbox is not allowed
     */
    public void			doAssignment(Token src, int prefixes) {
	beforeAssignment(this, prefixes);
	scanRegisterCode(); skipOptEquals();
	error("ImproperSetbox", this);
	afterAssignment();
    }

    public void			showAndPurge(int idx) {
	//XXX maybe addBoxOnDiagLog(String, Box) could by used but only
	//XXX if it is certain that error() ends the previous line in diagLog
        diagLog.add("The following box has been deleted:");
	TypoCommand.addBoxOnDiagLog(get(idx));
	foist(idx, VoidBoxNode.BOX);
    }

}
