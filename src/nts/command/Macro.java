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
// Filename: nts/command/Macro.java
// $Id: Macro.java,v 1.1.1.1 2000/06/22 23:29:28 ksk Exp $
package nts.command;

import nts.base.BoolPar;
import nts.base.BytePar;
import nts.io.CharCode;
import nts.io.Log;
import nts.io.Loggable;

/** Macro command which scans the parameters and expand itself in the process of macro expansion. */
public class Macro extends Expandable {

  private static final Token PAR_TOKEN = new CtrlSeqToken("par");

  /** List of parameter separators - the parameter mask */
  private /* final */ TokenList[] mask;

  /** List of character codes of match characters in the mask */
  private /* final */ CharCode[] matchCodes;

  /** The body of the macro */
  private /* final */ TokenList body; // XXX JavaC error?

  private /* final */ int prefixes;

  /**
   * Creates a Macro with given parameter mask and body.
   *
   * @param mask parameter mask
   * @param matchCodes character codes of match characters in the mask
   * @param body macro body
   */
  public Macro(TokenList[] mask, CharCode[] matchCodes, TokenList body, int prefixes) {
    this.mask = mask;
    this.matchCodes = matchCodes;
    this.body = body;
    this.prefixes = prefixes;
  }

  public Macro(TokenList body, int prefixes) {
    this.mask = new TokenList[0];
    this.matchCodes = null;
    this.body = body;
    this.prefixes = prefixes;
  }

  private static class ParAbort extends Exception {
    public Token tok;

    public ParAbort(Token tok) {
      this.tok = tok;
    }
  }

  private static class DoesNotMatch extends Exception {}

  public static final int BOOLP_TRACING_MACROS = newBoolParam();

  /**
   * Scans parameters and pushes its expanded body to the tokenizer stack.
   *
   * @param src source token for diagnostic output
   */
  /* TeXtp[389] */
  public void doExpansion(Token src) {
    if (getConfig().getBoolParam(BOOLP_TRACING_MACROS)) {
      src.addProperlyOn(diagLog.endLine());
      addMask(diagLog).add(body).startLine();
    }
    TokenList[] params = null;
    if (mask.length > 0) {
      try {
        params = scanParameters(src);
      } catch (DoesNotMatch d) {
        return;
      }
    }
    getTokStack().cleanFinishedLists();
    getTokStack().push(new Expansion(params, src));
  }

  private static final byte REJECT_AND_COMPLAIN = 0;
  private static final byte REJECT_ONLY = 1;
  private static final byte ACCEPT = 2;

  private static class MatchChecker extends ScanToksChecker {

    private BytePar parReact;

    public MatchChecker(TokenList.Buffer buf, Loggable src, BytePar pr) {
      super("OuterInMatch", "EOFinMatch", "argument", buf, src, PAR_TOKEN);
      parReact = pr;
    }

    protected void reportError(String ident) {
      parReact.set(REJECT_ONLY);
      super.reportError(ident);
    }
  }

  /* TeXtp[391] */
  private TokenList[] scanParameters(Token src) throws DoesNotMatch {
    BytePar parReact = new BytePar(((prefixes & DefPrim.LONG) != 0) ? ACCEPT : REJECT_AND_COMPLAIN);
    if (mask[0].length() != 0) scanCompulsory(src, mask[0], parReact);
    if (mask.length > 1) {
        /* the macro has some parameters */
      TokenList[] params = new TokenList[mask.length - 1];
      for (int i = 1; i < mask.length; i++) {
        TokenList.Buffer buf = new TokenList.Buffer(10, 10);
        MatchChecker mchk = new MatchChecker(buf, src, parReact);
        InpTokChecker savedChk = setTokenChecker(mchk);
        try {
          if (mask[i].length() != 0) scanParam(src, buf, mask[i], parReact);
          else scanParam(src, buf, parReact);
        } catch (ParAbort a) {
          if (parReact.get() == REJECT_AND_COMPLAIN) {
            runAway("argument", buf);
            backToken(a.tok);
            error("RunawayArg", src);
          }
          throw new DoesNotMatch();
        } finally {
          setTokenChecker(savedChk);
        }
        TokenList param = buf.toTokenList();
        params[i - 1] = param;
        if (getConfig().getBoolParam(BOOLP_TRACING_MACROS)) {
          diagLog.startLine().add(matchCodes[i - 1]);
          diagLog.add(Character.forDigit(i, 10)).add("<-");
          param.addOn(diagLog, 1000);
          diagLog.startLine();
        }
      }
      return params;
    } else return null;
  }

