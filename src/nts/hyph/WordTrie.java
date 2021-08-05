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
// Filename: nts/hyph/WordTrie.java
// $Id: WordTrie.java,v 1.1.1.1 2000/10/06 08:31:34 ksk Exp $
package nts.hyph;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

public class WordTrie implements WordMap {

  public static final WordTrie NULL = null;
  public static final Entry NULL_ENTRY = null;

  private static final Entry[] EMPTY_TABLE = {new Entry('\000', 0, null)};
  public static final WordTrie EMPTY = new WordTrie(EMPTY_TABLE, '\000');

  static class Entry implements Serializable {

    final char code;
    final int link;
    Object value;

    public Entry(char code, int link, Object value) {
      this.code = code;
      this.link = link;
      this.value = value;
    }
  }

  private /* final */ Entry[] table;
  private /* final */ char maxCode;

  public WordTrie(Entry[] table, char maxCode) {
    this.table = table;
    this.maxCode = maxCode;
  }

  public Object get(String word) {
    int size = word.length();
    int curr = 0;
    for (int i = 0; i < size; i++) {
      char code = word.charAt(i);
      curr = table[curr].link;
      if (curr == 0) return null;
      curr += code;
      if (curr <= 0 || curr >= table.length) return null;
      Entry ent = table[curr];
      if (ent == NULL_ENTRY || ent.code != code) return null;
    }
    return table[curr].value;
  }

  public Object put(String word, Object value) {
    throw new UnsupportedOperationException("packed");
  }

  public class Seeker implements WordMap.Seeker {

    private int curr = 0;

    public void reset() {
      curr = 0;
    }

    public boolean isValid() {
      return (curr >= 0);
    }

    public void seek(char code) {
      if (curr >= 0) {
        curr = table[curr].link;
        if (curr != 0) {
          curr += code;
          if (curr > 0 && curr < table.length) {
            Entry ent = table[curr];
            if (ent != NULL_ENTRY && ent.code == code) return;
          }
        }
        curr = -1;
      }
    }

    public Object get() {
      return (curr > 0) ? table[curr].value : null;
    }
  }

  public WordMap.Seeker seeker() {
    return new Seeker();
  }

  public Enumeration entries() {
    return new Enum();
  }

  public WordMap packed() {
    return this;
  }

  private class Level {
    int link;
    int curr;

    Level(int link, int curr) {
      this.link = link;
      this.curr = curr;
    }
  }

  private class Enum implements Enumeration {

    private Stack stack = new Stack();
    private int link = table[0].link;
    private int curr = (link != 0) ? next(Math.max(link, 1)) : 0;

    private int next(int i) {
      int k = Math.min(table.length, link + maxCode + 1);
      for (; i < k; i++) {
        Entry ent = table[i];
        if (ent != NULL_ENTRY && ent.code == (i - link)) return i;
      }
      return 0;
    }

    private void step() {
      int l = table[curr].link;
      if (l != 0) {
        stack.push(new Level(link, curr));
        link = l;
        curr = Math.max(link, 1);
      } else curr++;
      for (; ; ) {
        curr = next(curr);
        if (curr != 0) return;
        if (stack.empty()) {
          curr = link = 0;
          return;
        }
        Level top = (Level) stack.pop();
        curr = top.curr + 1;
        link = top.link;
      }
    }

    public boolean hasMoreElements() {
      return (curr != 0);
    }

    public Object nextElement() {
      if (curr == 0) throw new NoSuchElementException("WordTrie is finished");
      Object mapEnt =
          new Map.Entry() {

            private Entry ent = table[curr];
            private String key = makeKey();

            private String makeKey() {
              char[] codes = new char[stack.size() + 1];
              int i;
              for (i = 0; i < stack.size(); i++) {
                Level lev = (Level) stack.get(i);
                codes[i] = table[lev.curr].code;
              }
              codes[i] = table[curr].code;
              return new String(codes);
            }

            public Object getKey() {
              return key;
            }

            public Object getValue() {
              return ent.value;
            }

            public Object setValue(Object value) {
              Object old = ent.value;
              ent.value = value;
              return old;
            }
          };
      step();
      return mapEnt;
    }
  }
}
