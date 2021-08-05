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
// Filename: nts/tfm/TeXFontMetric.java
// $Id: TeXFontMetric.java,v 1.1.1.1 2001/02/09 12:36:21 ksk Exp $
package nts.tfm;

import nts.base.BinFraction;
import nts.base.Dimen;
import nts.base.Num;
import nts.io.CharCode;
import nts.io.CntxLog;
import nts.io.Log;
import nts.io.Name;
import nts.node.BaseFontMetric;
import nts.node.BaseNode;
import nts.node.Box;
import nts.node.BoxSizes;
import nts.node.ChrKernNode;
import nts.node.DiscretionaryNode;
import nts.node.FontMetric;
import nts.node.GlueSetting;
import nts.node.HBoxNode;
import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.NodeList;
import nts.node.SettingContext;
import nts.node.TreatNode;
import nts.node.TypeSetter;
import nts.node.VBoxNode;
import nts.node.WordBuilder;
import nts.node.WordRebuilder;

public class TeXFontMetric extends BaseFontMetric {

  private static final int[] pTab = new int[NUMBER_OF_DIMEN_PARAMS];

  /* Each element is either initialised explicitly or it has a negative
   * value
   */

  static {
    for (int i = 0; i < pTab.length; pTab[i++] = -1)
      ;

    pTab[DIMEN_PARAM_SLANT] = TeXFm.FP_SLANT;
    pTab[DIMEN_PARAM_SPACE] = TeXFm.FP_SPACE;
    pTab[DIMEN_PARAM_STRETCH] = TeXFm.FP_STRETCH;
    pTab[DIMEN_PARAM_SHRINK] = TeXFm.FP_SHRINK;
    pTab[DIMEN_PARAM_X_HEIGHT] = TeXFm.FP_X_HEIGHT;
    pTab[DIMEN_PARAM_QUAD] = TeXFm.FP_QUAD;
    pTab[DIMEN_PARAM_EXTRA_SPACE] = TeXFm.FP_EXTRA_SPACE;
    pTab[DIMEN_PARAM_MATH_X_HEIGHT] = TeXMathSymFm.FP_MATH_X_HEIGHT;
    pTab[DIMEN_PARAM_MATH_QUAD] = TeXMathSymFm.FP_MATH_QUAD;
    pTab[DIMEN_PARAM_NUM1] = TeXMathSymFm.FP_NUM1;
    pTab[DIMEN_PARAM_NUM2] = TeXMathSymFm.FP_NUM2;
    pTab[DIMEN_PARAM_NUM3] = TeXMathSymFm.FP_NUM3;
    pTab[DIMEN_PARAM_DENOM1] = TeXMathSymFm.FP_DENOM1;
    pTab[DIMEN_PARAM_DENOM2] = TeXMathSymFm.FP_DENOM2;
    pTab[DIMEN_PARAM_SUP1] = TeXMathSymFm.FP_SUP1;
    pTab[DIMEN_PARAM_SUP2] = TeXMathSymFm.FP_SUP2;
    pTab[DIMEN_PARAM_SUP3] = TeXMathSymFm.FP_SUP3;
    pTab[DIMEN_PARAM_SUB1] = TeXMathSymFm.FP_SUB1;
    pTab[DIMEN_PARAM_SUB2] = TeXMathSymFm.FP_SUB2;
    pTab[DIMEN_PARAM_SUP_DROP] = TeXMathSymFm.FP_SUP_DROP;
    pTab[DIMEN_PARAM_SUB_DROP] = TeXMathSymFm.FP_SUB_DROP;
    pTab[DIMEN_PARAM_DELIM1] = TeXMathSymFm.FP_DELIM1;
    pTab[DIMEN_PARAM_DELIM2] = TeXMathSymFm.FP_DELIM2;
    pTab[DIMEN_PARAM_AXIS_HEIGHT] = TeXMathSymFm.FP_AXIS_HEIGHT;
    pTab[DIMEN_PARAM_DEFAULT_RULE_THICKNESS] = TeXMathExtFm.FP_DEFAULT_RULE_THICKNESS;
    pTab[DIMEN_PARAM_BIG_OP_SPACING1] = TeXMathExtFm.FP_BIG_OP_SPACING1;
    pTab[DIMEN_PARAM_BIG_OP_SPACING2] = TeXMathExtFm.FP_BIG_OP_SPACING2;
    pTab[DIMEN_PARAM_BIG_OP_SPACING3] = TeXMathExtFm.FP_BIG_OP_SPACING3;
    pTab[DIMEN_PARAM_BIG_OP_SPACING4] = TeXMathExtFm.FP_BIG_OP_SPACING4;
    pTab[DIMEN_PARAM_BIG_OP_SPACING5] = TeXMathExtFm.FP_BIG_OP_SPACING5;
  }

