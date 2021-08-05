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
// Filename: nts/typo/ParShape.java
// $Id: ParShape.java,v 1.1.1.1 2000/04/19 00:18:53 ksk Exp $
package nts.typo;

import java.io.Serializable;
import nts.base.Dimen;
import nts.node.LinesShape;

public class ParShape implements Serializable, LinesShape {

  public static final ParShape NULL = null;

  private static final Dimen[] EMPTY_DIMENS = new Dimen[0];

  public static final ParShape EMPTY = new ParShape(EMPTY_DIMENS, EMPTY_DIMENS);

  public interface Provider {
    ParShape getParShapeValue();
  }

  private final Dimen[] indents;
  private final Dimen[] widths;

  public ParShape(Dimen[] indents, Dimen[] widths) {
    if (indents.length != widths.length) throw new RuntimeException("bad arguments");
    this.indents = indents;
    this.widths = widths;
  }

  public boolean isEmpty() {
    return (widths.length == 0);
  }

  public int getLength() {
    return widths.length;
  }

  public boolean isFinal(int idx) {
    return (idx >= widths.length - 1);
  }

  private int adjusted(int idx) {
    return (idx < widths.length) ? idx : widths.length - 1;
  }

  public Dimen getIndent(int idx) {
    return indents[adjusted(idx)];
  }

  public Dimen getWidth(int idx) {
    return widths[adjusted(idx)];
  }
}
