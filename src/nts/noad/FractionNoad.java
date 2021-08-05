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
// Filename: nts/noad/FractionNoad.java
// $Id: FractionNoad.java,v 1.1.1.1 2001/03/22 16:46:48 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.BoxSizes;
import nts.node.GlueSetting;
import nts.node.HBoxNode;
import nts.node.IntVKernNode;
import nts.node.Node;
import nts.node.NodeList;
import nts.node.VBoxNode;

public class FractionNoad extends PureNoad {

  public static final FractionNoad NULL = null;

  protected final Field numerator;
  protected final Field denominator;
  protected final Dimen thickness;

  public Field getNumerator() {
    return numerator;
  }

  public Field getDenominator() {
    return denominator;
  }

  public Dimen getThickness() {
    return thickness;
  }

  public FractionNoad(Field numerator, Field denominator, Dimen thickness) {
    this.numerator = numerator;
    this.denominator = denominator;
    this.thickness = thickness;
  }

  public FractionNoad numerated(Field numerator) {
    return new FractionNoad(numerator, denominator, thickness);
  }

  public FractionNoad denominated(Field denominator) {
    return new FractionNoad(numerator, denominator, thickness);
  }

  /* TeXtp[697] */
  public void addOn(Log log, CntxLog cntx) {
    log.addEsc(getDesc()).add(", thickness ");
    if (thickness == Dimen.NULL) log.add("= default");
    else log.add(thickness.toString());
    addDelimitersOn(log);
    numerator.addOn(log, cntx, '\\');
    denominator.addOn(log, cntx, '/');
  }

  public final boolean influencesBin() {
    return true;
  }

  protected String getDesc() {
    return "fraction";
  }

  protected void addDelimitersOn(Log log) {}

  public Delimiter getLeftDelimiter() {
    return Delimiter.VOID;
  }

  public Delimiter getRightDelimiter() {
    return Delimiter.VOID;
  }

  protected byte spacingType() {
    return SPACING_TYPE_INNER;
  }

  /* TeXtp[743] */
  public Egg convert(Converter conv) {
    Node numNode = numerator.cleanBox(conv, NUMERATOR);
    Node denNode = denominator.cleanBox(conv, DENOMINATOR);
    Dimen width = numNode.getWidth().max(denNode.getWidth());
    numNode = numNode.reboxedToWidth(width);
    denNode = denNode.reboxedToWidth(width);
    Dimen numShift;
    Dimen denShift;
    Dimen clr, delta;
    Dimen middle = conv.getDimPar(DP_AXIS_HEIGHT);
    Dimen drt = conv.getDimPar(DP_DEFAULT_RULE_THICKNESS);
    Dimen thick = (thickness != Dimen.NULL) ? thickness : drt;
    boolean display = (conv.getStyle() == DISPLAY_STYLE);
    NodeList list = new NodeList(5);
    list.append(numNode);
    if (thick.isZero()) {
      if (display) {
        numShift = conv.getDimPar(DP_NUM1);
        denShift = conv.getDimPar(DP_DENOM1);
        clr = drt.times(7);
      } else {
        numShift = conv.getDimPar(DP_NUM3);
        denShift = conv.getDimPar(DP_DENOM2);
        clr = drt.times(3);
      }
      delta =
          clr.minus(numShift.minus(numNode.getDepth()).minus(denNode.getHeight().minus(denShift)))
              .halved();
      if (delta.moreThan(0)) {
        numShift = numShift.plus(delta);
        denShift = denShift.plus(delta);
      }
      list.append(
          new IntVKernNode(
              numShift.minus(numNode.getDepth()).minus(denNode.getHeight().minus(denShift))));
    } else {
      if (display) {
        numShift = conv.getDimPar(DP_NUM1);
        denShift = conv.getDimPar(DP_DENOM1);
        clr = thick.times(3);
      } else {
        numShift = conv.getDimPar(DP_NUM2);
        denShift = conv.getDimPar(DP_DENOM2);
        clr = thick;
      }
      Dimen halfThick = thick.halved();
      delta = clr.minus(numShift.minus(numNode.getDepth()).minus(middle.plus(halfThick)));
      if (delta.moreThan(0)) numShift = numShift.plus(delta);
      delta = clr.minus(middle.minus(halfThick).minus(denNode.getHeight().minus(denShift)));
      if (delta.moreThan(0)) denShift = denShift.plus(delta);
      list.append(
              new IntVKernNode(numShift.minus(numNode.getDepth()).minus(middle.plus(halfThick))))
          .append(makeRule(thick))
          .append(
              new IntVKernNode(middle.minus(halfThick).minus(denNode.getHeight().minus(denShift))));
    }
    list.append(denNode);
    Node fraction =
        new VBoxNode(
            new BoxSizes(
                numNode.getHeight().plus(numShift),
                width,
                denNode.getDepth().plus(denShift),
                numNode.getLeftX().max(denNode.getLeftX())),
            GlueSetting.NATURAL,
            list);
    Dimen delSize = (display) ? conv.getDimPar(DP_DELIM1) : conv.getDimPar(DP_DELIM2);
    list = new NodeList(3);
    list.append(varDelimiter(getLeftDelimiter(), delSize, conv))
        .append(fraction)
        .append(varDelimiter(getRightDelimiter(), delSize, conv));
    return new StSimpleNodeEgg(HBoxNode.packedOf(list), spacingType());
  }
}
