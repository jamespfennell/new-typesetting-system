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
// Filename: nts/tex/KpseFileOpener.java
// $Id: KpseFileOpener.java,v 1.1.1.1 2001/03/06 21:02:04 ksk Exp $
package nts.tex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import nts.command.FileName;

public class KpseFileOpener implements FileOpener {

  private String progNameArg;

  public KpseFileOpener(String progName) {
    if (progName != null) progNameArg = "--progname=" + progName;
  }

  public KpseFileOpener() {}

  private static final String FUTILE_PREFIX = "." + File.separator;

  // static { System.err.println("FUTILE_PREFIX = '" + FUTILE_PREFIX + '\''); }

  public InputStream openForReading(FileName name, String format, boolean mustExist)
      throws IOException {
    String path = name.getPath();
    String found = findFileName(path, format, mustExist);
    if (found != null && found.length() > 0) {
      if (File.separatorChar != '/') found = found.replace('/', File.separatorChar);
      if (found.startsWith(FUTILE_PREFIX) && !path.startsWith(FUTILE_PREFIX))
        found = found.substring(FUTILE_PREFIX.length());
      // System.err.println("file found: " + path + " = " + found);
      name.setPath(found);
      path = found;
    } else {
      name.addDefaultExt(format);
      path = name.getPath();
    }
    return new FileInputStream(path);
  }

  private boolean texfmOutputFlag = false;
  private String texfmOutputVal = null;

  public OutputStream openForWriting(FileName name, String format) throws IOException {
    name.addDefaultExt(format);
    String path = name.getPath();
    // System.err.println("openForWriting('" + path + "')");
    try {
      return new FileOutputStream(path);
    } catch (IOException e) {
      if (!(new File(path)).isAbsolute()) {
        if (!texfmOutputFlag) {
          texfmOutputVal = variableValue("TEXMFOUTPUT");
          if (texfmOutputVal != null && texfmOutputVal.length() == 0) texfmOutputVal = null;
          texfmOutputFlag = true;
        }
        // System.err.println("TEXMFOUTPUT = '" + texfmOutputVal + "'");
        if (texfmOutputVal != null) {
          path = texfmOutputVal + File.separator + path;
          // System.err.println("path = '" + path + "'");
          OutputStream out = new FileOutputStream(path);
          name.setPath(path);
          return out;
        }
      }
      throw e;
    }
  }

  private boolean kpseConfigured = false;
  private boolean kpseWorking = false;
  private boolean kpseAdvanced = false;
  private boolean useProgName = false;

  private static final String S_KPSEWHICH = "kpsewhich";
  private static final String S_VERSION = "version";
  private static final String[] CONFIG_ARGS = {S_KPSEWHICH, "--" + S_VERSION};

  private void kpseConfigure() {
    if (!kpseConfigured) {
      try {
        String versionOutput = captureOutput(CONFIG_ARGS);
        kpseWorking = true;
        int idx = versionOutput.indexOf(S_VERSION);
        if (idx >= 0) {
          idx += S_VERSION.length();
          while (Character.isWhitespace(versionOutput.charAt(idx))) idx++;
          ParsePosition pos = new ParsePosition(idx);
          NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
          format.setParseIntegerOnly(true);
          int major = 0;
          int minor = 0;
          Number majorNum = format.parse(versionOutput, pos);
          if (majorNum != null) {
            major = majorNum.intValue();
            idx = pos.getIndex();
            if (versionOutput.charAt(idx) == '.') {
              pos.setIndex(idx + 1);
              Number minorNum = format.parse(versionOutput, pos);
              if (minorNum != null) minor = minorNum.intValue();
            }
          }
          kpseAdvanced = (major > 3 || major == 3 && minor >= 2);
          useProgName = (kpseAdvanced && (progNameArg != null));
        }
      } catch (IOException e) {
      }
      kpseConfigured = true;
    }
  }

  protected String variableValue(String var) {
    kpseConfigure();
    if (kpseWorking) {
      int argc = 2;
      if (useProgName) argc++;
      String[] args = new String[argc];
      int i = 0;
      args[i++] = S_KPSEWHICH;
      if (useProgName) args[i++] = progNameArg;
      args[i++] = "--expand-var=$" + var;
      try {
        return captureOutput(args);
      } catch (IOException e) {
      }
    }
    return null;
  }

  protected String findFileName(String name, String format, boolean mustExist) {
    kpseConfigure();
    if (kpseWorking) {
      int argc = 3;
      if (useProgName) argc++;
      if (mustExist) argc++;
      String[] args = new String[argc];
      int i = 0;
      args[i++] = S_KPSEWHICH;
      if (useProgName) args[i++] = progNameArg;
      if (kpseAdvanced) args[i++] = "--format=" + format;
      else args[i++] = "--format=." + format;
      if (mustExist) args[i++] = "--must-exist";
      args[i++] = name;
      try {
        return captureOutput(args);
      } catch (IOException e) {
      }
    }
    return null;
  }

  // XXX IOException = no such command - remember it!
  private String captureOutput(String[] args) throws IOException {
    /*
    System.err.print("exec:");
    for (int i = 0; i < args.length; i++) {
        System.err.print(' ');
        System.err.print(args[i]);
    }
    System.err.println();
       */
    Process proc = Runtime.getRuntime().exec(args);
    try {
      Reader in = makeReader(proc.getInputStream());
      StringBuffer buf = new StringBuffer();
      int c, len;
      while ((c = in.read()) >= 0) buf.append((char) c);
      len = buf.length();
      if (len > 0) {
        if (buf.charAt(len - 1) == '\r'
            || buf.charAt(len - 1) == '\n' && --len > 0 && buf.charAt(len - 1) == '\r') len--;
        buf.setLength(len);
      }
      // System.err.println("gives: " + buf);
      try {
        proc.waitFor();
        // System.err.println("status = " + proc.exitValue());
        if (proc.exitValue() == 0) {
          // System.err.println("success");
          return buf.toString();
        }
      } catch (InterruptedException e) {
      }
    } catch (IOException e) {
    }
    return null;
  }

  private static Reader makeReader(InputStream in) {
    try {
      return new InputStreamReader(in, "8859_1");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Can't happen");
    }
  }
}
