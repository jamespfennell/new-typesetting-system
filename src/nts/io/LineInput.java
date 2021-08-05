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
// Filename: nts/io/LineInput.java
// $Id: LineInput.java,v 1.1.1.1 1999/04/20 21:10:23 ksk Exp $
package	nts.io;

import	java.io.Reader;
import	java.io.LineNumberReader;
import	java.io.IOException;

/**
 * |LineInput| reads the input line by line and performs the conversions from
 * input character codes to their internal representation.
 */
public class	LineInput {

    /** Value returned by |readLine| when the input is finished */
    public static final InputLine	EOF = InputLine.NULL;

    /** Underlying reader */
    private LineNumberReader		in;

    /** Input character mapper */
    private InputLine.Mapper		mapper;

    private boolean			trouble = false;

    /**
     * Creates |LineInput| with given |LineNumberReader| and |InputLine.Mapper|.
     * @param	in the LineNumberReader to read from.
     * @param	mapper the mapper for character conversions etc.
     */
    public LineInput(LineNumberReader in, InputLine.Mapper mapper)
        { this.in = in; this.mapper = mapper; }

    /**
     * Creates |LineInput| with given |Reader| and |InputLine.Mapper|.
     * @param	in the Reader to read from.
     * @param	mapper the mapper for character conversions etc.
     */
    public LineInput(Reader in, InputLine.Mapper mapper)
        { this(new LineNumberReader(in), mapper); }

    /**
     * Releases the system resources associated with the |LineInput|.
     */
    public void		close() {
        try { in.close(); }
	catch (IOException e) { trouble = true; }
    }

    /**
     * Reads one line from the input, remove trailing characters which should
     * be ignored (usually spaces) and appends end line character if defined.
     * @param	addEolc flag indicating whether the |endLineChar| (if active)
     *		should be added.
     * @return	an object representing the current input line
     *		or |EOF| if the input is finished.
     */
    public InputLine	readLine() {
	String		str;
	try { str = in.readLine(); }
	catch (IOException e) {
	    str = null; trouble = true;
	    System.err.println("I/O error: " + e);
	}
	return (str != null) ? new InputLine(str, mapper) : EOF;
    }

    public int		getLineNumber() { return in.getLineNumber(); }

}
