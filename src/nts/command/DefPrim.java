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
// Filename: nts/command/DefPrim.java
// $Id: DefPrim.java,v 1.1.1.1 2000/06/22 18:59:00 ksk Exp $
package nts.command;

import nts.io.CharCode;
import nts.io.Log;
import nts.io.MaxLoggable;

/** Prim \def which defines a new macro and stores it to the table of equivalents. */
public class DefPrim extends PrefixPrim {

  /** Should the body be expanded during scanning? */
  private final boolean xpand;

  /** Built in prefixes (LONG, OUTER, GLOBAL) */
  private final int prefix;

  /** Creates and registers the |\def| primitive */
  public DefPrim(String name, boolean xpand, int prefix) {
    super(name);
    this.xpand = xpand;
    this.prefix = prefix;
  }

  /** Scans a macro definition and stores the new macro to table of equivalents */
  public void exec(Token src, int prefixes) {
    Token tok = definableToken();
    MacroDefining def = new MacroDefining(tok);
    scanDef(tok, def);
    prefixes |= prefix;
    Macro mac = def.toMacro(prefixes & (LONG | OUTER));
    tok.define(mac, globalAssignment(prefixes));
    afterAssignment();
  }

  /** Scans a definition of a macro. */
  public void scanDef(Token defMac, MacroDefining def) {
    Token hashBrace = Token.NULL;
    boolean valid = true;
    InpTokChecker savedChk = setTokenChecker(def);
    for (; ; ) {
      Token tok = nextRawToken();
      Command cmd = meaningOf(tok);
      if (cmd.isMacroParam()) {
        def.matchCode = cmd.charCode();
        tok = nextRawToken();
        if (meaningOf(tok).isLeftBrace()) {
          hashBrace = tok;
          def.addToMask(tok);
          break;
        }
        if (def.paramCnt() < 9) {
          if (!tok.matchOther(Character.forDigit(def.paramCnt() + 1, 10))) {
            backToken(tok);
            error("NonConseqParams");
          }
          def.addParam();
          continue;
        } else error("TooManyParams");
      } else if (tok.matchLeftBrace()) break;
      else if (tok.matchRightBrace()) {
        adjustBraceNesting(1);
        valid = false;
        error("MissingLeftDefBrace");
        break;
      }
      def.addToMask(tok);
    }
    def.bodyBuf = new MacroBody.Buffer(30);
    if (valid) scanBody(defMac, def);
    if (hashBrace != Token.NULL) def.bodyBuf.append(hashBrace);
    setTokenChecker(savedChk);
  }

  /* STRANGE
   * Note that the character code of |ParamToken| created in the following
   * method is not its original code but the code of the last match in the
   * parameter mask.  This simulates the TeX behavior where the |out_param|
   * token uses its character code for parameter number.
   */

  /** Scans a macro body with possible parameters. */
  private void scanBody(Token defMac, MacroDefining def) {
    MacroBody.Buffer buf = def.bodyBuf;
    for (int balance = 1; ; ) {
      Token tok = nextScannedToken(xpand, buf);
      if (tok != Token.NULL) {
        if (meaningOf(tok).isMacroParam()) {
          Token prevTok = tok;
          tok = (xpand) ? nextExpToken() : nextRawToken();
          if (!meaningOf(tok).isMacroParam()) {
            char dig = tok.otherChar();
            int digit;
            if (dig != CharCode.NO_CHAR && dig >= '1' && (digit = dig - '1') < def.paramCnt()) {
              buf.appendParam(digit, def.matchCode);
              continue;
            } else {
              backToken(tok);
              tok = prevTok;
              error("IllegalParamNum", defMac);
            }
          }
        } else if (tok.matchLeftBrace()) ++balance;
        else if (tok.matchRightBrace() && --balance == 0) break;
        buf.append(tok);
      }
    }
  }
}

class MacroDefining extends BaseToksChecker implements MaxLoggable {

  public CharCode matchCode = MacroParamToken.CODE;
  public MacroBody.Buffer bodyBuf = null;

  private CharCode[] matchCodes = new CharCode[9];
  private TokenList[] paramMask = new TokenList[10];
  private TokenList.Buffer paramBuf = new TokenList.Buffer(10, 10);
  private int count = 0;

  public MacroDefining(Token source) {
    super("OuterInDef", "EOFinDef", source);
  }

  public int paramCnt() {
    return count;
  }

  public void addToMask(Token tok) {
    paramBuf.append(tok);
  }

  public void addParam() {
    matchCodes[count] = matchCode;
    paramMask[count++] = paramBuf.toTokenList();
    paramBuf = new TokenList.Buffer(10, 10);
  }

  public Macro toMacro(int prefixes) {
    int i = count;
    if (i > 0 || paramBuf.length() > 0) paramMask[i++] = paramBuf.toTokenList();
    TokenList[] mask = new TokenList[i];
    System.arraycopy(paramMask, 0, mask, 0, i);
    CharCode[] codes = null;
    if (count > 0) {
      codes = new CharCode[count];
      System.arraycopy(matchCodes, 0, codes, 0, count);
    }
    TokenList body = (bodyBuf != null) ? bodyBuf.toTokenList() : TokenList.EMPTY;
    return new Macro(mask, codes, body, prefixes);
  }

  protected void tryToFix() {
    Command.insertTokenWithoutCleaning(RightBraceToken.TOKEN);
  }

  protected void reportRunAway() {
    Command.runAway("definition", this);
  }

  public void addOn(Log log, int maxCount) {
    toMacro(0).addMaxOn(log, maxCount);
  }
}
