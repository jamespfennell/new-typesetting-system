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
// Filename: nts/dvi/DviFormatWriter.java
// $Id: DviFormatWriter.java,v 1.1.1.1 2001/02/02 12:21:23 ksk Exp $
package	nts.dvi;

import	java.io.IOException;
import	java.io.OutputStream;
import	java.util.Arrays;
import	java.util.HashMap;

public class	DviFormatWriter {

    private static final int	SET		= 0;
    private static final int	SET1		= 128;
    private static final int	SET_RULE	= 132;
    private static final int	PUT1		= 133;
    private static final int	PUT_RULE	= 137;
    private static final int	NOP		= 138;
    private static final int	BOP		= 139;
    private static final int	EOP		= 140;
    private static final int	PUSH		= 141;
    private static final int	POP		= 142;
    private static final int	RIGHT1		= 143;
    private static final int	W0		= 147;
    private static final int	W1		= 148;
    private static final int	X0		= 152;
    private static final int	X1		= 153;
    private static final int	DOWN1		= 157;
    private static final int	Y0		= 161;
    private static final int	Y1		= 162;
    private static final int	Z0		= 166;
    private static final int	Z1		= 167;
    private static final int	FNT		= 171;
    private static final int	FNT1		= 235;
    private static final int	SPEC1		= 239;
    private static final int	FNT_DEF1	= 243;
    private static final int	PRE		= 247;
    private static final int	POST		= 248;
    private static final int	POST_POST	= 249;

    private static final byte	ID_BYTE		= 2;
    private static final int	DAVID_FUCHS	= 0xdf;

    private /* final */ DviOutputStream		out;
    private /* final */ int			num;
    private /* final */ int			den;
    private /* final */ int			mag;

    private boolean		trouble = false;

    public DviFormatWriter(OutputStream out, int num, int den, int mag,
			   byte[] comm, int size) {
	if (out instanceof DviOutputStream)
	    this.out = (DviOutputStream) out;
	else this.out = new DviOutputStream(out, size);
	this.num = num; this.den = den; this.mag = mag;
	outByte(PRE); outByte(ID_BYTE);
	outWord(num); outWord(den); outWord(mag);
	int	len = comm.length;
	if (len > 255) len = 255;
	outByte(len); outBytes(comm, 0, len);
    }

    public DviFormatWriter(OutputStream out, int num, int den, int mag,
    			   byte[] comm) { this(out, num, den, mag, comm, 0); }

    private int		numOfPages = 0;
    private int		lastPageLoc = -1;
    private boolean	pageStarted = false;

    private void	checkPage() {
	if (!pageStarted)
	    throw new RuntimeException("page not started in DVI");
    }

    public int		pageCount() { return numOfPages; }
    public int		byteCount() { return currLoc(); }

    public void		startPage(int[] pars) {
        if (!pageStarted) {
	    int		loc = currLoc();
	    outByte(BOP);
	    for (int i = 0; i < 10; i++)
		outWord((i < pars.length) ? pars[i] : 0);
	    outWord(lastPageLoc);
	    numOfPages++;
	    lastPageLoc = loc;
	    pageStarted = true;
	} else throw new RuntimeException("page already started in DVI");
    }

    static final MoveStack.Entry	NULL_MOVE_STACK_ENTRY = null;

    private class	MoveStack {

	class	Entry {
	    final int		size;
	    final Entry		next;
	    final int		loc;
	    boolean		here = false;
	    boolean		yOK = true;
	    boolean		zOK = true;
	    Entry(int size, Entry next)
		{ this.size = size; this.next = next; loc = currLoc(); }
	}

	/* final */ int		normal;
	/* final */ int		y0;
	/* final */ int		z0;
	/* final */ int		y1;
	/* final */ int		z1;

	MoveStack(int normal, int y0, int z0, int y1, int z1) {
	    this.normal = normal;
	    this.y0 = y0; this.z0 = z0;
	    this.y1 = y1; this.z1 = z1;
	}

	Entry		top = NULL_MOVE_STACK_ENTRY;

