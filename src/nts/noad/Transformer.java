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
// Filename: nts/noad/Transformer.java
// $Id: Transformer.java,v 1.1.1.1 2000/10/17 02:32:31 ksk Exp $
package nts.noad;

import nts.base.Dimen;
import nts.base.Glue;
import nts.io.CharCode;
import nts.node.Box;
import nts.node.Node;
import nts.node.NodeList;

public interface Transformer {

  void setStyle(byte style);

  byte getStyle();

  boolean isCramped();

  Dimen muToPt(Dimen dim);

  Glue muToPt(Glue skip);

  Dimen scriptSpace();

  Dimen getDimPar(int param);

  Dimen getDimPar(int param, byte how);

  Node fetchCharNode(byte fam, CharCode code);

  Node fetchCharNode(byte fam, CharCode code, byte how);

  Node fetchLargerNode(byte fam, CharCode code);

  Node fetchSufficientNode(Delimiter del, Dimen desired);

  Box fetchFittingWidthBox(byte fam, CharCode code, Dimen desired);

  Dimen skewAmount(byte fam, CharCode code);

  Dimen getXHeight(byte fam);

  NodeList convert(NoadEnum noads);

  NodeList convert(NoadEnum noads, byte how);
}
