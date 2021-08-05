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
// Filename: nts/builder/Builder.java
// $Id: Builder.java,v 1.1.1.1 2001/03/20 11:21:13 ksk Exp $
package nts.builder;

import nts.base.Dimen;
import nts.base.Glue;
import nts.base.Num;
import nts.io.Log;
import nts.io.Loggable;
import nts.node.Box;
import nts.node.BoxLeaders;
import nts.node.BoxSizes;
import nts.node.Language;
import nts.node.Leaders;
import nts.node.Node;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.PenaltyNode;

public abstract class Builder implements Loggable {

  public static final Builder NULL = null;

  public boolean isHorizontal() {
    return false;
  }

  public boolean isVertical() {
    return false;
  }

  public boolean isMath() {
    return false;
  }

  public boolean isInner() {
    return false;
  }

  public boolean isCharAllowed() {
    return false;
  }

  public boolean wantsMigrations() {
    return false;
  }

  public boolean willBeBroken() {
    return false;
  }

  public boolean forbidsThirdPartOfDiscretionary() {
    return false;
  }

  public abstract void addNode(Node node);

  public abstract void addNodes(NodeEnum nodes);

  public abstract void addKern(Dimen kern);

  public abstract void addSkip(Glue skip);

  public abstract void addNamedSkip(Glue skip, String name);

  public abstract void addRule(BoxSizes sizes);

  public abstract void addLeaders(Glue skip, Leaders lead);

  public abstract void addLeadRule(Glue skip, BoxSizes sizes, String desc);

  public void addSkip(Glue skip, String name) {
    if (name != null) addNamedSkip(skip, name);
    else addSkip(skip);
  }

  public void addPenalty(Num pen) {
    addNode(new PenaltyNode(pen));
  }

  public void addBox(Node box) {
    addNode(box);
  }

  public boolean unBox(Box box) {
    return false;
  }

  public BoxLeaders.Mover getBoxLeadMover() {
    return BoxLeaders.NULL_MOVER;
  }

  public int getStartLine() {
    return 0;
  }

  /* if the spaceFactor is supported it is always > 0 */
  public int getSpaceFactor() {
    return 0;
  }

  public void setSpaceFactor(int sf) {}

  public void resetSpaceFactor() {}

  public void adjustSpaceFactor(int sf) {
    throw new RuntimeException("char not allowed");
  }

  /* if the prevDepth is supported it is always != Dimen.NULL */
  public Dimen getPrevDepth() {
    return Dimen.NULL;
  }

  public void setPrevDepth(Dimen pd) {}

  public void buildPage() {}

  /* if the getParagraph is supported it is always != NodeList.NULL */
  public NodeList getParagraph() {
    return NodeList.NULL;
  }

  public boolean needsParSkip() {
    return false;
  }

  public boolean canTakeLastNode() {
    return true;
  }

  public boolean canTakeLastBox() {
    return canTakeLastNode();
  }

  /* if supported it is always != Language.NULL */
  public Language getInitLang() {
    return Language.NULL;
  }

  public Language getCurrLang() {
    return Language.NULL;
  }

  public void setCurrLang(Language lang) {}

  public abstract boolean isEmpty();

  public abstract Node lastNode();

  public abstract void removeLastNode();

  public abstract Node lastSpecialNode();

  /* TeXtp[218] */
  public void show(Log log, int depth, int breadth) {
    log.startLine().add("### ");
    addOn(log);
    log.add(" entered at line ").add(getStartLine());
    specialShow(log, depth, breadth);
  }

  protected void specialShow(Log log, int depth, int breadth) {}

  public void addOn(Log log) {
    log.add(modeName()).add(" mode");
  }

  public abstract String modeName();

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * The Builder stack
   */

  private static Builder topBuilder = NULL;

  protected Builder enclosing = NULL;

  public static final Builder top() {
    return topBuilder;
  }

  public static final void push(Builder b) {
    synchronized (b) {
      if (b.enclosing == NULL) {
        b.enclosing = topBuilder;
        topBuilder = b;
      } else throw new RuntimeException("builder alrady pushed");
    }
  }

  public static final Builder pop() {
    Builder b = topBuilder;
    if (b != NULL) {
      synchronized (b) {
        topBuilder = b.enclosing;
        b.enclosing = NULL;
      }
    }
    return b;
  }

  public static void showStack(Log log, int depth, int breadth) {
    for (Builder b = topBuilder; b != NULL; b = b.enclosing) b.show(log, depth, breadth);
  }

  public int getPrevGraf() {
    return (enclosing != NULL) ? enclosing.getPrevGraf() : 0;
  }

  public void setPrevGraf(int pg) {
    if (enclosing != NULL) enclosing.setPrevGraf(pg);
  }

  public int nearestValidSpaceFactor() {
    return (enclosing != NULL) ? enclosing.getSpaceFactor() : 0;
  }

  public Dimen nearestValidPrevDepth() {
    return (enclosing != NULL) ? enclosing.getPrevDepth() : Dimen.NULL;
  }
}
