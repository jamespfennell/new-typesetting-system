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
// Filename: nts/align/HAlignPrim.java
// $Id: HAlignPrim.java,v 1.1.1.1 2001/03/21 08:12:29 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.builder.ListBuilder;
import nts.builder.VBoxBuilder;
import nts.command.Command;
import nts.command.Primitive;
import nts.command.Token;
import nts.command.TokenList;

public class HAlignPrim extends AlignPrim {

  public HAlignPrim(String name, TokenList.Inserter everyCr, Primitive carRet, Command endv) {
    super(name, everyCr, carRet, endv);
  }

  protected ListBuilder makeOuterBuilder(int lineNo) {
    ListBuilder builder = new VBoxBuilder(lineNo);
    builder.setPrevDepth(Builder.top().nearestValidPrevDepth());
    return builder;
  }

  protected Alignment makeAlignment(
      Dimen size,
      boolean exactly,
      TokenList.Inserter everyCr,
      Token frzCr,
      Token frzEndt,
      ListBuilder builder) {
    return new HorizAlignment(size, exactly, everyCr, frzCr, frzEndt, builder);
  }
}
