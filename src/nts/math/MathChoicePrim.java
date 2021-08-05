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
// Filename: nts/math/MathChoicePrim.java
// $Id: MathChoicePrim.java,v 1.1.1.1 2000/03/20 21:16:46 ksk Exp $
package nts.math;

import nts.command.Token;
import nts.noad.ChoiceNoad;
import nts.noad.Noad;
import nts.noad.NoadList;

public class MathChoicePrim extends MathPrim {

  public MathChoicePrim(String name) {
    super(name);
  }

  public MathAction mathAction() {
    return NORMAL;
  }

  /* STRANGE
   * Empty Noad is added and then removed only for the case when
   * \showlists appear inside the group.
   */
  /* TeXtp[1172] */
  public final MathAction NORMAL =
      new MathAction() {
        public void exec(final MathBuilder bld, Token src) {
          NoadList[] choices = new NoadList[Noad.NUMBER_OF_STYLES];
          for (int i = 0; i < choices.length; i++) choices[i] = NoadList.EMPTY;
          bld.addNoad(new ChoiceNoad(choices));
          pushLevel(new MathChoiceGroup(choices));
        }
      };
}
