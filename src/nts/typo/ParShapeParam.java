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
// Filename: nts/typo/ParShapeParam.java
// $Id: ParShapeParam.java,v 1.1.1.1 2000/04/19 00:20:04 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.base.Num;
import nts.command.ParamPrim;
import nts.command.Token;
import nts.io.Log;

/** Setting paragraph shape parameter primitive. */
public class ParShapeParam extends ParamPrim implements ParShape.Provider, Num.Provider {

  private ParShape value;

  /**
   * Creates a new ParShapeParam with given name and value and stores it in language interpreter
   * |EqTable|.
   *
   * @param name the name of the ParShapeParam
   * @param val the value of the ParShapeParam
   */
  public ParShapeParam(String name, ParShape val) {
    super(name);
    value = val;
  }

  /**
   * Creates a new ParShapeParam with given name and stores it in language interpreter |EqTable|.
   *
   * @param name the name of the ParShapeParam
   */
  public ParShapeParam(String name) {
    this(name, ParShape.EMPTY);
  }

  public final Object getEqValue() {
    return value;
  }

  public final void setEqValue(Object val) {
    value = (ParShape) val;
  }

  public final void addEqValueOn(Log log) {
    log.add(value.getLength());
  }

  public final ParShape get() {
    return value;
  }

  public void set(ParShape val, boolean glob) {
    beforeSetting(glob);
    value = val;
  }

  protected void scanValue(Token src, boolean glob) {
    set(scanParShapeValue(src), glob);
  }

  /* TeXtp[1248] */
  protected ParShape scanParShapeValue(Token src) {
    int len = scanInt();
    if (len <= 0) return ParShape.EMPTY;
    Dimen[] indents = new Dimen[len];
    Dimen[] widths = new Dimen[len];
    for (int i = 0; i < len; i++) {
      indents[i] = scanDimen();
      widths[i] = scanDimen();
    }
    return new ParShape(indents, widths);
  }

  public ParShape getParShapeValue() {
    return get();
  }

  public boolean hasNumValue() {
    return true;
  }

  public Num getNumValue() {
    return Num.valueOf(get().getLength());
  }
}
