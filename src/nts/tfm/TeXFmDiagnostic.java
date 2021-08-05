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
// Filename: nts/tfm/TeXFmDiagnostic.java
// $Id: TeXFmDiagnostic.java,v 1.1.1.1 1999/06/11 14:32:07 ksk Exp $
package	nts.tfm;

/**
 * The interface which an object must implement if it is supplied for
 * reporting error messages.
 */
public interface	TeXFmDiagnostic {

    /**
     * Report a fatal error message.
     * @param	s the text of message.
     */
    void		fatal(String s);

    /**
     * Report a recoverable error message.
     * @param	s the text of message.
     */
    void		error(String s);

    /**
     * Report a warning message.
     * @param	s the text of message.
     */
    void		warning(String s);

    /**
     * Tells if the loading should be aborted also on recoverable errors
     * or on the fatals errors only.
     * @return	|true| if the loading has to be aborted on recoverable
     *		errors too.
     */
    boolean		abortOnError();

}

