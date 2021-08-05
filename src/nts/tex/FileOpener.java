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
// Filename: nts/tex/FileOpener.java
// $Id: FileOpener.java,v 1.1.1.1 2001/02/22 01:16:50 ksk Exp $
package nts.tex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import nts.command.FileName;

public interface FileOpener {
  FileOpener NULL = null;

  InputStream openForReading(FileName name, String format, boolean mustExist) throws IOException;

  OutputStream openForWriting(FileName name, String format) throws IOException;
}
