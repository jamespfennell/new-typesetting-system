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
// Filename: nts/math/RadicalPrim.java
// $Id: RadicalPrim.java,v 1.1.1.1 2000/03/02 15:03:24 ksk Exp $
package nts.math;

import nts.command.Token;
import nts.noad.Delimiter;
import nts.noad.EmptyField;
import nts.noad.Field;
import nts.noad.RadicalNoad;
import nts.noad.TreatField;

public class RadicalPrim extends MathPrim {

  public RadicalPrim(String name) {
    super(name);
  }

  public MathAction mathAction() {
    return NORMAL;
  }

  /* STRANGE
   * Empty Noad is added and then removed only for the case when
   * \showlists appear inside the group.
   */
  /* TeXtp[1163] */
  public final MathAction NORMAL =
      new MathAction() {
        public void exec(final MathBuilder bld, Token src) {
          final Delimiter radical = getMathConfig().delimiterForDelCode(scanDelimiterCode());
          bld.addNoad(new RadicalNoad(radical, EmptyField.FIELD));
          scanField(
              new TreatField() {
                public void execute(Field field) {
                  bld.replaceLastNoad(new RadicalNoad(radical, field));
                }
              });
        }
      };
}
