/*
 * $Id$
 */

package edu.jas.ring;

import java.io.Serializable;

    /**
     * Subclass to hold pairs of polynomial indices
     * What is this class good for?
     */

    class MiniPair implements Serializable {
	public final Integer i;
	public final Integer j;

	MiniPair(int i, int j) {
            this.i = new Integer(i); 
            this.j = new Integer(j);
	}

	public String toString() {
	    return "miniPair(" + i + "," + j + ")";
	}

    }
