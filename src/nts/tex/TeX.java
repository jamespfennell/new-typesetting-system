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
// Filename: nts/tex/TeX.java
// $Id: TeX.java,v 1.1.1.1 2001/08/31 15:58:02 ksk Exp $
package nts.tex;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import nts.builder.Builder;
import nts.builder.PageBuilder;
import nts.command.*;
import nts.io.*;
import nts.math.MathPrim;
import nts.typo.Page;
import nts.typo.TypoCommand;

public final class TeX {

  /* instances are forbidden */
  private TeX() {}

  public static void main(String args[]) {

    // Locale.setDefault(Locale.US);
    // System.setProperty("file.encoding", "8859_1");
    // System.err.println("enc = " + System.getProperty("file.encoding"));
    OutputStream stdout = new FileOutputStream(FileDescriptor.out);
    Writer stdWr = TeXIOHandler.makeWriter(stdout);
    PrintWriter out = new PrintWriter(stdWr, true);

    out.println(TeXConfig.BANNER);
    // (" (no format preloaded)");

    /*
     * NTS version #, Copyright (C) 2001  Karel Skoupy
     * Gnomovision comes with ABSOLUTELY NO WARRANTY; for details
     * type `show w'.  This is free software, and you are welcome
     * to redistribute it under certain conditions; type `show c'
     * for details.
     */

    out.println("NTS comes with ABSOLUTELY NO WARRANTY.");
    out.println("This is free software, and you are welcome to redistribute");
    out.println("it under certain conditions; for details see the file");
    out.println("COPYING in the distribution");

    TeXCharMapper mapper = new TeXCharMapper();
    /* mapper has no config yet */

    Token.setCharCodeMaker(mapper);

    Reader stdin = TeXIOHandler.makeReader(System.in);
    LineInput input = new LineInput(stdin, mapper);
    InputLine firstLine = InputLine.NULL;

    String fmtName = System.getProperty("nts.fmt");
    String progName = System.getProperty("nts.progname");

    if (progName == null) progName = fmtName;
    if (progName == null) progName = "nts";

    FileOpener opener = new KpseFileOpener(progName);

    if (args.length > 0) {
      StringBuffer buf = new StringBuffer(args[0]);
      for (int i = 1; i < args.length; i++) buf.append(' ').append(args[i]);
      firstLine = new InputLine(buf.toString(), mapper);
    }

    /* TeXtp[37] */
    for (; ; ) {
      if (firstLine != InputLine.NULL) {
        firstLine.skipSpaces();
        if (!firstLine.empty()) break;
        out.println("Please type the name of your input file.");
      }
      out.write("**");
      out.flush();
      firstLine = input.readLine();
      if (firstLine == LineInput.EOF) {
        out.println();
        out.println("! End of file on the terminal... why?");
        System.exit(1);
      }
    }

    Primitives config = null;
    Object fontSeed = null;

    if (fmtName != null) {
      FileName name = new TeXCharMapper.TeXFileName();
      name.setPath(fmtName);
      name.addDefaultExt(TeXConfig.FMT_EXT);
      try {
        ObjectInputStream format = new ObjectInputStream(opener.openForReading(name, "fmt", true));
        long time = System.currentTimeMillis();
        config = (Primitives) format.readObject();
        fontSeed = format.readObject();
        time = System.currentTimeMillis() - time;
        System.err.println(name.getPath() + " loaded in " + time + " milliseconds");
        format.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(2);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        System.exit(2);
      }
    } else config = new Primitives();

    Command.setConfig(config.getCommandConfig());
    TypoCommand.setTypoConfig(config.getTypoConfig());
    MathPrim.setMathConfig(config.getMathConfig());
    TeXCharMapper.setConfig(config.getCharMapConfig());

    firstLine = firstLine.addEndOfLineChar();
    InputLine savedFirstLine = new InputLine(firstLine);
    LineOutput terminal = new WriterLineOutput(stdWr, mapper, true, TeXConfig.MAX_PRINT_LINE);
    TeXIOHandler ioHand =
        new TeXIOHandler(config.getIOHandConfig(), savedFirstLine, mapper, opener);
    TeXFontHandler fontHand = new TeXFontHandler(config.getFontHandConfig(), ioHand, fontSeed);
    TokenizerStack tokstack = new TokenizerStack();

    tokstack.push(new StdinTokenizer(ioHand.getTokenMaker(), firstLine));

    EditException edit = null;
    int status = 0;

    Command.setIOHandler(ioHand);
    Command.setInput(input);
    Command.setTerminal(terminal);
    Command.setTokStack(tokstack);
    Builder.push(new PageBuilder(0, Page.getPageSplit()));
    TypoCommand.setTypoHandler(fontHand);
    try {
      if (ioHand.getTokenMaker().scanCat(firstLine.peekNextRawCode()) != InputLineTokenizer.ESCAPE)
        Command.startInput();
      boolean dumping = Command.mainLoop();
      ioHand.setAfterEnd();
      Command.cleanUp();
      CondPrim.cleanUp();
      if (dumping) {
        try {
          config.preparePatterns();
          ObjectOutputStream dumper = ioHand.getDumper();
          long time = System.currentTimeMillis();
          dumper.writeObject(config);
          dumper.writeObject(fontHand.getSeed());
          time = System.currentTimeMillis() - time;
          ;
          System.err.println("format file stored in " + time + " milliseconds");
          dumper.close();
        } catch (IOException e) {
          status = 2;
          System.err.println(e);
          // e.printStackTrace();
        }
      }
    } catch (FatalError e) {
      status = 1;
    } catch (EditException e) {
      edit = e;
    } catch (OutOfMemoryError e) {
      status = 2;
      System.err.println(e);
    }
    // XXX TeXtp[1333]
    ioHand.finishDvi();
    FileName logName = ioHand.getLogName();
    ioHand.closeLogFile();
    terminal.startLine();
    if (logName != FileName.NULL && !InteractionPrim.isSilent()) {
      terminal.add("Transcript written on " + logName.getPath() + '.');
      terminal.endLine(); // XXX getPath() is wrong here
    }
    terminal.close();
    if (edit != null) edit.exec();
    System.exit(status);
  }

  private static final class StdinTokenizer extends SequenceTokenizer {

    private InputLineTokenizer.TokenMaker maker;
    private InputLine line = InputLine.NULL;

    public StdinTokenizer(InputLineTokenizer.TokenMaker maker, InputLine first) {
      super(
          (first == InputLine.NULL)
              ? Tokenizer.NULL
              : new InputLineTokenizer(first, maker, "<*> "));
      this.maker = maker;
      line = first;
    }

    public Tokenizer nextTokenizer() {
      Command.ensureOpenLog();
      if (!InteractionPrim.isInteractive()) Command.fatalError("NoEnd");
      if (line != InputLine.NULL && line.wasEmpty(true))
        Command.normLog.startLine().add("(Please type a command or say `\\end')");
      Command.normLog.endLine();
      line = Command.promptInput("*");
      if (line != LineInput.EOF) {
        line = line.addEndOfLineChar();
        return new InputLineTokenizer(line, maker, "<*> ");
      }
      return Tokenizer.NULL;
    }
  }
}
