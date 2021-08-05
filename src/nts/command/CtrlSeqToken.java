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
// Filename: nts/command/CtrlSeqToken.java
// $Id: CtrlSeqToken.java,v 1.1.1.1 2000/01/28 05:50:44 ksk Exp $
package nts.command;

import nts.io.Log;
import nts.io.Name;

/** Control sequence Token. */
public class CtrlSeqToken extends Token {

  public interface Meaninger {
    Command get(Name name);

    void set(Name name, Command cmd, boolean glob);
  }

  protected static Meaninger meaninger;

  public static void setMeaninger(Meaninger m) {
    meaninger = m;
  }

  /** The name of the control sequence */
  private final Name name;

  /**
   * Creates a control sequence token of the given name.
   *
   * @param name the name of the control sequence
   */
  public CtrlSeqToken(Name name) {
    this.name = name;
  }

  public CtrlSeqToken(String name) {
    this.name = makeName(name);
  }

  /**
   * Gives object (potentially) associated in table of equivalents.
   *
   * @return the Command to interpret this token.
   */
  public Command meaning() {
    return meaninger.get(name);
  }

  /**
   * Tells that the meaning of the |Token| can be redefined.
   *
   * @return |true|.
   */
  public boolean definable() {
    return true;
  }

  /**
   * Define given |Command| to be equivalent in table of equivalents.
   *
   * @param cmd the object to interpret this token.
   * @param glob if |true| the equivalent is defined globaly.
   */
  public void define(Command cmd, boolean glob) {
    meaninger.set(name, cmd, glob);
  }

  public boolean match(Token tok) {
    return (tok instanceof CtrlSeqToken && ((CtrlSeqToken) tok).name.match(name));
  }

  public void addOn(Log log) {
    name.addEscapedOn(log);
  }

  public void addProperlyOn(Log log) {
    name.addProperlyEscapedOn(log);
  }

  public int numValue() {
    return (name.length() == 1) ? name.codeAt(0).numValue() : -1;
  }

  public Name controlName() {
    return name;
  }

  public String toString() {
    return "<CtrlSequence: " + name + '>';
  }
}
