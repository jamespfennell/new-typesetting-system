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
// Filename: nts/math/ScriptPrim.java
// $Id: ScriptPrim.java,v 1.1.1.1 2000/03/03 19:22:59 ksk Exp $
package nts.math;

import nts.command.Token;
import nts.noad.EmptyField;
import nts.noad.Field;
import nts.noad.Noad;
import nts.noad.OrdNoad;
import nts.noad.TreatField;

public abstract class ScriptPrim extends MathPrim {

  public ScriptPrim(String name) {
    super(name);
  }

  public MathAction mathAction() {
    return NORMAL;
  }

  /* TeXtp[1176] */
  public final MathAction NORMAL =
      new MathAction() {
        public void exec(final MathBuilder bld, Token src) {
          Noad last = bld.lastNoad();
          boolean scriptable = false;
          if (last != Noad.NULL && last.isScriptable()) {
            scriptable = true;
            if (conflicting(last)) {
              error(errorIdent());
              scriptable = false;
            }
          }
          final Noad body;
          if (scriptable) body = last;
          else {
            body = new OrdNoad(EmptyField.FIELD);
            bld.addNoad(body);
          }
          scanField(
              new TreatField() {
                public void execute(Field field) {
                  bld.replaceLastNoad(scripted(body, field));
                }
              });
        }
      };

  protected abstract boolean conflicting(Noad noad);

  protected abstract Noad scripted(Noad noad, Field script);

  protected abstract String errorIdent();
}
