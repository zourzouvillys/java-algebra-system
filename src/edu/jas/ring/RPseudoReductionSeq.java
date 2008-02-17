/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RegularRingElem;


/**
 * Polynomial regular ring pseudo reduction sequential use algorithm.
 * Implements fraction free normalform algorithm.
 * @author Heinz Kredel
 */

public class RPseudoReductionSeq<C extends RegularRingElem<C>>
             extends RReductionSeq<C> 
             /*implements RReduction<C>*/ {

    private static final Logger logger = Logger.getLogger(RPseudoReductionSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public RPseudoReductionSeq() {
    }


    /**
     * Normalform using r-reduction.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return r-nf(Ap) with respect to Pp.
     */
    @Override
    @SuppressWarnings("unchecked") 
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[])new GenPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector,C> m;
        ExpVector[] htl = new ExpVector[ l ];
        C[] lbc = (C[]) new RegularRingElem[ l ]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[])new GenPolynomial[ l ];
        int i;
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            if ( P[i] == null ) {
               continue;
            }
            p[i] = P[i].abs();
            m = p[i].leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
               j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a, b;
        C r = null;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        GenPolynomial<C> Rp, Sp;
        while ( S.length() > 0 ) { 
              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              //System.out.println("--a = " + a);
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) {
                     C c = lbc[i];
                     r = a.idempotent().multiply( c.idempotent() );
                     mt = ! r.isZERO(); // && mt
                     if ( mt ) {
                        f = ExpVector.EVDIF( e, htl[i] );
                        if ( a.remainder(c).isZERO() ) {   //c.isUnit() ) {
                           a = a.divide( c );
                           if ( a.isZERO() ) {
                              throw new RuntimeException("a.isZERO()");
                           }
                        } else {
                           c = c.fillOne();
                           S = S.multiply( c );
                           R = R.multiply( c );
                        }
                        Q = p[i].multiply( a, f );
                        S = S.subtract( Q );

                        f = S.leadingExpVector();
                        if ( !e.equals(f) ) {
                           a = Ap.ring.coFac.getZERO();
                           break;
                        } 
                        a = S.leadingBaseCoefficient();
                     }
                  }
              }
              if ( !a.isZERO() ) { //! mt ) { 
                 //logger.debug("irred");
                 R = R.sum( a, e );
                 S = S.reductum(); 
              }
        }
        return R.abs(); // not monic if not boolean closed
    }


    /**
     * Normalform using r-reduction.
     * @typeparam C coefficient type.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @param mf multiplication factor for Ap, is modified.
     * @return r-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked") 
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap,
                                       List<C> mf ) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[])new GenPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector,C> m;
        ExpVector[] htl = new ExpVector[ l ];
        C[] lbc = (C[]) new RegularRingElem[ l ]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[])new GenPolynomial[ l ];
        int i;
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            if ( P[i] == null ) {
               continue;
            }
            p[i] = P[i].abs();
            m = p[i].leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
               j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a, b;
        C r = null;
        boolean mt = false;
        C mfac = mf.get(0);
        if ( mfac == null ) {
           mfac = Ap.ring.getONECoefficient();
        }
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        GenPolynomial<C> Rp, Sp;
        while ( S.length() > 0 ) { 
              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              //System.out.println("--a = " + a);
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) {
                     C c = lbc[i];
                     r = a.idempotent().multiply( c.idempotent() );
                     mt = ! r.isZERO(); // && mt
                     if ( mt ) {
                        f = ExpVector.EVDIF( e, htl[i] );
                        if ( a.remainder(c).isZERO() ) {   //c.isUnit() ) {
                           a = a.divide( c );
                           if ( a.isZERO() ) {
                              throw new RuntimeException("a.isZERO()");
                           }
                        } else {
                           c = c.fillOne();
                           S = S.multiply( c );
                           R = R.multiply( c );
                           mfac = mfac.multiply( c );
                        }
                        Q = p[i].multiply( a, f );
                        S = S.subtract( Q );

                        f = S.leadingExpVector();
                        if ( !e.equals(f) ) {
                           a = Ap.ring.coFac.getZERO();
                           break;
                        } 
                        a = S.leadingBaseCoefficient();
                     }
                  }
              }
              if ( !a.isZERO() ) { //! mt ) { 
                 //logger.debug("irred");
                 R = R.sum( a, e );
                 S = S.reductum(); 
              }
        }
        mf.set(0,mfac);
        return R; //.abs(); // not monic if not boolean closed
    }


    /**
     * Normalform with recording.
     * @typeparam C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     * @deprecated multiplication factor will be lost.
     */
    @Override
    @SuppressWarnings("unchecked") 
    public GenPolynomial<C> 
        normalform(List<GenPolynomial<C>> row,
                   List<GenPolynomial<C>> Pp, 
                   GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
            return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }
        //throw new RuntimeException("not jet implemented");
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[])new GenPolynomial[l];
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector,C> m;
        ExpVector[] htl = new ExpVector[ l ];
        C[] lbc = (C[]) new RegularRingElem[ l ]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[])new GenPolynomial[ l ];
        int i;
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
               j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a, b;
        C r = null;
        boolean mt = false;
        GenPolynomial<C> fac = null;
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt = ExpVector.EVMT( e, htl[i] );
                  if ( mt ) {
                     C c = lbc[i];
                     r = a.idempotent().multiply( c.idempotent() );
                     //System.out.println("r = " + r);
                     mt = ! r.isZERO(); // && mt
                     if ( mt ) {
                        b = a.remainder( c );
                        if ( b.isZERO() ) {
                           a = a.divide( c );
                           if ( a.isZERO() ) {
                              throw new RuntimeException("a.isZERO()");
                           }
                        } else {
                           c = c.fillOne();
                           S = S.multiply( c );
                           R = R.multiply( c );
                        }
                        f = ExpVector.EVDIF( e, htl[i] );
                        //logger.info("red div = " + f);
                        Q = p[i].multiply( a, f );
                        S = S.subtract( Q ); // not ok with reductum

                        fac = row.get(i);
                        if ( fac == null ) {
                            fac = zero.sum( a, f );
                        } else {
                            fac = fac.sum( a, f );
                        }
                        row.set(i,fac);

                        f = S.leadingExpVector();
                        if ( !e.equals(f) ) {
                           a = Ap.ring.coFac.getZERO();
                           break;
                        } 
                        a = S.leadingBaseCoefficient();
                     }
                  }
              }
              if ( !a.isZERO() ) { //! mt ) { 
                 //logger.debug("irred");
                 R = R.sum( a, e );
                 S = S.reductum(); 
              }
        }
        return R; //.abs(); // not monic if not boolean closed
    }


    /*
     * -------- boolean closure stuff -----------------------------------------
     * -------- is all in superclass
     */

}
