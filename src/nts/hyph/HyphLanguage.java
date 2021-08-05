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
// Filename: nts/hyph/HyphLanguage.java
// $Id: HyphLanguage.java,v 1.1.1.1 2000/11/11 09:25:56 ksk Exp $
package nts.hyph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import nts.node.Hyphens;
import nts.node.Language;

public class HyphLanguage extends Language {

  private final HashHyphenation hashHyph;
  private final WordMap patterns;
  private final Map nodeMap;
  private final int[] nodeStats;

  public HyphLanguage(
      int ln,
      int lhm,
      int rhm,
      HashHyphenation hashHyph,
      WordMap patterns,
      Map nodeMap,
      int[] nodeStats) {
    super(ln, lhm, rhm);
    this.hashHyph = hashHyph;
    this.patterns = patterns;
    this.nodeMap = nodeMap;
    this.nodeStats = nodeStats;
  }

  /* TeXtp[923] */
  public Hyphens getHyphens(String word) {
    int size = word.length();
    if (size >= leftHyphenMin + rightHyphenMin) {
      int[] hyphens = hashHyph.getPositions(word);
      if (hyphens != null)
        return ArrayHyphens.forPositions(hyphens, leftHyphenMin, size - rightHyphenMin);
      char[] codes = new char[size + 2];
      word.getChars(0, size, codes, 1);
      codes[0] = codes[size + 1] = WORD_BOUNDARY;
      int[] values = new int[size + 3];
      WordMap.Seeker seeker = patterns.seeker();
      int right = size - rightHyphenMin + 2;
      for (int i = 0; i < right; i++) {
        for (int l = i; l < codes.length; l++) {
          seeker.seek(codes[l]);
          if (!seeker.isValid()) break;
          HyphNode hyph = (HyphNode) seeker.get();
          for (int k = i; hyph != HyphNode.NULL; hyph = hyph.next, k++) {
            k += hyph.offset;
            if (values[k] < hyph.value) values[k] = hyph.value;
          }
        }
        seeker.reset();
      }
      for (int i = 1; i <= leftHyphenMin; i++) values[i] = 0;
      int k = size - rightHyphenMin + 1;
      while (k > 0 && (values[k] & 1) == 0) k--;
      if (k > 0) {
        boolean[] array = new boolean[k];
        do {
          k--;
          array[k] = ((values[k + 1] & 1) != 0);
        } while (k > 0);
        return new ArrayHyphens(array);
      }
    }
    return ArrayHyphens.EMPTY;
  }

  public void setHyphException(String word, int[] positions) {
    hashHyph.setPositions(word, positions);
  }

  /* STRAGE
   * If the last digits associated to a pattern were all zeros
   * (HyphNode.ZERO) it is not considered to be a duplication.
   */
  /* TeXtp[963] */
  public boolean setHyphPattern(String patt, int[] values) {
    HyphNode hyph = makeHyphNodes(values);
    HyphNode old = (HyphNode) patterns.put(patt, hyph);
    return (old == HyphNode.NULL || old.isZero());
  }

  /* TeXtp[965] */
  private HyphNode makeHyphNodes(int[] values) {
    HyphNode hyph = HyphNode.NULL;
    int n = values.length;
    while (--n >= 0 && values[n] == 0)
      ;
    if (n < 0) return HyphNode.ZERO;
    while (n >= 0) {
      int i = n;
      while (--i >= 0 && values[i] == 0)
        ;
      hyph = new HyphNode(n - i - 1, values[n], hyph);
      HyphNode hashed = (HyphNode) nodeMap.get(hyph);
      if (hashed == HyphNode.NULL) nodeMap.put(hyph, hyph);
      else {
        hyph = hashed;
        nodeStats[1]++;
      }
      n = i;
      nodeStats[0]++;
    }
    return hyph;
  }

  public static String toString(String patt, HyphNode hyph) {
    StringBuffer buf = new StringBuffer(2 * patt.length());
    int h = (hyph != HyphNode.NULL) ? hyph.offset : -1;
    int i;
    for (i = 0; i < patt.length(); i++) {
      char code = patt.charAt(i);
      if (code == WORD_BOUNDARY) code = '.';
      if (i == h) {
        if (hyph.value != 0) buf.append(hyph.value);
        hyph = hyph.next;
        h = (hyph != HyphNode.NULL) ? h + hyph.offset + 1 : -1;
      } else if (Character.isDigit(code)) buf.append('0');
      buf.append(code);
    }
    if (i == h && hyph.value != 0) buf.append(hyph.value);
    return buf.toString();
  }

  public static void dumpPatterns(PrintWriter writer, WordMap patterns) throws IOException {
    Enumeration entries = patterns.entries();
    while (entries.hasMoreElements()) {
      Map.Entry entry = (Map.Entry) entries.nextElement();
      if (entry != null) {
        String key = (String) entry.getKey();
        HyphNode value = (HyphNode) entry.getValue();
        if (value != HyphNode.NULL) writer.println(toString(key, value));
      }
    }
  }
}
