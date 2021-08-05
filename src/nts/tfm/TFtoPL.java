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
// Filename: nts/tfm/TFtoPL.java
// $Id: TFtoPL.java,v 1.1.1.1 1999/07/28 08:07:15 ksk Exp $
package	nts.tfm;

import	java.io.IOException;
import	java.io.PrintWriter;

/**
 * The |TFtoPL| class simply puts other useful classes together and behave
 * like traditional tftopl program.
 *
 * So far it does not any special locating of source tfm file --- it simply
 * tries to open the file given as argument. The output is directed to the
 * standard output. It should be improved if the strict compatibility with
 * tftopl is required.
 */

/*
 * The program was tested to give the same output as tftopl on tfm file
 * compiled from trip.pl in \TeX\ trip distribution and on about thousand
 * other tfm files. It won't be compatible in case of error messages because
 * the classes used may discover the errors in tfm file in different order
 * than tftopl. Some errors cannot happen (insuficient prealocated memory)
 * and some new can be detected (empty input file).
 */

public class	TFtoPL {

    /** The class cannot be instantiated */
    private TFtoPL() { }

    /*
     * We must provide the object to print error messages during loading.
     * The form of error messages is the same as in tftopl program although
     * probably not completely and it should be still checked.
     * See the corresponding functions in TFtoPL[20,47].
     */

    /**
     * |Diag| implements |TeXFmDiagnostic| to provide
     * error messaging during loading.
     */
    private static class	Diag	implements TeXFmDiagnostic {

        /**
	 * Print a fatal error message.
	 * @param	s the text of the error message.
	 */
	public void	fatal(String s) {
	    System.err.println(s);
	    System.err.println("Sorry, but I can't go on;"
	    		       + " are you sure this is a TFM?");
	}

        /**
	 * Print a normal error message.
	 * @param	s the text of the error message.
	 */
	public void	error(String s) {
	    System.err.print("Bad TFM file: ");
	    System.err.println(s);
	}

        /**
	 * Print a warning message.
	 * @param	s the text of the warning message.
	 */
	public void	warning(String s) {
	    System.err.println(s);
	}

	/**
	 * Returns |false| to indicate that the loader should proceed further
	 * after normal (non fatal) errors.
	 * @return	|false|.
	 */
	public boolean	abortOnError() { return false; }

    }

    /**
     * The |main| program tries to open the file which name given as the first
     * argument, to load the content of file as a TeX Font Metric and list out
     * its representation as a property list.
     *
     * @exception	IOException if an I/O error ocurs or if the tfm file
     *			is malformed.
     */
    public static void	main(String[] args) throws IOException {
        if (args.length > 0) {
	    PrintWriterPLDumper	dmp = new PrintWriterPLDumper(
					new PrintWriter(System.out, true));
	    TeXFm.readFrom(args[0], new Diag()).dump(dmp);
	    dmp.closeOutput();
	}
    }

}