  public static int numberOfRawDimenPars() {
    return pTab.length;
  }

  public static int rawDimenParNumber(int idx) {
    return pTab[idx];
  }

  private /* final */ Name name;
  private /* final */ TeXFm tfm;
  private /* final */ Dimen atSize;
  private Name ident;

  public TeXFontMetric(Name name, TeXFm tfm, Dimen atSize, Name ident) {
    this.name = name;
    this.tfm = tfm;
    this.atSize = atSize;
    this.ident = ident;
    for (int i = 0; i < pTab.length; i++) if (pTab[i] >= 0) setTfmParam(i, pTab[i]);
  }

  /*
   * If a parameter is provided by underlying TeXFm we initialize the
   * corresponding dimension parameter in FontMetric. Otherwise we leave
   * it to be NULL.
   */

  private void setTfmParam(int i, int tfmIdx) {
    if (0 <= tfmIdx && tfmIdx < tfm.paramCount()) {
      FixWord param = tfm.getParam(tfmIdx);
      if (param != FixWord.NULL) {
        Dimen val = (tfmIdx == tfm.FP_SLANT) ? Dimen.valueOf(param) : atSize.times(param);
        setDimenParam(i, val);
      }
    }
  }

  public Name getName() {
    return name;
  }

  public Dimen getAtSize() {
    return atSize;
  }

  public Name getIdent() {
    return ident;
  }

  public void setIdent(Name ident) {
    this.ident = ident;
  }

  public int getCheckSum() {
    return tfm.getCheckSum();
  }

  public Dimen getDesignSize() {
    return Dimen.valueOf(tfm.getDesignSize());
  }

  /* TeXtp[1261] */
  public void addDescOn(Log log) {
    log.add(name);
    if (!atSize.equals(tfm.getDesignSize())) log.add(" at " + atSize + "pt");
  }

  /* TeXtp[134] */
  protected abstract class AnyNode extends BaseNode {
    /* corresponding to char_node, ligature_node */

    private final Dimen width;
    private final Dimen height;
    private final Dimen depth;

    public AnyNode(Dimen w, Dimen h, Dimen d) {
      width = w;
      height = h;
      depth = d;
    }

    public Dimen getWidth() {
      return width;
    }

    public Dimen getLeftX() {
      return Dimen.ZERO;
    }

    public Dimen getHeight() {
      return height;
    }

    public Dimen getDepth() {
      return depth;
    }

    protected boolean allegedlyVisible() {
      return true;
    }

    public final FontMetric getFontMetric() {
      return TeXFontMetric.this;
    }

    public void addOn(Log log, CntxLog cntx) {
      getIdent().addEscapedOn(log);
      log.add(' ');
      finishAddOn(log);
    }

    public FontMetric addShortlyOn(Log log, FontMetric metric) {
      // if (metric != TeXFontMetric.this) {	//SSS
      if (!TeXFontMetric.this.equals(metric)) {
        getIdent().addEscapedOn(log);
        log.add(' ');
        metric = TeXFontMetric.this;
      }
      finishShortlyAddOn(log);
      return metric;
    }

    public Dimen getItalCorr() {
      TeXFm.CharInfo info = tfm.getCharInfo(getIdx());
      return (info != TeXFm.CharInfo.NULL) ? atSize.times(info.getItalic()) : Dimen.NULL;
    }

    public abstract short getIdx();

    public abstract void finishAddOn(Log log);

    public abstract void finishShortlyAddOn(Log log);

    public boolean canBePartOfDiscretionary() {
      return true;
    }

