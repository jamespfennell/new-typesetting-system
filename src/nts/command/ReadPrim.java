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
// Filename: nts/command/ReadPrim.java
// $Id: ReadPrim.java,v 1.1.1.1 2000/01/31 21:47:03 ksk Exp $
package nts.command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import nts.base.BoolPar;
import nts.io.Loggable;
import nts.io.MaxLoggable;

public class ReadPrim extends AssignPrim {

  private transient HashMap<Integer, ReadInput> table;

  private void initTable() {
    table = new HashMap<Integer, ReadInput>(23);
  }

  public ReadPrim(String name) {
    super(name);
    initTable();
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    initTable();
  }

  public ReadInput get(int num) {
    return table.get(Integer.valueOf(num));
  }

  private ReadInput replace(int num, ReadInput input) {
    return table.put(Integer.valueOf(num), input);
    // XXX remove if NULL
  }

  public void set(int num, ReadInput input) {
    ReadInput old = replace(num, input);
    if (old != input && old != ReadInput.NULL) old.close();
  }

  public boolean eof(int num) {
    return (get(num) == ReadInput.NULL);
  }

  private final Token FROZEN_END_READ = new FrozenToken("endread", Command.NULL);

  /* TeXtp[1225,482] */
  protected void assign(Token src, boolean glob) {
    BoolPar canExpand = new BoolPar();
    int num = scanInt();
    if (!scanKeyword("to")) error("MissingToForRead");
    Token def = definableToken();
    MacroBody.Buffer buf = new MacroBody.Buffer();
    InpTokChecker savedChk = setTokenChecker(new ReadToksChecker(buf, def));
    ReadInput input = get(num);
    int balance = 0;
    int ln = 0;
    if (input == ReadInput.NULL) input = getIOHandler().defaultRead(num);
    do {
      getTokStack().push(new SentinelToken(FROZEN_END_READ));
      Tokenizer tokenizer = input.nextTokenizer(def, ln++);
      if (tokenizer != Tokenizer.NULL) getTokStack().push(tokenizer);
      else {
        set(num, ReadInput.NULL);
        tokenizer = input.emptyLineTokenizer();
        getTokStack().push(tokenizer);
        if (balance > 0) {
          runAway("definition", buf);
          error("EOFinRead", esc("read"));
          balance = 0;
        }
      }
      for (; ; ) {
        Token tok = nextRawToken();
        if (tok == FROZEN_END_READ) break;
        if (balance >= 0) {
          if (tok.matchLeftBrace()) ++balance;
          else if (tok.matchRightBrace() && --balance < 0) continue;
          /* STRANGE
           * Note that we could pop the current tokenizer off the
           * stack (and insertions above it) if the balance
           * become < 0, but in such case we would loose the
           * errors 'Forbidden control sequence found ...'
           * past the unmatched right brace token.
           */
          buf.append(tok);
        }
      }
      getTokStack().dropPop();
    } while (balance > 0);
    setTokenChecker(savedChk);
    def.define(new Macro(buf.toMacroBody(), 0), glob);
  }
}

class SentinelToken extends Tokenizer { // XXX compare to WritePrim

  private Token token;
  private boolean readed = false;

  public SentinelToken(Token tok) {
    token = tok;
  }

  public Token nextToken(BoolPar canExpand) {
    canExpand.set(false);
    if (readed) return Token.NULL;
    readed = true;
    return token;
  }

  public boolean finishedList() {
    return readed;
  }

  public int show(ContextDisplay disp, boolean force, int lines) {
    return 0;
  }
}

class ReadToksChecker extends ScanToksChecker {

  public ReadToksChecker(MaxLoggable list, Loggable src) {
    super("OuterInDef", "EOFinDef", "definition", list, src);
  }

  protected void handleOuterToken(Token tok) {}
}
