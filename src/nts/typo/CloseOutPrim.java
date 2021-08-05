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
// Filename: nts/typo/CloseOutPrim.java
// $Id: CloseOutPrim.java,v 1.1.1.1 2000/05/26 21:14:44 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Token;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.SettingContext;
import nts.node.TypeSetter;

public class CloseOutPrim extends BuilderPrim {

  private WritePrim write;

  public CloseOutPrim(String name, WritePrim write) {
    super(name);
    this.write = write;
  }

  private void close(int num) {
    write.set(num, Log.NULL);
  }

  public void exec(Builder bld, Token src) {
    bld.addNode(new CloseOutNode(scanInt()));
  }

  /* TeXtp[1353] */
  public boolean immedExec(Token src) {
    close(scanInt());
    return true;
  }

  /* STRANGE
   * \closeout alows any number as a parameter (in spite of \closein and
   * \openout which alow only 0-15). The reason is, that the close_node has
   * the same size as write_node - see TeXtp[1350].
   */

  protected class CloseOutNode extends WritePrim.FileNode {
    /* corresponding to whatsit_node */

    public CloseOutNode(int num) {
      super(num);
    }

    /* TeXtp[1356] */
    public void addOn(Log log, CntxLog cntx) {
      addName(log, "closeout");
    }

    /* TeXtp[1366, 1367] */
    public void typeSet(TypeSetter setter, SettingContext sctx) {
      if (sctx.allowIO) close(num);
    }
  }
}
