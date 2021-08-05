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
// Filename: nts/command/LineInputTokenizer.java
// $Id: LineInputTokenizer.java,v 1.1.1.1 1999/07/28 08:07:11 ksk Exp $
package nts.command;

import nts.io.InputLine;
import nts.io.LineInput;

/**
 * The |LineInputTokenizer| reads internal character codes from a |LineInput| and converts them into
 * |Token|s.
 */
public class LineInputTokenizer extends SequenceTokenizer {

  public interface InputHandler {
    void closeInput();

    InputLine emptyLine();

    InputLine confirmLine(InputLine line);

    Tokenizer makeTokenizer(InputLine line, String desc, boolean addEolc);
  }

  /** The underlying line oriented input reader */
  private LineInput input;

  /** The parametrization object */
  private InputHandler inpHandler;

  private FileName name;

  /**
   * Creates an |LineInputTokenizer| with given input and parametrization object.
   *
   * @param input the underlying reader.
   * @param inpHandler the parametrization object.
   */
  public LineInputTokenizer(LineInput input, InputHandler inpHandler, FileName name) {
    this.input = input;
    this.inpHandler = inpHandler;
    this.name = name;
  }

  /** Closes the underlying |LineInput|. */
  public boolean close() {
    input.close();
    inpHandler.closeInput();
    return true;
  }

  private boolean forceEOF = false;

  public boolean endInput() {
    forceEOF = true;
    return true;
  }

  private boolean used = false;
  private int lineNum = 0;

  public Tokenizer nextTokenizer() {
    if (forceEOF) return Tokenizer.NULL;
    InputLine line = input.readLine();
    lineNum = input.getLineNumber();
    if (line == LineInput.EOF) {
      // XXX maybe use parameter first of SequenceTokenizer
      if (used) return Tokenizer.NULL;
      line = inpHandler.emptyLine();
      lineNum = 1;
    }
    used = true;
    line = inpHandler.confirmLine(line);
    return inpHandler.makeTokenizer(line, "l." + lineNum + ' ', true);
  }

  public boolean enoughContext() {
    return true;
  }

  public FilePos filePos() {
    return new FilePos(name, lineNum);
  }
}
