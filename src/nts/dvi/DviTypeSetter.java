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
// Filename: nts/dvi/DviTypeSetter.java
// $Id: DviTypeSetter.java,v 1.1.1.1 2001/04/27 19:09:53 ksk Exp $
package nts.dvi;

import java.io.OutputStream;
import java.util.HashMap;
import nts.base.Dimen;
import nts.io.CharCode;
import nts.node.FontMetric;
import nts.node.TypeSetter;

public class DviTypeSetter extends DviFormatWriter implements TypeSetter {

  public static final DviTypeSetter NULL = null;

  public interface FontInfo {
    FontInfo NULL = null;

    int getIdNumber();

    byte[] getDirName();

    byte[] getFileName();

    int getCheckSum();

    Dimen getAtSize();

    Dimen getDesignSize();

    Dimen getCharWidth(char ch);
  }

  public interface FontInformator {
    FontInfo getInfo(FontMetric metric);
  }

  protected static final int DVI_NUM = 25400000;
  protected static final int DVI_DEN = 473628672;
  protected static final int DIM_DEN = 0x10000;

  private static int dim(Dimen x) {
    return x.toInt(DIM_DEN);
  }

  private /* final */ FontInformator fontInf;

  public DviTypeSetter(OutputStream out, FontInformator inf, int mag, byte[] comm, int size) {
    super(out, DVI_NUM, DVI_DEN, mag, comm, size);
    fontInf = inf;
  }

  public DviTypeSetter(OutputStream out, FontInformator inf, int mag, byte[] comm) {
    this(out, inf, mag, comm, 0);
  }

  private int currX = 0;
  private int currY = 0;

  public void moveLeft(Dimen x) {
    currX -= dim(x);
    tryToSetPendRule();
  }

  public void moveRight(Dimen x) {
    currX += dim(x);
    tryToSetPendRule();
  }

  public void moveUp(Dimen y) {
    currY -= dim(y);
  }

  public void moveDown(Dimen y) {
    currY += dim(y);
  }

  private HashMap fontMap = new HashMap();
  private FontMetric currMetric = FontMetric.NULL;
  private FontInfo currInfo = FontInfo.NULL;

  public void set(char ch, FontMetric metric) {
    syncHoriz();
    syncVert();
    if (!metric.equals(currMetric)) {
      FontInfo info = (FontInfo) fontMap.get(metric);
      if (info == FontInfo.NULL) {
        info = fontInf.getInfo(metric);
        fontMap.put(metric, info);
        defFont(
            info.getIdNumber(),
            info.getCheckSum(),
            dim(info.getAtSize()),
            dim(info.getDesignSize()),
            info.getDirName(),
            info.getFileName());
      }
      setFont(info.getIdNumber());
      currMetric = metric;
      currInfo = info;
    }
    Dimen w = currInfo.getCharWidth(ch);
    if (w == Dimen.NULL) putChar(ch);
    else {
      setChar(ch);
      dviX += dim(w);
    }
  }

  public void set(CharCode code, FontMetric metric) {
    set(code.toChar(), metric);
  }

  static final PendRule NULL_PEND_RULE = null;

  private class PendRule {
    private final int height;
    private final int width;

    PendRule(int h, int w) {
      height = h;
      width = w;
    }

    void put() {
      putRule(height, width);
    }

    void set() {
      setRule(height, width);
      dviX += width;
    }

    boolean canBeSet() {
      return (currX == dviX + width);
    }
  }

  private PendRule pendRule = NULL_PEND_RULE;

  private void putPendRule() {
    if (pendRule != NULL_PEND_RULE) {
      pendRule.put();
      pendRule = NULL_PEND_RULE;
    }
  }

  private void tryToSetPendRule() {
    if (pendRule != NULL_PEND_RULE && pendRule.canBeSet()) {
      pendRule.set();
      pendRule = NULL_PEND_RULE;
    }
  }

  public void setRule(Dimen h, Dimen w) {
    int height = dim(h);
    int width = dim(w);
    if (height > 0 && width > 0) {
      syncHoriz();
      syncVert();
      pendRule = new PendRule(height, width);
    }
  }

  public void setSpecial(byte[] spec) {
    syncHoriz();
    syncVert();
    super.setSpecial(spec);
  }

  public TypeSetter.Mark mark() {
    return new TypeSetter.Mark() {

      private final int x = currX;
      private final int y = currY;

      public void move() {
        currX = x;
        currY = y;
      }

      public Dimen xDiff() {
        return Dimen.valueOf(currX - x, DIM_DEN);
      }

      public Dimen yDiff() {
        return Dimen.valueOf(currY - y, DIM_DEN);
      }
    };
  }

  // XXX provide also mark origin
  // XXX (for example for uniform visualisation dashed lines)

  private static class Level {
    static final Level NULL = null;
    final int x, y;
    final Level next;

    Level(int x, int y, Level next) {
      this.x = x;
      this.y = y;
      this.next = next;
    }
  }

  private int dviX = 0;
  private int dviY = 0;
  private Level stack = Level.NULL;

  public void syncHoriz() {
    putPendRule();
    if (dviX != currX) {
      moveX(currX - dviX);
      dviX = currX;
    }
  }

  public void syncVert() {
    putPendRule();
    if (dviY != currY) {
      moveY(currY - dviY);
      dviY = currY;
    }
  }

  public void push() {
    putPendRule();
    super.push();
    stack = new Level(dviX, dviY, stack);
  }

  public void pop() {
    putPendRule();
    if (stack != Level.NULL) {
      dviX = stack.x;
      dviY = stack.y;
      stack = stack.next;
      super.pop();
    } else throw new RuntimeException("too many pops in DVI");
  }

  private int maxH = 0;
  private int maxW = 0;

  public void startPage(Dimen yOffset, Dimen xOffset, Dimen height, Dimen width, int[] nums) {
    int y = dim(yOffset);
    int x = dim(xOffset);
    int h = dim(height) + y;
    int w = dim(width) + x;
    if (maxH < h) maxH = h;
    if (maxW < w) maxW = w;
    currMetric = FontMetric.NULL;
    currInfo = FontInfo.NULL;
    dviX = dviY = 0;
    stack = Level.NULL;
    currX = x;
    currY = y;
    startPage(nums);
  }

  public void endPage() {
    putPendRule();
    super.endPage();
  }

  public void close() {
    putPendRule();
    close(maxH, maxW);
  }
}
