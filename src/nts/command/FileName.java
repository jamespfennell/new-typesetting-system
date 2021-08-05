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
// Filename: nts/command/FileName.java
// $Id: FileName.java,v 1.1.1.1 2001/03/06 20:33:50 ksk Exp $
package nts.command;

import java.io.Serializable;
import nts.io.CharCode;
import nts.io.Loggable;
import nts.io.Name;

public interface FileName extends Serializable, Loggable {

  FileName NULL = null;

  Name baseName();

  String getPath();

  void setPath(String path);

  void append(char chr);

  void append(String str);

  int accept(CharCode code);

  boolean addDefaultExt(String ext);

  FileName copy();
}
