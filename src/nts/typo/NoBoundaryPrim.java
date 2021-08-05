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
// Filename: nts/typo/NoBoundaryPrim.java
// $Id: NoBoundaryPrim.java,v 1.1.1.1 2000/11/13 02:00:21 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Command;
import nts.command.Token;
import nts.io.CharCode;
import nts.node.WordBuilder;

public class NoBoundaryPrim extends BuilderPrim {

  public NoBoundaryPrim(String name) {
    super(name);
  }

  /* TeXtp[1030] */
  public final Action NORMAL =
      new Action() {
        public void exec(Builder bld, Token src) {
          Token tok = nextExpToken();
          Command cmd = meaningOf(tok);
          CharCode code = cmd.charCodeToAdd();
          if (code != CharCode.NULL) {
            if (getConfig().getBoolParam(BOOLP_TRACING_COMMANDS)) traceCommand(cmd);
            WordBuilder word =
                getCurrFontMetric().getWordBuilder(APPENDER, false, bld.willBeBroken());
            bld.adjustSpaceFactor(code.spaceFactor());
            fixLanguage(bld);
            if (word.add(code)) appendCharsTo(bld, word);
            else charWarning(getCurrFontMetric(), code);
          } else cmd.execute(tok);
        }
      };

  public boolean isNoBoundary() {
    return true;
  }
}