	void	move(int size) {
	    Entry		ent = top;
	    boolean		ySeen = false;
	    boolean		zSeen = false;
	    top = new Entry(size, top);
	    while (ent != NULL_MOVE_STACK_ENTRY) {
	    	if (size == ent.size) {
		    if (!ySeen && ent.yOK) {
		        if (!ent.here) {
			    if (!changePack(ent.loc, normal, y1)) break;
			    ent.here = true; ent.zOK = false;
			}
			outByte(y0); top.here = true; top.zOK = false;
			for (Entry e = top; (e = e.next) != ent;	//SSS
			     e.yOK = false);
			return;
		    } else if (!zSeen && ent.zOK) {
		        if (!ent.here) {
			    if (!changePack(ent.loc, normal, z1)) break;
			    ent.here = true; ent.yOK = false;
			}
			outByte(z0); top.here = true; top.yOK = false;
			for (Entry e = top; (e = e.next) != ent;	//SSS
			     e.zOK = false);
			return;
		    }
		} else if (ent.here) {
		    if (ent.yOK) ySeen = true;
		    if (ent.zOK) zSeen = true;
		    if (ySeen && zSeen) break;
		}
		ent = ent.next;
	    }
	    outPack(normal, size);
	}

    }

    private MoveStack	xStack = new MoveStack(RIGHT1, W0, X0, W1, X1);
    private MoveStack	yStack = new MoveStack(DOWN1, Y0, Z0, Y1, Z1);

    public final void	moveX(int x) { checkPage(); xStack.move(x); }

    public final void	moveY(int y) { checkPage(); yStack.move(y); }

    private static class	Level {
        static final Level	NULL = null;
	final MoveStack.Entry		xTop;
	final MoveStack.Entry		yTop;
	final int			loc;
	final Level			next;
	Level(MoveStack.Entry xTop, MoveStack.Entry yTop,
	      int loc, Level next) {
	    this.xTop = xTop; this.yTop = yTop;
	    this.loc = loc; this.next = next;
	}
    }

    private int		maxPushLevel = 0;
    private int		pushLevel = 0;
    private Level	stack = Level.NULL;

    public void		push() {
        checkPage();
	outByte(PUSH); pushLevel++;
	stack = new Level(xStack.top, yStack.top, currLoc(), stack);
        if (maxPushLevel < pushLevel) maxPushLevel = pushLevel;
    }

    public void		pop() {
        checkPage();
        if (stack != Level.NULL) {
	    xStack.top = stack.xTop; yStack.top = stack.yTop;
	    if (stack.loc != currLoc() || !dropLastByte()) outByte(POP);
	    pushLevel--; stack = stack.next;
	} else throw new RuntimeException("too many pops in DVI");
    }

    public void		endPage() {
        checkPage();
	while (pushLevel > 0) pop();
	xStack.top = NULL_MOVE_STACK_ENTRY;
	yStack.top = NULL_MOVE_STACK_ENTRY;
	outByte(EOP); pageStarted = false;
    }

    public final void		setChar(int code) {
        checkPage();
        if (0 <= code && code < SET1 - SET) outByte(SET + code);
	else if (code < 256) { outByte(SET1); outByte(code); }	// STRANGE
	else outPack(SET1, code);
    }

    public final void		putChar(int code)
	{ checkPage(); outPack(PUT1, code); }

    public final void		setRule(int h, int w)
	{ checkPage(); outByte(SET_RULE); outWord(h); outWord(w); }

    public final void		putRule(int h, int w)
	{ checkPage(); outByte(PUT_RULE); outWord(h); outWord(w); }

    /* STRANGE
     * why not use outPack() ?
     */
    public void		setSpecial(byte[] spec) {
        checkPage();
        int		len = spec.length;
	if ((len & ~0xff) != 0) { outByte(SPEC1 + 3); outWord(len); }
	else { outByte(SPEC1); outByte(len); }
	outBytes(spec);
    }

