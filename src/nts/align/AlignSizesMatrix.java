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
// Filename: nts/align/AlignSizesMatrix.java
// $Id: AlignSizesMatrix.java,v 1.1.1.1 2000/08/10 03:28:29 ksk Exp $
package nts.align;

import java.util.HashMap;
import java.util.Vector;
import nts.base.Dimen;
import nts.base.IntPairKey;

public class AlignSizesMatrix {

  private final Vector<Dimen> diagonal;
  private final HashMap<IntPairKey, Dimen> matrix;

  public AlignSizesMatrix() {
    diagonal = new Vector<Dimen>();
    matrix = new HashMap<IntPairKey, Dimen>();
  }

  public AlignSizesMatrix(int initialSize) {
    diagonal = new Vector<Dimen>(initialSize);
    matrix = new HashMap<IntPairKey, Dimen>();
  }

  public int size() {
    return diagonal.size();
  }

  public Dimen get(int i) {
    return (i < diagonal.size()) ? diagonal.get(i) : Dimen.NULL;
  }

  public Dimen get(int i, int j) {
    return (i != j) ? matrix.get(new IntPairKey(i, j)) : get(i);
  }

  public Dimen set(int i, int j, Dimen value) {
    if (i != j) return (Dimen) matrix.put(new IntPairKey(i, j), value);
    if (i >= diagonal.size()) diagonal.setSize(i + 1);
    return (Dimen) diagonal.set(i, value);
  }

  public void setMax(int i, int j, Dimen value) {
    if (i != j) {
      IntPairKey key = new IntPairKey(i, j);
      Dimen old = (Dimen) matrix.get(key);
      if (old == Dimen.NULL || value.moreThan(old)) matrix.put(key, value);
    } else if (i >= diagonal.size()) {
      diagonal.setSize(i + 1);
      diagonal.set(i, value);
    } else {
      Dimen old = (Dimen) diagonal.get(i);
      if (old == Dimen.NULL || value.moreThan(old)) diagonal.set(i, value);
    }
  }
}
