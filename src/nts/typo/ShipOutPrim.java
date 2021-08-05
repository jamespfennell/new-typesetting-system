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
// Filename: nts/typo/ShipOutPrim.java
// $Id: ShipOutPrim.java,v 1.1.1.1 1999/12/05 11:51:29 ksk Exp $
package nts.typo;

import nts.command.Token;
import nts.node.Box;
import nts.node.NodeEnum;
import nts.node.TreatBox;

public class ShipOutPrim extends TypoPrim {

  public ShipOutPrim(String name) {
    super(name);
  }

  public void exec(Token src) {
    scanBox(
        new TreatBox() {
          public void execute(Box box, NodeEnum mig) {
            if (!box.isVoid()) shipOut(box);
          }
        });
  }
}
