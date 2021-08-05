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
// Filename: nts/typo/BuilderCommand.java
// $Id: BuilderCommand.java,v 1.1.1.1 2001/03/22 15:55:21 ksk Exp $
package	nts.typo;

import	java.io.ObjectInputStream;
import	java.io.ObjectOutputStream;
import	java.io.Serializable;
import	java.io.IOException;
import	nts.base.Glue;
import	nts.base.ClassAssoc;
import	nts.io.CharCode;
import	nts.builder.Builder;
import	nts.command.Token;

public abstract class	BuilderCommand	extends TypoCommand {

    private static ClassAssoc	assoc;
    private static Object	charHandlerMark;

    public static void		makeStaticData() {
	assoc = new ClassAssoc(Builder.class);
	charHandlerMark = new Serializable() {
	    public int	hashCode() { return 53479; }
	};
    }

    public static void		writeStaticData(ObjectOutputStream output)
    					throws IOException {
	output.writeObject(assoc);
	output.writeObject(charHandlerMark);
    }

    public static void		readStaticData(ObjectInputStream input)
				throws IOException, ClassNotFoundException {
	assoc = (ClassAssoc) input.readObject();
	charHandlerMark = input.readObject();
    }

    public static void		registerBuilder(Class bld)
	{ assoc.record(bld); }

    public final BuilderCommand	defineAction(Class bld, Action act)
	{ assoc.put(bld, this, act); return this; }

    public final Action		getAction(Class bld)
	{ return (Action) assoc.get(bld, this); }

    public final void		exec(Token src) {
        Builder		bld = getBld();
        Action		act = getAction(bld.getClass());
	if (act == Action.NULL) exec(bld, src);
	else act.exec(bld, src);
    }

    public final Glue		getSkipForLeaders() {
        Builder		bld = getBld();
        Action		act = getAction(bld.getClass());
	return (act == Action.NULL)
	     ? getSkipForLeaders(bld) : act.getSkipForLeaders(bld);
    }

    public void		exec(Builder bld, Token src) { illegalCase(bld); }

    public Glue		getSkipForLeaders(Builder bld) { return Glue.NULL; }

    /* ****************************************************************** */

    public static void		defineCharHandler(Class bld, CharHandler hnd)
	{ assoc.put(bld, charHandlerMark, hnd); }

    public static CharHandler	getCharHandler(Class bld)
	{ return (CharHandler) assoc.get(bld, charHandlerMark); }

    public static void		handleChar(CharCode code, Token src) {
        Builder		bld = getBld();
        CharHandler	hnd = getCharHandler(bld.getClass());
	if (hnd == CharHandler.NULL)
	    error("CantUseIn", str("character"), bld);
	else hnd.handle(bld, code, src);
    }

    public static void		handleSpace(Token src) {
        Builder		bld = getBld();
        CharHandler	hnd = getCharHandler(bld.getClass());
	if (hnd == CharHandler.NULL)
	    error("CantUseIn", str("space"), bld);
	else hnd.handleSpace(bld, src);
    }

}
