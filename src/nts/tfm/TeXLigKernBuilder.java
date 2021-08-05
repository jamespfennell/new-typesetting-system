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
// Filename: nts/tfm/TeXLigKernBuilder.java
// $Id: TeXLigKernBuilder.java,v 1.1.1.1 2001/01/28 22:23:03 ksk Exp $
package nts.tfm;

import java.util.Stack;
import nts.base.BinFraction;
import nts.base.BoolPar;
import nts.base.IntPar;
import nts.io.CharCode;
import nts.io.Name;
import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.WordRebuilder;
import nts.tfm.TeXFm.*;

public abstract class TeXLigKernBuilder implements WordRebuilder, MathWordBuilder {

  public static final short NO_CHAR_CODE = TeXFm.NO_CHAR_CODE;

  /** Char, ligature or boundary */
  private interface Cell {

    /** no instance */
    Cell NULL = null;

    /** index to TeXFontMetric tables */
    short getIdx();

    /** Character code for character and ligture */
    CharCode getCode();

    /** Is it left or right boundary? */
    boolean isBoundary();

    /** Make node with possible left or right hit */
    void makeNode(boolean lh, boolean rh);

    /** Make larger node (for math operators) with possible left or right hit */
    Node makeNode(boolean lh, boolean rh, boolean larger);
  }

  /** left hand working cell */
  private Cell left = Cell.NULL;

  /** stack of right hand ligatures, maybe with character on the bottom */
  private Stack<Cell> stack = new Stack<Cell>();

  /** accumulated string of character for a ligature */
  private Name.Buffer buffer = new Name.Buffer();

  /** Did the last character collapsed to previous ligature */
  private boolean collapsed = false;

  /** Did the last character collapsed to previous ligature */
  public boolean lastHasCollapsed() {
    return collapsed;
  }

  /** Push cell on the ligature stack */
  private void push(Cell cell) {
    stack.push(cell);
    collapsed = false;
  }

  /** Pop a cell from the ligature stack */
  private Cell pop() {
    return (Cell) stack.pop();
  }

  /** What is the right hand cell (top of ligature stack) */
  private Cell peek() {
    return (Cell) stack.peek();
  }

  /** Nothing on the right hand side? */
  private boolean empty() {
    return stack.empty();
  }

  /** Cell for ordinary character */
  private class CharCell implements Cell {
    private final CharCode code;

    public CharCell(CharCode code) {
      this.code = code;
    }

    public short getIdx() {
      return (short) code.toChar();
    }

    public CharCode getCode() {
      return code;
    }

    public boolean isBoundary() {
      return false;
    }

    public void makeNode(boolean lh, boolean rh) {
      // System.err.println("... CHAR(" + code + ')');
      makeChar(code);
    }

    public Node makeNode(boolean lh, boolean rh, boolean larger) {
      return makeChar(code, larger);
    }
    // public String	toString() { return "CHAR(" + code + ')'; }
  }

  /** Cell for ligature */
  private class LigCell implements Cell {
    private final short index;
    private final CharCode code;

    public LigCell(short index, CharCode code) {
      this.index = index;
      this.code = code;
    }

    public LigCell(short index) {
      this(index, CharCode.NULL);
    }

    public short getIdx() {
      return index;
    }

    public CharCode getCode() {
      return code;
    }

    public boolean isBoundary() {
      return false;
    }

    public void makeNode(boolean lh, boolean rh) {
      // System.err.println("... LIG(" + index + ", `" + buffer.toName()
      //		       + "', " + lh + ", " + rh + ')');
      makeLig(index, buffer.toName(), lh, rh);
    }

    public Node makeNode(boolean lh, boolean rh, boolean larger) {
      return makeLig(index, buffer.toName(), lh, rh, larger);
    }
    // public String	toString() {
    //     return "LIG(" + index
    //    	  + ((code != CharCode.NULL) ? ", " + code : "") + ')';
    // }
  }

  /** Cell for left or right boundary */
  private static class BoundCell implements Cell {
    public short getIdx() {
      return NO_CHAR_CODE;
    }

    public CharCode getCode() {
      return CharCode.NULL;
    }

    public boolean isBoundary() {
      return true;
    }

    public void makeNode(boolean lh, boolean rh) {
      // System.err.println("... ignoring boundary");
    }

    public Node makeNode(boolean lh, boolean rh, boolean larger) {
      return Node.NULL;
    }
    // public String	toString() { return "BOUNDARY"; }
  }

  /* STRANGE
   * this serves only for reconstitution after hyphenation.
   * isBoundary() returns true which is not correct.
   * But it's compatible with TeX. Maybe it's even a bug.
   */
  /** Cell for right boundary which is ordinary character */
  private static class CharBoundCell extends BoundCell {
    private final CharCode code;

