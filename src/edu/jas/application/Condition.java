/*
 * $Id$
 */

package edu.jas.application;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ColorPolynomial;
import edu.jas.poly.ExpVector;


/**
 * Condition as a pair of zero and non-zero polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class Condition<C extends GcdRingElem<C> > 
             implements Serializable /*, Comparable<Condition>*/ {


    /**
     * Colors.
     */
    public static enum Color { GREEN, RED, WHITE };




    /**
     * Data structure for condition zero.
     */
    public final Ideal<C> zero;


    /**
     * Data structure for condition non-zero.
     */
    public final List<GenPolynomial<C>> nonZero;




    /**
     * Condition constructor.
     * @param ring polynomial ring factory for coefficients.
     */
    public Condition(GenPolynomialRing<C> ring) {
        this( new Ideal<C>( ring, new ArrayList<GenPolynomial<C>>() ) );
        if ( ring == null ) {
           throw new RuntimeException("only for non null rings");
        }
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     * @param nz a list of non-zero polynomials.
     */
    public Condition(Ideal<C> z, List<GenPolynomial<C>> nz) {
        if ( z == null || nz == null ) {
            throw new RuntimeException("only for non null condition parts");
        }
        zero = z;
        nonZero = nz;
    }


    /**
     * Condition constructor.
     * @param z an ideal of zero polynomials.
     */
    public Condition(Ideal<C> z) {
        this(z, new ArrayList<GenPolynomial<C>>());
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "Condition[ 0 == " + zero.list.list.toString() 
                      + ", 0 != " + nonZero + " ]";
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        Condition<C> c = null;
        try {
            c = (Condition<C>) ob;
        } catch (ClassCastException e) {
            return false;
        }
     // if ( ! zero.getList().equals( c.zero.getList() ) ) {
        if ( ! zero.equals( c.zero ) ) {
           return false;
        }
        for ( GenPolynomial<C> p : nonZero ) {
            if ( ! c.nonZero.contains( p ) ) {
               return false;
            }
        }
        List<GenPolynomial<C>> cnz = c.nonZero;
        for ( GenPolynomial<C> p : c.nonZero ) {
            if ( ! nonZero.contains( p ) ) {
               return false;
            }
        }
        return true;
    }


    /**
     * Is empty condition.
     * @return true if this is the empty condition, else false.
     */
    public boolean isEmpty() {
        return ( zero.isZERO() && nonZero.size() == 0 );
    }


    /**
     * Extend with condition zero polynomial.
     * @param z a polynomial to be treated as zero.
     */
    public Condition<C> extendZero(GenPolynomial<C> z) {
        //List<GenPolynomial<C>> list = new ArrayList<GenPolynomial<C>>( zero.getList() );
        //list.add( z.monic() );
        //Ideal<C> id = new Ideal<C>( zero.getRing(), list );
        //return new Condition<C>( id, nonZero );
       z = zero.engine.squarefreePart( z ); // leads to errors in nonZero? -no more
       Ideal<C> idz = zero.sum( z );
       List<GenPolynomial<C>> list = idz.normalform( nonZero );
       if ( list.size() != nonZero.size() ) { // contradiction
          System.out.println("zero    = " + zero.getList());
          System.out.println("z       = " + z);
          System.out.println("idz     = " + idz.getList());
          System.out.println("list    = " + list);
          System.out.println("nonZero = " + nonZero);
          return null;
       }
       List<GenPolynomial<C>> L = new ArrayList<GenPolynomial<C>>( list.size() );
       for ( GenPolynomial<C> h : list ) {
           if ( h != null && !h.isZERO() ) {
              GenPolynomial<C> r = h.monic();
              L.add( r );
           }
       }
       return new Condition<C>( idz, L );
    }


    /**
     * Extend with condition non-zero polynomial.
     * @param nz a polynomial to be treated as non-zero.
     */
    public Condition<C> extendNonZero(GenPolynomial<C> nz) {
        //nz = nz.monic();
        /*
        ll.add( nz );
        Ideal<C> p = new Ideal<C>( zero.getRing(), ll );
        Ideal<C> q = p.quotient( zero );
        System.out.println("Quotient q = " + q);
        if ( q.getList().size() == 1 ) {
           list.add( q.getList().get(0) );
        } else {
           System.out.println("size() != 1 ");
           list.add( nz );
        }
        */
        /*
        List<GenPolynomial<C>> ll = new ArrayList<GenPolynomial<C>>( zero.engine.squarefreeFactors(nz).values() );
        System.out.println("squarefree..... of " + nz + ":");
        System.out.println("squarefreeFactors = " + ll);
        GenPolynomial<C> n = zero.engine.squarefreePart(nz);
        System.out.println("squarefreePart = " + n);
        */
        nz = zero.normalform(nz).monic();
        if ( nz == null || nz.isZERO() ) {
           return this;
        } 
        GenPolynomial<C> n = zero.engine.squarefreePart( nz );
        if ( !n.equals(nz) ) {
           System.out.println("squarefree... of " + nz + ":");
           System.out.println("squarefreePart = " + n);
           GenPolynomial<C> q = nz.divide(n);
           List<GenPolynomial<C>> list = addNonZero( n );
           Condition<C> nc = new Condition<C>( zero, list );
           list = nc.addNonZero( q );
           return new Condition<C>( zero, list );
        }
        //List<GenPolynomial<C>> list = new ArrayList<GenPolynomial<C>>( nonZero );
        // list.add( nz );
        List<GenPolynomial<C>> list = addNonZero( nz );
        return new Condition<C>( zero, list );
    }


    /**
     * Determine color of polynomial.
     * @param c polynomial to be colored.
     */
    public Color color(GenPolynomial<C> c) {
        c = zero.normalform(c).monic();
        if ( zero.contains( c ) ) { 
           //System.out.println("c in id = " + c);
           return Color.GREEN;
        } 
        if ( c.isConstant() ) {
           //System.out.println("c constant " + c);
           return Color.RED;
        }
        // if ( nonZero.contains( c ) ) {
        if ( isNonZero( c ) ) {
           System.out.println("c in nonzero " + c);
           return Color.RED;
        }
        return Color.WHITE;
    }


    /**
     * Test if a polynomial is contained in nonZero.
     * NonZero is treated as multiplicative set.
     * @param c polynomial searched in nonZero.
     */
    public boolean isNonZero(GenPolynomial<C> c) {
        if ( c == null || c.isZERO() ) { // do not look into zero
           return false;
        }
        if ( nonZero == null || nonZero.size() == 0 ) {
           return false;
        }
        for ( GenPolynomial<C> n : nonZero ) {
            GenPolynomial<C> q;
            GenPolynomial<C> r;
            do {
                GenPolynomial<C>[] qr = c.divideAndRemainder( n );
                q = qr[0];
                r = qr[1];
                if ( r != null && ! r.isZERO() ) {
                    continue;
                }
                if ( q != null && q.isConstant() ) {
                    return true;
                }
                c = q;
            } while ( r.isZERO() && !c.isConstant() );
        }
        return false;
    }


    /**
     * A polynomial to nonZero.
     * NonZero is treated as multiplicative set.
     * @param c polynomial to bee added to nonZero.
     */
    public List<GenPolynomial<C>> addNonZero(GenPolynomial<C> c) {
        if ( c == null || c.isZERO() ) { // do not look into zero
           return nonZero;
        }
        List<GenPolynomial<C>> list;
        if ( nonZero == null ) { // cannot happen
           list = new ArrayList<GenPolynomial<C>>();
           list.add( c );
           return list;
        }
        list = new ArrayList<GenPolynomial<C>>( nonZero );
        if ( nonZero.size() == 0 ) {
           System.out.println("added to empty nonzero = " + c);
           list.add( c );
           return list;
        }
        for ( GenPolynomial<C> n : nonZero ) {
            GenPolynomial<C> q;
            GenPolynomial<C> r;
            do {
                GenPolynomial<C>[] qr = c.divideAndRemainder( n );
                q = qr[0];
                r = qr[1];
                if ( r != null && ! r.isZERO() ) {
                    continue;
                }
                if ( q != null && q.isConstant() ) {
                    return list;
                }
                c = q;
            } while ( r.isZERO() && !c.isConstant() );
        }
        System.out.println("added to nonzero = " + c);
        list.add( c );
        return list;
    }


    /**
     * Determine polynomial.
     * If this condition does not determine the polynomial, then 
     * a run-time exception is thrown.
     * @param A polynomial.
     * @return new determined colored polynomial.
     */
    public ColorPolynomial<C> determine( GenPolynomial<GenPolynomial<C>> A ) {  
        ColorPolynomial<C> cp = null;
        if ( A == null ) {
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> zero = A.ring.getZERO();
        GenPolynomial<GenPolynomial<C>> green = zero;
        GenPolynomial<GenPolynomial<C>> red = zero;
        GenPolynomial<GenPolynomial<C>> white = zero;
        if ( A.isZERO() ) {
           cp = new ColorPolynomial<C>(green,red,white); 
           return cp;
        }
        GenPolynomial<GenPolynomial<C>> Ap = A;
        GenPolynomial<GenPolynomial<C>> Bp;
        while( !Ap.isZERO() ) {
            Map.Entry<ExpVector,GenPolynomial<C>> m = Ap.leadingMonomial();
            ExpVector e = m.getKey();
            GenPolynomial<C> c = m.getValue();
            Bp = Ap.reductum();
            //System.out.println( "color(" + c + ") = " + color(c) );
            switch ( color( c ) ) {
            case GREEN:
               green = green.sum(c,e);
               Ap = Bp;
               continue;
            case RED:
               red = red.sum(c,e);
               white = Bp;
               return new ColorPolynomial<C>(green,red,white); 
               // since break is not possible
            default: 
               System.out.println("error cond = " + this);
               throw new RuntimeException("error, c is white = " + c);
            }
        }
        cp = new ColorPolynomial<C>(green,red,white); 
        //System.out.println("determined = " + cp);
        return cp;
    }


    /**
     * Determine list of polynomials.
     * If this condition does not determine all polynomials, then 
     * a run-time exception is thrown.
     * The returned list does not contain zero polynomials with all green terms.
     * @param L list of polynomial.
     * @return new determined list of colored polynomials.
     */
    public List<ColorPolynomial<C>> determine( List<GenPolynomial<GenPolynomial<C>>> L ) {
        List<ColorPolynomial<C>> cl = null;
        if ( L == null ) {
           return cl;
        }
        cl = new ArrayList<ColorPolynomial<C>>( L.size() );
        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
            ColorPolynomial<C> c = determine( A );
            if ( c != null && ! c.isZERO() ) {
               cl.add( c );
            }
        }
        return cl;
    }


}
