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
// Filename: nts/noad/Conversion.java
// $Id: Conversion.java,v 1.1.1.1 2000/10/17 13:40:54 ksk Exp $
package nts.noad;

import java.util.Iterator;
import java.util.Vector;
import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.io.CharCode;
import nts.node.Box;
import nts.node.FontMetric;
import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.PenaltyNode;
import nts.node.TreatNode;

public class Conversion implements TransfConstants {

  private /* final */ NoadEnumStack stack;
  private /* final */ ConvStyle convStyle;

  protected Conversion(NoadEnum noads, ConvStyle convStyle) {
    stack = new NoadEnumStack(noads);
    this.convStyle = convStyle;
  }

  /* TeXtp[726] */
  protected NodeList convert() {
    Hen converter = new Hen(convStyle);
    Clutch clutch = new Clutch();
    while (stack.hasMoreNoads()) {
      Noad noad = stack.nextNoad();
      boolean again;
      do {
        again = false;
        if (noad.startsWord()) {
          MathWordBuilder word = noad.getMathWordBuilder(converter, clutch.appender());
          if (word == MathWordBuilder.NULL) {
            clutch.add(noad, noad.convert(converter));
            break;
          }
          byte fam = noad.wordFamily();
          clutch.suppressingAllowed = !converter.forcedItalCorr(fam);
          noad.contributeToWord(word);
          for (; ; ) {
            if (!stack.hasMoreNoads()) {
              clutch.addOrd(word.takeLastNode(), false);
              break;
            }
            noad = stack.nextNoad();
            if (noad.canBePartOfWord() && noad.wordFamily() == fam) {
              noad.contributeToWord(word);
              if (noad.finishesWord()) {
                Egg egg = noad.wordFinishingEgg(word, converter);
                // XXX incorrect
                // XXX if last has collapsed but has scripts
                // XXX noad must be changed to an OrdNoad
                if (egg != Egg.NULL) {
                  clutch.add(noad, egg);
                  break;
                }
              }
            } else {
              clutch.addOrd(word.takeLastNode(), false);
              again = true;
              break;
            }
          }
        } else clutch.add(noad, noad.convert(converter));
      } while (again);
    }
    clutch.finish();
    byte leftPenType = SPACING_TYPE_NULL;
    byte leftSpacType = SPACING_TYPE_NULL;
    Coop coop = new Coop(convStyle, clutch.maxHeight, clutch.maxDepth);
    Iterator iter = clutch.iterator();
    while (iter.hasNext()) {
      Egg egg = (Egg) iter.next();
      byte right = egg.spacingType();
      if (!egg.isPenalty()) {
        Num pen = coop.getConv().getPenalty(leftPenType, right);
        if (pen != Num.NULL && pen.lessThan(Node.INF_PENALTY)) coop.append(new PenaltyNode(pen));
      }
      leftPenType = right;
      if (right != SPACING_TYPE_NULL) {
        if (leftSpacType != SPACING_TYPE_NULL) {
          Node space = coop.getConv().getSpacing(leftSpacType, right);
          if (space != Node.NULL) coop.append(space);
        }
        leftSpacType = right;
      }
      egg.chipShell(coop);
      coop.setIgnoreSpace(egg.ignoreNextScriptSpace());
    }
    return coop.getList();
  }

  private class Clutch {

    public Dimen maxHeight = Dimen.ZERO;
    public Dimen maxDepth = Dimen.ZERO;
    private Vector data = new Vector();
    private boolean prevCanPrecedeBin = false;
    private Egg lastInfluencing = VoidEgg.EGG;
    public boolean suppressingAllowed = true;

    public Iterator iterator() {
      return data.iterator();
    }

    public TreatNode appender() {
      return new TreatNode() {
        public void execute(Node node) {
          // XXX misused property of character node
          if (node.uniformMetric() != FontMetric.NULL) addOrd(node, true);
          else addEgg(new SimpleNodeEgg(node));
        }
      };
    }

    public void add(Noad noad, Egg egg) {
      if (noad.influencesBin()) {
        boolean forced = (egg.isBin() && !prevCanPrecedeBin);
        if (forced) {
          egg.dontBeBin();
          prevCanPrecedeBin = true;
        } else {
          if (!noad.canFollowBin() && lastInfluencing.isBin()) lastInfluencing.dontBeBin();
          prevCanPrecedeBin = noad.canPrecedeBin();
        }
        lastInfluencing = egg;
      }
      addEgg(egg);
    }

