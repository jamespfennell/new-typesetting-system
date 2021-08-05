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
// Filename: nts/node/VLeadRuleNode.java
// $Id: VLeadRuleNode.java,v 1.1.1.1 2001/03/06 14:55:26 ksk Exp $
package nts.node;

import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CntxLog;
import nts.io.Log;

public class VLeadRuleNode extends VSkipNode {
  /* corresponding to glue_node */

  protected final BoxSizes sizes;
  private final String desc;

  public VLeadRuleNode(Glue skip, BoxSizes sizes, String desc) {
    super(skip);
    this.sizes = sizes;
    this.desc = desc;
  }

  public BoxSizes getSizes() {
    return sizes;
  }

  public Dimen getWidth() {
    return sizes.getWidth();
  }

  public Dimen getLeftX() {
    return sizes.getLeftX();
  }

  protected boolean allegedlyVisible() {
    return true;
  }

  /* TeXtp[190] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(desc).add(' ').add(skip.toString());
    cntx.addOn(log, new RuleNode(sizes));
  }

  public void typeSet(TypeSetter setter, SettingContext sctx) {
    BoxSizes leading =
        new BoxSizes(sctx.setting.set(skip, true), sizes.rawWidth(), Dimen.ZERO, sizes.rawLeftX());
    RuleNode.typeSet(setter, leading.replenished(sctx.around));
  }

  public String toString() {
    return "VLeadRule(" + skip + "; " + sizes + "; " + desc + ')';
  }
}
