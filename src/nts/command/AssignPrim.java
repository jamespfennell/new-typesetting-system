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
// Filename: nts/command/AssignPrim.java
// $Id: AssignPrim.java,v 1.1.1.1 2001/02/01 16:01:45 ksk Exp $
package nts.command;

import nts.io.Log;

/** Abstract ancestor of each assign primitive but \def. */
public abstract class AssignPrim extends PrefixPrim {

  /**
   * Creates a new AssignPrim with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the AssignPrim
   */
  protected AssignPrim(String name) {
    super(name);
  }

  /**
   * Performs itself in the process of interpretation of the macro language after sequence of prefix
   * commands.
   *
   * @param src source token for diagnostic output.
   * @param prefixes accumulated code of prefixes.
   */
  public final void exec(Token src, int prefixes) {
    beforeAssignment(this, prefixes);
    assign(src, globalAssignment(prefixes));
    afterAssignment();
  }

  /**
   * Performs the assignment.
   *
   * @param src source token for diagnostic output.
   * @param glob indication that the assignment is global.
   */
  protected abstract void assign(Token src, boolean glob);

  /* TeXtp[1226] */
  public static TokenList scanToks(Token src, boolean wrapup) {
    TokenList.Buffer buf = new TokenList.Buffer(30);
    Token tok = nextNonRelax();
    Command cmd = meaningOf(tok);
    if (!cmd.isLeftBrace()) {
      if (cmd.hasToksValue()) return cmd.getToksValue();
      backToken(tok);
      error("MissingLeftBrace");
    }
    /* STRANGE
     * the descriptor ("text") below is included in the error message
     * ("OuterInToks") too. The same holds for "definition" and
     * "preamble", the "argument"/"use" is the only exception.
     */
    scanBalanced(buf, new ScanToksChecker("OuterInToks", "EOFinToks", "text", buf, src));
    if (wrapup && buf.length() != 0) {
      buf.insertTokenAt(LeftBraceToken.TOKEN, 0);
      buf.append(RightBraceToken.TOKEN);
    }
    return buf.toTokenList();
  }

  private static void scanBalanced(TokenList.Buffer buf, InpTokChecker tchk) {
    InpTokChecker savedChk = setTokenChecker(tchk);
    for (int balance = 1; ; ) {
      Token tok = nextRawToken();
      if (tok.matchLeftBrace()) ++balance;
      else if (tok.matchRightBrace() && --balance == 0) break;
      buf.append(tok);
    }
    setTokenChecker(savedChk);
  }

  public class NumKind extends PrefixPrim.NumKind {

    protected void addDescOn(int idx, Log log) {
      log.addEsc(getEqDesc()).add(idx);
    }

    protected void addValueOn(int idx, Log log) {
      addEqValueOn(idx, log);
    }
  }

  public String getEqDesc() {
    return getName();
  }

  public void addEqValueOn(int idx, Log log) {}
}
