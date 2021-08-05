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
// Filename: nts/noad/NoadList.java
// $Id: NoadList.java,v 1.1.1.1 2001/03/22 15:54:53 ksk Exp $
package nts.noad;

import java.io.Serializable;
import java.util.Vector;
import nts.node.Node;
import nts.node.NodeEnum;

public class NoadList implements Serializable {

  public static final NoadList NULL = null;

  public static final NoadList EMPTY = new NoadList();

  public static final NoadEnum EMPTY_ENUM =
      new NoadEnum() {
        public Noad nextNoad() {
          return Noad.NULL;
        }

        public boolean hasMoreNoads() {
          return false;
        }
      };

  protected Vector<Noad> data;

  protected NoadList(Vector<Noad> data) {
    this.data = data;
  }

  public NoadList() {
    data = new Vector<Noad>();
  }

  public NoadList(int initCap) {
    data = new Vector<Noad>(initCap);
  }

  public NoadList(int initCap, int capIncrement) {
    data = new Vector<Noad>(initCap, capIncrement);
  }

  public NoadList(Noad noad) {
    this(1);
    append(noad);
  }

  public NoadList(NoadEnum noads) {
    this();
    append(noads);
  }

  public NoadList(Noad[] noads) {
    this(noads.length);
    append(noads);
  }

  public NoadList(Noad[] noads, int offset, int count) {
    this(count);
    append(noads, offset, count);
  }

  public final int length() {
    return data.size();
  }

  public final boolean isEmpty() {
    return data.isEmpty();
  }

  protected void clear() {
    data.clear();
  }

  public final Noad noadAt(int idx) {
    return data.elementAt(idx);
  }

  public NoadList append(Noad noad) {
    data.addElement(noad);
    return this;
  }

  public NoadList append(Noad[] noads, int offset, int count) {
    data.ensureCapacity(data.size() + count);
    while (count-- > 0) append(noads[offset++]);
    return this;
  }

  public NoadList append(Noad[] noads) {
    return append(noads, 0, noads.length);
  }

  public NoadList append(NoadEnum noads) {
    while (noads.hasMoreNoads()) append(noads.nextNoad());
    return this;
  }

  public NoadList append(NoadList list) {
    return append(list.noads());
  }

  public Noad lastNoad() {
    return (length() > 0) ? noadAt(length() - 1) : Noad.NULL;
  }

  public void removeLastNoad() {
    if (length() > 0) data.removeElementAt(length() - 1);
  }

  public void replaceLastNoad(Noad noad) {
    if (length() > 0) data.set(length() - 1, noad);
  }

  public Noad[] toArray() {
    Noad[] noads = new Noad[data.size()];
    return (Noad[]) data.toArray(noads);
  }

  private class Enum extends NoadEnum {
    private int idx;
    private final int end;

    public Enum(int idx, int end) {
      this.idx = idx;
      this.end = end;
    }

    public Noad nextNoad() {
      return noadAt(idx++);
    }

    public boolean hasMoreNoads() {
      return (idx < end);
    }
  }

  public NoadEnum noads() {
    return new Enum(0, length());
  }

  public NoadEnum noads(int start) {
    return new Enum(start, length());
  }

  public NoadEnum noads(int start, int end) {
    return new Enum(start, end);
  }

  public static NoadEnum noads(final Noad noad) {
    return new NoadEnum() {
      private boolean fresh = true;

      public Noad nextNoad() {
        fresh = false;
        return noad;
      }

      public boolean hasMoreNoads() {
        return fresh;
      }
    };
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    NoadEnum noads = noads();
    while (noads.hasMoreNoads()) buf.append(noads.nextNoad()).append(", ");
    return buf.toString();
  }

  /* ****************************************************************** */

  public NoadList append(Node node) {
    append(new NodeNoad(node));
    return this;
  }

  public NoadList append(NodeEnum nodes) {
    while (nodes.hasMoreNodes()) append(nodes.nextNode());
    return this;
  }

  public Node lastNode() {
    Noad last = lastNoad();
    return (last != Noad.NULL && last.isNode()) ? last.getNode() : Node.NULL;
  }

  public void removeLastNode() {
    Noad last = lastNoad();
    if (last != Noad.NULL && last.isNode()) removeLastNoad();
  }

  public Node lastSpecialNode() {
    return lastNode();
  }
}
