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
// Filename: nts/typo/FontNamePrim.java
// $Id: FontNamePrim.java,v 1.1.1.1 1999/06/12 14:46:52 ksk Exp $
package nts.typo;

import nts.command.ExpandablePrim;
import nts.command.Token;
import nts.command.TokenList;
import nts.command.TokenListOutput;
import nts.io.Log;
import nts.node.FontMetric;

/* TeXtp[470] */
public class FontNamePrim extends ExpandablePrim {

  public FontNamePrim(String name) {
    super(name);
  }

  public void expand(Token src) {
    FontMetric metric = TypoCommand.scanFontMetric();
    TokenList.Buffer buf = new TokenList.Buffer();
    Log log = makeLog(new TokenListOutput(buf));
    metric.addDescOn(log);
    insertList(buf.toTokenList());
  }
}
