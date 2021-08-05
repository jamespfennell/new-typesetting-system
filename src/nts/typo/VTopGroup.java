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
// Filename: nts/typo/VTopGroup.java
// $Id: VTopGroup.java,v 1.1.1.1 2001/04/27 16:04:14 ksk Exp $
package nts.typo;

import nts.base.Dimen;
import nts.node.Box;
import nts.node.BoxSizes;
import nts.node.NodeList;
import nts.node.SizesEvaluator;
import nts.node.TreatBox;
import nts.node.VBoxNode;
import nts.node.VertIterator;

public class VTopGroup extends VBoxGroup {

  public VTopGroup(Dimen size, boolean exactly, TreatBox proc) {
    super(size, exactly, proc);
  }

  public static final VTopPacker packer = new VTopPacker();

  protected Box makeBox(NodeList list) {
    return packer.packVTop(list, size, exactly, maxDepth);
  }

  public static class VTopPacker extends TypoCommand.VBoxPacker {

    /* STRANGE
     * two vboxes are created: vtop as a final result and vbox solely for
     * diagnostic messages because TeX displays vbox when reporting bad
     * vbox and only after that moves the reference point to make vtop
     * from the vbox.
     */
    /* TeXtp[1087] */
    public VBoxNode packVTop(NodeList list, Dimen desired, boolean exactly, Dimen maxDepth) {
      SizesEvaluator pack = new SizesEvaluator();
      VertIterator.summarize(list.nodes(), pack);
      if (maxDepth != Dimen.NULL) pack.restrictDepth(maxDepth);
      Dimen size = pack.getBody().plus(pack.getHeight());
      boolean empty = list.isEmpty();
      if (exactly) {
        pack.evaluate(desired.minus(size), empty);
        size = desired;
      } else {
        pack.evaluate(desired, empty);
        size = size.plus(desired);
      }
      BoxSizes sizes = new BoxSizes(size, pack.getWidth(), pack.getDepth(), pack.getLeftX());
      VBoxNode vbox = new VBoxNode(sizes, pack.getSetting(), list);
      if (check(pack)) reportBox(vbox);
      Dimen height = pack.getHeight();
      // XXX misusing of instanceof and not correct (discretionary)
      if (!(list.isEmpty() || list.nodeAt(0) instanceof nts.node.AnyBoxedNode)) height = Dimen.ZERO;
      sizes =
          new BoxSizes(
              height, pack.getWidth(), size.minus(height).plus(pack.getDepth()), pack.getLeftX());
      VBoxNode vtop = new VBoxNode(sizes, pack.getSetting(), list);
      return vtop;
    }
  }
}
