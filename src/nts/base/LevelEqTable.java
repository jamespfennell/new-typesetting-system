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
// Filename: nts/base/LevelEqTable.java
// $Id: LevelEqTable.java,v 1.1.1.1 2000/02/01 00:22:32 ksk Exp $
package nts.base;

import java.io.Serializable;
import java.util.HashMap;

/**
 * |LevelEqTable| maintains equivalents for numbers and |Object|s. The equivalent objects can be
 * stored by |put| and retrieved by |get| method. The first parameter of these methods determine the
 * kind of equivalence.
 *
 * <p>|LevelEqTable| also maintains levels. When the level is pushed by method |pushLevel| the new
 * equivalents override the old ones for the same key. After invoking |popLevel| the equivalents
 * from top level are thrown away and the old ones are restored. Methods |gput| assigns equivalents
 * globally.
 *
 * <p>Additionally it supports so-called external equivalents. Those are objects which have some
 * associated value which should be saved and restored when the level of |LevelEqTable| pushes and
 * pops like the values associated to the keys. Such objects must implement interface |ExtEquiv| and
 * are maintained by methods |save| and |drop|.
 */
/* For TeX version of table of equivalents see TeXtp[220]. */
public class LevelEqTable extends EqTable implements Serializable {

  /** abstract interface for internal representation of keys */
  private interface TabKey extends Serializable {
    void restored(Object oldVal);

    void retained();
  }

  /** internal representation of numerical keys */
  private static final class NumKey implements TabKey {

    private NumKind kind;

    /** The numerical key */
    private int num;

    /**
     * Creates |NumKey| for given kind and number.
     *
     * @param k the kind of equivalence.
     * @param n the numerical key.
     */
    public NumKey(NumKind k, int n) {
      kind = k;
      num = n;
    }

    public void restored(Object oldVal) {
      kind.restored(num, oldVal);
    }

    public void retained() {
      kind.retained(num);
    }

    /**
     * Hash code of the key representation (used for table lookup).
     *
     * @return the hash code.
     */
    public int hashCode() {
      return kind.hashCode() * 383 + num;
    }

    /**
     * Compares the internal key representation to another object. The result is |true| if and only
     * if the object is the same key representation of the same kind.
     *
     * @param o the object to compare this key against.
     * @return |true| if the argument is equal, |false| otherwise.
     */
    public boolean equals(Object o) {
      if (o != null && o.getClass() == this.getClass()) {
        NumKey k = (NumKey) o;
        return (k.kind.equals(kind) && k.num == num);
      }
      return false;
    }
  }

  /** internal representation of |Object| keys */
  private static final class ObjKey implements TabKey {

    private ObjKind kind;

    /** The object key */
    private Object obj;

    /**
     * Creates |ObjKey| for given kind and object.
     *
     * @param k the kind of equivalence.
     * @param n the object key.
     */
    public ObjKey(ObjKind k, Object o) {
      kind = k;
      obj = o;
    }

    public void restored(Object oldVal) {
      kind.restored(obj, oldVal);
    }

    public void retained() {
      kind.retained(obj);
    }

    /**
     * Hash code of the key representation (used for table lookup).
     *
     * @return the hash code.
     */
    public int hashCode() {
      return kind.hashCode() * 383 + obj.hashCode();
    }

    /**
     * Compares the internal key representation to another object. The result is |true| if and only
     * if the object is the same key representation of the same kind.
     *
     * @param o the object to compare this key against.
     * @return |true| if the argument is equal, |false| otherwise.
     */
    public boolean equals(Object o) {
      if (o != null && o.getClass() == this.getClass()) {
        ObjKey k = (ObjKey) o;
        return (k.kind.equals(kind) && k.obj.equals(obj));
      }
      return false;
    }
  }

  /** internal representation of equivalent */
  private static final class TabEntry implements Serializable {

    /** The equivalent value */
    Object val;

    /** The level on which it was pushed */
    int lev;

    /**
     * Creates |TabEntry| for given equivalent and level.
     *
     * @param v the equivalent object.
     * @param l the level on which it is pushed.
     */
    TabEntry(Object v, int l) {
      val = v;
      lev = l;
    }
  }

  /** base class for |IntSavEntry| and |ExtSavEntry| */
  private abstract static class SavEntry implements Serializable {

    /** The level on which it was saved */
    int sav;

    /* The next item in the save stack */
    SavEntry next;

    /**
     * Creates |SavEntry|.
     *
     * @param s the save level.
     * @param x the entry below in the stack.
     */
    SavEntry(int s, SavEntry x) {
      sav = s;
      next = x;
    }

    abstract void restore(HashMap<TabKey, TabEntry> tab);
  }

  /** Internal representation of saved equivalent */
  private static final class IntSavEntry extends SavEntry {

    /** The represenation of the key */
    TabKey key;

    /** The saved equivalent entry */
    TabEntry ent;

    /**
     * Creates |IntSavEntry|.
     *
     * @param k the key.
     * @param e the equivalent entry.
     * @param s the save level.
     * @param x the entry below in the stack.
     */
    IntSavEntry(TabKey k, TabEntry e, int s, SavEntry x) {
      super(s, x);
      key = k;
      ent = e;
    }

    void restore(HashMap<TabKey, TabEntry> tab) {
      TabEntry e = tab.get(key);
      if (ent == null) {
        /* originaly the value was not set */

        /* the value was set in popped level */
        if (e != null && e.lev > 0) {
          tab.remove(key); /* throw it away */
          key.restored(e.val);
        } else key.retained();

        /* value was overriden in popped level */
      } else if (e == null || ent.lev < e.lev) {
        tab.put(key, ent); /* restore it */
        key.restored((e != null) ? e.val : null);
      } else key.retained();
    }
  }

