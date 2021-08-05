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
// Filename: nts/base/EqTable.java
// $Id: EqTable.java,v 1.1.1.1 2000/02/01 00:17:24 ksk Exp $
package nts.base;

import java.io.Serializable;

public abstract class EqTable {

  public static class NumKind {

    public void restored(int key, Object oldVal) {}

    public void retained(int key) {}
  }

  public static class ObjKind {

    public void restored(Object key, Object oldVal) {}

    public void retained(Object key) {}
  }

  /*
   * method |save| which saves its equivalent for restoring later
   * if overriden.
   *
   * In practical terms when an object wants to get benefit of saving and
   * restoring its associated value when the hash level pushes and pops
   * it must do four things:
   * (1) implement this interface.
   * (2) have a member (say |eqLevel|) which is initially |0| and is
   *	   accessed by |getEqLevel| and assigned by |setEqLevel|.
   * (3) call |save(this)| of appropriate |EqTable| whenever before
   *     its new associated value change non globaly.
   * (4) call |drop(this)| of appropriate |EqTable| whenever before
   *     its new associated value change globaly.
   */
  public interface ExtEquiv {

    /**
     * It *must* give the number supplied to last call of |setEqLevel| or |0| if the |setEqLevel|
     * was not called yet.
     *
     * @return the previously set level or |0|.
     */
    int getEqLevel();

    /**
     * It *must* save the parameter to be available for a call of |getEqLevel|.
     *
     * @param lev the level to be saved.
     */
    void setEqLevel(int lev);

    /**
     * Gives the Object version of its value for saving if necessary.
     *
     * @return the Object representation of its value.
     */
    Object getEqValue();

    /**
     * Sets the previously saved value of this external equivalent.
     *
     * @param val the Object representation of value to be set.
     */
    void restoreEqValue(Object val);

    void retainEqValue();
  }

  /**
   * Gets the equivalent for numeric key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @return equivalent object.
   */
  public abstract Object get(NumKind kind, int key);

  /**
   * Puts the equivalent for numeric key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @param val equivalent object.
   */
  public abstract void put(NumKind kind, int key, Serializable val);

  public abstract Object nastyReplace(NumKind kind, int key, Serializable val);

  /**
   * Puts the equivalent for numeric key of specific kind globally.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @param val equivalent object.
   */
  public abstract void gput(NumKind kind, int key, Serializable val);

  /**
   * Gets the equivalent for object key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @return equivalent object.
   */
  public abstract Object get(ObjKind kind, Serializable key);

  /**
   * Puts the equivalent for object key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @param val equivalent object.
   */
  public abstract void put(ObjKind kind, Serializable key, Serializable val);

  public abstract Object nastyReplace(ObjKind kind, Serializable key, Serializable val);

  /**
   * Puts the equivalent for object key of specific kind globally.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @param val equivalent object.
   */
  public abstract void gput(ObjKind kind, Serializable key, Serializable val);

  /**
   * Saves the value of external equivalent if necessary.
   *
   * @param ext the external equivalent.
   */
  public abstract void save(ExtEquiv ext);

  /**
   * Throws away all saved values of an external equivalent.
   *
   * @param ext the external equivalent.
   */
  public static void drop(ExtEquiv ext) {
    ext.setEqLevel(0);
  }

  public final void beforeSetting(ExtEquiv ext, boolean glob) {
    if (glob) drop(ext);
    else save(ext);
  }
}
