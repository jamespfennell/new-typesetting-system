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
// Filename: nts/command/LineInputReadInput.java
// $Id: LineInputReadInput.java,v 1.1.1.1 1999/05/31 11:18:48 ksk Exp $
package	nts.command;

import	nts.io.InputLine;
import	nts.io.LineInput;

public class	LineInputReadInput	implements ReadInput {

    public interface	InputHandler {
	InputLine	emptyLine();
	Tokenizer	makeTokenizer(InputLine line, String desc,
				      boolean addEolc);
    }

    /** The underlying line oriented input reader */
    private LineInput		input;

    /** The parametrization object */
    private InputHandler	inpHandler;

    private String		desc;

    /**
     * Creates an |LineInputReadInput| with given input and parametrization
     * object.
     * @param	input the underlying reader.
     * @param	inpHandler the parametrization object.
     */
    public LineInputReadInput(LineInput input, InputHandler inpHandler,
			      int num) {
	this.input = input;
	this.inpHandler = inpHandler;
	desc = "<read " + num + "> ";
    }

    /**
     * Closes the underlying |LineInput|.
     */
    public void		close() { input.close(); }

    public Tokenizer	nextTokenizer(Token def, int ln) {
	InputLine	line = input.readLine();
	return (line == LineInput.EOF) ? Tokenizer.NULL
	     : inpHandler.makeTokenizer(line, desc, true);
    }

    public Tokenizer	emptyLineTokenizer()
	{ return inpHandler.makeTokenizer(inpHandler.emptyLine(), desc, true); }

}