    public void		setFont(int font) {
        checkPage();
        if (0 <= font && font < FNT1 - FNT) outByte(FNT + font);
	else outPack(FNT1, font);
	//XXX maybe check if the font is defined
    }

    private static class	FontDef {

	static final FontDef	NULL = null;

	final int	chksum;
	final int	size;
	final int	dsize;
	final byte[]	dir;
	final byte[]	name;

	FontDef(int chksum, int size, int dsize,
		       byte[] dir, byte[] name) {
	    this.chksum = chksum; this.size = size;
	    this.dsize = dsize; this.dir = dir; this.name = name;
	    if (dir.length > 255 || name.length > 255)
	        throw new RuntimeException("font name too long in DVI");
	}

	boolean		equals(int chksum, int size, int dsize,
			       byte[] dir, byte[] name) {
	    return (  this.chksum == chksum
	    	   && this.size == size && this.dsize == dsize
	    	   && Arrays.equals(this.dir, dir)
	    	   && Arrays.equals(this.name, name)  );

	}

    }

    protected void	defFont(int font, FontDef def) {
	outPack(FNT_DEF1, font); outWord(def.chksum);
	outWord(def.size); outWord(def.dsize);
	outByte(def.dir.length); outByte(def.name.length);
	outBytes(def.dir); outBytes(def.name);
    }

    private HashMap	fonts = new HashMap();

    public void		defFont(int font, int chksum, int size, int dsize,
				byte[] dir, byte[] name) {
	Integer		key = new Integer(font);
	FontDef		val = (FontDef) fonts.get(key);
	if (val == FontDef.NULL) {
	    val = new FontDef(chksum, size, dsize, dir, name);
	    fonts.put(key, val); defFont(font, val);
	} else if (!val.equals(chksum, size, dsize, dir, name))
	    throw new RuntimeException("font redefinition in DVI");
    }

    /* STRANGE
     * why it is not computed in normal way but suplied here?
     */
    public void		close(int maxH, int maxW) {
        if (pageStarted) endPage();
	int		loc = currLoc();
	outByte(POST); outWord(lastPageLoc);
	outWord(num); outWord(den); outWord(mag);
	outWord(maxH); outWord(maxW);
	outHalf(maxPushLevel); outHalf(numOfPages);
	Object[]	keys = fonts.keySet().toArray();
	Arrays.sort(keys);
	int		i = keys.length;
	while (i-- > 0)
	    defFont(((Integer) keys[i]).intValue(),
		    (FontDef) fonts.get(keys[i]));
	outByte(POST_POST); outWord(loc); outByte(ID_BYTE);
	i = 4 + (5 - (currLoc() + 1 & 3) & 3);
	while (i-- > 0) outByte(DAVID_FUCHS);
	try { out.close(); }
	catch (IOException e) { trouble = true; }
    }

    private void	outHalf(int i)
	{ outByte(i >>>  8 & 0xff); outByte(i & 0xff); }

    private void	outWord(int i) {
        outByte(i >>> 24 & 0xff); outByte(i >>> 16 & 0xff);
        outByte(i >>>  8 & 0xff); outByte(i & 0xff);
    }

    private void	outPack(int cmd, int par) {
	int	i = 3;
        int	abs = (par < 0) ? -par : par;
	while (i > 0 && (abs >>> 8 * i - 1) == 0) i--;
	outByte(cmd + i);
	do outByte(par >>> 8 * i & 0xff); while (i-- > 0);
    }

    private boolean	changePack(int loc, int orig, int cmd) {
        int		there = out.get(loc);
	return (there >= 0 && out.set(loc, cmd + there - orig));
    }

    private void	outByte(int b) {
	try { out.write(b); }
	catch (IOException e) { trouble = true; }
    }

    private void	outBytes(byte[] buf) {
	try { out.write(buf); }
	catch (IOException e) { trouble = true; }
    }

    private void	outBytes(byte[] buf, int start, int end) {
	try { out.write(buf, start, end); }
	catch (IOException e) { trouble = true; }
    }

    private int		currLoc() { return out.pos(); }
    private boolean	dropLastByte() { return out.dropLast(); }

}
