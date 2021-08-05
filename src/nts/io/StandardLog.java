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
// Filename: nts/io/StandardLog.java
// $Id: StandardLog.java,v 1.1.1.1 2001/02/06 12:37:07 ksk Exp $
package	nts.io;

/**
 * Object which performs printing to the terminal and log file.
 */
public class	StandardLog	implements Log {

    public interface	Escape { CharCode	getEscape(); }

    protected final LineOutput		out;
    protected final Escape		esc;

    public StandardLog(LineOutput out, Escape esc)
	{ this.out = out; this.esc = esc; }

    public final LineOutput	getOutput() { return out; }
    public final Escape		getEscape() { return esc; }

    public final Log	add(char ch) { out.add(ch); return this; }
    public final Log	add(char ch, int count)
	{ while (count-- > 0) out.add(ch); return this; }
    public final Log	add(String str) { out.add(str); return this; }
    public final Log	add(CharCode code) { out.add(code); return this; }
    public final Log	endLine() { out.endLine(); return this; }
    public final Log	startLine() { out.startLine(); return this; }
    public final Log	flush() { out.flush(); return this; }
    public final void	close() { out.close(); }

    public final Log	add(boolean val) { return add(String.valueOf(val)); }
    public final Log	add(int num) { return add(String.valueOf(num)); }

    public final Log	addEsc() {
        CharCode	escape = esc.getEscape();
	return (escape != CharCode.NULL) ? add(escape) : this;
    }

    public final Log	addEsc(String str) { addEsc(); return add(str); }

    public final Log	add(Loggable x) { x.addOn(this); return this; }
    public Log		add(Loggable[] array)
	{ return add(array, 0, array.length); }
    public Log		add(Loggable[] array, int offset, int length) {
	for (int i = 0; i < length; add(array[offset + i++]));
	return this;
    }

    public final Log	resetCount() { out.resetCount(); return this; }
    public final int	getCount() { return out.getCount(); }
    public final Log	voidCounter()
	{ return new StandardLog(out.voidCounter(), esc); }

    public final Log	sepRoom(int count) {
	if (!out.stillFits(count)) out.endLine();
	else if (!out.isStartLine()) out.add(' ');
	return this;
    }

}
