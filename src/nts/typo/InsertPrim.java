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
// Filename: nts/typo/InsertPrim.java
// $Id: InsertPrim.java,v 1.1.1.1 2000/06/22 18:38:45 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.builder.Builder;
import nts.command.Prim;
import nts.command.Token;
import nts.node.InsertNode;
import nts.node.Insertion;
import nts.node.NodeList;
import nts.node.VBoxNode;

public class InsertPrim extends BuilderPrim {

  public InsertPrim(String name) {
    super(name);
  }

  /* TeXtp[1099] */
  public void exec(Builder bld, Token src) {
    int num = Prim.scanRegisterCode();
    int outBoxNum = getConfig().getIntParam(INTP_OUTPUT_BOX_NUM);
    if (num == outBoxNum) {
      num = 0;
      error("CantInsertOutbox", InsertPrim.this, num(outBoxNum), num(num));
    }
    pushLevel(new InsertGroup(num));
    scanLeftBrace();
  }

  public static final int INTP_FLOATING_PENALTY = newIntParam();

  /* TeXtp[1100] */
  public static class InsertGroup extends VertGroup {

    private int num;

    protected InsertGroup(int num) {
      this.num = num;
    }

    private Glue topSkip;
    private Dimen maxDepth;
    private int floatCost;

    public void stop() {
      super.stop();
      topSkip = getConfig().getGlueParam(GLUEP_SPLIT_TOP_SKIP);
      maxDepth = getConfig().getDimParam(DIMP_SPLIT_MAX_DEPTH);
      floatCost = getConfig().getIntParam(INTP_FLOATING_PENALTY);
    }

    public void close() {
      super.close();
      NodeList list = builder.getList();
      VBoxNode vbox = VBoxNode.packedOf(list);
      Dimen size = vbox.getHeight().plus(vbox.getDepth());
      Builder bld = getBld();
      bld.addNode(
          new InsertNode(
              new Insertion(num, list, size, topSkip, maxDepth, Num.valueOf(floatCost))));
      bld.buildPage();
    }
  }
}
