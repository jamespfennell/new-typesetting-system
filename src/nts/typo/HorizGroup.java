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
// Filename: nts/typo/HorizGroup.java
// $Id: HorizGroup.java,v 1.1.1.1 2000/08/01 06:25:57 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.builder.HBoxBuilder;
import nts.command.SimpleGroup;

public class HorizGroup extends SimpleGroup {

  protected final HBoxBuilder builder;

  public HorizGroup(HBoxBuilder builder) {
    this.builder = builder;
  }

  public HorizGroup() {
    this(new HBoxBuilder(currLineNumber()));
  }

  public void start() {
    Builder.push(builder);
  }

  public void close() {
    Builder.pop();
  }
}
