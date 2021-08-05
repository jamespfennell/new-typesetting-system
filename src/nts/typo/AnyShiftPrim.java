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
// Filename: nts/typo/AnyShiftPrim.java
// $Id: AnyShiftPrim.java,v 1.1.1.1 2000/04/10 17:55:02 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.Token;
import nts.node.Box;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.TreatBox;

public abstract class AnyShiftPrim extends BuilderPrim {

  public AnyShiftPrim(String name) {
    super(name);
  }

  /* TeXtp[1073,1076] */
  public final Action NORMAL =
      new Action() {
        public void exec(final Builder bld, Token src) {
          final Dimen shift = scanDimen();
          scanBox(
              new TreatBox() {
                public boolean wantsMig() {
                  return bld.wantsMigrations();
                }

                public void execute(Box box, NodeEnum mig) {
                  appendBox(bld, makeNode(box, shift), mig);
                }
              });
        }
      };

  protected abstract Node makeNode(Node node, Dimen shift);
}
