/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SoftReferenceHashTable.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.imagehelpers;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

public class SoftReferenceHashTable<K,V> {
    Hashtable<K, SoftReference<V>> mTable = new Hashtable<K, SoftReference<V>>();
    
    public V put(K key, V value) {
        SoftReference<V> old = mTable.put(key, new SoftReference<V>(value));
        if (old == null)
            return null;
        return old.get();
    }
    
    public V get(K key) {
        SoftReference<V> val = mTable.get(key);
        if (val == null)
            return null;
        V ret = val.get();
        if (ret == null)
            mTable.remove(key);
        return ret;
    }
    
    public V remove(K k) {
        SoftReference<V> v = mTable.remove(k);
        if (v == null)
            return null;
        return v.get();
    }
}
