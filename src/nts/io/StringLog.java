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
// Filename: nts/io/StringLog.java
// $Id: StringLog.java,v 1.1.1.1 2001/02/06 12:36:42 ksk Exp $
package nts.io;

public class StringLog extends StandardLog {

  public StringLog(Escape esc, StringLineOutput out) {
    super(out, esc);
  }

  public StringLog(Escape esc) {
    super(new StringLineOutput(), esc);
  }

  public String toString() {
    return out.toString();
  }
}
