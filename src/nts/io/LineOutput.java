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
// Filename: nts/io/LineOutput.java
// $Id: LineOutput.java,v 1.1.1.1 2000/01/27 14:30:40 ksk Exp $
package nts.io;

public abstract class LineOutput {

  public static final LineOutput NULL = null;

  public abstract void add(char ch);

  public abstract void add(String str);

  public abstract void add(CharCode code);

  public abstract void endLine();

  public abstract void setStartLine();

  public abstract boolean isStartLine();

  public abstract boolean stillFits(int count);

  public abstract void resetCount();

  public abstract int getCount();

  public abstract LineOutput voidCounter();

  public void startLine() {
    if (!isStartLine()) endLine();
  }

  public void flush() {}

  public void close() {}
}