  private void scanCompulsory(Token src, TokenList compulsory, BytePar parReact)
      throws DoesNotMatch {
    TokenList.Buffer buf = new TokenList.Buffer(0);
    MatchChecker mchk = new MatchChecker(buf, src, parReact);
    InpTokChecker savedChk = setTokenChecker(mchk);
    try {
      for (int i = 0; i < compulsory.length(); i++) {
        Token tok = nextRawToken();
        if (!tok.match(compulsory.tokenAt(i))) {
          error("UseDoesntMatch", src);
          throw new DoesNotMatch();
        }
      }
    } finally {
      setTokenChecker(savedChk);
    }
  }

  /**
   * Scans an undelimited parameter.
   *
   * @param src source token for diagnostic output
   */
  private void scanParam(Token src, TokenList.Buffer buf, BytePar parReact) throws ParAbort {
    for (; ; ) {
      Token tok = nextRawToken();
      if (PAR_TOKEN.match(tok) && parReact.get() != ACCEPT) throw new ParAbort(tok);
      else if (tok.matchLeftBrace()) {
        buf.append(tok);
        addGroup(buf, parReact);
        buf.removeLastToken();
        buf.removeTokenAt(0);
      } else if (tok.matchRightBrace()) {
        reportExtraRightBrace(tok, src);
        parReact.set(REJECT_AND_COMPLAIN);
      } else if (tok.matchSpace()) continue; /* skip spaces */
      else buf.append(tok); /* first non blank non spec token */
      break;
    }
  }

  /**
   * Scans a delimited parameter.
   *
   * @param src source token for diagnostic output
   * @param separator the separator of the scanned parameter
   */
  private void scanParam(Token src, TokenList.Buffer buf, TokenList separator, BytePar parReact)
      throws ParAbort {
    int matchLen = separator.length();
    Token[] matchBuf = new Token[matchLen];
    int matchIdx = 0;
    int groupCnt = 0;
    Match:
    for (; ; ) {
      Token tok = nextRawToken();
      if (tok.match(separator.tokenAt(matchIdx))) {
        matchBuf[matchIdx++] = tok;
        if (matchIdx >= matchLen) {
          if (tok.matchLeftBrace()) adjustBraceNesting(-1);
          if (groupCnt == 1 && buf.length() >= 2) {
            Token first = buf.tokenAt(0);
            Token last = buf.lastToken();
            if (first.matchLeftBrace() && last.matchRightBrace()) {
              buf.removeLastToken();
              buf.removeTokenAt(0);
            }
          }
          break;
        }
      } else {
        matchBuf[matchIdx++] = tok;
        for (int i = 0; ; ) {
          buf.append(matchBuf[i++]);
          if (i >= matchIdx) break;
          int j = i;
          while (j < matchIdx && matchBuf[j].match(separator.tokenAt(j - i))) j++;
          if (j >= matchIdx) {
            matchIdx -= i;
            System.arraycopy(matchBuf, i, matchBuf, 0, matchIdx);
            continue Match; /* partial match in effect */
          }
        }
        matchIdx = 0;
        if (PAR_TOKEN.match(tok) && parReact.get() != ACCEPT) {
          buf.removeLastToken();
          throw new ParAbort(tok);
        } else if (tok.matchLeftBrace()) {
          addGroup(buf, parReact);
          groupCnt++;
        } else if (tok.matchRightBrace()) {
          buf.removeLastToken();
          reportExtraRightBrace(tok, src);
          parReact.set(REJECT_AND_COMPLAIN);
        }
      }
    }
  }

  /**
   * Adds balanced group of tokens to a buf.
   *
   * @param buf list of |Tokens|.
   */
  private void addGroup(TokenList.Buffer buf, BytePar parReact) throws ParAbort {
    for (int balance = 1; ; ) {
      Token tok = nextRawToken();
      if (PAR_TOKEN.match(tok) && parReact.get() != ACCEPT) {
        adjustBraceNesting(-balance);
        throw new ParAbort(tok);
      }
      buf.append(tok);
      if (tok.matchLeftBrace()) ++balance;
      else if (tok.matchRightBrace() && --balance == 0) break;
    }
  }

