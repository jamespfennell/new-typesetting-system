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
// Filename: nts/io/BufferLineOutput.java
// $Id: BufferLineOutput.java,v 1.1.1.1 2001/02/03 13:13:46 ksk Exp $
package nts.io;

public final class BufferLineOutput extends BaseLineOutput
    implements Loggable, CharCode.CodeWriter {

  private CharCode buffer[];
  private int maxCount;
  private int pos;
  private CharCode.Maker maker;

  public BufferLineOutput(int size, int maxCount, CharCode.Maker maker) {
    buffer = new CharCode[size];
    this.maxCount = maxCount;
    this.maker = maker;
    pos = 0;
  }

  public void add(CharCode code) {
    code.writeExpCodes(this);
  }

  public void add(char chr) {
    maker.writeExpCodes(chr, this);
  }

  public void writeCode(CharCode code) {
    if (maxCount <= 0 || pos < maxCount) buffer[pos++ % buffer.length] = code;
    charCount++;
  }

  public void reset() {
    for (int i = 0; i < buffer.length; buffer[i++] = CharCode.NULL)
      ;
    pos = 0;
    resetCount();
  }

  public int size() {
    return (pos <= buffer.length) ? pos : buffer.length;
  }

  public void addOn(Log log, int idx) {
    log.add(codeAt(idx));
  }

  public void addOn(Log log, int beg, int end) {
    while (beg < end) log.add(codeAt(beg++));
  }

  public void addOn(Log log) {
    addOn(log, 0, size());
  }

  private CharCode codeAt(int idx) {
    if (idx >= 0) {
      if (pos <= buffer.length) {
        if (idx < pos) return buffer[idx];
      } else if (idx < buffer.length) return buffer[(pos + idx) % buffer.length];
    }
    throw new ArrayIndexOutOfBoundsException("BufferLineOutput: index out of range: " + idx);
  }
}
