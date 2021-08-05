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
// Filename: nts/math/MathGroup.java
// $Id: MathGroup.java,v 1.1.1.1 2001/03/09 11:06:16 ksk Exp $
package nts.math;

import nts.builder.Builder;
import nts.command.SimpleGroup;
import nts.noad.Field;
import nts.noad.NoadList;
import nts.noad.NoadListField;
import nts.noad.TreatField;
import nts.noad.VoidField;

public class MathGroup extends SimpleGroup {

  protected final TreatField proc;
  protected final FormulaBuilder builder;

  public MathGroup(TreatField proc, FormulaBuilder builder) {
    this.proc = proc;
    this.builder = builder;
  }

  public MathGroup(TreatField proc) {
    this(proc, new FormulaBuilder(currLineNumber()));
  }

  public void start() {
    Builder.push(builder);
  }

  /* TeXtp[1186] */
  public void close() {
    Builder.pop();
    NoadList list = builder.getList();
    Field field = Field.NULL;
    if (list.isEmpty()) field = VoidField.FIELD;
    else {
      if (list.length() == 1) field = list.noadAt(0).ordinaryField();
      if (field == Field.NULL) field = new NoadListField(list);
    }
    proc.execute(field);
  }
}