    public boolean kernAfterCanBeSpared() {
      return true;
    }

    public FontMetric uniformMetric() {
      return TeXFontMetric.this;
    }

    public byte afterWord() {
      return SKIP;
    }
  }

  /* TeXtp[134] */
  protected class CharNode extends AnyNode {
    /* root corresponding to char_node */

    private final CharCode code;

    public CharNode(Dimen w, Dimen h, Dimen d, CharCode c) {
      super(w, h, d);
      code = c;
    }

    public short getIdx() {
      return (short) code.toChar();
    }

    public void finishAddOn(Log log) {
      log.add(code);
    }

    public void finishShortlyAddOn(Log log) {
      log.add(code);
    }

    public void typeSet(TypeSetter setter, SettingContext sctx) {
      setter.set(code, TeXFontMetric.this);
    }

    public byte beforeWord() {
      return (code.toCanonicalLetter() != CharCode.NO_CHAR) ? SUCCESS : SKIP;
    }

    public boolean canBePartOfWord() {
      return (code.toCanonicalLetter() != CharCode.NO_CHAR);
    }

    public void contributeCharCodes(Name.Buffer buf) {
      buf.append(code);
    }

    public boolean providesRebuilder(boolean prev) {
      return true;
    }

    public WordRebuilder makeRebuilder(TreatNode proc, boolean prev) {
      return (prev) ? new LigKernBuilder(code, proc) : new LigKernBuilder(false, proc);
    }

    public String toString() {
      return "Char(" + code + ')';
    }
  }

  /* TeXtp[143] */
  protected class LigNode extends AnyNode {
    /* root corresponding to ligature_node */

    private final short index;
    private final Name subst;
    private final boolean leftHit;
    private final boolean rightHit;

    public LigNode(Dimen w, Dimen h, Dimen d, short i, Name s, boolean lh, boolean rh) {
      super(w, h, d);
      index = i;
      subst = s;
      leftHit = lh;
      rightHit = rh;
    }

    public short getIdx() {
      return index;
    }

    public void finishAddOn(Log log) {
      log.add((char) index).add(" (ligature ");
      if (leftHit) log.add('|');
      log.add(subst);
      if (rightHit) log.add('|');
      log.add(')');
    }

    public void finishShortlyAddOn(Log log) {
      log.add(subst);
    }

    public void typeSet(TypeSetter setter, SettingContext sctx) {
      setter.set((char) index, TeXFontMetric.this);
    }

    public byte beforeWord() {
      return (subst.length() > 0 && subst.codeAt(0).toCanonicalLetter() != CharCode.NO_CHAR)
          ? SUCCESS
          : SKIP;
    }

    public boolean canBePartOfWord() {
      for (int i = 0; i < subst.length(); i++)
        if (subst.codeAt(i).toCanonicalLetter() == CharCode.NO_CHAR) return false;
      return true;
    }

    public void contributeCharCodes(Name.Buffer buf) {
      buf.append(subst);
    }

    public boolean rightBoundary() {
      return rightHit;
    }

    public boolean providesRebuilder(boolean prev) {
      return true;
    }

    public WordRebuilder makeRebuilder(TreatNode proc, boolean prev) {
      return (!prev && !leftHit)
          ? new LigKernBuilder(false, proc)
          : (!leftHit || prev && subst.length() > 0)
              ? new LigKernBuilder(index, subst, leftHit, proc)
              : new LigKernBuilder(true, proc);
    }

    public String toString() {
      return "Lig(" + index + "; " + subst + "; " + leftHit + "; " + rightHit + ')';
    }
  }

  protected class LigKernBuilder extends TeXLigKernBuilder {

    protected final TreatNode proc;
    protected final boolean discs;
    protected final boolean zeroKerns;

    public LigKernBuilder(boolean leftBoundary, TreatNode proc, boolean discs, boolean zeroKerns) {
      super(leftBoundary);
      this.proc = proc;
      this.discs = discs;
      this.zeroKerns = zeroKerns;
    }

    public LigKernBuilder(boolean leftBoundary, TreatNode proc) {
      this(leftBoundary, proc, false, false);
    }

