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
// Filename: nts/typo/PatternsPrim.java
// $Id: PatternsPrim.java,v 1.1.1.1 2000/06/13 23:52:24 ksk Exp $
package nts.typo;

import java.util.Vector;
import nts.command.Command;
import nts.command.Prim;
import nts.command.Token;
import nts.io.CharCode;
import nts.node.Language;

public class PatternsPrim extends TypoAssignPrim {

  public PatternsPrim(String name) {
    super(name);
  }

  private static final char WORD_BOUNDARY = Language.WORD_BOUNDARY;

  /* STRANGE
   * \global\patterns is allowed but has no effect
   * STRANGE
   * note the asymetry of the error handling for late patterns.
   * The method for loaded format is not robust.
   */
  /* TeXtp[960,1252] */
  protected void assign(Token src, boolean glob) {
    Config cfg = getTypoConfig();
    if (!cfg.patternsAllowed()) {
      if (getConfig().formatLoaded()) {
        error("CantLoadPatterns");
        while (!nextRawToken().matchRightBrace())
          ;
      } else {
        error("LatePatterns", this);
        Prim.scanTokenList(src, false);
      }
      return;
    }
    scanLeftBrace();
    StringBuffer buf = new StringBuffer();
    Vector valBuf = new Vector();
    boolean digitExpected = true;
    for (; ; ) {
      Token tok = nextExpToken();
      Command cmd = meaningOf(tok);
      CharCode code = cmd.charCode();
      if (code != CharCode.NULL) {
        CharCode toAdd = cmd.charCodeToAdd();
        if (toAdd != CharCode.NULL && code.match(toAdd)) {
          char chr = code.toChar();
          if (digitExpected && chr >= '0' && chr <= '9') {
            int index = buf.length();
            if (index >= valBuf.size()) valBuf.setSize(index + 1);
            valBuf.set(index, Integer.valueOf(chr - '0'));
            digitExpected = false;
          } else {
            char letter;
            if (code.match('.')) letter = WORD_BOUNDARY;
            else {
              letter = code.toCanonicalLetter();
              if (letter == CharCode.NO_CHAR) {
                error("NonLetter");
                letter = WORD_BOUNDARY;
              }
            }
            buf.append(letter);
            digitExpected = true;
          }
          continue;
        }
      }
      if (cmd.isSpacer() || cmd.isRightBrace()) {
        int size = buf.length();
        if (size > 0) {
          int[] values = new int[valBuf.size()];
          for (int i = 0; i < values.length; i++) {
            Integer val = (Integer) valBuf.get(i);
            values[i] = (val != null) ? val.intValue() : 0;
          }
          if (values.length > 0 && size > 0) {
            if (buf.charAt(0) == WORD_BOUNDARY) values[0] = 0;
            if (buf.charAt(size - 1) == WORD_BOUNDARY && values.length > size) values[size] = 0;
          }
          if (!cfg.getLanguage().setHyphPattern(buf.toString(), values)) error("DupPattern");
          buf.setLength(0);
          valBuf.clear();
        }
        if (cmd.isRightBrace()) break;
        digitExpected = true;
      } else error("BadPatterns", this);
    }
  }
}
