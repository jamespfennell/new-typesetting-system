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
// Filename: nts/typo/ParPrim.java
// $Id: ParPrim.java,v 1.1.1.1 2001/03/20 09:56:24 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Token;

public class ParPrim extends BuilderPrim {

  public ParPrim(String name) {
    super(name);
  }

  /* TeXtp[1094] */
  public final Action NORMAL =
      new Action() {
        public void exec(Builder bld, Token src) {
          getTypoConfig().checkParagraph(src);
          Paragraph.finish();
          getBld().buildPage();
        }
      };

  /* TeXtp[1094] */
  public final Action RESET =
      new Action() {
        public void exec(Builder bld, Token src) {
          getTypoConfig().resetParagraph();
          bld.buildPage();
        }
      };
}
