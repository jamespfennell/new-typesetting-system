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
// Filename: nts/tfm/TeXMathExtFm.java
// $Id: TeXMathExtFm.java,v 1.1.1.1 2000/03/24 16:19:48 ksk Exp $
package nts.tfm;

/** Internal representation of TeX Math Extension font metric. */
public class TeXMathExtFm extends TeXFm {

  /**
   * Creates new internal representation of TeX font metric.
   *
   * @param checkSum 32 bit checksum of tfm file.
   * @param designSize design size of the font.
   * @param firstCharCode first character code present in the font.
   * @param charTable table of character information.
   * @param boundaryChar code of invisible boundary character.
   * @param boundaryStart index to |ligKernTable| where the ligature/kern program for he boundary
   *     char starts.
   * @param ligKernTable table of ligature/kern instructions.
   * @param paramTable table of font dimension parameters.
   * @param codingScheme coding scheme.
   * @param family family name.
   * @param face Xerox face code.
   * @param sevenBitSafe indication if only 7 bit character codes are used.
   * @param headerRest uninterpreted rest of tfm header.
   * @param restIndex starting index of uninterpreted header rest.
   */
  TeXMathExtFm(
      int checkSum,
      FixWord designSize,
      short firstCharCode,
      CharInfo[] charTable,
      short boundaryChar,
      int boundaryStart,
      LigKern[] ligKernTable,
      FixWord[] paramTable,
      String codingScheme,
      String family,
      int face,
      boolean sevenBitSafe,
      int[] headerRest,
      int restIndex) {
    super(
        checkSum,
        designSize,
        firstCharCode,
        charTable,
        boundaryChar,
        boundaryStart,
        ligKernTable,
        paramTable,
        codingScheme,
        family,
        face,
        sevenBitSafe,
        headerRest,
        restIndex);
  }

  /*
   * Does the same dump as its superclass only forces the chracter codes
   * to be printed in numerical form. See TFtoPL[38].
   */

  /**
   * Dumps its content on property list dumper.
   *
   * @param dmp the property list dumper.
   * @return the dumper for subsequent dumps.
   */
  public PLDumper dump(PLDumper dmp) {
    dmp.forceNumChars();
    return super.dump(dmp);
  }

  /*
   * The property names of first 7 font dimension parameters are common for
   * all types of tfm files and they are provided by the base class.
   * In case of TeX Math Symbols the subsequent names are stored in
   * the following table. See TFtoPL[61].
   */

  private static int init_fp = TeXFm.FP_MAX;
  public static final int FP_DEFAULT_RULE_THICKNESS = init_fp++,
      FP_BIG_OP_SPACING1 = init_fp++,
      FP_BIG_OP_SPACING2 = init_fp++,
      FP_BIG_OP_SPACING3 = init_fp++,
      FP_BIG_OP_SPACING4 = init_fp++,
      FP_BIG_OP_SPACING5 = init_fp++,
      FP_MAX = init_fp++;

  /** Table of property names unique for this tfm file type */
  protected static final String[] paramLabel = {
    "DEFAULTRULETHICKNESS", "BIGOPSPACING1", "BIGOPSPACING2",
    "BIGOPSPACING3", "BIGOPSPACING4", "BIGOPSPACING5",
  };

  /**
   * Gives the property name for font dimension parameter.
   *
   * @param i the number of the parameter.
   * @return the property name.
   */
  protected String paramName(int i) {
    return (i < super.paramLabel.length)
        ? super.paramLabel[i]
        : ((i -= super.paramLabel.length) < paramLabel.length) ? paramLabel[i] : null;
  }
}
