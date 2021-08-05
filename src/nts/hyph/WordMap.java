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
// Filename: nts/hyph/WordMap.java
// $Id: WordMap.java,v 1.1.1.1 2000/06/13 09:58:22 ksk Exp $
package nts.hyph;

import java.io.Serializable;
import java.util.Enumeration;

public interface WordMap extends Serializable {

  WordMap NULL = null;

  interface Seeker {
    void reset();

    boolean isValid();

    void seek(char code);

    Object get();
  }

  Object get(String word);

  Object put(String word, Object value);

  Seeker seeker();

  Enumeration entries();

  WordMap packed();
}
