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
// Filename: nts/noad/NoadEnumStack.java
// $Id: NoadEnumStack.java,v 1.1.1.1 2000/03/20 19:17:47 ksk Exp $
package nts.noad;

import java.util.Stack;

public class NoadEnumStack extends NoadEnum {

  private NoadEnum top;
  private Stack stack = new Stack();

  public NoadEnumStack(NoadEnum top) {
    this.top = top;
  }

  public Noad nextNoad() {
    while (!top.hasMoreNoads())
      if (stack.empty()) return Noad.NULL;
      else top = (NoadEnum) stack.pop();
    return top.nextNoad();
  }

  public boolean hasMoreNoads() {
    while (!top.hasMoreNoads())
      if (stack.empty()) return false;
      else top = (NoadEnum) stack.pop();
    return true;
  }

  public void push(NoadEnum noads) {
    stack.push(top);
    top = noads;
  }
}
