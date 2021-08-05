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
// Filename: nts/tfm/PrintWriterPLDumper.java
// $Id: PrintWriterPLDumper.java,v 1.1.1.1 1999/06/05 04:18:58 ksk Exp $
package nts.tfm;

import java.io.PrintWriter;

/**
 * Prints a property lists to |PrintWriter| in the same format as tftopl program.
 *
 * <p>This implementaion of |PLDumper| produces exactly the same formatting as tftopl and sends it
 * output to associated |PrintWriter|.
 */
class PrintWriterPLDumper implements PLDumper {

  /** |PrintWriter| the output be sended to */
  private PrintWriter output;

  /** Nesting level of the current property list */
  private int level = 0;

  /** |true| on the beginning of the output line */
  private boolean newLine = true;

  /**
   * Creates new |PrintWriterPLDumper| with associated |PrintWriter|.
   *
   * @param o the output |PrintWriter|.
   */
  public PrintWriterPLDumper(PrintWriter o) {
    output = o;
  }

  /**
   * Prints single character on the output.
   *
   * @param c the character to be printed.
   * @return |this| reference for subsequent printing.
   */
  private PrintWriterPLDumper out(char c) {
    output.print(c);
    newLine = false;
    return this;
  }

  /**
   * Prints a character string on the output.
   *
   * @param s the string to be printed.
   * @return |this| reference for subsequent printing.
   */
  private PrintWriterPLDumper out(String s) {
    output.print(s);
    newLine = false;
    return this;
  }

  /*
   * The next three methods |outLn|, |open| and |close| correspond
   * to the |out_ln|, |left| and |right| functions in TFtoPL[34].
   */

  /**
   * Prints new line and the apropriate amount of indentation.
   *
   * @return |this| reference for subsequent printing.
   */
  private PrintWriterPLDumper outLn() {
    output.println();
    newLine = true;
    for (int i = level; i-- > 0; output.print("   "))
      ;
    return this;
  }

  /**
   * Increases nesting level and prints left parenthesis folowed by the property name.
   *
   * @param s the property name.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper open(String p) {
    if (!newLine) outLn();
    ++level;
    return out('(').out(p);
  }

  /**
   * Decreases nesting level and prints right parenthesis.
   *
   * @return |this| reference for subsequent printing.
   */
  public PLDumper close() {
    level--;
    return out(')').outLn();
  }

  /*
   * The following methods for symbolic printing correspond to functions in
   * TFtoPL[35..40].
   */

  /**
   * Prints |'D'| prefix and decimal number.
   *
   * @param i the number to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addDec(int i) {
    return out(" D " + i);
  }

  /**
   * Prints |'O'| prefix and octal number.
   *
   * @param i the number to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addOct(int i) {
    return out(" O " + Integer.toOctalString(i));
  }

  /**
   * Prints |'R'| prefix and real number. An |Object| reference is used for representation of the
   * real number. The |toString| method of such object is used to get the textual representation.
   *
   * @param o the object which represents the real number.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addReal(Object o) {
    return out(" R " + o);
  }

  /** Roman or Italic slope */
  static final char[] RI = {'R', 'I'};

  /** Medium, Bold or Light weight */
  static final char[] MBL = {'M', 'B', 'L'};

  /** Regular, Condensed or Extended expansion */
  static final char[] RCE = {'R', 'C', 'E'};

  /**
   * Prints |'F'| prefix and Xerox face code. The code is printed in the three character
   * slope/weight/expansion form or in octal if the symbolic form cannot be found.
   *
   * @param f the Xerox face code to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addFace(int face) {
    int f = face;
    int ri = f % RI.length;
    f /= RI.length;
    int mbl = f % MBL.length;
    f /= MBL.length;
    int rce = f % RCE.length;
    f /= RCE.length;
    return (f != 0) ? addOct(face) : out(" F ").out(MBL[mbl]).out(RI[ri]).out(RCE[rce]);
  }

  /**
   * Prints a character string after one space.
   *
   * @param s the string to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addStr(String s) {
    return out(' ').out(s);
  }

  /**
   * Prints a symbolic form of boolean value (|"TRUE"| or |"FALSE"|).
   *
   * @param b the boolean value to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addBool(boolean b) {
    return out((b) ? " TRUE" : " FALSE");
  }

  /**
   * If set, the character codes are printed in octal even if they represent a printable character.
   */
  private boolean octChars = false;

  /**
   * Prints symbolic representation of character code. If the character code represents printable
   * character and the member |octChars| is |false| then it prints |'C'| prefix folowed by the
   * character. Otherwise it prints the octal representation (with |'O'| prefix).
   *
   * @param c the character code to be printed.
   * @return |this| reference for subsequent printing.
   */
  public PLDumper addChar(short c) {
    return (!octChars && ('0' <= c && c <= '9' || 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
        ? out(" C ").out((char) c)
        : addOct(c);
  }

  /**
   * Tells the |PrintWriterPLDumper| that it should always print character codes in numerical
   * (octal) format.
   */
  public void forceNumChars() {
    octChars = true;
  }

  /** Finishes all posibly unclosed property lists and closes the associated |PrintWriter|. */
  public void closeOutput() {
    while (level-- > 0) close();
    output.close();
  }
}