    private LigKernBuilder(CharCode code, TreatNode proc) {
      super(code);
      this.proc = proc;
      this.discs = false;
      this.zeroKerns = false;
    }

    private LigKernBuilder(short index, Name subst, boolean leftHit, TreatNode proc) {
      super(index, subst, leftHit);
      this.proc = proc;
      this.discs = false;
      this.zeroKerns = false;
    }

    protected boolean exists(short index) {
      return (tfm.getCharInfo(index) != TeXFm.CharInfo.NULL);
    }

    protected TeXFm.LigKern getLigKern(short left, short right) {
      return tfm.getLigKern(left, right);
    }

    protected void makeChar(CharCode code) {
      TeXFm.CharInfo info = tfm.getCharInfo((short) code.toChar());
      if (info != TeXFm.CharInfo.NULL)
        proc.execute(
            new CharNode(
                atSize.times(info.getWidth()),
                atSize.times(info.getHeight()),
                atSize.times(info.getDepth()),
                code));
      makeDisc(code);
    }

    protected void makeLig(short lig, Name subst, boolean lh, boolean rh) {
      TeXFm.CharInfo info = tfm.getCharInfo(lig);
      if (info != TeXFm.CharInfo.NULL)
        proc.execute(
            new LigNode(
                atSize.times(info.getWidth()),
                atSize.times(info.getHeight()),
                atSize.times(info.getDepth()),
                lig,
                subst,
                lh,
                rh));
      int l = subst.length();
      if (l > 0) makeDisc(subst.codeAt(l - 1));
    }

    protected void makeKern(BinFraction kern) {
      Dimen dim = atSize.times(kern);
      if (zeroKerns || !dim.isZero()) proc.execute(new ChrKernNode(dim));
    }

    private void makeDisc(CharCode last) {
      if (discs) {
        Num num = getNumParam(NUM_PARAM_HYPHEN_CHAR);
        if (num != Num.NULL && last.match(num)) proc.execute(DiscretionaryNode.EMPTY);
      }
    }

    protected Node makeChar(CharCode code, boolean larger) {
      return (larger) ? getLargerNode(code) : getCharNode(code);
    }

    protected Node makeLig(short lig, Name subst, boolean lh, boolean rh, boolean larger) {
      throw new RuntimeException("larger ligatures not supported");
    }
  }

  protected class MathLigKernBuilder extends LigKernBuilder {

    public MathLigKernBuilder(TreatNode proc) {
      super(false, proc, false, true);
    }

    protected void makeLig(short lig, Name subst, boolean lh, boolean rh) {
      TeXFm.CharInfo info = tfm.getCharInfo(lig);
      if (info != TeXFm.CharInfo.NULL) proc.execute(indexNode(lig, info));
      /*
          int		l = subst.length();
          if (l > 0) makeDisc(subst.codeAt(l - 1));
      */
    }

    protected Node makeLig(short lig, Name subst, boolean lh, boolean rh, boolean larger) {
      Node node = Node.NULL;
      TeXFm.CharInfo info = tfm.getCharInfo(lig);
      if (info != TeXFm.CharInfo.NULL)
        node = (larger) ? largerNode(lig, info) : indexNode(lig, info);
      return node;
    }
  }

  private short indexFrom(char chr) {
    return (chr != CharCode.NO_CHAR) ? (short) chr : TeXFm.NO_CHAR_CODE;
  }

  private TeXFm.CharInfo getCharInfo(short index) {
    return (index == TeXFm.NO_CHAR_CODE) ? TeXFm.CharInfo.NULL : tfm.getCharInfo(index);
  }

  private TeXFm.CharInfo getCharInfo(CharCode code) {
    char chr = code.toChar();
    return (chr != CharCode.NO_CHAR) ? tfm.getCharInfo((short) chr) : TeXFm.CharInfo.NULL;
  }

  public Node getCharNode(CharCode code) {
    TeXFm.CharInfo info = getCharInfo(code);
    return (info == TeXFm.CharInfo.NULL)
        ? Node.NULL
        : new CharNode(
            atSize.times(info.getWidth()),
            atSize.times(info.getHeight()),
            atSize.times(info.getDepth()),
            code);
  }

