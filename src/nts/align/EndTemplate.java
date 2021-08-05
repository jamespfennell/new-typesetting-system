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
// Filename: nts/align/EndTemplate.java
// $Id: EndTemplate.java,v 1.1.1.1 2001/04/04 23:03:16 ksk Exp $
package nts.align;

import nts.command.Expandable;
import nts.command.Token;
import nts.io.Log;

public class EndTemplate extends Expandable {

  private final Token body;

  public EndTemplate(Token body) {
    this.body = body;
  }

  /* STRANGE
   * backing up the body token is the method used in expand procedure in TeX.
   * However in get_x_token it is bypassed and the body is returned straight
   * away without backing up. That makes differences when showing context.
   * Keeping compatibility with TeX would require boolean parameter to
   * nextExpToken and another method to Command interface which is clearly
   * too expensive for such a rare case.
   * The sentence: "The |get_x_token| procedure is equivalent to two
   * consecutive procedure calls: |get_next; x_token|." in TeXtp[381]
   * is therefore not valid.
   */
  /* TeXtp[375,380] */
  public void doExpansion(Token src) {
    backToken(body);
  }

  public boolean isOuter() {
    return true;
  }

  /* TeXtp[296,1295] */
  public void addExpandable(Log log, boolean full) {
    log.addEsc("outer").add(" endtemplate");
    if (full) {
      log.add(':').endLine();
    }
  }
}