  /* TeXtp[395] */
  private void reportExtraRightBrace(Token tok, Token src) {
    backToken(tok);
    insertToken(PAR_TOKEN);
    adjustBraceNesting(1);
    error("ExtraRightBrace", src);
  }

  /**
   * Logs symbolic representation of the mask on the |Log|.
   *
   * @param log the |Log| to log on.
   */
  private Log addMask(Log log) {
    if (mask.length > 0) {
      log.add(mask[0]);
      for (int i = 1; i < mask.length; i++)
        log.add(matchCodes[i - 1]).add(Character.forDigit(i, 10)).add(mask[i]);
    }
    return log;
  }

  public void addMaxOn(Log log, int maxCount) {
    maxCount += log.getCount();
    if (mask.length > 0) {
      mask[0].addOn(log, maxCount - log.getCount());
      for (int i = 1; i < mask.length && log.getCount() < maxCount; i++) {
        log.add(matchCodes[i - 1]).add(Character.forDigit(i, 10));
        mask[i].addOn(log, maxCount - log.getCount());
      }
    }
    body.addOn(log, maxCount - log.getCount());
  }

  public boolean isOuter() {
    return ((prefixes & DefPrim.OUTER) != 0);
  }

  /* TeXtp[223] */
  public void addExpandable(Log log, int maxCount) {
    addPrefix(log);
    log.add(':');
    addMaxOn(log, maxCount);
  }

  /* TeXtp[296,1295] */
  public void addExpandable(Log log, boolean full) {
    addPrefix(log);
    if (full) {
      log.add(':').endLine();
      addMask(log).add(body);
    }
  }

  /* TeXtp[1295] */
  private void addPrefix(Log log) {
    if ((prefixes & DefPrim.LONG) != 0) log.addEsc("long");
    if ((prefixes & DefPrim.OUTER) != 0) log.addEsc("outer");
    if ((prefixes & (DefPrim.LONG | DefPrim.OUTER)) != 0) log.add(' ');
    log.add("macro");
  }

  public boolean sameAs(Command cmd) {
    if (cmd instanceof Macro) {
      Macro mac = (Macro) cmd;
      if (prefixes == mac.prefixes) {
        int len = mask.length;
        if (len == mac.mask.length) {
          if (--len >= 0 && !mask[len].match(mac.mask[len])) return false;
          while (--len >= 0)
            if (!matchCodes[len].match(mac.matchCodes[len]) || !mask[len].match(mac.mask[len]))
              return false;
          return body.match(mac.body);
        }
      }
    }
    return false;
  }

  /** Tokenizer for Macro body with parameters and associated parameter values. */
  private class Expansion extends Tokenizer {

    /** List of parameters */
    private TokenList[] params;

    /** position of the next token in the body */
    private int pos = 0;

    private Token src;

    /**
     * Creates tokenizer for macro expansion with given list of parameters.
     *
     * @param params the list of parameters
     * @param src source token for diagnostic output
     */
    public Expansion(TokenList[] params, Token src) {
      this.params = params;
      this.src = src;
    }

    /**
     * Gives the next Token from the expansion.
     *
     * @param canExpand boolean output parameter querying whether the acquired |Token| can be
     *     expanded (e.g. was not preceded by \noexpand).
     * @return next Token or |Token.NULL| when the sequence is finished.
     */
    public Token nextToken(BoolPar canExpand) {
      if (pos < body.length()) {
        Token tok = body.tokenAt(pos++);
        if (tok.isMacroParameter()) {
          TokenList param = params[tok.macroParameterNumber()];
          pushList(param, "argument");
          return getStack().nextToken(canExpand);
        }
        canExpand.set(true);
        return tok;
      }
      canExpand.set(false);
      return Token.NULL;
    }

    public boolean finishedList() {
      return (pos >= body.length());
    }

    /* STRANGE
     * all context traces start with print_nl("...") in TeX but
     * macro expansion is the only exception. This causes generating
     * nonsense empty lines after previous full lines but it is
     * compatible with TeX
     */

    public int show(ContextDisplay disp, boolean force, int lines) {
      src.addProperlyOn(disp.normal().endLine());
      Log left = disp.left();
      int count = left.getCount();
      count += 100000 - addMask(left).getCount(); // XXX why so much?
      body.addContext(left, disp.right(), pos, count);
      disp.show();
      return 1;
    }
  }
}
