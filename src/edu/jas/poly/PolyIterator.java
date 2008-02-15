
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;


/**
 * Iterator over monomials of a polynomial. 
 * Adaptor for val.entrySet().iterator().
 * @author Heinz Kredel
 */

public class PolyIterator<C extends RingElem<C> > 
             implements Iterator< Monomial<C> > {


    /** 
     * Internal iterator over polynomial map.
     */
    protected final Iterator< Map.Entry<ExpVector,C> > ms;


    /** 
     * Constructor of polynomial iterator.
     * @param m SortetMap of a polynomial.
     */
    public PolyIterator( SortedMap<ExpVector,C> m ) {
        ms = m.entrySet().iterator();
    }


    /** 
     * Test for availability of a next monomial.
     * @return true if the iteration has more monomials, else false.
     */
    @Override
    public boolean hasNext() {
        return ms.hasNext();
    }


    /** 
     * Get next monomial element.
     * @return next monomial.
     */
    @Override
    public Monomial<C> next() {
        return new Monomial<C>( ms.next() );
    }


    /** 
     * Remove next monomial from underlying set if allowed.
     */
    @Override
    public void remove() {
        ms.remove();
    }

}
