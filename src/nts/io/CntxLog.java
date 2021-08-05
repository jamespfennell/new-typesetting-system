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
// Filename: nts/io/CntxLog.java
// $Id: CntxLog.java,v 1.1.1.1 2000/05/02 06:02:30 ksk Exp $
package nts.io;

public final class CntxLog {

  private final int maxDepth;
  private final int maxCount;
  private final String prefix;

  private CntxLog(int md, int mc, String p) {
    maxDepth = md;
    maxCount = mc;
    prefix = p;
  }

  private CntxLog(int md, int mc) {
    maxDepth = md;
    maxCount = mc;
    prefix = "";
  }

  private int count = 0;

  /* STRANGE
   * this method is public (and count is not a local variable)
   * only for third part of discretionary
   */
  /* TeXtp[182] */
  public void addItems(Log log, CntxLoggableEnum items) {
    if (prefix.length() <= maxDepth) {
      while (count <= maxCount && items.hasMoreContextLoggables()) {
        log.endLine().add(prefix);
        if (count++ == maxCount) log.add("etc.");
        else items.nextContextLoggable().addOn(log, this);
      }
    } else if (count <= maxCount && items.hasMoreContextLoggables()) log.add(" []");
  }

  private void addItem(Log log, CntxLoggable item) {
    if (prefix.length() <= maxDepth) {
      if (count <= maxCount) {
        log.endLine().add(prefix);
        if (count++ == maxCount) log.add("etc.");
        else item.addOn(log, this);
      }
    } else if (count <= maxCount) log.add(" []");
  }

  private CntxLog descendant(char p) {
    return new CntxLog(maxDepth, maxCount, prefix + p);
  }

  public void addOn(Log log, CntxLoggableEnum items, char p) {
    descendant(p).addItems(log, items);
  }

  public void addOn(Log log, CntxLoggable item, char p) {
    descendant(p).addItem(log, item);
  }

  public void addOn(Log log, CntxLoggableEnum items) {
    addOn(log, items, '.');
  }

  public void addOn(Log log, CntxLoggable item) {
    addOn(log, item, '.');
  }

  public static void addItems(Log log, CntxLoggableEnum items, int md, int mc) {
    (new CntxLog(md, mc)).addItems(log, items);
  }

  public static void addItem(Log log, CntxLoggable item, int md, int mc) {
    (new CntxLog(md, mc)).addItem(log, item);
  }
}
