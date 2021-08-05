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
// Filename: nts/command/InputLineTokenizer.java
// $Id: InputLineTokenizer.java,v 1.1.1.1 2000/01/27 14:31:13 ksk Exp $
package	nts.command;

import	nts.base.BoolPar;
import	nts.io.CharCode;
import	nts.io.Name;
import	nts.io.InputLine;

/**
 * The |InputLineTokenizer| reads internal character codes from a |LineReader| and
 * converts them into |Token|s.
 */
public class	InputLineTokenizer extends Tokenizer {

    public static final InputLineTokenizer	NULL = null;

    /** The current input line */
    private InputLine		line;

    /** The parametrization object */
    private TokenMaker		maker;

    private String		desc;

    /**
     * Creates an |InputLineTokenizer| with given input and parametrization
     * object.
     * @param	input the underlying reader.
     * @param	maker the parametrization object.
     */
    public InputLineTokenizer(InputLine line, TokenMaker maker, String desc)
	{ this.line = line; this.maker = maker; this.desc = desc; }

    /** Value of |state| when the scanner is on the begining of line */
    private static final byte	NEW_LINE = 0;

    /** Value of |state| when the scanner is in the middle of line */
    private static final byte	MID_LINE = 1;

    /** Value of |state| when the scanner is ignoring blank spaces */
    private static final byte	SKIP_BLANKS = 2;

    /**
     * The state of the scanner.
     * Can be |NEW_LINE| or |MID_LINE| or |SKIP_BLANKS|.
     */
    private byte		state = NEW_LINE;

    public void		setMidLine() { state = MID_LINE; }

    /**
     * Gets the next |Token| from the input line.
     * @param	canExpand boolean output parameter querying whether the
     *			  acquired |Token| can be expanded (e.g. was not
     *			  preceded by \noexpand).
     * @return	the next token from the input or |Token.NULL| if the input
     *		is finished.
     */
    public Token	nextToken(BoolPar canExpand)
	{ canExpand.set(true); return nextToken(); }

    /**
     * The scanning category indicating that the corresponding character is
     * the escape character --- i.e. it starts a control sequence.
     */
    public static final byte	ESCAPE = 0;

    /**
     * The scanning category indicating that the coresponding character can be
     * a part of multi-letter control sequence.
     */
    public static final byte	LETTER = 1;

    /**
     * The scanning category indicating that the coresponding character is a
     * blank space --- i.e. the following characters of the same category are
     * ignored.
     */
    public static final byte	SPACER = 2;

    /**
     * The scanning category similar to |SPACER|, only consequent characters
     * of this category generate blank lines (paragraph ends).
     */
    public static final byte	ENDLINE = 3;

    /**
     * The scanning category indicating that the corresponding character and
     * the rest of line should be ignored.
     */
    public static final byte	COMMENT = 4;

    /**
     * The scanning category indicating that the corresponding character
     * should be ignored.
     */
    public static final byte	IGNORE = 5;

    /**
     * The scanning category indicating that no special processing should be
     * taken for corresponding character.
     */
    public static final byte	OTHER = 6;

    /**
     * A |TokenMaker| is used for parametrization of tokenization process.
     * It provides parameters which are dependent on current state of the
     * language interpreter.
     */
    public interface TokenMaker {

	/**
	 * Gives the scanning category of given internal character code.
	 * The return value must be one of: |ESCAPE|, |LETTER|, |EXPAND|,
	 * |SPACER|, |ENDLINE|, |COMMENT|, |IGNORE|, |OTHER|.
	 * @param	code internal character code.
	 * @return	scanning category of |code|.
	 */
	byte		scanCat(CharCode code);

	/**
	 * Makes a control sequence |Token| with given name.
	 * @param	name the name of the control sequence.
	 * @return	the control sequence |Token|.
	 */
	Token		make(Name name);

	/**
	 * Makes a character |Token| for given internal character code.
	 * @param	code the internal character code.
	 * @return	the character |Token|.
	 */
	Token		make(CharCode code);

	/**
	 * Makes a |Token| corresponding to space.
	 * @return	the space |Token|.
	 */
	Token		makeSpace();

	/**
	 * makes a |Token| corresponding to blank line (paragraph end).
	 * @return	the paragraph |Token|.
	 */
	Token		makePar();

    }

    /**
     * Gives the |Token| constructed from the next internal character code
     * read from input line or from a sequence of such codes.
     * @return	the next token from the input line or |Token.NULL| if
     *		the input line is finished.
     */
    public Token	nextToken() {
	for (;;) {
	    CharCode	code = line.getNext();
	    if (code == InputLine.EOL)
		return Token.NULL;
	    else switch (maker.scanCat(code)) {
		case ESCAPE:
		    code = line.getNext();
		    if (code == InputLine.EOL) {
			/* empty control sequence */
			return maker.make(new Name());
		    } else {
			int	cat = maker.scanCat(code);
			if (cat != LETTER) {
			    /* non letter control sequence */
			    state = (cat == SPACER) ? SKIP_BLANKS : MID_LINE;
			    return maker.make(new Name(code));
			} else {
			    /* multiletter control sequence */
			    Name.Buffer	buf = new Name.Buffer();
			    buf.append(code);
			    while (  (code = line.peekNext()) != InputLine.EOL
				  && maker.scanCat(code) == LETTER  )
				{ buf.append(code); line.getNext(); }
			    state = SKIP_BLANKS;
			    return maker.make(buf.toName());
			}
		    }
		    // break;	/* unreachable statement */
		case ENDLINE:
		    line.skipAll();
		    if (state == MID_LINE) return maker.makeSpace();
		    else if (state == NEW_LINE) return maker.makePar();
		    break;
		case SPACER:
		    if (state == MID_LINE) {
			state = SKIP_BLANKS;
			return maker.makeSpace();
		    }
		    break;
		case COMMENT:
		    line.skipAll();
		    break;
		case IGNORE:
		    break;
		default:
		    state = MID_LINE;
		    return maker.make(code);
	    }
	}
    }

    public boolean	finished() { return line.empty(); }

    public boolean	finishedInsert() { return line.empty(); }

    public int		show(ContextDisplay disp, boolean force, int lines) {
        disp.normal().startLine().add(desc);
	line.addContext(disp.left(), disp.right(), true);
	disp.show();
        return 1;
    }

}
