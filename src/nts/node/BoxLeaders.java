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
// Filename: nts/node/BoxLeaders.java
// $Id: BoxLeaders.java,v 1.1.1.1 2000/02/19 01:42:33 ksk Exp $
package nts.node;

import java.io.Serializable;
import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CntxLog;
import nts.io.Log;

public abstract class BoxLeaders implements Leaders {

  public interface Mover extends Serializable {
    Dimen offset(TypeSetter.Mark start);

    Dimen size(Node node);

    void move(TypeSetter setter, Dimen gap);

    void movePrev(TypeSetter setter, Node node);

    void movePast(TypeSetter setter, Node node);
  }

  public static final Mover NULL_MOVER = null;

  protected final Node node;
  protected final Mover mover;

  public BoxLeaders(Node node, Mover mover) {
    this.node = node;
    this.mover = mover;
  }

  public Dimen getHeight() {
    return node.getHeight();
  }

  public Dimen getWidth() {
    return node.getWidth();
  }

  public Dimen getDepth() {
    return node.getDepth();
  }

  public Dimen getLeftX() {
    return node.getLeftX();
  }

  /* TeXtp[190] */
  public void addOn(Log log, CntxLog cntx, Glue skip) {
    log.addEsc(getDesc()).add(' ').add(skip.toString());
    cntx.addOn(log, node);
  }

  private static final Dimen compensation = Dimen.valueOf(10, 0x10000);

  public void typeSet(TypeSetter setter, Dimen size, SettingContext sctx) {
    if (size.moreThan(0)) {
      Dimen nodeSize = mover.size(node);
      if (nodeSize.moreThan(0))
        typeSet(setter, sctx.allowingIO(false), size.plus(compensation), nodeSize);
    }
  }

  /* TeXtp[626,628,635,637] */
  protected void typeSet(
      TypeSetter setter, SettingContext sctx, int count, Dimen start, Dimen gap) {
    mover.move(setter, start);
    while (count-- > 0) {
      mover.movePrev(setter, node);
      node.typeSet(setter, sctx);
      mover.movePast(setter, node);
      mover.move(setter, gap);
    }
  }

  protected abstract void typeSet(
      TypeSetter setter, SettingContext sctx, Dimen size, Dimen nodeSize);

  protected abstract String getDesc();
}
