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
// Filename: nts/align/PreambleScanning.java
// $Id: PreambleScanning.java,v 1.1.1.1 2001/03/20 03:02:17 ksk Exp $
package nts.align;

import java.util.Vector;
import nts.base.Glue;
import nts.command.BaseToksChecker;
import nts.command.BraceNesting;
import nts.command.Command;
import nts.command.RightBraceToken;
import nts.command.Token;
import nts.command.TokenList;

public class PreambleScanning extends BaseToksChecker implements BraceNesting {

  private Vector entries = new Vector();
  private TokenList.Buffer buf = new TokenList.Buffer();
  private TokenList uPart = TokenList.NULL;
  private int loopIndex = -1;
  private int braceNesting = 0;
  private final TokenList crAndBrace;

  public PreambleScanning(Token source, Token frozenCr) {
    super("OuterInPreamble", "EOFinPreamble", source);
    Token[] toks = {frozenCr, RightBraceToken.TOKEN};
    crAndBrace = new TokenList(toks);
  }

  public boolean balanced() {
    return (braceNesting == 0);
  }

  public void append(Token tok) {
    buf.append(tok);
  }

  public void finishHalf() {
    uPart = buf.toTokenList();
    buf = new TokenList.Buffer();
  }

  public void finishRecord(Glue skip) {
    entries.add(new Preamble.Entry(uPart, buf.toTokenList(), skip));
    uPart = TokenList.NULL;
    buf = new TokenList.Buffer();
  }

  public boolean setLoopIndex() {
    if (loopIndex >= 0) return false;
    loopIndex = entries.size();
    return true;
  }

  public Preamble toPreamble(Glue firstSkip, String skipName, Token endTemplate) {
    Preamble.Entry[] records = new Preamble.Entry[entries.size()];
    entries.copyInto(records);
    return new Preamble(firstSkip, skipName, endTemplate, records, loopIndex);
  }

  public void adjust(int count) {
    braceNesting += count;
  }

  /* TeXtp[339] */
  // XXX align_state [339]
  protected void tryToFix() {
    Command.insertList(crAndBrace);
  }

  protected void reportRunAway() {
    Command.runAway("preamble", buf);
  }
}