  /*
      public BoxSizes	getCharSizes(CharCode code) {
  	TeXFm.CharInfo	info = getCharInfo(code);
  	return (info == TeXFm.CharInfo.NULL) ? BoxSizes.NULL
  	     : new BoxSizes(atSize.times(info.getHeight()),
  			    atSize.times(info.getWidth()),
  			    atSize.times(info.getDepth()), Dimen.ZERO);
      }
  */

  public Dimen getCharWidth(char chr) {
    TeXFm.CharInfo info = tfm.getCharInfo((short) chr);
    return (info != TeXFm.CharInfo.NULL) ? atSize.times(info.getWidth()) : Dimen.NULL;
  }

  public WordBuilder getWordBuilder(TreatNode proc, boolean boundary, boolean discretionaries) {
    return new LigKernBuilder(boundary, proc, discretionaries, true);
  }

  public WordRebuilder getWordRebuilder(TreatNode proc, boolean boundary) {
    return new LigKernBuilder(boundary, proc);
  }

  public MathWordBuilder getMathWordBuilder(TreatNode proc) {
    return new MathLigKernBuilder(proc);
  }

  /*
   * possible optimization:
   * the nodes can be cached just one for each char, made on demand.
   * getCharSizes() is not necessary, cached node provides the same
   * information.
   */

  // XXX is CharNode necessary? Isn't IndexNode as good as it?
  /* TeXtp[134] */
  protected class IndexNode extends AnyNode {
    /* root corresponding to char_node */

    private final short index;

    public IndexNode(Dimen w, Dimen h, Dimen d, short i) {
      super(w, h, d);
      index = i;
    }

    public short getIdx() {
      return index;
    }

    public void finishAddOn(Log log) {
      log.add((char) index);
    }

    public void finishShortlyAddOn(Log log) {
      log.add((char) index);
    }

    public void typeSet(TypeSetter setter, SettingContext sctx) {
      setter.set((char) index, TeXFontMetric.this);
    }

    public String toString() {
      return "Index(" + index + ')';
    }
  }

  /* TeXtp[749] */
  public Node getLargerNode(CharCode code) {
    Node node = Node.NULL;
    short index = indexFrom(code.toChar());
    if (index != TeXFm.NO_CHAR_CODE) {
      TeXFm.CharInfo info = tfm.getCharInfo(index);
      if (info != TeXFm.CharInfo.NULL) node = largerNode(index, info);
    }
    return node;
  }

  private Node largerNode(short index, TeXFm.CharInfo info) {
    short nextIndex = info.nextChar();
    if (nextIndex != TeXFm.NO_CHAR_CODE) {
      TeXFm.CharInfo nextInfo = tfm.getCharInfo(nextIndex);
      if (nextInfo != TeXFm.CharInfo.NULL) {
        index = nextIndex;
        info = nextInfo;
      }
    }
    return indexNode(index, info);
  }

  /* TeXtp[708,710] */
  public Node getSufficientNode(CharCode code, Dimen desired) {
    short maxIndex = TeXFm.NO_CHAR_CODE;
    TeXFm.CharInfo maxInfo = TeXFm.CharInfo.NULL;
    Dimen maxSize = Dimen.ZERO;
    short index = indexFrom(code.toChar());
    while (index != TeXFm.NO_CHAR_CODE) {
      TeXFm.CharInfo info = tfm.getCharInfo(index);
      if (info == TeXFm.CharInfo.NULL) break;
      if (info.extRep() != TeXFm.NO_CHAR_CODE) return makeExt(info, desired);
      Dimen size = heightPlusDepth(info);
      if (size.moreThan(maxSize)) {
        maxIndex = index;
        maxInfo = info;
        maxSize = size;
        if (!size.lessThan(desired)) break;
      }
      index = info.nextChar();
    }
    return (maxInfo != TeXFm.CharInfo.NULL) ? pretendingCharBox(maxIndex, maxInfo) : Node.NULL;
  }

