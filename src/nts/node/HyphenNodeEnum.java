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
// Filename: nts/node/HyphenNodeEnum.java
// $Id: HyphenNodeEnum.java,v 1.1.1.1 2001/01/28 19:12:37 ksk Exp $
package nts.node;

import nts.io.CharCode;
import nts.io.Name;

public abstract class HyphenNodeEnum extends InsetedNodeEnum {

  private final boolean ucHyph;
  private Language currLang;
  private boolean spaceBreaking = true;

  public HyphenNodeEnum(NodeEnum in, Language lang, boolean ucHyph) {
    super(in);
    this.currLang = lang;
    this.ucHyph = ucHyph;
  }

  /* TeXtp[866] */
  private Node getNextNode() {
    Node node = super.nextNode();
    if (node.allowsSpaceBreaking()) spaceBreaking = true;
    else if (node.forbidsSpaceBreaking()) spaceBreaking = false;
    Language altLang = node.alteringLanguage();
    if (altLang != Language.NULL) currLang = altLang;
    return node;
  }

  private boolean wordBlock = false;

  /* TeXtp[866] */
  public Node nextNode() {
    if (wordBlock) {
      NodeList block = nextWordBlock();
      if (!block.isEmpty()) inseted = block.nodes();
    }
    Node node = getNextNode();
    wordBlock = (node.startsWordBlock() && spaceBreaking);
    return node;
  }

  /* TeXtp[894-899] */
  protected NodeList nextWordBlock() {
    NodeList list = new NodeList();
    Node node;
    for (; ; ) {
      if (!hasMoreNodes()) return list;
      node = getNextNode();
      list.append(node);
      byte r = node.beforeWord();
      if (r == Node.FAILURE) return list;
      else if (r == Node.SUCCESS) break;
    }
    Name.Buffer word = new Name.Buffer();
    FontMetric wordMetric = FontMetric.NULL;
    int beg = list.length() - 1;
    Language lang = Language.NULL;
    CharCode hyphCode = CharCode.NULL;
    int count = 0;
    while (node.canBePartOfWord()) {
      FontMetric metric = node.uniformMetric();
      if (metric != FontMetric.NULL) {
        if (wordMetric == FontMetric.NULL) {
          hyphCode = hyphenChar(metric);
          if (hyphCode == CharCode.NULL) return list;
          wordMetric = metric;
        } else if (!metric.equals(wordMetric)) break;
      }
      node.contributeCharCodes(word);
      int cnt = word.length();
      if (cnt < 64) count = cnt;
      else break; // XXX sym const
      lang = currLang;
      if (!hasMoreNodes())
        return hyphenated(word, count, wordMetric, lang, hyphCode, list, beg, list.length());
      node = getNextNode();
      list.append(node);
    }
    int end = list.length() - 1;
    for (; ; ) {
      byte r = node.afterWord();
      if (r == Node.FAILURE) return list;
      else if (r == Node.SUCCESS || !hasMoreNodes())
        return hyphenated(word, count, wordMetric, lang, hyphCode, list, beg, end);
      node = getNextNode();
      list.append(node);
    }
  }

  protected NodeList hyphenated(
      Name.Buffer buf,
      int count,
      FontMetric metric,
      Language lang,
      CharCode hyphCode,
      NodeList list,
      int beg,
      int end) {
    if (count > 0 && metric != FontMetric.NULL) {
      CharCode[] word = new CharCode[count];
      buf.getCodes(0, count, word, 0);
      if (ucHyph || word[0].toCanonicalLetter() == word[0].toChar()) {
        char[] canon = new char[count];
        for (int i = 0; i < count; i++) canon[i] = word[i].toCanonicalLetter();
        Hyphens hyphens = lang.getHyphens(new String(canon));
        // System.err.println(hyphens.toString(new String(canon))); //XXX
        if (!hyphens.isEmpty())
          return (new Hyphenator(word, metric, hyphens, hyphCode, list, beg, end)).hyphenated();
      }
    }
    return list;
  }

  private static class Appender implements TreatNode {
    // , nts.io.Loggable { //XXX

    private NodeList list = new NodeList();

    public void execute(Node node) {
      list.append(node);
    }

    public void clear() {
      if (!list.isEmpty()) list = new NodeList();
    }

    public NodeList takeList() {
      if (list.isEmpty()) return NodeList.EMPTY;
      else {
        NodeList curr = list;
        list = new NodeList();
        return curr;
      }
    }

    public String toString() {
      return list.toString();
    }

    // XXX
    // public int		length() { return list.length(); }
    // public void		addOn(nts.io.Log log)
    // { nts.typo.TypoCommand.addItemsOn(log, list.nodes()); }
    // XXX

  }

  private class Hyphenator {

    private final CharCode[] word;
    private final FontMetric metric;
    private final Hyphens hyphens;
    private final CharCode hyphCode;
    private final NodeList source;
    private final int beg;
    private final int end;

    public Hyphenator(
        CharCode[] word,
        FontMetric metric,
        Hyphens hyphens,
        CharCode hyphCode,
        NodeList source,
        int beg,
        int end) {
      this.word = word;
      this.metric = metric;
      this.hyphens = hyphens;
      this.hyphCode = hyphCode;
      this.source = source;
      this.beg = beg;
      this.end = end;
    }

    private int hyphenDone;
    private int passed;

