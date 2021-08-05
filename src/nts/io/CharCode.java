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
// Filename: nts/io/CharCode.java
// $Id: CharCode.java,v 1.1.1.1 2001/02/03 07:20:48 ksk Exp $
package nts.io;

import java.io.Serializable;
import nts.base.Num;

public interface CharCode extends Serializable, Loggable {

  /** The reference to non existent internal character code */
  CharCode NULL = null;

  /** Symbolic constant for non ascii caharacter code */
  char NO_CHAR = 0xffff;

  public interface CodeWriter {
    void writeCode(CharCode code);
  }

  public interface CharWriter {
    void writeChar(char chr);
  }

  public interface Maker {
    CharCode make(char chr);

    CharCode make(int num);

    boolean isNewLine(char chr);

    void writeExpCodes(char chr, CodeWriter out);

    void writeExpChars(char chr, CharWriter out);
  }

  char toChar();

  int numValue();

  char toCanonicalLetter();

  boolean match(CharCode x);

  boolean match(char c);

  boolean match(int num);

  boolean match(Num num);

  CharCode toLowerCase();

  CharCode toUpperCase();

  int spaceFactor();

  int mathCode();

  int delCode();

  boolean isLetter();

  boolean isEscape();

  boolean isEndLine();

  boolean isNewLine();

  boolean startsExpand();

  boolean startsFileExt();

  void writeExpCodes(CodeWriter out);

  void writeExpChars(CharWriter out);

  void writeRawChars(CharWriter out);
}
