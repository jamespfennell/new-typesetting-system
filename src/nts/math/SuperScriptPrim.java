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
// Filename: nts/math/SuperScriptPrim.java
// $Id: SuperScriptPrim.java,v 1.1.1.1 2000/03/03 19:05:59 ksk Exp $
package nts.math;

import nts.noad.Field;
import nts.noad.Noad;
import nts.noad.SuperScriptNoad;

public class SuperScriptPrim extends ScriptPrim {

  public SuperScriptPrim(String name) {
    super(name);
  }

  protected boolean conflicting(Noad noad) {
    return noad.alreadySuperScripted();
  }

  protected Noad scripted(Noad noad, Field script) {
    return new SuperScriptNoad(noad, script);
  }

  protected String errorIdent() {
    return "DoubleSuperscript";
  }
}