    public CharBoundCell(CharCode code) {
      this.code = code;
    }

    public short getIdx() {
      return (short) code.toChar();
    }
    // public String	toString() { return "BOUNDARY(" + code + ')'; }
  }

  /** Boundary instance */
  private static final Cell BOUNDARY = new BoundCell();

  /**
   * Add one character to ligature/kern building process.
   *
   * @param code the character code to be added.
   * @return |true| if the character exists in the font metric, |false| otherwise.
   */
  /* TeXtp[1034] */
  public boolean add(CharCode code) {
    char index = code.toChar();
    if (index != CharCode.NO_CHAR && exists((short) index)) {
      CharCell cell = new CharCell(code);
      if (left == Cell.NULL) {
        left = cell;
        buffer.append(code);
      } else {
        push(cell);
        proceed();
      }
      return true;
    }
    close();
    return false;
  }

  /** Does the last ligature keep left hand cell (/=:, ...)? */
  private BoolPar keepLeft = new BoolPar();

  /** Does the last ligature keep right hand cell (=:/, ...)? */
  private BoolPar keepRight = new BoolPar();

  /** How many cells to skip after last ligature (&gt;) */
  private IntPar stepOver = new IntPar();

  /* TeXtp[906] */
  public byte addIfBelongsToCut(CharCode code) {
    char index = code.toChar();
    // System.err.println("addIfBelongsToCut('" + code + "'):");
    if (index != CharCode.NO_CHAR && exists((short) index)) {
      CharCell cell = new CharCell(code);
      if (left == Cell.NULL) {
        left = cell;
        buffer.append(code);
        // System.err.println("virgin rebuilder");
      } else {
        byte how = INDEPENDENT;
        push(cell);
        while (!empty()) {
          // System.err.print("ligKern(" +  left.getIdx()
          // + ", " + peek().getIdx() + ") = ");
          LigKern lk = getLigKern(left.getIdx(), peek().getIdx());
          if (lk != LigKern.NULL) {
            how = AFFECTING;
            short lig = lk.getLig(keepLeft, keepRight, stepOver);
            if (lig != NO_CHAR_CODE) {
              // System.err.println("lig: " + lig);
              ligStep(lig);
              continue;
            } else {
              FixWord kern = lk.getKern();
              if (kern != FixWord.NULL) {
                // System.err.println("kern: " + kern);
                if (peek() == cell) {
                  pop();
                  close();
                  makeKern(kern);
                  // System.err.println((how == AFFECTING) ?
                  // "AFFECTING" : "INDEPENDENT");
                  return how;
                }
                move();
                makeKern(kern);
                continue;
              }
            }
          }
          // System.err.println("nothing");
          if (peek() == cell) {
            pop();
            close();
            // System.err.println((how == AFFECTING) ?
            // "AFFECTING" : "INDEPENDENT");
            return how;
          }
          move();
        }
      }
      // System.err.println("BELONGING");
      return BELONGING;
    }
    close();
    // System.err.println("INVALID");
    return INDEPENDENT;
  }

  /**
   * Is the character dependent on the previous characters?
   *
   * @param code the character which is tested for independence.
   * @return |false| if it is independent, |true| if the character produces ligature or kern with
   *     characters added before.
   */
  /* TeXtp[909] */
  public boolean prolongsCut(CharCode code) {
    if (left != Cell.NULL) {
      char index = code.toChar();
      if (index != CharCode.NO_CHAR && exists((short) index)) {
        LigKern lk = getLigKern(left.getIdx(), (short) index);
        return (lk != LigKern.NULL);
      }
    }
    return false;
  }

  /** Go on with the ligature/kern process until the ligature stack is empty. */
  /* TeXtp[1040] */
  private void proceed() {
    while (!empty()) {
      // System.err.println("left = " + left + ", lh = " + leftHit
      // 			+ ", buffer = `" + buffer.toName() + '\'');
      // System.err.println("stack = " + stack + ", rh = " + rightHit);
      LigKern lk = getLigKern(left.getIdx(), peek().getIdx());
      if (lk != LigKern.NULL) {
        short lig = lk.getLig(keepLeft, keepRight, stepOver);
        if (lig != NO_CHAR_CODE) {
          ligStep(lig);
          continue;
        } else {
          FixWord kern = lk.getKern();
          if (kern != FixWord.NULL) {
            // System.err.println("KRN");
            move();
            makeKern(kern);
            // System.err.println("... KRN");
            continue;
          }
        }
      }
      move();
    }
  }

  /*
   * leftHit and rightHit cannot be attribures of LigCell because:
   * (1) both can be replaced by another ligature
   * (2) right ligature can be followed by another
   *     if the right boundary is kept
   */
  private boolean leftHit = false;
  private boolean rightHit = false;

