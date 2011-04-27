/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class ComplexRootTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>ComplexRootTest</CODE> object.
     * @param name String.
     */
    public ComplexRootTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ComplexRootTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<Complex<BigRational>> dfac;


    ComplexRing<BigRational> cfac;


    BigRational eps;


    Complex<BigRational> ceps;


    GenPolynomial<Complex<BigRational>> a;


    GenPolynomial<Complex<BigRational>> b;


    GenPolynomial<Complex<BigRational>> c;


    GenPolynomial<Complex<BigRational>> d;


    GenPolynomial<Complex<BigRational>> e;


    int rl = 1;


    int kl = 3;


    int ll = 3;


    int el = 5;


    float q = 0.7f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new ComplexRing<BigRational>(new BigRational(1));
        String[] vars = new String[] { "z" };
        dfac = new GenPolynomialRing<Complex<BigRational>>(cfac, rl, to, vars);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
        ceps = new Complex<BigRational>(cfac,eps);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        dfac = null;
        cfac = null;
        eps = null;
    }


    /**
     * Test complex roots, imaginary.
     */
    public void testComplexRootsImag() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.parse("z^3 - i2");
        a = dfac.random(ll+1).monic();
        a = dfac.parse("z^7 - 2 z");
        a = dfac.parse("z^6 - i2");
        //System.out.println("a = " + a);

        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a); 
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for ( Complex<RealAlgebraicNumber<BigRational>> root : roots ) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }


    /*
     * Test complex roots, random polynomial.
     */
    public void testComplexRootsRand() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.random(ll+1).monic();
        if ( a.isZERO() || a.isONE() ) {
             a = dfac.parse("z^6 - i2");
        }
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a); 
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for ( Complex<RealAlgebraicNumber<BigRational>> root : roots ) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }

}