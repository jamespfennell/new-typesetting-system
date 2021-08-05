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
// Filename: nts/typo/BuilderPrim.java
// $Id: BuilderPrim.java,v 1.1.1.1 2000/04/18 21:38:46 ksk Exp $
package nts.typo;

import nts.builder.Builder;
import nts.command.Command;
import nts.command.CtrlSeqToken;
import nts.command.Primitive;
import nts.command.Token;
import nts.command.TokenList;
import nts.io.Log;

public abstract class BuilderPrim extends BuilderCommand implements Primitive {

  /** The name of the primitive */
  private String name;

  protected BuilderPrim(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final Command getCommand() {
    return this;
  }

  public final void addOn(Log log) {
    log.addEsc(name);
  }

  public final String toString() {
    return "@" + name;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   */

  public static final int INTP_MAX_MARK_WIDTH = newIntParam();

  public static void addNodeToks(Log log, TokenList list) {
    log.add('{');
    list.addOn(log, getConfig().getIntParam(INTP_MAX_MARK_WIDTH));
    log.add('}');
  }

  private static final Token PAR_TOKEN = new CtrlSeqToken("par");

  /* TeXtp[1090] */
  public static final Action START_PAR =
      new Action() {
        public void exec(Builder bld, Token src) {
          backToken(src);
          Paragraph.start(true);
        }
      };

  /* TeXtp[1094,1095] */
  public static final Action FINISH_PAR =
      new Action() {
        public void exec(Builder bld, Token src) {
          backToken(src);
          insertToken(PAR_TOKEN);
        }
      };

  /* TeXtp[1094,1095] */
  public static final Action REJECT =
      new Action() {
        public void exec(Builder bld, Token src) {
          getGrp().reject(src);
        }
      };

  public static final Action EMPTY =
      new Action() {
        public void exec(Builder bld, Token src) {}
      };
}