  /* TeXtp[714,713,711] */
  private Node makeExt(TeXFm.CharInfo info, Dimen size) {
    TeXFm.CharInfo topInfo = getCharInfo(info.extTop());
    TeXFm.CharInfo midInfo = getCharInfo(info.extMid());
    TeXFm.CharInfo botInfo = getCharInfo(info.extBot());
    TeXFm.CharInfo repInfo = getCharInfo(info.extRep());
    if (repInfo != TeXFm.CharInfo.NULL) {
      Dimen total = Dimen.ZERO;
      Dimen rep = heightPlusDepth(repInfo);
      if (topInfo != TeXFm.CharInfo.NULL) total = total.plus(heightPlusDepth(topInfo));
      if (midInfo != TeXFm.CharInfo.NULL) {
        total = total.plus(heightPlusDepth(midInfo));
        rep = rep.times(2);
      }
      if (botInfo != TeXFm.CharInfo.NULL) total = total.plus(heightPlusDepth(botInfo));
      int cnt = 0;
      if (rep.moreThan(0))
        while (total.lessThan(size)) {
          total = total.plus(rep);
          cnt++;
        }
      NodeList list = new NodeList();
      Node repNode = pretendingCharBox(info.extRep(), repInfo);
      if (topInfo != TeXFm.CharInfo.NULL) list.append(pretendingCharBox(info.extTop(), topInfo));
      for (int i = cnt; i-- > 0; list.append(repNode))
        ;
      if (midInfo != TeXFm.CharInfo.NULL) {
        list.append(pretendingCharBox(info.extMid(), midInfo));
        for (int i = cnt; i-- > 0; list.append(repNode))
          ;
      }
      if (botInfo != TeXFm.CharInfo.NULL) list.append(pretendingCharBox(info.extBot(), botInfo));
      Dimen height = (list.isEmpty()) ? Dimen.ZERO : list.nodeAt(0).getHeight();
      Dimen width = atSize.times(repInfo.getWidth()).plus(atSize.times(repInfo.getItalic()));
      return new VBoxNode(
          new BoxSizes(height, width, total.minus(height), Dimen.ZERO), GlueSetting.NATURAL, list);
    }
    return Node.NULL;
  }

  /* TeXtp[740] */
  public Box getFittingWidthBox(CharCode code, Dimen desired) {
    Box box = Box.NULL;
    short index = indexFrom(code.toChar());
    if (index != TeXFm.NO_CHAR_CODE) {
      TeXFm.CharInfo info = tfm.getCharInfo(index);
      if (info != TeXFm.CharInfo.NULL) {
        short maxIndex;
        TeXFm.CharInfo maxInfo;
        do {
          maxIndex = index;
          maxInfo = info;
          index = info.nextChar();
          if (index == TeXFm.NO_CHAR_CODE) break;
          info = tfm.getCharInfo(index);
        } while (info != TeXFm.CharInfo.NULL && !atSize.times(info.getWidth()).moreThan(desired));
        box = pretendingCharBox(maxIndex, maxInfo);
      }
    }
    return box;
  }

  /* TeXtp[741] */
  public Dimen getKernBetween(CharCode left, CharCode right) {
    TeXFm.LigKern ligKern = tfm.getLigKern(indexFrom(left.toChar()), indexFrom(right.toChar()));
    if (ligKern != TeXFm.LigKern.NULL) {
      FixWord kern = ligKern.getKern();
      if (kern != FixWord.NULL) return atSize.times(kern);
    }
    return Dimen.NULL;
  }

  /* TeXtp[712] */
  private Dimen heightPlusDepth(TeXFm.CharInfo info) {
    return atSize.times(info.getHeight()).plus(atSize.times(info.getDepth()));
  }

  private IndexNode indexNode(short index, TeXFm.CharInfo info) {
    return new IndexNode(
        atSize.times(info.getWidth()),
        atSize.times(info.getHeight()),
        atSize.times(info.getDepth()),
        index);
  }

  /* TeXtp[709] */
  private HBoxNode pretendingCharBox(short index, TeXFm.CharInfo info) {
    IndexNode node = indexNode(index, info);
    Dimen width = node.getWidth();
    Dimen ital = node.getItalCorr();
    if (ital != Dimen.NULL) width = width.plus(ital);
    return new HBoxNode(
        new BoxSizes(node.getHeight(), width, node.getDepth(), node.getLeftX()),
        GlueSetting.NATURAL,
        new NodeList(node));
  }
}
