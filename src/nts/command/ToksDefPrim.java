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
// Filename: nts/command/ToksDefPrim.java
// $Id: ToksDefPrim.java,v 1.1.1.1 1999/06/24 10:58:30 ksk Exp $
package nts.command;

public class ToksDefPrim extends ShorthandDefPrim {

  private ToksPrim reg;

  public ToksDefPrim(String name, ToksPrim reg) {
    super(name);
    this.reg = reg;
  }

  protected final Command makeShorthand(int idx) {
    return new Shorthand(idx);
  }

  public class Shorthand extends ShorthandDefPrim.Shorthand implements TokenList.Provider {

    public Shorthand(int idx) {
      super(idx);
    }

    protected RegisterPrim getReg() {
      return reg;
    }

    /**
     * Performs the assignment.
     *
     * @param src source token for diagnostic output.
     * @param glob indication that the assignment is global.
     */
    protected final void assign(Token src, boolean glob) {
      skipOptEquals();
      reg.set(index, AssignPrim.scanToks(src, false), glob);
    }

    public boolean hasToksValue() {
      return true;
    }

    public TokenList getToksValue() {
      return reg.get(index);
    }
  }
}
