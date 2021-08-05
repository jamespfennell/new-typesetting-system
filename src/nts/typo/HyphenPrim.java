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
// Filename: nts/typo/HyphenPrim.java
// $Id: HyphenPrim.java,v 1.1.1.1 2000/04/30 19:14:12 ksk Exp $
package nts.typo;

import nts.base.Num;
import nts.builder.Builder;
import nts.command.Token;
import nts.io.CharCode;
import nts.node.DiscretionaryNode;
import nts.node.FontMetric;
import nts.node.Node;
import nts.node.NodeList;

public class HyphenPrim extends BuilderPrim {

  public HyphenPrim(String name) {
    super(name);
  }

  /* TeXtp[1117] */
  public final Action NORMAL =
      new Action() {
        public void exec(final Builder bld, Token src) {
          NodeList pre = NodeList.EMPTY;
          FontMetric metric = getCurrFontMetric();
          Num num = metric.getNumParam(FontMetric.NUM_PARAM_HYPHEN_CHAR);
          if (num != Num.NULL) {
            CharCode code = Token.makeCharCode(num.intVal());
            if (code != CharCode.NULL) {
              Node node = metric.getCharNode(code);
              if (node != Node.NULL) pre = new NodeList(node);
              else charWarning(metric, code);
            }
          }
          bld.addNode(new DiscretionaryNode(pre, NodeList.EMPTY, NodeList.EMPTY));
        }
      };
}
