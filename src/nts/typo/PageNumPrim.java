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
// Filename: nts/typo/PageNumPrim.java
// $Id: PageNumPrim.java,v 1.1.1.1 2000/01/03 14:03:43 ksk Exp $
package nts.typo;

import nts.base.IntProvider;
import nts.base.Num;
import nts.command.AssignPrim;
import nts.command.Token;

public abstract class PageNumPrim extends AssignPrim implements IntProvider, Num.Provider {

  protected Page.List page;

  public PageNumPrim(String name, Page.List page) {
    super(name);
    this.page = page;
  }

  /* STRANGE
   * \global\pagegoal is allowed but has no effect
   */
  /* TeXtp[1246] */
  protected void assign(Token src, boolean glob) {
    skipOptEquals();
    int num = scanInt();
    if (page.canChangeNums()) set(num);
  }

  /* TeXtp[421] */
  public boolean hasNumValue() {
    return true;
  }

  public final Num getNumValue() {
    return Num.valueOf(get());
  }

  public final int intVal() {
    return get();
  }

  protected abstract int get();

  protected abstract void set(int num);
}
