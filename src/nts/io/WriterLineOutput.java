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
// Filename: nts/io/WriterLineOutput.java
// $Id: WriterLineOutput.java,v 1.1.1.1 2001/02/03 13:22:24 ksk Exp $
package nts.io;

import java.io.IOException;
import java.io.Writer;

public final class WriterLineOutput extends BaseLineOutput implements CharCode.CharWriter {

  private Writer out;
  private CharCode.Maker maker;
  private boolean worryRoom;
  private int maxLineChars;
  private int numLineChars = 0;
  private String lineSeparator;
  private boolean trouble = false;

  public WriterLineOutput(Writer out, CharCode.Maker maker, boolean worryRoom, int maxLineChars) {
    this.out = out;
    this.maker = maker;
    this.worryRoom = (worryRoom && maxLineChars > 0);
    this.maxLineChars = maxLineChars;
    lineSeparator = System.getProperty("line.separator");
  }

  public WriterLineOutput(Writer out, CharCode.Maker maker) {
    this(out, maker, false, 0);
  }

  public final void add(CharCode code) {
    if (code.isNewLine()) endLine();
    else code.writeExpChars(this);
  }

  public final void add(char chr) {
    if (maker.isNewLine(chr)) endLine();
    else maker.writeExpChars(chr, this);
  }

  public final void writeChar(char chr) {
    try {
      out.write(chr);
      charCount++;
      numLineChars++;
      if (maxLineChars > 0 && numLineChars >= maxLineChars) endLine();
    } catch (IOException x) {
      trouble = true;
    }
  }

  public final void addRaw(String str) {
    try {
      out.write(str);
    } catch (IOException x) {
      trouble = true;
    }
  }

  public final void endLine() {
    try {
      out.write(lineSeparator);
      out.flush();
    } catch (IOException x) {
      trouble = true;
    }
    numLineChars = 0;
  }

  public final void flush() {
    try {
      out.flush();
    } catch (IOException x) {
      trouble = true;
    }
  }

  public final void close() {
    try {
      out.close();
    } catch (IOException x) {
      trouble = true;
    }
  }

  public final void setStartLine() {
    numLineChars = 0;
  }

  public final boolean isStartLine() {
    return (numLineChars == 0);
  }

  public final boolean stillFits(int count) {
    return !(worryRoom && numLineChars + count > maxLineChars);
  }
}
