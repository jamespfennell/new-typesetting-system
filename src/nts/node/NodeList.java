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
// Filename: nts/node/NodeList.java
// $Id: NodeList.java,v 1.1.1.1 2000/06/06 09:00:25 ksk Exp $
package nts.node;

import java.io.Serializable;
import java.util.Vector;
import nts.io.Log;

public class NodeList implements Serializable {

  public static final NodeList NULL = null;

  public static final NodeList EMPTY = new NodeList();

  public static final NodeEnum EMPTY_ENUM =
      new NodeEnum() {
        public Node nextNode() {
          return Node.NULL;
        }

        public boolean hasMoreNodes() {
          return false;
        }
      };

  protected Vector<Node> data;

  protected NodeList(Vector<Node> data) {
    this.data = data;
  }

  public NodeList() {
    data = new Vector<Node>();
  }

  public NodeList(int initCap) {
    data = new Vector<Node>(initCap);
  }

  public NodeList(int initCap, int capIncrement) {
    data = new Vector<Node>(initCap, capIncrement);
  }

  public NodeList(Node node) {
    this(1);
    append(node);
  }

  public NodeList(NodeEnum nodes) {
    this();
    append(nodes);
  }

  public NodeList(Node[] nodes) {
    this(nodes.length);
    append(nodes);
  }

  public NodeList(Node[] nodes, int offset, int count) {
    this(count);
    append(nodes, offset, count);
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

  public final Node nodeAt(int idx) {
    return (Node) data.elementAt(idx);
  }

  public NodeList append(Node node) {
    data.addElement(node);
    return this;
  }

  public NodeList append(Node[] nodes, int offset, int count) {
    data.ensureCapacity(data.size() + count);
    while (count-- > 0) append(nodes[offset++]);
    return this;
  }

  public NodeList append(Node[] nodes) {
    return append(nodes, 0, nodes.length);
  }

  public NodeList append(NodeEnum nodes) {
    while (nodes.hasMoreNodes()) append(nodes.nextNode());
    return this;
  }

  public NodeList append(NodeList list) {
    return append(list.nodes());
  }

  public Node lastNode() {
    return (length() > 0) ? nodeAt(length() - 1) : Node.NULL;
  }

  public void removeLastNode() {
    if (length() > 0) data.removeElementAt(length() - 1);
  }

  public Node lastSpecialNode() {
    return lastNode();
  }

  public Node[] toArray() {
    Node[] nodes = new Node[data.size()];
    return (Node[]) data.toArray(nodes);
  }

  private class Enum extends NodeEnum {
    private int idx;
    private final int end;

    public Enum(int idx, int end) {
      this.idx = idx;
      this.end = end;
    }

    public Node nextNode() {
      return nodeAt(idx++);
    }

    public boolean hasMoreNodes() {
      return (idx < end);
    }
  }

  public NodeEnum nodes() {
    return new Enum(0, length());
  }

  public NodeEnum nodes(int start) {
    return new Enum(start, length());
  }

  public NodeEnum nodes(int start, int end) {
    return new Enum(start, end);
  }

  public static NodeEnum nodes(final Node node) {
    return new NodeEnum() {
      private boolean fresh = true;

      public Node nextNode() {
        fresh = false;
        return node;
      }

      public boolean hasMoreNodes() {
        return fresh;
      }
    };
  }

  /* TeXtp[655] */
  public NodeList extractedMigrations() {
    int cnt = 0, i = 0;
    while (i < length()) if (nodeAt(i++).isMigrating()) cnt++;
    if (cnt > 0) {
      NodeList list = new NodeList();
      i = 0;
      while (i < length()) {
        Node node = nodeAt(i);
        if (node.isMigrating()) {
          list.append(node.getMigration());
          data.remove(i);
        } else i++;
      }
      return list;
    } else return EMPTY;
  }

  /* TeXtp[174] */
  public FontMetric addShortlyOn(Log log, FontMetric metric) {
    NodeEnum nodes = nodes();
    while (nodes.hasMoreNodes()) metric = nodes.nextNode().addShortlyOn(log, metric);
    return metric;
  }

  public FontMetric addShortlyOn(Log log) {
    return addShortlyOn(log, FontMetric.NULL);
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    NodeEnum nodes = nodes();
    if (nodes.hasMoreNodes()) {
      buf.append(nodes.nextNode());
      while (nodes.hasMoreNodes()) buf.append(", ").append(nodes.nextNode());
    }
    return buf.toString();
  }
}
