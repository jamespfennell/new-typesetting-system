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
// Filename: nts/command/TokenList.java
// $Id: TokenList.java,v 1.1.1.1 2000/01/28 17:09:24 ksk Exp $
package nts.command;

import java.io.Serializable;
import java.util.Vector;
import nts.io.CharCode;
import nts.io.Log;
import nts.io.Loggable;
import nts.io.MaxLoggable;
import nts.io.Name;

public class TokenList implements Serializable, Loggable, MaxLoggable {

  public static final TokenList NULL = null;

  public static final TokenList EMPTY = new TokenList();

  public interface Provider {
    TokenList getToksValue();
  }

  public interface Maintainer extends Provider {
    void setToksValue(TokenList list);

    boolean isEmpty();
  }

  public interface Inserter {
    void insertToks();

    boolean isEmpty();
  }

  private static Token tokenFor(char chr) {
    if (chr == ' ') return SpaceToken.TOKEN;
    else return new OtherToken(Token.makeCharCode(chr));
  }

  private static Token tokenFor(CharCode code) {
    if (code.match(' ')) return SpaceToken.TOKEN;
    else return new OtherToken(code);
  }

  public static class Buffer implements Loggable, MaxLoggable {

    protected Vector data;

    public Buffer() {
      data = new Vector();
    }

    public Buffer(int initCap) {
      data = new Vector(initCap);
    }

    public Buffer(int initCap, int capIncrement) {
      data = new Vector(initCap, capIncrement);
    }

    public Buffer append(Token tok) {
      data.addElement(tok);
      return this;
    }

    public Buffer append(char chr) {
      return append(tokenFor(chr));
    }

    public Buffer append(CharCode code) {
      return append(tokenFor(code));
    }

    public Buffer append(Token[] tokens, int offset, int count) {
      data.ensureCapacity(data.size() + count);
      while (count-- > 0) data.addElement(tokens[offset++]);
      return this;
    }

    public Buffer append(Token[] tokens) {
      return append(tokens, 0, tokens.length);
    }

    public Buffer append(TokenList list) {
      return append(list.tokens);
    }

    public void insertTokenAt(Token tok, int idx) {
      data.insertElementAt(tok, idx);
    }

    public void insertTokenAt(CharCode code, int idx) {
      data.insertElementAt(tokenFor(code), idx);
    }

    public void insertTokenAt(char chr, int idx) {
      data.insertElementAt(tokenFor(chr), idx);
    }

    public int length() {
      return data.size();
    }

    public void setLength(int size) {
      data.setSize(size);
    }

    public Token tokenAt(int idx) {
      return (Token) data.elementAt(idx);
    }

    public Token lastToken() {
      return (Token) data.elementAt(data.size() - 1);
    }

    public void removeTokenAt(int idx) {
      data.removeElementAt(idx);
    }

    public void removeLastToken() {
      data.removeElementAt(data.size() - 1);
    }

    public void getTokens(int beg, int end, Token[] dst, int offset) {
      while (beg < end) dst[offset++] = tokenAt(beg++);
    }

    public TokenList toTokenList() {
      if (length() == 0) return EMPTY;
      Token[] tokens = new Token[length()];
      data.copyInto(tokens);
      return new TokenList(tokens);
    }

    public void addOn(Log log) {
      toTokenList().addOn(log);
    }

    public void addOn(Log log, int maxCount) {
      toTokenList().addOn(log, maxCount);
    }
  }

  private Token[] tokens;

  public TokenList() {
    tokens = new Token[0];
  }

  public TokenList(Token[] tokens) {
    this.tokens = tokens;
  }

  public TokenList(Token[] tokens, int offset, int count) {
    this.tokens = new Token[count];
    System.arraycopy(tokens, offset, this.tokens, 0, count);
  }

  public TokenList(Token tok) {
    tokens = new Token[1];
    tokens[0] = tok;
  }

  /* TeXtp[464] */
  public TokenList(String str) {
    tokens = new Token[str.length()];
    for (int i = 0; i < str.length(); i++) tokens[i] = tokenFor(str.charAt(i));
  }

  public TokenList(Name name) {
    tokens = new Token[name.length()];
    for (int i = 0; i < name.length(); i++) tokens[i] = tokenFor(name.codeAt(i));
  }

  public final int length() {
    return tokens.length;
  }

  public final boolean isEmpty() {
    return (length() == 0);
  }

  public final Token tokenAt(int idx) {
    return tokens[idx];
  }

  public final boolean match(TokenList list) {
    if (length() == list.length()) {
      for (int i = 0; i < length(); i++) if (!tokenAt(i).match(list.tokenAt(i))) return false;
      return true;
    }
    return false;
  }

  public void addOn(Log log) {
    for (int i = 0; i < length(); i++) tokenAt(i).addProperlyOn(log);
  }

  public void addOn(Log log, int maxCount) {
    int i;
    maxCount += log.getCount();
    for (i = 0; i < length() && log.getCount() < maxCount; i++) tokenAt(i).addProperlyOn(log);
    if (i < length()) log.addEsc("ETC.");
  }

  public void addContext(Log left, Log right, int pos, int maxCount) {
    Log log = left;
    int i;
    maxCount += log.getCount();
    for (i = 0; i < length() && log.getCount() < maxCount; i++) {
      if (i == pos) {
        maxCount -= log.getCount();
        log = right;
        maxCount += log.getCount();
      }
      tokenAt(i).addProperlyOn(log);
    }
    if (i < length()) log.addEsc("ETC.");
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < length(); buf.append(tokenAt(i++)))
      ;
    return buf.toString();
  }
}
