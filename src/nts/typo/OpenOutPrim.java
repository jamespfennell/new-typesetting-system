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
// Filename: nts/typo/OpenOutPrim.java
// $Id: OpenOutPrim.java,v 1.1.1.1 2000/05/26 21:14:44 ksk Exp $
package	nts.typo;

import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.TypeSetter;
import	nts.node.SettingContext;
import	nts.builder.Builder;
import	nts.command.FileName;
import	nts.command.Token;
import	nts.command.Prim;

public class	OpenOutPrim	extends BuilderPrim {

    private WritePrim		write;

    public OpenOutPrim(String name, WritePrim write)
	{ super(name); this.write = write; }

    private void	open(int num, FileName name)
	{ write.set(num, getIOHandler().openWrite(name, num)); }

    public void		exec(Builder bld, Token src) {
        int		num = Prim.scanFileCode();
	skipOptEquals();
	FileName	name = scanFileName();
	bld.addNode(new OpenOutNode(num, name));
    }

    /* TeXtp[1351] */
    public boolean		immedExec(Token src) {
        int		num = Prim.scanFileCode();
	skipOptEquals();
	FileName	name = scanFileName();
	open(num, name);
	return true;
    }

    protected class	OpenOutNode	extends WritePrim.FileNode {
	/* corresponding to whatsit_node */

	protected FileName		name;

	public OpenOutNode(int num, FileName name)
	    { super(num); this.name = name; }

	/* TeXtp[1356] */
	public void	addOn(Log log, CntxLog cntx)
	    { addName(log, "openout").add('=').add(name); }

	/* TeXtp[1366, 1367] */
	public void	typeSet(TypeSetter setter, SettingContext sctx)
	    { if (sctx.allowIO) open(num, name); }

    }

}
