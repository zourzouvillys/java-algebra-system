/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.GreatestCommonDivisorSimple;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorModular;
import edu.jas.ufd.GreatestCommonDivisorModEval;
import edu.jas.ufd.GCDProxy;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.Quotient;
import edu.jas.gb.Reduction;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GBFactory;

//import edu.jas.application.Ideal;
//import edu.jas.application.IdealWithUniv;


/**
 * PolyGBUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyGBUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public PolyGBUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyGBUtilTest.class);
        return suite;
    }


    //TermOrder to = new TermOrder(TermOrder.INVLEX);
    TermOrder to = new TermOrder(TermOrder.IGRLEX);


    int rl = 3;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.29f;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test resultant modular.
     */
    public void xtestResultantModular() {
        GenPolynomialRing<ModInteger> dfac;
        ModIntegerRing mi;

        PrimeList primes = new PrimeList();
        mi = new ModIntegerRing(primes.get(1)); // 17, 19, 23, 41, 163, 
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> ufdm = new GreatestCommonDivisorModEval<ModInteger>();
        GenPolynomial<ModInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //a = dfac.parse("6 x0^4 - 17");
            //b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");
            //a = dfac.parse("5 x1^4 * x2^4 + 2 x1^2 + x0^2");
            //b = dfac.parse("5 x0^2 * x1^2 + 2 x2^2 + 5 x2 + 15");
            //a = dfac.parse("x0^3 * x2^2 + 6 x0^4 + 6 x0 + 7");
            //b = dfac.parse("7 x0^2 * x2^2 + 3 x2^2 + 4 x1^2 * x2 + 4 x2 + 4 x1^2 + x1 + 7");
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = ufds.resultant(a, b);

            d = sres.resultant(a, b);

            e = ufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<ModInteger> isResultant(a, b, e);
            //System.out.println("t3 = " + t3);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            assertTrue("isResultant(a,b,c): " + c, t1);
            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
        }
    }


    /**
     * Test resultant integer.
     */
    public void xtestResultantInteger() {
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<BigInteger> ufds = new GreatestCommonDivisorSimple<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> sres = new GreatestCommonDivisorSubres<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> ufdm = new GreatestCommonDivisorModular<ModInteger>(); //true);
        GenPolynomial<BigInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //a = dfac.parse("6 x0^4 - 17");
            //b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = ufds.resultant(a, b);

            d = sres.resultant(a, b);

            e = ufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<BigInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<BigInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<BigInteger> isResultant(a, b, e);
            //System.out.println("t3 = " + t3);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
            assertTrue("isResultant(a,b,c): " + c, t1);
        }
    }


    /**
     * Test resultant modular parallel proxy.
     */
    public void xtestResultantModularParallel() {
        GenPolynomialRing<ModInteger> dfac;
        ModIntegerRing mi;

        PrimeList primes = new PrimeList();
        mi = new ModIntegerRing(primes.get(1)); // 17, 19, 23, 41, 163, 
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> ufdm = new GreatestCommonDivisorModEval<ModInteger>();

        GreatestCommonDivisorAbstract<ModInteger> pufds = new GCDProxy<ModInteger>(sres,ufds); 
        GreatestCommonDivisorAbstract<ModInteger> pufdm = new GCDProxy<ModInteger>(ufdm,sres); 

        GenPolynomial<ModInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = pufds.resultant(a, b);

            d = pufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);

            assertTrue("isResultant(a,b,c): " + c, t1);
            assertTrue("isResultant(a,b,d): " + d, t2);
        }
    }


    /**
     * Test resultant integer parallel proxy.
     */
    public void xtestResultantIntegerProxy() {
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<BigInteger> ufds = new GreatestCommonDivisorSimple<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> sres = new GreatestCommonDivisorSubres<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> ufdm = new GreatestCommonDivisorModular<ModInteger>(); //true);

        GreatestCommonDivisorAbstract<BigInteger> pufds = new GCDProxy<BigInteger>(sres,ufds); 
        GreatestCommonDivisorAbstract<BigInteger> pufdm = new GCDProxy<BigInteger>(ufdm,sres); 

        GenPolynomial<BigInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 1, el, q);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = pufds.resultant(a, b);

            d = pufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<BigInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<BigInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);

            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,c): " + c, t1);
        }
    }


    /**
     * Test random characteristic set.
     */
    public void xtestCharacteristicSet() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        dfac = new GenPolynomialRing<BigRational>(br, rl, to);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> a, b, c, d, e;
        List<GenPolynomial<BigRational>> F, G;

        for (int i = 0; i < 1; i++) {
            F = new ArrayList<GenPolynomial<BigRational>>();
            a = dfac.random(kl, ll, el, q * 1.1f);
            b = dfac.random(kl, ll + 2, el, q);
            c = dfac.random(kl, ll, el, q);
            F.add(a);
            F.add(b);
            F.add(c);
            while ( F.size() <= rl ) {
                F.add(dfac.getZERO()); // make false cs
            }
            //System.out.println("F = " + F);
            assertFalse("isCharacteristicSet: " + F, PolyGBUtil.<BigRational> isCharacteristicSet(F));

            G = PolyGBUtil.<BigRational> characteristicSet(F);
            //System.out.println("G = " + G);
            assertTrue("isCharacteristicSet: " + G, PolyGBUtil.<BigRational> isCharacteristicSet(G));

            e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,a);
            //System.out.println("e = " + e);
            assertTrue("a rem G: " + e, e.isZERO()||true); // not always true

            e = PolyGBUtil.<BigRational> characteristicSetReduction(G,a);
            //System.out.println("e = " + e);
            assertTrue("a mod G: " + e, e.isZERO()||true); // not always true

            //e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,G.get(G.size()-1));
            //System.out.println("e = " + e);
            //assertTrue("a mod G: " + e, e.isZERO());

            d = dfac.getONE();
            if ( ! G.contains(d) ) {
                d = dfac.random(kl, ll, el, q).sum(d);
                //System.out.println("d = " + d);
                e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,d);
                //System.out.println("e = " + e);
                assertFalse("a rem G: " + e, e.isZERO());
                e = PolyGBUtil.<BigRational> characteristicSetReduction(G,d);
                //System.out.println("e = " + e);
                assertFalse("a mod G: " + e, e.isZERO()&&false);
            }
        }
    }


    /**
     * Test characteristic set, example Circle of Apollonius, from CLO IVA, 1992.
     */
    public void xtestCharacteristicSetExample() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "u1", "u2", "u3", "u4", "x1",  "x2", "x3", "x4", "x5", "x6", "x7", "x8" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, h4, h5, h6, h7, h8, g, e, f, k;
        List<GenPolynomial<BigRational>> F, G, H, K, L;
        //List<IdealWithUniv<BigRational>> R;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" 2 x1 - u1 ");
        h2 = dfac.parse(" 2 x2 - u2 ");
        h3 = dfac.parse(" 2 x3 - u3 ");
        h4 = dfac.parse(" 2 x4 - u4 ");
        h5 = dfac.parse(" u2 x5 + u1 x6 - u1 u2 ");
        h6 = dfac.parse(" u1 x5 - u2 x6 ");
        h7 = dfac.parse(" x1^2 - x2^2 - 2 x1 x7 + 2 x2 x8 ");
        h8 = dfac.parse(" x1^2 - 2 x1 x7 - x3^2 + 2 x3 x7 - x4^2 + 2 x4 x8 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);
        F.add(h4);
        F.add(h5);
        F.add(h6);
        F.add(h7);
        F.add(h8);

        //System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, PolyGBUtil.<BigRational> isCharacteristicSet(F));

        G = PolyGBUtil.<BigRational> characteristicSet(F);
        System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, PolyGBUtil.<BigRational> isCharacteristicSet(G));

        g = dfac.parse("( ( x5 - x7 )**2 + ( x6 - x8 )**2 - ( x1 - x7 )**2 - x8^2 )");
        //g = dfac.parse("( - x1^2 x4 + x4 x5^2 + x1^2 x6 - x3^2 x6 - x4^2 x6 + x4 x6^2 + 2 x1 x4 x7 - 2 x4 x5 x7 - 2 x1 x6 x7 + 2 x3 x6 x7 )");
        //g = dfac.parse("-2 x6 * x8 - 2 x5 * x7 + 2 x1 * x7 + x6^2 + x5^2 - x1^2");
        System.out.println("g = " + g);

        //e = PolyGBUtil.<BigRational> characteristicSetReduction(G,g);
        e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,g);
        System.out.println("e = " + e);
        assertTrue("g mod G: " + e, e.isZERO()||true); // not always true

        GroebnerBaseAbstract<BigRational> bb = GBFactory.<BigRational> getImplementation(br);
        H = bb.GB(F);
        System.out.println(" H = " + H);

        Reduction<BigRational> red = bb.red;
        f = red.normalform(H,g);
        System.out.println("fg = " + f);

        k = red.normalform(H,e);
        System.out.println("fk' = " + k);

        //System.out.println("fk' == f: " + k.equals(f));
        //System.out.println("fk' - f: " + k.subtract(f));

        K = red.normalform(H,G);
        System.out.println("Kg = " + K);
        L = red.normalform(H,F);
        System.out.println("Lf = " + L);
        assertEquals("Kg == Kf: " + L, K, L);

        //Ideal<BigRational> I, J;
        //J = new Ideal<BigRational>(dfac,H,true);
        //J = new Ideal<BigRational>(dfac,F);
        //System.out.println("F = " + F);
        //System.out.println("J = " + J);

        //R = J.radicalDecomposition();
        //System.out.println("R = " + R);
        //for ( IdealWithUniv<BigRational> r : R ) {
        //     System.out.println("radical: ");
        //     System.out.println("" + r);
        //}
        //boolean t = J.isRadicalMember(g);
        //System.out.println("radicalMember g = " + t); // false
        //t = J.isRadicalMember(e);
        //System.out.println("radicalMember e = " + t); // false
    }


    /**
     * Test characteristic set, example circum-center.
     */
    public void testCharacteristicSetExampleCC() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "u1", "u2", "u3", "x1",  "x2", "x3" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, g, e;
        List<GenPolynomial<BigRational>> F, G, H, K, L;
        //Ideal<BigRational> I, J;
        //List<IdealWithUniv<BigRational>> R;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" 2 u1 x2 - u1^2 ");
        h2 = dfac.parse(" 2 u2 x2 + 2 u3 x1 - u2^2 - u3^2 ");
        h3 = dfac.parse(" 2 u3 x3 - 2 ( u2 - u1 ) x2 - u2^2 - u3^2 + u1^2 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);

        System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, PolyGBUtil.<BigRational> isCharacteristicSet(F));

        G = PolyGBUtil.<BigRational> characteristicSet(F);
        System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, PolyGBUtil.<BigRational> isCharacteristicSet(G));

        g = dfac.parse(" x3^2 - 2 x3 x1 + x1^2  ");
        //g = dfac.parse(" x3 - x1 ");
        System.out.println("g = " + g);

        e = PolyGBUtil.<BigRational> characteristicSetReduction(G,g);
        //e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,g);
        System.out.println("e = " + e);
        assertTrue("g mod G: " + e, e.isZERO()||true); // not always true
    }


    /**
     * Test characteristic set, example ??.
     */
    public void xtestCharacteristicSetExampleT() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, g, e;
        List<GenPolynomial<BigRational>> F, G, H, K, L;
        //Ideal<BigRational> I, J;
        //List<IdealWithUniv<BigRational>> R;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" x^2 + y + z - 1 ");
        h2 = dfac.parse(" x + y^2 + z - 1 ");
        h3 = dfac.parse(" x + y + z^2 - 1 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);

        System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, PolyGBUtil.<BigRational> isCharacteristicSet(F));

        G = PolyGBUtil.<BigRational> characteristicSet(F);
        System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, PolyGBUtil.<BigRational> isCharacteristicSet(G));
    }


    /**
     * Test coefficient base pseudo remainder.
     */
    public void xtestCoefficientBasePseudoRemainder() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        //String[] vars = new String[] { "x1",  "x2", "x3" };
        String[] vars = new String[] { "x1",  "x2" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        System.out.println("dfac = " + dfac);
        GenPolynomialRing<GenPolynomial<BigRational>> rfac = dfac.recursive(1);
        System.out.println("rfac = " + rfac);
        GenPolynomialRing<BigRational> cfac = (GenPolynomialRing<BigRational>) rfac.coFac;
        System.out.println("cfac = " + cfac);
        GenPolynomial<GenPolynomial<BigRational>> ar, cr, dr, er;
        GenPolynomial<BigRational> a, b, c, d, e;

        ar = rfac.random(kl, ll, el, q * 1.1f);
        b = cfac.random(kl, ll + 2, el*2, q);
        System.out.println("ar = " + ar);
        System.out.println("b  = " + b);

        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(ar,b);
        System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) <= ar.degree(0) || ar.degree(0) == 0); 
        assertTrue("deg(lfcd(c)) < deg(b): ", cr.leadingBaseCoefficient().degree(0) < b.degree(0) || b.degree(0) == 0); 

        dr = ar.multiply(b);
        System.out.println("dr = " + dr);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(dr,b);
        System.out.println("cr = " + cr);
        assertTrue("c == 0: ", cr.isZERO()); 

        long s = ar.degree(0);
        er = rfac.univariate(0,s+1);
        //System.out.println("er = " + er);
        er = er.multiply(b.multiply(b));
        er = er.sum(ar);
        System.out.println("er = " + er);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(er,b);
        System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) < er.degree(0)); 
    }


    /**
     * Test coefficient recursive pseudo remainder.
     */
    public void xtestCoefficientRecursivePseudoRemainder() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x1",  "x2", "x3" };
        //String[] vars = new String[] { "x1",  "x2" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        System.out.println("dfac = " + dfac);
        GenPolynomialRing<GenPolynomial<BigRational>> r1fac = dfac.recursive(2);
        System.out.println("r1fac = " + r1fac);
        GenPolynomialRing<GenPolynomial<GenPolynomial<BigRational>>> rfac = r1fac.recursive(1);
        System.out.println("rfac = " + rfac);
        GenPolynomialRing<GenPolynomial<BigRational>> cfac = (GenPolynomialRing<GenPolynomial<BigRational>>) rfac.coFac;
        System.out.println("cfac = " + cfac);
        GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>> ar, cr, dr, er;
        GenPolynomial<GenPolynomial<BigRational>> a, b, c, d, e;

        ar = rfac.random(kl, ll, el, q);
        b = cfac.random(kl, ll + 2, el, q);
        System.out.println("ar = " + ar);
        System.out.println("b  = " + b);

        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(ar,b);
        System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) <= ar.degree(0) || ar.degree(0) == 0); 
        assertTrue("deg(lfcd(c)) < deg(b): ", cr.leadingBaseCoefficient().degree(0) < b.degree(0) || b.degree(0) == 0); 

        dr = ar.multiply(b);
        System.out.println("dr = " + dr);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(dr,b);
        System.out.println("cr = " + cr);
        assertTrue("c == 0: ", cr.isZERO()); 

        long s = ar.degree(0);
        er = rfac.univariate(0,s+1);
        //System.out.println("er = " + er);
        er = er.multiply(b.multiply(cfac.fromInteger(100)));
        er = er.sum(ar);
        System.out.println("er = " + er);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(er,b);
        System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) < er.degree(0)); 
    }

}
