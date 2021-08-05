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
// Filename: nts/builder/ListBuilder.java
// $Id: ListBuilder.java,v 1.1.1.1 2000/04/12 10:43:14 ksk Exp $
package nts.builder;

import nts.io.CntxLog;
import nts.io.Log;
import nts.node.Box;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;

public abstract class ListBuilder extends Builder {

  protected NodeList list;
  protected final int startLine;

  protected ListBuilder(int startLine, NodeList list) {
    this.startLine = startLine;
    this.list = list;
  }

  protected ListBuilder(int line) {
    this(line, new NodeList());
  }

  public void addNode(Node node) {
    list.append(node);
  }

  public void addNodes(NodeEnum nodes) {
    list.append(nodes);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Node lastNode() {
    return list.lastNode();
  }

  public void removeLastNode() {
    list.removeLastNode();
  }

  public Node lastSpecialNode() {
    return list.lastSpecialNode();
  }

  public NodeList getList() {
    return list;
  }

  public int getStartLine() {
    return startLine;
  }

  protected void specialShow(Log log, int depth, int breadth) {
    CntxLog.addItems(log, list.nodes(), depth, breadth);
    // XXX maybe there should be log.endLine()
    // XXX check everything which originates from show_box()
    specialShow(log);
  }

  protected abstract void specialShow(Log log);

  public boolean unBox(Box box) {
    NodeEnum nodes = unBoxList(box);
    if (nodes != NodeEnum.NULL) {
      addNodes(nodes);
      return true;
    }
    return false;
  }

  protected abstract NodeEnum unBoxList(Box box);
}
