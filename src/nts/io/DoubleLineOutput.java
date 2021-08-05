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
// Filename: nts/io/DoubleLineOutput.java
// $Id: DoubleLineOutput.java,v 1.1.1.1 1999/04/19 07:37:15 ksk Exp $
package nts.io;

public final class DoubleLineOutput extends LineOutput {

  LineOutput first;
  LineOutput second;

  public DoubleLineOutput(LineOutput first, LineOutput second) {
    this.first = first;
    this.second = second;
  }

  public final void add(char ch) {
    first.add(ch);
    second.add(ch);
  }

  public final void add(String str) {
    first.add(str);
    second.add(str);
  }

  public final void add(CharCode code) {
    first.add(code);
    second.add(code);
  }

  public final void endLine() {
    first.endLine();
    second.endLine();
  }

  public final void flush() {
    first.flush();
    second.flush();
  }

  public final void close() {
    first.close();
    second.close();
  }

  public final void setStartLine() {
    first.setStartLine();
    second.setStartLine();
  }

  public final boolean isStartLine() {
    return (first.isStartLine() && second.isStartLine());
  }

  public final boolean stillFits(int count) {
    return (first.stillFits(count) && second.stillFits(count));
  }

  public final void resetCount() {
    first.resetCount();
    second.resetCount();
  }

  public final int getCount() {
    int x = first.getCount();
    int y = second.getCount();
    return (x > y) ? x : y;
  }

  public final LineOutput voidCounter() {
    return new DoubleLineOutput(first.voidCounter(), second.voidCounter());
  }
}
