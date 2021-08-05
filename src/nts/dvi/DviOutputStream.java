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
// Filename: nts/dvi/DviOutputStream.java
// $Id: DviOutputStream.java,v 1.1.1.1 1999/09/03 17:16:55 ksk Exp $
package	nts.dvi;

import	java.io.IOException;
import	java.io.OutputStream;
import	java.io.FilterOutputStream;

public class	DviOutputStream	extends FilterOutputStream {

    private static final int	DEFAULT_BUF_SIZE = 0x4000;

    private final int			half;
    private final byte[]		buffer;

    private int			offset = 0;
    private int			gone = 0;
    private int			ptr = 0;
    private int			limit;

    public DviOutputStream(OutputStream out, int size) {
        super(out);
	if (size <= 0) size = DEFAULT_BUF_SIZE;
	size = size + 7 & ~7; half = size / 2;
	buffer = new byte[size]; limit = size;
    }

    public DviOutputStream(OutputStream out) { this(out, 0); }

    /* STRANGE
     * Buffer could be flushed on next write leaving larger space for set().
     */
    /* TeXtp[598] */
    public void		write(int b) throws IOException {
        buffer[ptr++] = (byte) b;
	if (ptr == limit) {
	    if (limit == half) limit = buffer.length;
	    else { offset += ptr; ptr = 0; limit = half; }
	    out.write(buffer, ptr, half);
	    gone += half;
	}
    }

    /* TeXtp[599] */
    public void		flush() throws IOException {
        if (limit == half)
	    { out.write(buffer, half, half); gone += half; }
	if (ptr > 0) {
	    out.write(buffer, 0, ptr); gone += ptr;
	    offset += ptr; ptr = 0;
	}
	out.flush(); limit = buffer.length;
    }

    /* STRANGE
     * It could be droped almost always
     * (except after flush() and just after starting).
     */
    public boolean	dropLast() {
        if (ptr > 0) { ptr--; return true; }
	else return false;
    }

    public int		pos() { return offset + ptr; }

    public boolean	present(int pos)
	{ return (pos >= gone && pos < offset + ptr); }

    public int		get(int pos) {
        if (pos >= gone && (pos -= offset) < ptr) {
	    if (pos < 0) pos += buffer.length;
	    return (buffer[pos] & 0xff);
	}
	return -1;
    }

    public boolean	set(int pos, int b) {
        if (pos >= gone && (pos -= offset) < ptr) {
	    if (pos < 0) pos += buffer.length;
	    buffer[pos] = (byte) b; return true;
	}
	return false;
    }

}