  /** internal representation of saved externals */
  private static final class ExtSavEntry extends SavEntry {

    /** The value of saved equivalent */
    Object val;

    /** The level on which it was defined */
    int lev;

    /** The the external equivalent */
    ExtEquiv ext;

    /**
     * Creates |ExtSavEntry|.
     *
     * @param v the value.
     * @param l the level of definition.
     * @param e the external equivalent.
     * @param s the save level.
     * @param x the entry below in the stack.
     */
    ExtSavEntry(Object v, int l, ExtEquiv e, int s, SavEntry x) {
      super(s, x);
      val = v;
      lev = l;
      ext = e;
    }

    void restore(HashMap tab) {
      if (lev < ext.getEqLevel()) {
        ext.restoreEqValue(val);
        ext.setEqLevel(lev);
      } else ext.retainEqValue();
    }
  }

  /** table of equivalents for current level */
  private HashMap<TabKey, TabEntry> table;

  /** stack of saved equivalents for pushed levels */
  private SavEntry saves = null;

  /** current level */
  private int level = 0;

  /**
   * Creates new |LevelEqTable| with initial size of hash table specified.
   *
   * @param size initial hash table size
   */
  public LevelEqTable(int size) {
    table = new HashMap<TabKey, TabEntry>(size);
  }

  /** Creates new |LevelEqTable| with default sizes of hash table. */
  public LevelEqTable() {
    table = new HashMap<TabKey, TabEntry>();
  }

  /**
   * Gives the current level of equivalents.
   *
   * @return current level
   */
  public final int getLevel() {
    return level;
  }

  /**
   * Pushes new level of equivalents.
   *
   * @return current level
   */
  public int pushLevel() {
    return ++level;
  }

  /**
   * Pops one level of equivalents and restores the saved equivalents from this level.
   *
   * @return current level
   */
  public int popLevel() {
    if (level == 0) return 0;

    /* restoring saved equivalents */
    SavEntry s;
    while ((s = saves) != null && s.sav == level) {
      s.restore(table);
      saves = s.next;
    }

    /* pop the level */
    return --level;
  }

  /**
   * Gets equivalent object for internal key.
   *
   * @param k internal key.
   * @return equivalent object.
   */
  private Object get(TabKey k) {
    TabEntry e = table.get(k);
    return (e != null) ? e.val : null;
  }

  /**
   * Puts the equivalent object for internal key.
   *
   * @param k internal key.
   * @param v equivalent object.
   */
  private void put(TabKey k, Serializable v) {
    if (level > 0) {
      /* change is not global */
      TabEntry e = table.get(k);

      /* save in each level only once */
      if (e == null || e.lev != level) saves = new IntSavEntry(k, e, level, saves);
    }
    table.put(k, new TabEntry(v, level));
  }

  private Object nastyReplace(TabKey k, Serializable v) {
    Object old = null;
    TabEntry e = (TabEntry) table.get(k);
    if (e != null) {
      old = e.val;
      e.val = v;
    } else {
      if (level > 0) saves = new IntSavEntry(k, e, level, saves);
      table.put(k, new TabEntry(v, level));
    }
    return old;
  }

  /**
   * Puts the equivalent object for internal key globally.
   *
   * @param k internal key.
   * @param v equivalent object.
   */
  private void gput(TabKey k, Serializable v) {
    table.put(k, new TabEntry(v, 0));
  }

  /**
   * Gets the equivalent for numeric key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @return equivalent object.
   */
  public final Object get(NumKind kind, int key) {
    return get(new NumKey(kind, key));
  }

  /**
   * Puts the equivalent for numeric key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @param val equivalent object.
   */
  public final void put(NumKind kind, int key, Serializable val) {
    put(new NumKey(kind, key), val);
  }

  public final Object nastyReplace(NumKind kind, int key, Serializable val) {
    return nastyReplace(new NumKey(kind, key), val);
  }

  /**
   * Puts the equivalent for numeric key of specific kind globally.
   *
   * @param kind the kind of equivalnce.
   * @param key numeric key.
   * @param val equivalent object.
   */
  public final void gput(NumKind kind, int key, Serializable val) {
    gput(new NumKey(kind, key), val);
  }

  /**
   * Gets the equivalent for object key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @return equivalent object.
   */
  public final Object get(ObjKind kind, Serializable key) {
    return get(new ObjKey(kind, key));
  }

  /**
   * Puts the equivalent for object key of specific kind.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @param val equivalent object.
   */
  public final void put(ObjKind kind, Serializable key, Serializable val) {
    put(new ObjKey(kind, key), val);
  }

  public final Object nastyReplace(ObjKind kind, Serializable key, Serializable val) {
    return nastyReplace(new ObjKey(kind, key), val);
  }

  /**
   * Puts the equivalent for object key of specific kind globally.
   *
   * @param kind the kind of equivalnce.
   * @param key object key.
   * @param val equivalent object.
   */
  public final void gput(ObjKind kind, Serializable key, Serializable val) {
    gput(new ObjKey(kind, key), val);
  }

  /**
   * Saves the value of external equivalent if necessary.
   *
   * @param ext the external equivalent.
   */
  public void save(ExtEquiv ext) {
    if (level > 0) {
      /* change is not global */
      int l = ext.getEqLevel();
      if (l != level) saves = new ExtSavEntry(ext.getEqValue(), l, ext, level, saves);
    }
    ext.setEqLevel(level);
  }
}
