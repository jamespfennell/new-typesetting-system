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
// Filename: nts/typo/SpecialPrim.java
// $Id: SpecialPrim.java,v 1.1.1.1 2000/05/27 02:14:03 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Prim;
import nts.command.Token;
import nts.command.TokenList;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.BaseNode;
import nts.node.SettingContext;
import nts.node.TypeSetter;

public class SpecialPrim extends BuilderPrim {

  public SpecialPrim(String name) {
    super(name);
  }

  /* TeXtp[1354] */
  public void exec(Builder bld, Token src) {
    bld.addNode(new SpecialNode(Prim.scanTokenList(src, true)));
  }

  protected class SpecialNode extends BaseNode {
    /* root corresponding to whatsit_node */

    protected TokenList list;

    public SpecialNode(TokenList list) {
      this.list = list;
    }

    public boolean sizeIgnored() {
      return true;
    }

    /* TeXtp[1356] */
    public void addOn(Log log, CntxLog cntx) {
      addNodeToks(log.addEsc("special"), list);
    }

    /* TeXtp[1368] */
    public void typeSet(TypeSetter setter, SettingContext sctx) {
      Log log = getIOHandler().makeStringLog();
      log.add(list);
      setter.setSpecial(log.toString().getBytes());
      // XXX getBytes() is not proper
    }

    public byte beforeWord() {
      return SKIP;
    }

    public byte afterWord() {
      return SUCCESS;
    }
  }
}
