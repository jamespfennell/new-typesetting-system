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
// Filename: nts/math/MathCompPrim.java
// $Id: MathCompPrim.java,v 1.1.1.1 2000/03/02 15:02:42 ksk Exp $
package nts.math;

import nts.command.Token;
import nts.noad.EmptyField;
import nts.noad.Field;
import nts.noad.Noad;
import nts.noad.TreatField;

public abstract class MathCompPrim extends MathPrim {

  public MathCompPrim(String name) {
    super(name);
  }

  public MathAction mathAction() {
    return NORMAL;
  }

  /* STRANGE
   * Empty Noad is added and then removed only for the case when
   * \showlists appear inside the group.
   */
  /* TeXtp[1158] */
  public final MathAction NORMAL =
      new MathAction() {
        public void exec(final MathBuilder bld, Token src) {
          bld.addNoad(makeNoad(EmptyField.FIELD));
          scanField(
              new TreatField() {
                public void execute(Field field) {
                  bld.replaceLastNoad(makeNoad(field));
                }
              });
        }
      };

  public abstract Noad makeNoad(Field field);
}
