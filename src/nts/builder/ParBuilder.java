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
// Filename: nts/builder/ParBuilder.java
// $Id: ParBuilder.java,v 1.1.1.1 2001/03/20 11:21:47 ksk Exp $
package nts.builder;

import nts.io.Log;
import nts.node.Language;
import nts.node.LanguageNode;
import nts.node.NodeList;

public class ParBuilder extends HorizBuilder {

  private final Language initLang;
  private Language currLang;

  public ParBuilder(int line, Language initLang) {
    super(line);
    this.initLang = initLang;
    currLang = initLang;
  }

  public String modeName() {
    return "horizontal";
  }

  public boolean willBeBroken() {
    return true;
  }

  public NodeList getParagraph() {
    return list;
  }

  public Language getInitLang() {
    return initLang;
  }

  public Language getCurrLang() {
    return currLang;
  }

  public void setCurrLang(Language lang) {
    currLang = lang;
    addNode(new LanguageNode(lang));
  }

  /* TeXtp[218] */
  protected void specialShow(Log log, int depth, int breadth) {
    if (!initLang.isCommon()) log.add(" (").add(initLang).add(')');
    super.specialShow(log, depth, breadth);
  }

  /* TeXtp[219] */
  protected void specialShow(Log log) {
    super.specialShow(log);
    if (!currLang.isZero()) {
      log.add(", current language ");
      currLang.addNumberOn(log);
    }
  }
}
