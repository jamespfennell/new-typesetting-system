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
// Filename: nts/command/CondPrim.java
// $Id: CondPrim.java,v 1.1.1.1 2001/03/13 11:03:57 ksk Exp $
package nts.command;

import nts.base.BoolPar;
import nts.io.Loggable;

public abstract class CondPrim extends ExpandablePrim {

  public static final int NOTHING = 0;
  public static final int WAIT = 1;
  public static final int FI = 2;
  public static final int ELSE = 3;
  public static final int OR = 4;

  protected static boolean conforms(int endLev, int limit) {
    return (endLev <= limit);
  }

  protected static boolean closesBranch(int endLev) {
    return (endLev > WAIT);
  }

  public CondPrim(String name) {
    super(name);
  }

  protected static final class CondEntry {
    public static final CondEntry NULL = null;

    public final CondEntry next;
    public int limit;
    public final Expandable curr;
    public final FilePos fpos;

    CondEntry(CondEntry next, int limit, Expandable curr, FilePos fpos) {
      this.next = next;
      this.limit = limit;
      this.curr = curr;
      this.fpos = fpos;
    }
  }

  private static CondEntry condTop = CondEntry.NULL;

  protected static CondEntry pushCond(int limit, Expandable curr) {
    FilePos fpos = getTokStack().filePos();
    return condTop = new CondEntry(condTop, limit, curr, fpos);
  }

  protected static void popCond() {
    condTop = condTop.next;
  }

  protected static CondEntry topCond() {
    return condTop;
  }

  protected static boolean noCond() {
    return (condTop == CondEntry.NULL);
  }

  /* TeXtp[1335] */
  public static void cleanUp() {
    while (condTop != CondEntry.NULL) {
      normLog.startLine().add('(').addEsc("end").add(" occurred when ");
      condTop.curr.addExpandable(normLog);
      if (condTop.fpos != FilePos.NULL) normLog.add(" on line ").add(condTop.fpos.line);
      normLog.add(" was incomplete)");
      popCond();
    }
  }

  /* TeXtp[494] */
  protected static int skipBranch() {
    int endLev;
    BoolPar exp = new BoolPar();
    Loggable[] params = {exp(condTop.curr), num(currLineNumber())};
    InpTokChecker savedChk = setTokenChecker(new SkipToksChecker(params));
    for (int level = 0; ; ) {
      Token tok = nextRawToken(exp);
      Command cmd = meaningOf(tok, exp.get());
      endLev = cmd.endBranchLevel();
      if (closesBranch(endLev)) {
        if (level == 0) break;
        if (endLev == FI) --level;
      } else if (cmd.isConditional()) ++level;
    }
    setTokenChecker(savedChk);
    return endLev;
  }
}

class SkipToksChecker implements InpTokChecker {

  private final Loggable[] params;

  public SkipToksChecker(Loggable[] params) {
    this.params = params;
  }

  public Token checkToken(Token tok, BoolPar canExpand) {
    if (Command.meaningOf(tok, canExpand.get()).isOuter()) {
      Command.backTokenWithoutCleaning(tok);
      reportError("OuterInSkipped");
      canExpand.set(true);
      return SpaceToken.TOKEN;
    }
    return tok;
  }

  public void checkEndOfFile() {
    reportError("EOFinSkipped");
  }

  protected void reportError(String ident) {
    Command.insertToken(Command.getConfig().frozenFi());
    Command.nonDelError(ident, params);
  }
}
