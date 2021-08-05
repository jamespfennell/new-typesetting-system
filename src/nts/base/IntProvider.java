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
// Filename: nts/base/IntProvider.java
// $Id: IntProvider.java,v 1.1.1.1 2001/05/16 11:15:27 ksk Exp $
package nts.base;

/**
 * Provider of integer value.
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 */
public interface IntProvider {
  /**
   * Provides the value of type |int|
   *
   * @return the provided |int| value
   */
  int intVal();
}
