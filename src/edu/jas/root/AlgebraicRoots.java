/*
 * $Id$
 */

package edu.jas.root;


import java.util.List;
import java.io.Serializable;

import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the real and complex algebraic roots of a univariate polynomial.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class AlgebraicRoots<C extends GcdRingElem<C> & Rational > implements Serializable { 


    /**
     * univariate polynomial.
     */
    public final GenPolynomial<C> p;


    /**
     * real algebraic roots.
     */
    public final List<RealAlgebraicNumber<C>> real;


    /**
     * complex algebraic roots.
     */
    public final List<ComplexAlgebraicNumber<C>> complex;


    /**
     * Constructor.
     * @param p univariate polynomial
     * @param r list of real algebraic roots
     * @param c list of complex algebraic roots
     */
    public AlgebraicRoots(GenPolynomial<C> p, 
                          List<RealAlgebraicNumber<C>> r, List<ComplexAlgebraicNumber<C>> c) {
        this.p = p;
        this.real = r;
        this.complex = c;
    }


    /**
     * String representation of AlgebraicRoots.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + p + ", real=" + real + ", complex=" + complex + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Interval.
     */
    public String toScript() {
        // Python case
        return "[" + p.toScript() + ", real=" + real + ", complex=" + complex + "]";
        // todo real and complex toScript
    }


    /**
     * Copy this.
     * @return a copy of this.
     */
    public AlgebraicRoots<C> copy() {
        return new AlgebraicRoots<C>(p, real, complex);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof AlgebraicRoots)) {
            return false;
        }
        AlgebraicRoots<C> a = null;
        try {
            a = (AlgebraicRoots<C>) b;
        } catch (ClassCastException e) {
            return false;
        }
        return p.equals(a.p) && real.equals(a.real) && complex.equals(a.complex);
    }


    /**
     * Hash code for this AlgebraicRoots.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (161 * p.hashCode() + 37 ) * real.hashCode() + complex.hashCode();
    }

}
