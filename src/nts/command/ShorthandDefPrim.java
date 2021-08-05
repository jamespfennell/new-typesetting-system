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
// Filename: nts/command/ShorthandDefPrim.java
// $Id: ShorthandDefPrim.java,v 1.1.1.1 2001/03/22 13:35:01 ksk Exp $
package nts.command;

import nts.io.Log;

public abstract class ShorthandDefPrim extends AssignPrim {

  public ShorthandDefPrim(String name) {
    super(name);
  }

  protected void assign(Token src, boolean glob) {
    Token tok = definableToken();
    tok.define(Relax.getRelax(), glob);
    skipOptEquals();
    int idx = scanRegisterCode();
    tok.define(makeShorthand(idx), glob);
  }

  protected abstract Command makeShorthand(int idx);

  public abstract static class Shorthand extends Command {

    protected final int index;

    public Shorthand(int idx) {
      index = idx;
    }

    /** Non prefixed version of exec */
    public final void exec(Token src) {
      exec(src, 0);
    }

    public final boolean assignable() {
      return true;
    }

    public final void doAssignment(Token src, int prefixes) {
      exec(src, prefixes);
    }

    /**
     * Performs itself in the process of interpretation of the macro language after sequence of
     * prefix commands.
     *
     * @param src source token for diagnostic output.
     * @param prefixes accumulated code of prefixes.
     */
    public final void exec(Token src, int prefixes) {
      PrefixPrim.beforeAssignment(this, prefixes);
      assign(src, PrefixPrim.globalAssignment(prefixes));
      PrefixPrim.afterAssignment();
    }

    protected abstract void assign(Token src, boolean glob);

    public final void addOn(Log log) {
      log.addEsc(getReg().getEqDesc()).add(index);
    }

    public boolean sameAs(Command cmd) {
      return (getClass() == cmd.getClass()
          && getReg().sameAs(((Shorthand) cmd).getReg())
          && index == ((Shorthand) cmd).index);
    }

    protected abstract RegisterPrim getReg();
  }
}
