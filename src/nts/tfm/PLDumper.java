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
// Filename: nts/tfm/PLDumper.java
// $Id: PLDumper.java,v 1.1.1.1 1999/07/28 08:07:15 ksk Exp $
package nts.tfm;

/**
 * Interface |PLDumper| defines methods which class should provide to produce a property list.
 *
 * <p>Each property list consist of property name folowed by zero or more property values. The
 * values in turn may be property lists too. For detailed description of property list see the
 * documentation of pltotf program.
 *
 * <p>The interface is abstract in the sense that it makes no asumption about output formatting or
 * media.
 *
 * <p>Each of output methods returns reference to PLDumper (typicaly |this| reference). It allows
 * for chaining several output steps together. For example:
 * |dumper.open("NUMBER").addDec(number).close();|
 */
public interface PLDumper {

  /**
   * Opens a new property list with given property name.
   *
   * @param s the name of the property to be listed.
   * @return |PLDumper| for further output.
   */
  PLDumper open(String p);

  /**
   * Finishes the current list.
   *
   * @return |PLDumper| for further output.
   */
  PLDumper close();

  /**
   * Add a decimal number as a property value.
   *
   * @param i the number to be added to current list.
   * @return |PLDumper| for further output.
   */
  PLDumper addDec(int i);

  /**
   * Add an octal number as a property value.
   *
   * @param i the number to be added to current list.
   * @return |PLDumper| for further output.
   */
  PLDumper addOct(int i);

  /**
   * Add a real number as a property value. An |Object| reference is used for representation of the
   * real number. The |toString| method of such object is used to get the textual representation.
   *
   * @param o the object which represents the real number.
   * @return |PLDumper| for further output.
   */
  PLDumper addReal(Object o);

  /**
   * Add a Xerox face code as a property value.
   *
   * @param f the numeric Xerox face code.
   * @return |PLDumper| for further output.
   */
  PLDumper addFace(int f);

  /**
   * Add a character string as a property value.
   *
   * @param s the string to be added to current list.
   * @return |PLDumper| for further output.
   */
  PLDumper addStr(String s);

  /**
   * Add a character code as a property value.
   *
   * @param s the character code to be added to current list.
   * @return |PLDumper| for further output.
   */
  PLDumper addChar(short c);

  /**
   * Add a boolean as a property value.
   *
   * @param b the boolean value to be added to current list.
   * @return |PLDumper| for further output.
   */
  PLDumper addBool(boolean b);

  /**
   * Tells the |PLDumper| that it should use numeric notation for character codes even in cases
   * where it would output the symbolic form.
   */
  void forceNumChars();
}
