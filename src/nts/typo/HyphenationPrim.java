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
// Filename: nts/typo/HyphenationPrim.java
// $Id: HyphenationPrim.java,v 1.1.1.1 2000/06/12 21:39:52 ksk Exp $
package nts.typo;

import java.util.Vector;
import nts.command.Command;
import nts.command.Token;
import nts.io.CharCode;

public class HyphenationPrim extends TypoAssignPrim {

  public HyphenationPrim(String name) {
    super(name);
  }

  /* STRANGE
   * \global\hyphenation is allowed but has no effect
   * STRANGE
   * in one error message \hyphenation is passed in other it is literal
   */
  /* TeXtp[934] */
  protected void assign(Token src, boolean glob) {
    scanLeftBrace();
    StringBuffer buf = new StringBuffer();
    Vector hyphBuf = new Vector();
    int index = 0;
    for (; ; ) {
      Token tok = nextExpToken();
      Command cmd = meaningOf(tok);
      CharCode code = cmd.charCodeToAdd();
      if (code != CharCode.NULL) {
        if (code.match('-')) hyphBuf.add(Integer.valueOf(index));
        else {
          char letter = code.toCanonicalLetter();
          if (letter != CharCode.NO_CHAR) {
            buf.append(letter);
            index++;
          } else error("NonLetterInHyph");
        }
      } else if (cmd.isSpacer() || cmd.isRightBrace()) {
        if (index > 1) {
          int[] positions = new int[hyphBuf.size()];
          for (int i = 0; i < positions.length; i++)
            positions[i] = ((Integer) hyphBuf.get(i)).intValue();
          getTypoConfig().getLanguage().setHyphException(buf.toString(), positions);
          index = 0;
          buf.setLength(0);
          hyphBuf.clear();
        }
        if (cmd.isRightBrace()) break;
      } else error("ImproperHyphen", this);
    }
  }
}
