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
// Filename: nts/math/MathChoiceGroup.java
// $Id: MathChoiceGroup.java,v 1.1.1.1 2000/03/05 09:20:08 ksk Exp $
package nts.math;

import nts.builder.Builder;
import nts.command.SimpleGroup;
import nts.noad.NoadList;

public class MathChoiceGroup extends SimpleGroup {

  private final NoadList[] choices;
  private MathBuilder builder;
  private int index = 0;

  public MathChoiceGroup(NoadList[] choices) {
    this.choices = choices;
  }

  public void start() {
    builder = new FormulaBuilder(currLineNumber());
    Builder.push(builder);
    scanLeftBrace();
  }

  /* TeXtp[1174] */
  public void close() {
    Builder.pop();
    choices[index] = builder.getList();
    if (++index < choices.length) pushLevel(this);
  }
}
