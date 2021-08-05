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
// Filename: nts/tfm/BadTeXFmException.java
// $Id: BadTeXFmException.java,v 1.1.1.1 1999/07/28 08:07:15 ksk Exp $
package nts.tfm;

import java.io.IOException;

/**
 * The exception thrown during loading if the tfm file is malformed and there is no `softer' way how
 * to indicate error.
 */
public class BadTeXFmException extends IOException {

  /** Construct a |BadTeXFmException| with no detail message */
  public BadTeXFmException() {
    super();
  }

  /**
   * Construct a |BadTeXFmException| with the specified detail message.
   *
   * @param s the detail message.
   */
  public BadTeXFmException(String s) {
    super(s);
  }
}
