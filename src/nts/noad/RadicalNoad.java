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
// Filename: nts/noad/RadicalNoad.java
// $Id: RadicalNoad.java,v 1.1.1.1 2000/04/15 18:04:20 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.HBoxNode;
import nts.node.HShiftNode;
import nts.node.Node;
import nts.node.NodeList;

public class RadicalNoad extends ScriptableNoad {

  protected final Delimiter radical;
  protected final Field nucleus;

  public RadicalNoad(Delimiter radical, Field nucleus) {
    this.radical = radical;
    this.nucleus = nucleus;
  }

  /* TeXtp[696] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc()).add(radical);
    nucleus.addOn(log, cntx, '.');
  }

  protected String getDesc() {
    return "radical";
  }

  protected byte spacingType() {
    return SPACING_TYPE_ORD;
  }

  public Egg convert(Converter conv) {
    Node node = nucleus.cleanBox(conv, CRAMPED);
    Dimen drt = conv.getDimPar(DP_DEFAULT_RULE_THICKNESS);
    Dimen clr =
        drt.plus(
            ((conv.getStyle() != DISPLAY_STYLE) ? drt : conv.getDimPar(DP_MATH_X_HEIGHT))
                .absolute()
                .over(4));
    Dimen size = node.getHeight().plus(node.getDepth()).plus(clr);
    Node rad = conv.fetchSufficientNode(radical, size.plus(drt));
    Dimen height = rad.getHeight();
    size = rad.getDepth().minus(size);
    if (size.moreThan(0)) clr = clr.plus(size.halved());
    rad = HShiftNode.shiftingUp(rad, node.getHeight().plus(clr));
    NodeList list = new NodeList(2);
    list.append(rad).append(makeOverBar(node, clr, height, height));
    return new StSimpleNodeEgg(HBoxNode.packedOf(list), spacingType());
  }
}