  public TeXLigKernBuilder(boolean leftBoundary) {
    if (leftBoundary) left = BOUNDARY;
  }

  protected TeXLigKernBuilder(CharCode code) {
    left = new CharCell(code);
  }

  protected TeXLigKernBuilder(short index, Name subst, boolean leftHit) {
    left = new LigCell(index);
    buffer.append(subst);
    this.leftHit = leftHit;
  }

  /**
   * Perform the ligature operation. It expect that |keepLeft|, |keepRight| and |stepOver| are set
   * correctly so |LigKern.getLig(keepLeft, keepRight, stepOver)| must be called before and the
   * result must by used as the parameter |lig|.
   *
   * @param lig ligature code, result of previous |LigKern.getLig()|.
   */
  /* TeXtp[1040] */
  private void ligStep(short lig) {

    // StringBuffer	buf = new StringBuffer();
    // if (keepLeft.get()) buf.append('/');
    // buf.append("LIG");
    // if (keepRight.get()) buf.append('/');
    // for (int i = stepOver.get(); i > 0; i--) buf.append('>');
    // System.err.println(buf.toString());

    if (left.isBoundary()) leftHit = true;
    else if (peek().isBoundary()) rightHit = true;
    CharCode code = (keepRight.get()) ? CharCode.NULL : pop().getCode();
    LigCell cell = new LigCell(lig, code);
    if (keepLeft.get()) push(cell);
    else {
      setLeft(cell);
      collapsed = empty();
    }
    for (int i = stepOver.get(); i > 0; i--) move();
    // XXX is collapsed really correct? Test it thoroughly!
  }

  /** Close the building of ligatures/kerns with boundary. */
  public void close(boolean rightBoundary) {
    if (rightBoundary) close(BOUNDARY);
    else close();
  }

  /**
   * Close the building of ligatures/kerns with special character boundary.
   *
   * @param code the character which is treated like boundary.
   */
  public void close(CharCode code) {
    close(new CharBoundCell(code));
  }

  /**
   * Close the building of ligatures/kerns.
   *
   * @param cell an explicitly given boundary cell.
   */
  private void close(Cell cell) {
    if (left != Cell.NULL && !left.isBoundary()) {
      push(cell);
      proceed();
    }
    close();
  }

  /** Close the building of ligatures/kerns. */
  public void close() {
    if (left != Cell.NULL) {
      left.makeNode(leftHit, rightHit);
      left = Cell.NULL;
    }
    leftHit = rightHit = false;
    buffer.clear();
    // System.err.println("========================================");
    // System.err.println();
  }

  /**
   * Give the node produced from left hand cell.
   *
   * @param larger give larger variant (for math operators) if true.
   * @return node or |Node.NULL| if there was nothing left.
   */
  private Node takeLastNode(boolean larger) {
    Node node = Node.NULL;
    if (left != Cell.NULL) {
      node = left.makeNode(leftHit, rightHit, larger);
      left = Cell.NULL;
    }
    leftHit = rightHit = false;
    buffer.clear();
    return node;
  }

  /**
   * Move the building ligature cursor one step. Left hand cell produces its output and is forgoten.
   * Right hand cell (top of the ligature stack) moves to the left.
   */
  /* TeXtp[1036,1037] */
  private void move() {
    move(pop());
  }

  /**
   * Move the building ligature cursor one step. Left hand cell produces its output and is forgoten.
   * Right hand cell moves to the left.
   *
   * @param right explicitly given right hand cell
   */
  /* TeXtp[1036,1037] */
  private void move(Cell right) {
    if (rightHit && right.isBoundary()) {
      left.makeNode(leftHit, true);
      rightHit = false;
    } else left.makeNode(leftHit, false);
    if (!left.isBoundary()) leftHit = false;
    buffer.clear();
    setLeft(right);
  }

  /**
   * Set left hand cell and accumulate its character code to buffer.
   *
   * @param cell the cell to be set.
   */
  private void setLeft(Cell cell) {
    left = cell;
    CharCode code = cell.getCode();
    if (code != CharCode.NULL) buffer.append(code);
  }

  public Node takeLastNode() {
    return takeLastNode(false);
  }

  public Node takeLastLargerNode() {
    return takeLastNode(true);
  }

  protected abstract boolean exists(short index);

  protected abstract LigKern getLigKern(short left, short right);

  protected abstract void makeChar(CharCode code);

  protected abstract void makeLig(short lig, Name subst, boolean lh, boolean rh);

  protected abstract void makeKern(BinFraction kern);

  protected abstract Node makeChar(CharCode code, boolean larger);

  protected abstract Node makeLig(short lig, Name subst, boolean lh, boolean rh, boolean larger);
}
