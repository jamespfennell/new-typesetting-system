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
// Filename: nts/typo/AccentPrim.java
// $Id: AccentPrim.java,v 1.1.1.1 2001/09/09 22:17:59 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.builder.Builder;
import nts.command.Command;
import nts.command.Prim;
import nts.command.Token;
import nts.io.CharCode;
import nts.node.AccKernNode;
import nts.node.FontMetric;
import nts.node.HBoxNode;
import nts.node.HShiftNode;
import nts.node.Node;

public class AccentPrim extends BuilderPrim {

  public AccentPrim(String name) {
    super(name);
  }

  /* TeXtp[1122-1125] */
  public final Action NORMAL =
      new Action() {
        public void exec(final Builder bld, Token src) {
          CharCode code = Token.makeCharCode(Prim.scanCharacterCode());
          if (code == CharCode.NULL) throw new RuntimeException("no char number scanned");
          FontMetric metric = getCurrFontMetric();
          Node node = metric.getCharNode(code);
          if (node == Node.NULL) charWarning(metric, code);
          else {
            /* Bernd Raichle:
               The function nextNonAssignment() can execute assignments
               as in \accent <accent_char> [<assignments>] <base_char>
               and these assignments can change parameters like
               the current x height (\fontdimen5\font) and slant
               (\fontdimen1\font).  Thus we have to get the x height and
               slant _before_ calling nextNonAssignment()!
            */
            Dimen x = metric.getDimenParam(FontMetric.DIMEN_PARAM_X_HEIGHT);
            Dimen s = metric.getDimenParam(FontMetric.DIMEN_PARAM_SLANT);
            Token tok = nextNonAssignment();
            Command cmd = meaningOf(tok);
            code = cmd.charCodeToAdd();
            if (code == CharCode.NULL) backToken(tok);
            else {
              FontMetric nextMetric = getCurrFontMetric();
              Node nextNode = nextMetric.getCharNode(code);
              if (nextNode == Node.NULL) charWarning(nextMetric, code);
              else {
                Dimen h = nextNode.getHeight();
                if (!h.equals(x)) {
                  node = HBoxNode.packedOf(node);
                  node = HShiftNode.shiftingDown(node, x.minus(h));
                }
                Dimen a = node.getWidth();
                Dimen delta =
                    makeDelta(
                        a,
                        x,
                        s,
                        nextNode.getWidth(),
                        h,
                        nextMetric.getDimenParam(FontMetric.DIMEN_PARAM_SLANT));
                bld.addNode(new AccKernNode(delta));
                bld.addNode(node);
                node = nextNode;
                bld.addNode(new AccKernNode(delta.plus(a).negative()));
              }
            }
            bld.addNode(node);
            bld.resetSpaceFactor();
          }
        }
      };

  private static Dimen makeDelta(Dimen a, Dimen x, Dimen s, Dimen w, Dimen h, Dimen t) {
    return Dimen.valueOf(
        w.minus(a).toDouble() / 2 + h.toDouble() * t.toDouble() - x.toDouble() * s.toDouble());
  }

  /*
      private static Dimen	makeDelta(Dimen a, Dimen x, Dimen s,
      					  Dimen w, Dimen h, Dimen t)
  	{ return w.minus(a).over(2).plus(h.times(t)).minus(x.times(s)); }
  */

  private static int spoints(Dimen d) {
    return d.toInt(Dimen.REPR_UNITY);
  }
}
