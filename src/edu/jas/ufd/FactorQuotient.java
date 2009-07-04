/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.Power;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.Monomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Rational function coefficients factorization algorithms.
 * This class implements factorization methods for polynomials over rational functions,
 * that is, with coefficients from class <code>application.Quotient</code>.
 * @author Heinz Kredel
 */

public class FactorQuotient<C extends GcdRingElem<C>> extends FactorAbstract<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(FactorQuotient.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for normal coefficients.
     */
    protected final FactorAbstract<C> nengine;


    /**
     * No argument constructor. 
     */
    protected FactorQuotient() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor. 
     * @param fac coefficient quotient ring factory.
     */
    public FactorQuotient(QuotientRing<C> fac) {
        super( fac );
        nengine = FactorFactory.<C>getImplementation( fac.ring.coFac );
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> baseFactorsSquarefree(GenPolynomial<Quotient<C>> P) {
        return factorsSquarefree(P);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> factorsSquarefree(GenPolynomial<Quotient<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<Quotient<C>>> factors = new ArrayList<GenPolynomial<Quotient<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        GenPolynomial<Quotient<C>> Pr = P;
        Quotient<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        QuotientRing<C> qi = (QuotientRing<C>)pfac.coFac;
        GenPolynomialRing<C> ci = qi.ring;
        GenPolynomialRing<GenPolynomial<C>> ifac = new GenPolynomialRing<GenPolynomial<C>>(ci, pfac);
        GenPolynomial<GenPolynomial<C>> Pi = PolyUfdUtil.<C>integralFromQuotientCoefficients(ifac, Pr);
        //System.out.println("Pi = " + Pi);

	// factor in C[x_1,...,x_n][y_1,...,y_m]
        List<GenPolynomial<GenPolynomial<C>>> irfacts = nengine.recursiveFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("irfacts = " + irfacts);
        }
        if (irfacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<Quotient<C>>> qfacts = PolyUfdUtil.<C>quotientFromIntegralCoefficients(pfac, irfacts);
        //System.out.println("qfacts = " + qfacts);
        qfacts = PolyUtil.monic(qfacts);
        //System.out.println("qfacts = " + qfacts);
        if ( !ldcf.isONE() ) {
            GenPolynomial<Quotient<C>> r = qfacts.get(0);
            qfacts.remove(r);
            r = r.multiply(ldcf);
            qfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("qfacts = " + qfacts);
        }
        factors.addAll(qfacts);
        return factors;
    }


    /**
     * Factorization of a squarefree Quotient.
     * @param P squarefree Quotient.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<Quotient<C>> quotientFactorsSquarefree(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<Quotient<C>> factors = new ArrayList<Quotient<C>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomial<C> num = P.num;
        GenPolynomial<C> den = P.den;
        QuotientRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if ( !num.isONE() ) {
            List<GenPolynomial<C>> nfac = nengine.factorsSquarefree(num);
            System.out.println("nfac = " + nfac);
            for ( GenPolynomial<C> nfp : nfac ) {
                Quotient<C> nf = new Quotient<C>(pfac,nfp);
                factors.add(nf);
            }
        }
        if ( den.isONE() ) {
            if ( factors.size() == 0 ) {
                factors.add(P);
            } 
            return factors;
        }
        List<GenPolynomial<C>> dfac = nengine.factorsSquarefree(den);
        System.out.println("dfac = " + dfac);
        for ( GenPolynomial<C> dfp : dfac ) {
            Quotient<C> df = new Quotient<C>(pfac,one,dfp);
            factors.add(df);
        }
        return factors;
    }


    /**
     * Factorization of a Quotient.
     * @param P Quotient.
     * @return [p_1 -&gt; e_1,...,p_k - &gt; e_k] with P = prod_{i=1, ..., k} p_i**e_k.
     */
    public SortedMap<Quotient<C>,Long> quotientFactors(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<Quotient<C>,Long> factors = new TreeMap<Quotient<C>,Long>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P,1L);
            return factors;
        }
        GenPolynomial<C> num = P.num;
        GenPolynomial<C> den = P.den;
        QuotientRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if ( !num.isONE() ) {
            SortedMap<GenPolynomial<C>,Long> nfac = nengine.factors(num);
            System.out.println("nfac = " + nfac);
            for ( GenPolynomial<C> nfp : nfac.keySet() ) {
                Quotient<C> nf = new Quotient<C>(pfac,nfp.monic());
                factors.put(nf,nfac.get(nfp));
            }
        }
        if ( den.isONE() ) {
            if ( factors.size() == 0 ) {
                factors.put(P,1L);
            } 
            return factors;
        }
        SortedMap<GenPolynomial<C>,Long> dfac = nengine.factors(den);
        System.out.println("dfac = " + dfac);
        for ( GenPolynomial<C> dfp : dfac.keySet() ) {
            Quotient<C> df = new Quotient<C>(pfac,one,dfp.monic());
            factors.put(df,dfac.get(dfp));
        }
        return factors;
    }


    /**
     * Squarefree factors of a Quotient.
     * @param P Quotient.
     * @return [p_1 -&gt; e_1,...,p_k - &gt; e_k] with P = prod_{i=1, ..., k} p_i**e_k.
     */
    public SortedMap<Quotient<C>,Long> quotientSquarefreeFactors(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<Quotient<C>,Long> factors = new TreeMap<Quotient<C>,Long>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P,1L);
            return factors;
        }
        GenPolynomial<C> num = P.num;
        GenPolynomial<C> den = P.den;
        QuotientRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if ( !num.isONE() ) {
            SortedMap<GenPolynomial<C>,Long> nfac = nengine.squarefreeFactors(num);
            System.out.println("nfac = " + nfac);
            for ( GenPolynomial<C> nfp : nfac.keySet() ) {
                Quotient<C> nf = new Quotient<C>(pfac,nfp.monic());
                factors.put(nf,nfac.get(nfp));
            }
        }
        if ( den.isONE() ) {
            if ( factors.size() == 0 ) {
                factors.put(P,1L);
            } 
            return factors;
        }
        SortedMap<GenPolynomial<C>,Long> dfac = nengine.squarefreeFactors(den);
        System.out.println("dfac = " + dfac);
        for ( GenPolynomial<C> dfp : dfac.keySet() ) {
            Quotient<C> df = new Quotient<C>(pfac,one,dfp.monic());
            factors.put(df,dfac.get(dfp));
        }
        return factors;
    }


    /**
     * Characteristics root of a Quotient.
     * @param P Quotient.
     * @return [p -&gt; k] if exists k with e=charactristic(P)*k and P = p**e, else null.
     */
    public SortedMap<Quotient<C>,Long> quotientRootCharacteristic(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if ( c.signum() == 0 ) {
            return null;
        }
        SortedMap<Quotient<C>,Long> root = new TreeMap<Quotient<C>,Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P,1L);
            return root;
        }
        SortedMap<Quotient<C>,Long> sf = quotientSquarefreeFactors(P);
        // better: test if sf.size() == 2 // no, since num and den factors 
        Long k = null;
        for (Quotient<C> p: sf.keySet() ) {
            if ( p.isConstant() ) {
                continue;
            }
            Long e = sf.get(p);
            java.math.BigInteger E = new java.math.BigInteger(e.toString());
            java.math.BigInteger r = E.remainder(c);
            if ( !r.equals(java.math.BigInteger.ZERO) ) {
                System.out.println("r = " + r);
                return null;
            }
            if ( k == null ) {
                k = e;
            } else if ( k.compareTo(e) >= 0 ) {
                k = e;
            }
        }
        // now c divides all exponents
        Long cl = c.longValue();
        Quotient<C> rp = P.ring.getONE();
        for (Quotient<C> q : sf.keySet()) {
            Long e = sf.get(q);
            if ( e > k ) {
                long ep = e / cl;
                q = Power.<Quotient<C>> positivePower(q, ep);
            }
            rp = rp.multiply(q);
        }
        k = k / cl;
        root.put(rp,k);
        return root;
    }


    /**
     * Characteristics root of a polynomial.
     * @param P polynomial.
     * @return [p -&gt; k] if exists k with e=k*charactristic(P) and P = p**e, else null.
     */
    public SortedMap<GenPolynomial<C>,Long> rootCharacteristic(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if ( c.signum() == 0 ) {
            return null;
        }
        SortedMap<GenPolynomial<C>,Long> root = new TreeMap<GenPolynomial<C>,Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P,1L);
            return root;
        }
        SortedMap<GenPolynomial<C>,Long> sf = nengine.squarefreeFactors(P.monic());
        System.out.println("sf = " + sf);
        // better: test if sf.size() == 1 // not ok
        Long k = null;
        for (GenPolynomial<C> p: sf.keySet() ) {
            if ( p.isConstant() ) {
                continue;
            }
            Long e = sf.get(p);
            java.math.BigInteger E = new java.math.BigInteger(e.toString());
            java.math.BigInteger r = E.remainder(c);
            if ( !r.equals(java.math.BigInteger.ZERO) ) {
                System.out.println("r = " + r);
                return null;
            }
            if ( k == null ) {
                k = e;
            } else if ( k.compareTo(e) >= 0 ) {
                k = e;
            }
        }
        // now c divides all exponents
        Long cl = c.longValue();
        GenPolynomial<C> rp = P.ring.getONE();
        for (GenPolynomial<C> q : sf.keySet()) {
            Long e = sf.get(q);
            if ( e > k ) {
                long ep = e / cl;
                q = Power.<GenPolynomial<C>> positivePower(q, ep);
            }
            rp = rp.multiply(q);
        }
        k = k / cl;
        root.put(rp,k);
        return root;
    }


    /**
     * Quotient is factorization.
     * @param P Quotient.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(Quotient<C> P, List<Quotient<C>> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        Quotient<C> t = P.ring.getONE();
        for (Quotient<C> f : F) {
            t = t.multiply(f);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(list): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(list): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * Quotient is factorization.
     * @param P Quotient.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization(Quotient<C> P, SortedMap<Quotient<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        Quotient<C> t = P.ring.getONE();
        for (Quotient<C> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            Quotient<C> g = Power.<Quotient<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P GenPolynomial.
     * @return char-th_rootOf(P).
     */
    // param <C> base coefficient type must be ModInteger.
    public GenPolynomial<C> baseRootCharacteristic( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // basePthRoot not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        if ( rf.characteristic().signum() != 1  ) { 
           // basePthRoot not possible
           throw new RuntimeException(P.getClass().getName()
                     + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<C> d = pfac.getZERO().clone();
        for ( Monomial<C> m : P ) {
            ExpVector f = m.e;  
            long fl = f.getVal(0);
            if ( fl % mp != 0 ) {
                 throw new RuntimeException(P.getClass().getName()
                       + " exponent not divisible by m " + fl);
            }
            fl = fl / mp;
            ExpVector e = ExpVector.create( 1, 0, fl );  
            d.doPutToMap(e,m.c);
        }
        return d; 
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    // param <C> base coefficient type must be ModInteger.
    public GenPolynomial<GenPolynomial<C>> 
      recursiveRootCharacteristic( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // basePthRoot not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<GenPolynomial<C>> rf = pfac.coFac;
        if ( rf.characteristic().signum() != 1  ) { 
           // basePthRoot not possible
           throw new RuntimeException(P.getClass().getName()
                     + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        for ( Monomial<GenPolynomial<C>> m : P ) {
            ExpVector f = m.e;  
            long fl = f.getVal(0);
            if ( fl % mp != 0 ) {
                return null;
//                  throw new RuntimeException(P.getClass().getName()
//                        + " exponent not divisible by m " + fl);
            }
            fl = fl / mp;
            SortedMap<GenPolynomial<C>, Long> sm = rootCharacteristic(m.c);
            if ( sm == null ) {
                return null;
//                  throw new RuntimeException(P.getClass().getName()
//                        + " coefficient not char-th root, sm " + m.c);
            }
            GenPolynomial<C> r = sm.firstKey();
            long gl = sm.get(r);
            if ( gl > fl ) {
                r = Power.<GenPolynomial<C>> positivePower(r, gl);
//                  throw new RuntimeException(P.getClass().getName()
//                        + " coefficient not char-th root " + m.c + ", gl = " + gl + ", fl = " + fl);
            }
            ExpVector e = ExpVector.create( 1, 0, fl );  
            d.doPutToMap(e,r);
        }
        return d; 
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P univariate GenPolynomial with Quotient coefficients.
     * @return char-th_rootOf(P), or null, if P is no char-th root.
     */
    public GenPolynomial<Quotient<C>> 
      quotientRootCharacteristic( GenPolynomial<Quotient<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // basePthRoot not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<Quotient<C>> rf = pfac.coFac;
        if ( rf.characteristic().signum() != 1  ) { 
           // basePthRoot not possible
           throw new RuntimeException(P.getClass().getName()
                     + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<Quotient<C>> d = pfac.getZERO().clone();
        for ( Monomial<Quotient<C>> m : P ) {
            ExpVector f = m.e;  
            long fl = f.getVal(0);
            if ( fl % mp != 0 ) {
                return null;
//                 throw new RuntimeException(P.getClass().getName()
//                        + " exponent not divisible by m " + fl);
            }
            fl = fl / mp;
            SortedMap<Quotient<C>, Long> sm = quotientRootCharacteristic(m.c);
            if ( sm == null ) {
                return null;
//                  throw new RuntimeException(P.getClass().getName()
//                        + " coefficient not char-th root, sm " + m.c);
            }
            Quotient<C> r = sm.firstKey();
            long gl = sm.get(r);
            if ( gl > fl ) {
                r = Power.<Quotient<C>> positivePower(r, gl);

//                  throw new RuntimeException(P.getClass().getName()
//                        + " coefficient not char-th root " + m.c + ", gl = " + gl + ", fl = " + fl);
            }
            ExpVector e = ExpVector.create( 1, 0, fl );  
            d.doPutToMap(e,r);
        }
        return d; 
    }

}