    /* TeXtp[913] */
    public NodeList hyphenated() {
      findBoundary();
      NodeList result = new NodeList();
      result.append(source.nodes(0, replaceFrom()));
      Appender pre = new Appender();
      Appender post = new Appender();
      Appender list = new Appender();
      int n = word.length;
      // System.err.println("n = " + n);
      hyphenDone = -1;
      // nts.command.Command.normLog.startLine()
      // .add("Reconstituing nodes from: ").add(0).endLine();
      for (int j = -1; j < n; ) {
        int l = j;
        // nts.command.Command.normLog.startLine()
        // .add("next iteration from: ").add(j + 1).endLine();
        // System.err.println("l = j = " + j);
        j = passingReconstitute(j, n, list);
        // System.err.println("j = " + j + ", passed = " + passed);
        if (passed < 0) {
          result.append(list.takeList());
          if (hyphenAt(j)) {
            l = j;
            passed = j;
          }
        }
        // nts.command.Command.normLog.startLine()
        // .add("j = ").add(j + 1)
        // .add(", hyphen_passed = ").add((passed < 0) ? 0 : passed)
        // .endLine();
        while (passed >= 0) {
          // nts.command.Command.normLog.startLine()
          // .add("no break list (length: ")
          // .add(list.length()).add("):").add(list);
          hyphenDone = passed;
          hyphenReconstitute(l, passed, pre);
          // nts.command.Command.normLog.startLine()
          // .add("pre break list:").add(pre);
          boolean bound = true;
          int i = passed;
          do {
            do {
              i = reconstitute(i, n, post, bound);
              // nts.command.Command.normLog.startLine()
              // .add("post break list (prolonged):")
              // .add(post);
              bound = false;
            } while (i < j);
            while (j < i) j = reconstitute(j, n, list, false);
            // nts.command.Command.normLog.startLine()
            // .add("no break list (prolonged) (length: ")
            // .add(list.length()).add("):").add(list);
          } while (i < j);
          NodeList main = list.takeList();
          if (main.length() > DiscretionaryNode.MAX_LIST_RECONS_LENGTH) {
            result.append(main);
            pre.clear();
            post.clear();
          } else result.append(new DiscretionaryNode(pre.takeList(), post.takeList(), main));
          passed = (hyphenAt(j)) ? j : -1;
          l = j;
          // if (passed >= 0)
          // nts.command.Command.normLog
          // .add("and appending not justified hyphen ...")
          // .endLine();
        }
      }
      // nts.command.Command.normLog.startLine()
      // .add("Hyphenation done").endLine().endLine();
      return result.append(source.nodes(end));
    }

    private int passingReconstitute(int j, int n, TreatNode proc) {
      WordRebuilder reb;
      if (j >= 0) reb = metric.getWordRebuilder(proc, false);
      else {
        j = 0;
        reb = firstRebuilder(proc);
      }
      for (passed = -1; ; j++) {
        if (passed < 0 && hyphenAt(j) && reb.prolongsCut(hyphCode)) passed = j;
        if (j >= n) finalClose(reb);
        else {
          // System.err.println("word[" + j + "] = " + word[j]);
          byte how = reb.addIfBelongsToCut(word[j]);
          // System.err.println("how = " + how);
          if (passed < 0 && how != WordRebuilder.INDEPENDENT && hyphenAt(j)) passed = j;
          if (how == WordRebuilder.BELONGING) continue;
        }
        return j;
      }
    }

    private void hyphenReconstitute(int j, int n, TreatNode proc) {
      WordRebuilder reb;
      if (j >= 0) reb = metric.getWordRebuilder(proc, false);
      else {
        j = 0;
        reb = firstRebuilder(proc);
      }
      while (j < n) reb.add(word[j++]);
      if (!reb.add(hyphCode)) complain(metric, hyphCode);
      reb.close(true);
    }

    private int reconstitute(int j, int n, TreatNode proc, boolean bound) {
      WordRebuilder reb = metric.getWordRebuilder(proc, bound);
      for (; ; j++) {
        // System.err.println("reconstitute: j = " + j + ", n = " + n
        // + ", bound = " + bound);
        if (j >= n) {
          finalClose(reb);
          return j;
        } else {
          byte code = reb.addIfBelongsToCut(word[j]);
          // System.err.println("word[j] = " + word[j]
          // + ", code = " + code);
          if (code != WordRebuilder.BELONGING) return j;
          // if (reb.addIfBelongsToCut(word[j])
          // != WordRebuilder.BELONGING) return j;
        }
      }
    }

    private boolean hyphenAt(int pos) {
      return (pos > hyphenDone && hyphens.hyphenAt(pos));
    }

    private int replaceFrom() {
      return (beg > 0 && metric.equals(source.nodeAt(beg - 1).uniformMetric())) ? beg - 1 : beg;
    }

    private WordRebuilder firstRebuilder(TreatNode proc) {
      if (beg > 0) {
        FontMetric prevMetric = source.nodeAt(beg - 1).uniformMetric();
        if (prevMetric == FontMetric.NULL) {
          return source.nodeAt(beg).makeRebuilder(proc, false);
        } else if (metric.equals(prevMetric)) {
          return source.nodeAt(beg - 1).makeRebuilder(proc, true);
        }
      }
      return metric.getWordRebuilder(proc, true);
    }

    private CharCode boundary = CharCode.NULL;
    private boolean rightHit = false;

    private void findBoundary() {
      if (end < source.length()) {
        Node node = source.nodeAt(end);
        if (metric.equals(node.uniformMetric())) {
          Name.Buffer buf = new Name.Buffer();
          node.contributeCharCodes(buf);
          if (buf.length() > 0) boundary = buf.codeAt(0);
        }
      }
      if (boundary == CharCode.NULL) rightHit = source.nodeAt(end - 1).rightBoundary();
    }

    private void finalClose(WordRebuilder reb) {
      if (boundary != CharCode.NULL) reb.close(boundary);
      else reb.close(rightHit);
    }
  }

  protected abstract CharCode hyphenChar(FontMetric metric);

  protected abstract void complain(FontMetric metric, CharCode code);
}
