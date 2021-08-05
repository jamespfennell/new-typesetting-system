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
// Filename: nts/command/GroupCommand.java
// $Id: GroupCommand.java,v 1.1.1.1 2000/04/16 10:26:00 ksk Exp $
package nts.command;

import nts.base.ClassAssoc;

public abstract class GroupCommand extends Command {

  private static ClassAssoc assoc = new ClassAssoc(Group.class);

  public static void registerGroup(Class grp) {
    assoc.record(grp);
  }

  public final GroupCommand defineClosing(Class grp, Closing clos) {
    assoc.put(grp, this, clos);
    return this;
  }

  public final Closing getClosing(Class grp) {
    return (Closing) assoc.get(grp, this);
  }

  public final void exec(Token src) {
    Group grp = getGrp();
    Closing clos = getClosing(grp.getClass());
    if (clos == Closing.NULL) exec(grp, src);
    else clos.exec(grp, src);
  }

  public abstract void exec(Group grp, Token src);
}
