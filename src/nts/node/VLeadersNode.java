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
// Filename: nts/node/VLeadersNode.java
// $Id: VLeadersNode.java,v 1.1.1.1 2001/03/06 14:55:42 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CntxLog;
import nts.io.Log;

public class VLeadersNode extends VSkipNode {
  /* corresponding to glue_node */

  protected final Leaders lead;

  public VLeadersNode(Glue skip, Leaders lead) {
    super(skip);
    this.lead = lead;
  }

  public Dimen getWidth() {
    return lead.getWidth();
  }

  public Dimen getLeftX() {
    return lead.getLeftX();
  }

  protected boolean allegedlyVisible() {
    return true;
  }

  public void addOn(Log log, CntxLog cntx) {
    lead.addOn(log, cntx, skip);
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {
    TypeSetter.Mark here = setter.mark();
    setter.moveUp(getHeight(sctx.setting));
    lead.typeSet(setter, sctx.setting.set(skip, true), sctx);
    here.move();
  }

  public static final BoxLeaders.Mover BOX_MOVER =
      new BoxLeaders.Mover() {

        public Dimen offset(TypeSetter.Mark start) {
          return start.yDiff();
        }

        public Dimen size(Node node) {
          return node.getHeight().plus(node.getDepth());
        }

        public void back(TypeSetter setter, Dimen gap) {
          setter.moveUp(gap);
        }

        public void move(TypeSetter setter, Dimen gap) {
          setter.moveDown(gap);
        }

        public void movePrev(TypeSetter setter, Node node) {
          setter.moveDown(node.getHeight());
          setter.syncHoriz();
          setter.syncVert();
        }

        public void movePast(TypeSetter setter, Node node) {
          setter.moveDown(node.getDepth());
        }
      };

  public String toString() {
    return "VLeaders(" + skip + "; " + lead + ')';
  }
}
