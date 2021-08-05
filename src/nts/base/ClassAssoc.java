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
// Filename: nts/base/ClassAssoc.java
// $Id: ClassAssoc.java,v 1.1.1.1 2001/05/16 22:02:29 ksk Exp $
package nts.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Associative table which associates objects to combinations of classes and objects. If an object
 * |o| is associated to combination of class |c| and object (key) |k| then |o| is associated also to
 * all combinations of any subclass of |c| and key |k| unless overriden by explicit association for
 * the subclass. In other words it observes the class hierarchy and works like virtual methods.
 *
 * <p>|ClassAssoc| is used when we need polymorphism but the virtual methods are not appropriate
 * because the feature is out of class responsibility, e.g. something which the class is not (and
 * should not be) aware of. Such functionality is natively supported in other languages (CLOS,
 * Dylan).
 *
 * <p>To get the polymorphic association right it is necessary first to put all the associations for
 * a given class and then to register the class. This must be done in descendent order (subclasses
 * after superclasses) and all classes in the class subtree must be registered even if they do not
 * have any explicit associations. However associations for other classes my precede the class
 * registration so it is completely OK to make all the associations first (in any order) and then to
 * to make all the registrations (in descendant order). This inconvenience might be removed in
 * future implementations (and the interface simplified).
 *
 * @author Karel Skoupy
 * @version ${VERSION}
 * @since NTS1.0
 * @see HashMap
 */
public class ClassAssoc implements Serializable {

  /** Root of the class subtree used in associations */
  private Class root;

  /** Internal representation of the associative table */
  private HashMap matrix;

  /** list of */
  private Vector list = new Vector(10);

  /**
   * Constructs a new, empty |ClassAssoc| with given |HashMap| parameters.
   *
   * @param root root of the class subtree used in associations
   * @param initialSize initial size of the map
   * @param loadFactor load factor of the map
   * @see HashMap
   */
  public ClassAssoc(Class root, int initialSize, float loadFactor) {
    this.root = root;
    matrix = new HashMap(initialSize, loadFactor);
  }

  /**
   * Constructs a new, empty |ClassAssoc| with default |HashMap| parameters.
   *
   * @param root root of the class subtree used in associations
   * @see HashMap
   */
  public ClassAssoc(Class root) {
    this(root, 1009, 0.5F);
  }

  /**
   * Gets the root of the class subtree used for associations.
   *
   * @return root of the class subhierarchy
   */
  public Class getRoot() {
    return root;
  }

  /**
   * Test whether a given class is registered.
   *
   * @param cls class to be tested (if registered)
   * @return |true| if |cls| is registered, |false| otherwise
   */
  public boolean recorded(Class cls) {
    return list.contains(cls);
  }

  /**
   * Checks whether the class is descendant of the root and is not already registered.
   *
   * @param cls checked class
   * @throws RuntimeException if |cls| is not a descendant of the root <br>
   *     or if it is already registered
   */
  private void check(Class cls) {
    if (!root.isAssignableFrom(cls)) throw new RuntimeException(cls + "is not a " + root);
    if (list.contains(cls)) throw new RuntimeException(cls + " already registered");
  }

  /**
   * Registeres a class.
   *
   * @param cls class to be registered
   * @throws RuntimeException if |cls| is not a descendant of the root <br>
   *     or if it is already registered <br>
   *     or if its superclass is not yet registered
   */
  public void record(Class cls) { // XXX do that automatically
    check(cls);
    if (!list.isEmpty()) {
      Class sup = cls.getSuperclass();
      if (!list.contains(sup))
        throw new RuntimeException("Super class (" + sup + ")  of " + cls + " not registered");
      Set keySet = matrix.keySet();
      PairKey[] inheritedKeys = new PairKey[keySet.size()];
      Object[] inheritedVals = new Object[keySet.size()];
      int index = 0;
      Iterator keys = keySet.iterator();
      while (keys.hasNext()) {
        PairKey curr = (PairKey) keys.next();
        if (curr.first.equals(sup)) {
          PairKey key = new PairKey(cls, curr.second);
          if (!matrix.containsKey(key)) {
            inheritedKeys[index] = key;
            inheritedVals[index] = matrix.get(curr);
            index++;
          }
        }
      }
      while (--index >= 0) matrix.put(inheritedKeys[index], inheritedVals[index]);
    }
    list.addElement(cls);
  }

  /**
   * Makes the association of an object to a combination of class and key
   *
   * @param cls class to be associated to
   * @param key key to be associated to
   * @param val value to be associated for combination of |cls| and |key|
   * @throws RuntimeException if |cls| is not a descendant of the root <br>
   *     or if |cls| is already registered
   */
  public final void put(Class cls, Object key, Object val) {
    check(cls);
    matrix.put(new PairKey(cls, key), val);
  }

  /**
   * Gets the object associated to given combination of class and key
   *
   * @param cls class to be associated to
   * @param key key to be associated to
   * @return the associated object or |null| if there is no object associated. Works properly for
   *     subclass of |cls| only if the subclass is registered.
   */
  public final Object get(Class cls, Object key) {
    return matrix.get(new PairKey(cls, key));
  }
}
