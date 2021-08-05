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
// Filename: nts/typo/WritePrim.java
// $Id: WritePrim.java,v 1.1.1.1 2000/05/27 02:14:18 ksk Exp $
package	nts.typo;

import	java.io.ObjectInputStream;
import	java.io.IOException;
import	java.util.HashMap;
import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.BaseNode;
import	nts.node.TypeSetter;
import	nts.node.SettingContext;
import	nts.builder.Builder;
import	nts.builder.NullBuilder;
import	nts.command.Token;
import	nts.command.TokenList;
import	nts.command.FrozenToken;
import	nts.command.LeftBraceToken;
import	nts.command.RightBraceToken;
import	nts.command.Macro;
import	nts.command.MacroBody;
import	nts.command.Prim;
import	nts.command.PrefixPrim;

public class	WritePrim extends BuilderPrim {

    private transient HashMap		table;

    private void	initTable() { table = new HashMap(23); }

    public WritePrim(String name) { super(name); initTable(); }

    private void	readObject(ObjectInputStream input)
				throws IOException, ClassNotFoundException
	{ input.defaultReadObject(); initTable(); }

    public Log		get(int num)
	{ return (Log) table.get(Integer.valueOf(num)); }

    private Log		replace(int num, Log output) {
	return (Log) table.put(Integer.valueOf(num), output);
	//XXX remove if NULL
    }

    public void		set(int num, Log output) {
	Log		old = replace(num, output);
	if (old != output && old != Log.NULL) old.close();
    }

    public void		exec(Builder bld, Token src) {
	final int		num = scanInt();
	final TokenList		list = Prim.scanTokenList(src, false);
	bld.addNode(new WriteNode(num, list));
    }

    /* TeXtp[1352] */
    public boolean	immedExec(Token src) {
	int		num = scanInt();
	TokenList	list = Prim.scanTokenList(src, false);
	write(num, list); return true;
    }

    private static final Token		FROZEN_END_WRITE
	= new FrozenToken("endwrite",
			  new Macro(MacroBody.EMPTY, PrefixPrim.OUTER));

    private static final Token[]	END_TOKS
	= { RightBraceToken.TOKEN, FROZEN_END_WRITE };

    private static final TokenList	END_LIST = new TokenList(END_TOKS);

    private static final NullBuilder	NULL_BUILDER = new NullBuilder();

    /* TeXtp[1370] */
    protected void	write(int num, TokenList list) {
	insertList(END_LIST);
	tracedPushXList(list, "write");
	insertTokenWithoutCleaning(LeftBraceToken.TOKEN);
	Builder.push(NULL_BUILDER);
	// list = Prim.scanTokenList(esc(getName()), true);
	list = Prim.scanTokenList(this, true);
	Token	tok = nextRawToken();
	if (tok != FROZEN_END_WRITE) {
	    error("UnbalancedWrite");
	    do tok = nextRawToken();
	    while (tok != FROZEN_END_WRITE);
	}
	Builder.pop();
	getTokStack().dropFinishedPop();
	Log		output;
	if (num < 0) output = fileLog.startLine();
	else if ((output = get(num)) == Log.NULL)
	    output = normLog.startLine();
	output.add(list).endLine();
    }

    protected abstract static class	FileNode	extends BaseNode {
	/* root corresponding to whatsit_node */

	protected int			num;

	public FileNode(int num) { this.num = num; }

	public boolean	sizeIgnored() { return true; }

	/* TeXtp[1355] */
	protected Log	addName(Log log, String name) {
	    log.addEsc(name);
	    return (num < 0) ? log.add('-')
		: (num > getConfig().getIntParam(Prim.INTP_MAX_FILE_CODE))
		? log.add('*') : log.add(num);
	}

	public byte	beforeWord() { return SKIP; }
	public byte	afterWord() { return SUCCESS; }

    }

    protected class	WriteNode	extends FileNode {
	/* corresponding to whatsit_node */

	protected TokenList		list;

	public WriteNode(int num, TokenList list)
	    { super(num); this.list = list; }

	/* TeXtp[1356] */
	public void	addOn(Log log, CntxLog cntx)
	    { addNodeToks(addName(log, "write"), list); }

	/* TeXtp[1366, 1367] */
	public void	typeSet(TypeSetter setter, SettingContext sctx)
	    { if (sctx.allowIO) write(num, list); }

    }

}
