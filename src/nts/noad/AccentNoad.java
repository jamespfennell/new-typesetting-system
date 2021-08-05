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
// Filename: nts/noad/AccentNoad.java
// $Id: AccentNoad.java,v 1.1.1.1 2000/10/17 02:39:55 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.Box;
import nts.node.BoxSizes;
import nts.node.IntVKernNode;
import nts.node.Node;
import nts.node.NodeList;
import nts.node.VBoxNode;
import nts.node.VShiftNode;

public class AccentNoad extends ScriptableNoad {

  protected final CharField accent;
  protected final Field nucleus;

  public AccentNoad(CharField accent, Field nucleus) {
    this.accent = accent;
    this.nucleus = nucleus;
  }

  protected String getDesc() {
    return "accent";
  }

  public boolean isOrdinary() {
    return true;
  }

  protected byte spacingType() {
    return SPACING_TYPE_ORD;
  }

  /* if accent char is not available in current font it becomes just a char;
   * otherwise if nucleus is a char the scripts are swapped below the accent
   * so for them it is still just a char
   */
  public boolean isJustChar() {
    return nucleus.isJustChar();
  }

  /* TeXtp[696] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc()).add(accent);
    nucleus.addOn(log, cntx, '.');
  }

  public Egg convert(Converter conv) {
    return sharedConvert(conv, EmptyField.FIELD, EmptyField.FIELD);
  }

  public Egg convertWithScripts(Converter conv, Field sup, Field sub) {
    return (nucleus.isJustChar())
        ? sharedConvert(conv, sup, sub)
        : super.convertWithScripts(conv, sup, sub);
  }

  /* STRANGE
   * the first test (accent.convertedBy(conv) != Node.NULL) is redundant
   * but we need this order of char warnings for compatibility
   */
  /* TeXtp[738] */
  private Egg sharedConvert(Converter conv, Field sup, Field sub) {
    if (accent.convertedBy(conv) != Node.NULL) {
      Dimen skew = nucleus.skewAmount(conv);
      Node nuclNode = nucleus.cleanBox(conv, CRAMPED);
      Dimen height = nuclNode.getHeight();
      Dimen width = nuclNode.getWidth();
      Dimen delta = height.min(accent.xHeight(conv));
      Box accBox = accent.fittingTo(conv, width);
      if (accBox != Box.NULL) {
        if (!(sup.isEmpty() && sub.isEmpty())) {
          Noad noad = new OrdNoad(nucleus);
          if (!sub.isEmpty()) noad = new SubScriptNoad(noad, sub);
          if (!sup.isEmpty()) noad = new SuperScriptNoad(noad, sup);
          nuclNode = (new NoadListField(new NoadList(noad))).cleanBox(conv, CURRENT);
          delta = delta.plus(nuclNode.getHeight().minus(height));
          height = nuclNode.getHeight();
        }
        NodeList list = new NodeList(3);
        Dimen shift = width.minus(accBox.getWidth()).halved().plus(skew);
        accBox =
            accBox.pretendSizesCopy(
                new BoxSizes(
                    accBox.getHeight(), Dimen.ZERO,
                    accBox.getDepth(), Dimen.ZERO));
        list.append(VShiftNode.shiftingRight(accBox, shift))
            .append(new IntVKernNode(delta.negative()))
            .append(nuclNode);
        Box box = VBoxNode.packedOf(list);
        if (box.getHeight().lessThan(height)) {
          NodeList list2 = new NodeList(4);
          list2.append(new IntVKernNode(height.minus(box.getHeight()))).append(list);
          box = VBoxNode.packedOf(list2);
        }
        box =
            box.pretendSizesCopy(
                new BoxSizes(
                    box.getHeight(), nuclNode.getWidth(),
                    box.getDepth(), nuclNode.getLeftX()));
        return new StSimpleNodeEgg(box, spacingType());
      }
    }
    return new StItalNodeEgg(nucleus.convertedBy(conv), spacingType());
  }
}