    public void addOrd(Node node, boolean suppress) {
      prevCanPrecedeBin = true;
      Egg egg = new StItalNodeEgg(node, SPACING_TYPE_ORD);
      if (suppress && suppressingAllowed) egg.suppressItalCorr();
      lastInfluencing = egg;
      addEgg(egg);
    }

    private void addEgg(Egg egg) {
      maxHeight = maxHeight.max(egg.getHeight());
      maxDepth = maxDepth.max(egg.getDepth());
      data.add(egg);
    }

    public void finish() {
      if (lastInfluencing.isBin()) lastInfluencing.dontBeBin();
    }
  }

  protected class Transf implements Transformer {

    protected ConvStyle conv;

    public Transf(ConvStyle conv) {
      this.conv = conv;
    }

    public ConvStyle getConv() {
      return conv;
    }

    public void setStyle(byte style) {
      conv = conv.makeNew(style);
    }

    public byte getStyle() {
      return conv.getStyle();
    }

    public boolean isCramped() {
      return conv.isCramped();
    }

    public Dimen muToPt(Dimen dim) {
      return conv.muToPt(dim);
    }

    public Glue muToPt(Glue skip) {
      return conv.muToPt(skip);
    }

    public Dimen scriptSpace() {
      return conv.scriptSpace();
    }

    public Dimen getDimPar(int param) {
      return conv.getDimPar(param);
    }

    public Dimen getDimPar(int param, byte how) {
      return conv.derived(how).getDimPar(param);
    }

    public Node fetchCharNode(byte fam, CharCode code) {
      return conv.fetchCharNode(fam, code);
    }

    public Node fetchCharNode(byte fam, CharCode code, byte how) {
      return conv.derived(how).fetchCharNode(fam, code);
    }

    public Node fetchLargerNode(byte fam, CharCode code) {
      return conv.fetchLargerNode(fam, code);
    }

    /* var delimiters are not affected by local style change */
    public Node fetchSufficientNode(Delimiter del, Dimen desired) {
      return convStyle.fetchSufficientNode(del, desired);
    }

    public Box fetchFittingWidthBox(byte fam, CharCode code, Dimen desired) {
      return conv.fetchFittingWidthBox(fam, code, desired);
    }

    public Dimen skewAmount(byte fam, CharCode code) {
      return conv.skewAmount(fam, code);
    }

    public Dimen getXHeight(byte fam) {
      return conv.getXHeight(fam);
    }

    /* derived(CURRENT)
     * is necessary for turning penalties off */
    public NodeList convert(NoadEnum noads) {
      return madeOf(noads, conv.derived(CURRENT));
    }

    public NodeList convert(NoadEnum noads, byte how) {
      return madeOf(noads, conv.derived(how));
    }
  }

  protected class Hen extends Transf implements Converter {

    public Hen(ConvStyle conv) {
      super(conv);
    }

    public void push(NoadEnum noads) {
      stack.push(noads);
    }

    public MathWordBuilder getWordBuilder(byte fam, TreatNode proc) {
      return conv.getWordBuilder(fam, proc);
    }

    public boolean forcedItalCorr(byte fam) {
      return conv.forcedItalCorr(fam);
    }
  }

  protected class Coop extends Transf implements Nodery {

    private final Dimen height;
    private final Dimen depth;

    public Coop(ConvStyle conv, Dimen height, Dimen depth) {
      super(conv);
      this.height = height;
      this.depth = depth;
    }

    private NodeList list = new NodeList();
    private boolean ignore = false;

    public NodeList getList() {
      return list;
    }

    public void setIgnoreSpace(boolean ign) {
      ignore = ign;
    }

    public boolean ignoresSpace() {
      return (ignore && conv.isScript());
    }

    public void append(Node node) {
      list.append(node);
    }

    public void append(NodeEnum nodes) {
      list.append(nodes);
    }

    public Dimen delimiterSize() {
      Dimen middle = getDimPar(DP_AXIS_HEIGHT);
      return conv.delimiterSize(height.minus(middle), depth.plus(middle));
    }
  }

  public static NodeList madeOf(NoadEnum noads, ConvStyle conv) {
    return (new Conversion(noads, conv)).convert();
  }
}
