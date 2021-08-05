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
// Filename: nts/node/DiscardableNode.java
// $Id: DiscardableNode.java,v 1.1.1.1 2000/05/26 21:14:42 ksk Exp $
package nts.node;

import nts.io.Log;

public abstract class DiscardableNode extends BaseNode {
  /* corresponding to kern_node, glue_node, math_node, penalty_node */

  public final boolean discardable() {
    return true;
  }

  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    return metric;
  }
}
