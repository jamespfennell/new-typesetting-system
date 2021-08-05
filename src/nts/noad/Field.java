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
// Filename: nts/noad/Field.java
// $Id: Field.java,v 1.1.1.1 2000/10/17 13:26:15 ksk Exp $
package nts.noad;

import java.io.Serializable;
import nts.base.Dimen;
import nts.io.CntxLog;
import nts.io.Log;
import nts.node.HBoxNode;
import nts.node.MathWordBuilder;
import nts.node.Node;
import nts.node.TreatNode;

public abstract class Field implements Serializable, TransfConstants {

  public static final Field NULL = null;

  public abstract void addOn(Log log, CntxLog cntx, char p);

  public Noad ordinaryNoad() {
    return Noad.NULL;
  }

  public boolean isEmpty() {
    return false;
  }

  public boolean isJustChar() {
    return false;
  }

  public Dimen skewAmount(Converter conv) {
    return Dimen.ZERO;
  }

  public Node convertedBy(Converter conv) {
    return Node.NULL;
  }

  /* STRANGE
   * why is cleanBox(x, CURRENT) != convertedBy(x)?
   */
  public Node cleanBox(Converter conv, byte how) {
    return HBoxNode.EMPTY;
  }

  public Operator makeOperator(final Converter conv, final boolean larger) {
    return new Operator() {

      public Node getNodeToBeLimited() {
        return cleanBox(conv, CURRENT);
      }

      public Egg getEggToBeScripted(byte spType) {
        return new StItalNodeEgg(convertedBy(conv), spType);
      }

      public Dimen getItalCorr() {
        return Dimen.NULL;
      }
    };
  }

  public MathWordBuilder getMathWordBuilder(Converter conv, TreatNode proc) {
    throw new RuntimeException("non char field cannot provide math word builder");
  }

  public byte wordFamily() {
    return -1;
  }

  public void contributeToWord(MathWordBuilder word) {}

  public Operator takeLastOperator(MathWordBuilder word, Converter conv, boolean larger) {
    throw new RuntimeException("non char field cannot be part of math word");
  }
}
