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
// Filename: nts/tex/TeXFontHandler.java
// $Id: TeXFontHandler.java,v 1.1.1.1 2001/03/06 15:18:09 ksk Exp $
package nts.tex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;
import nts.base.Dimen;
import nts.base.Num;
import nts.base.PairKey;
import nts.command.CommandBase;
import nts.command.FileName;
import nts.dvi.DviTypeSetter;
import nts.io.Log;
import nts.io.Loggable;
import nts.io.Name;
import nts.node.FontMetric;
import nts.node.TypeSetter;
import nts.tfm.BadTeXFmException;
import nts.tfm.TeXFm;
import nts.tfm.TeXFontMetric;
import nts.typo.NullFontMetric;
import nts.typo.TypoCommand;

public class TeXFontHandler extends CommandBase
    implements TypoCommand.TypoHandler, DviTypeSetter.FontInformator {

  public interface Config {
    Num defaultHyphenChar();

    Num defaultSkewChar();
  }

  private static final class Sequencer implements Serializable {
    public int nextIdNum = 0;
    public FontMetric lastLoaded = NullFontMetric.METRIC;
    /* STRANGE
     * The only purpose of this field is to artificially restrict creating
     * of new \fontdimen parameters only to the last loaded metric.
     */
  }

  protected static final TeXFmGroup NULL_GROUP = null;

  protected static class TeXFmGroup implements Serializable {

    private final Name name;
    private final TeXFm tfm;
    private final byte[] dirName;
    private final byte[] fileName;
    private final Vector members = new Vector();

    public TeXFmGroup(Name name, TeXFm tfm, String path) {
      this.name = name;
      this.tfm = tfm;
      String fname = (new File(path)).getName();
      int i = path.lastIndexOf(fname);
      dirName = (i > 0) ? path.substring(0, i).getBytes() : new byte[0];
      for (i = fname.length(); --i > 0 && fname.charAt(i) != '.'; )
        ;
      fileName = ((i > 0) ? fname.substring(0, i) : fname).getBytes();
    }

    public Name getName() {
      return name;
    }

    public TeXFm getTfm() {
      return tfm;
    }

    /* TeXtp[1260] */
    public FontMetric get(Dimen size, Num scale, Name ident, Config config, Sequencer seq) {
      if (size == Dimen.NULL) size = Dimen.valueOf(tfm.getDesignSize());
      if (scale != Num.NULL) size = size.times(scale.intVal(), 1000);
      TeXFontMetric metric;
      for (int i = 0; i < members.size(); i++) {
        metric = (TeXFontMetric) members.elementAt(i);
        if (size.equals(metric.getAtSize())) {
          metric.setIdent(ident);
          return metric;
        }
      }
      metric = new InfoTeXFontMetric(name, tfm, size, ident, seq.nextIdNum++, dirName, fileName);
      metric.setNumParam(FontMetric.NUM_PARAM_HYPHEN_CHAR, config.defaultHyphenChar());
      metric.setNumParam(FontMetric.NUM_PARAM_SKEW_CHAR, config.defaultSkewChar());
      members.addElement(metric);
      seq.lastLoaded = metric;
      return metric;
    }
  }

  private static class Seed implements Serializable {
    public final HashMap groupTab;
    public final Sequencer sequencer;

    public Seed(HashMap tab, Sequencer seq) {
      groupTab = tab;
      sequencer = seq;
    }
  }

  private Config config;
  private TeXIOHandler ioHand;
  private HashMap groupTab;
  private Sequencer sequencer;

  public TeXFontHandler(Config config, TeXIOHandler ioHand, Object seed) {
    this.config = config;
    this.ioHand = ioHand;
    if (seed != null && seed instanceof Seed) {
      Seed s = (Seed) seed;
      groupTab = s.groupTab;
      sequencer = s.sequencer;
    } else {
      groupTab = new HashMap();
      sequencer = new Sequencer();
    }
  }

  public TeXFontHandler(Config config, TeXIOHandler ioHand) {
    this(config, ioHand, null);
  }

  public Object getSeed() {
    return new Seed(groupTab, sequencer);
  }

  public FontMetric getMetric(FileName name, Dimen size, Num scale, Name ident, Loggable tok) {
    String path = name.getPath();
    TeXFmGroup group = (TeXFmGroup) groupTab.get(path);
    if (group == NULL_GROUP) {
      Name groupName = name.baseName();
      TeXFm tfm;
      try {
        tfm = TeXFm.readFrom(ioHand.openTeXFm(name));
      } catch (FileNotFoundException e) {
        fontError("TFMnotFound", path, size, scale, tok);
        return FontMetric.NULL;
      } catch (BadTeXFmException e) {
        fontError("TFMisBad", path, size, scale, tok);
        return FontMetric.NULL;
      } catch (IOException e) {
        System.err.println(e + "when reading " + name);
        return FontMetric.NULL;
      }
      group = new TeXFmGroup(groupName, tfm, path);
      groupTab.put(path, group);
    }
    return group.get(size, scale, ident, config, sequencer);
  }

  private void fontError(
      String err, final String path, final Dimen size, final Num scale, final Loggable tok) {
    error(
        err,
        new Loggable() {
          public void addOn(Log log) {
            log.add(tok).add('=').add(path);
            if (size != Dimen.NULL) log.add(" at " + size + "pt");
            if (scale != Num.NULL) log.add(" scaled " + scale);
          }
        });
  }

  /*
   *		Indexed Font Dimension Parameters
   */
  // XXX treat the font numeric parameters in the same way

  private HashMap paramTab = new HashMap();

  public TypoCommand.FontDimen getFontDimen(FontMetric metric, int num) {
    int idx = num - 1;
    int maxDefined = -1;
    if (validRawDimIdx(idx)) {
      if (getRawDimPar(metric, idx) != Dimen.NULL) return makeFontDimen(metric, idx);
      else if (metric == sequencer.lastLoaded) {
        defineRawDimParsUpTo(metric, idx);
        return makeFontDimen(metric, idx);
      }
    } else {
      /*
       * If there already are some parameters in paramTab associated with
       * the metric, the upper bound is simply associated to metric.
       */
      Num max = (Num) paramTab.get(metric);
      if (max != Num.NULL) maxDefined = max.intVal();
      /*
       * We can increase the upper bound only if the metric is the last
       * loaded. It is artificial constraint for compatibility with TeX.
       */
      if (idx > maxDefined && metric == sequencer.lastLoaded) { // SSS
        defineRawDimParsUpTo(metric, idx);
        maxDefined = idx;
        paramTab.put(metric, Num.valueOf(maxDefined));
      }
      if (0 <= idx && idx <= maxDefined)
        return makeFontDimen(new PairKey(metric, Num.valueOf(idx)));
    }
    /*
     * If all above fails the error message is isued and fake DimenParam
     * is returned.
     */
    if (maxDefined < 0) maxDefined = maxDefinedRawDimPar(metric);
    error("TooBigFontdimenNum", esc(metric.getIdent()), num(maxDefined + 1));
    return new TypoCommand.FontDimen() {
      public Dimen get() {
        return Dimen.ZERO;
      }

      public void set(Dimen dim) {}
    };
  }

  private TypoCommand.FontDimen makeFontDimen(final FontMetric metric, final int idx) {
    return new TypoCommand.FontDimen() {
      public Dimen get() {
        return getRawDimPar(metric, idx);
      }

      public void set(Dimen dim) {
        setRawDimPar(metric, idx, dim);
      }
    };
  }

  private TypoCommand.FontDimen makeFontDimen(final PairKey key) {
    return new TypoCommand.FontDimen() {
      public Dimen get() {
        Dimen dim = (Dimen) paramTab.get(key);
        return (dim != Dimen.NULL) ? dim : Dimen.ZERO;
      }

      public void set(Dimen dim) {
        paramTab.put(key, dim);
      }
    };
  }

  /*
   *		reverse mapping from high to raw dimen param indexes
   */

  private static final int[] rTab;

  static {
    int lTab = TeXFontMetric.numberOfRawDimenPars();
    int mTab = -1;
    for (int i = 0; i < lTab; i++)
      if (mTab < TeXFontMetric.rawDimenParNumber(i)) mTab = TeXFontMetric.rawDimenParNumber(i);

    rTab = new int[mTab + 1];
    for (int i = 0; i < rTab.length; rTab[i++] = -1)
      ;
    for (int i = 0; i < lTab; i++) {
      int j = TeXFontMetric.rawDimenParNumber(i);
      if (j >= 0) rTab[j] = i;
    }
  }

  private static boolean validRawDimIdx(int idx) {
    return (0 <= idx && idx < rTab.length && rTab[idx] >= 0);
  }

  private Dimen getRawDimPar(FontMetric metric, int idx) {
    return metric.getDimenParam(rTab[idx]);
  }

  private void setRawDimPar(FontMetric metric, int idx, Dimen val) {
    int lTab = TeXFontMetric.numberOfRawDimenPars();
    for (int i = 0; i < lTab; i++)
      if (TeXFontMetric.rawDimenParNumber(i) == idx) metric.setDimenParam(i, val);
  }

  private int maxDefinedRawDimPar(FontMetric metric) {
    for (int i = rTab.length; --i >= 0; )
      if (rTab[i] >= 0 && metric.getDimenParam(rTab[i]) != Dimen.NULL) return i;
    return -1;
  }

  private void defineRawDimParsUpTo(FontMetric metric, int idx) {
    int lTab = TeXFontMetric.numberOfRawDimenPars();
    for (int i = 0; i < lTab; i++)
      if (TeXFontMetric.rawDimenParNumber(i) <= idx && metric.getDimenParam(i) == Dimen.NULL)
        metric.setDimenParam(i, Dimen.ZERO);
  }

  /*
   *		Setter
   */

  public TypeSetter getSetter() {
    return ioHand.getTypeSetter(this);
  }

  public DviTypeSetter.FontInfo getInfo(FontMetric metric) {
    if (metric instanceof InfoTeXFontMetric) return (InfoTeXFontMetric) metric;
    else throw new RuntimeException("No info about a font metric");
  }
}

class InfoTeXFontMetric extends TeXFontMetric implements DviTypeSetter.FontInfo {

  private final int idNum;
  private final byte[] dirName;
  private final byte[] fileName;

  public InfoTeXFontMetric(
      Name name, TeXFm tfm, Dimen atSize, Name ident, int idNum, byte[] dirName, byte[] fileName) {
    super(name, tfm, atSize, ident);
    this.idNum = idNum;
    this.dirName = dirName;
    this.fileName = fileName;
  }

  public int getIdNumber() {
    return idNum;
  }

  public byte[] getDirName() {
    return dirName;
  }

  public byte[] getFileName() {
    return fileName;
  }

  public String toString() {
    return "<TeXFontMetric "
        + getName()
        + '('
        + ((dirName.length != 0) ? new String(dirName) + '/' : "")
        + new String(fileName)
        + ")>";
  }
}
