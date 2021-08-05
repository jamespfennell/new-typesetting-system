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
// Filename: nts/command/SpaceToken.java
// $Id: SpaceToken.java,v 1.1.1.1 2000/02/16 11:01:14 ksk Exp $
package nts.command;

import nts.io.CharCode;
import nts.io.Log;

public class SpaceToken extends Token {

  public static final CharCode CODE = CharToken.makeCharCode(' ');
  public static final SpaceToken TOKEN = new SpaceToken();

  public CharCode nonActiveCharCode() {
    return CODE;
  }

  public int numValue() {
    return CODE.numValue();
  }

  public CharCode charCode() {
    return CODE;
  }

  public Token makeCharToken(CharCode code) {
    return DirtySpaceToken.MAKER.make(code);
  }

  public boolean matchSpace() {
    return true;
  }

  public boolean match(Token tok) {
    return (tok instanceof SpaceToken);
  }

  public boolean sameCatAs(Token tok) {
    return (tok instanceof SpaceToken || tok instanceof DirtySpaceToken);
  }

  public void addOn(Log log) {
    log.add(' ');
  }

  public String toString() {
    return "<Space>";
  }

  private static CharHandler handler;

  public static void setHandler(CharHandler hnd) {
    handler = hnd;
    DirtySpaceToken.setHandler(hnd);
  }

  public static final String DESCRIPTION = "blank space";

  public static final Command MEANING =
      new Command() {

        public final boolean isSpacer() {
          return true;
        }

        public CharCode charCode() {
          return CODE;
        }

        public void exec(Token src) {
          handler.handle(CODE, src);
        }

        public final void exec(Token src, int prefixes) {
          Token tok = nextExpToken();
          meaningOf(tok).execute(tok, prefixes);
        }

        public void addOn(Log log) {
          log.add(DESCRIPTION).add(' ').add(' ');
        }

        public boolean sameAs(Command cmd) {
          return (getClass() == cmd.getClass());
        }

        public Token origin() {
          return TOKEN;
        }
      };

  public Command meaning() {
    return MEANING;
  }
}

class DirtySpaceToken extends CharToken {

  public static final Maker MAKER =
      new Maker() {
        public Token make(CharCode code) {
          if (code.match(' ')) return SpaceToken.TOKEN;
          return new DirtySpaceToken(code);
        }
      };

  public DirtySpaceToken(CharCode code) {
    super(code);
  }

  public boolean match(CharToken tok) {
    return (tok instanceof DirtySpaceToken && tok.match(code));
  }

  public boolean sameCatAs(Token tok) {
    return SpaceToken.TOKEN.sameCatAs(tok);
  }

  public Maker getMaker() {
    return MAKER;
  }

  public String toString() {
    return "<DirtySpace: " + code + '>';
  }

  private static CharHandler handler;

  public static void setHandler(CharHandler hnd) {
    handler = hnd;
  }

  public Command meaning() {
    return new Meaning() {

      public void exec(Token src) {
        handler.handle(code, src);
      }

      public final void exec(Token src, int prefixes) {
        Token tok = nextExpToken();
        meaningOf(tok).execute(tok, prefixes);
      }

      protected String description() {
        return SpaceToken.DESCRIPTION;
      }
    };
  }
}
