/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;


/**
 * BigComplex class based on BigRational implementing the RingElem
 * interface and with the familiar SAC static method names.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class BigComplex implements RingElem<BigComplex>, 
                                   RingFactory<BigComplex> {

    /** Real part of the data structure. 
      */
    protected final BigRational re;

    /** Imaginary part of the data structure. 
      */
    protected final BigRational im;

    private final static Random random = new Random();

    private static Logger logger = Logger.getLogger(BigComplex.class);


    /** The constructor creates a BigComplex object 
     * from two BigRational objects real and imaginary part. 
     * @param r real part.
     * @param i imaginary part.
     */
    public BigComplex(BigRational r, BigRational i) {
        re = r;
        im = i;
    }


    /** The constructor creates a BigComplex object 
     * from a BigRational object as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public BigComplex(BigRational r) {
        this(r,BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * from a long element as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public BigComplex(long r) {
        this(new BigRational(r),BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * with real part 0 and imaginary part 0. 
     */
    public BigComplex() {
        this(BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * from a String representation.
     * @param s string of a BigComplex.
     * @throws NumberFormatException
     */
    public BigComplex(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            return;
        } 
        s = s.trim();
        int i = s.indexOf("i");
        if ( i < 0 ) {
           re = new BigRational( s );
           im = BigRational.ZERO;
           return;
        }
        //logger.warn("String constructor not done");
        String sr = "";
        if ( i > 0 ) {
            sr = s.substring(0,i);
        }
        String si = "";
        if ( i < s.length() ) {
            si = s.substring(i+1,s.length());
        }
        //int j = sr.indexOf("+");
        re = new BigRational( sr.trim() );
        im = new BigRational( si.trim() );
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    public BigComplex clone() {
        return new BigComplex( re, im );
    }


    /** Copy BigComplex element c.
     * @param c BigComplex.
     * @return a copy of c.
     */
    public BigComplex copy(BigComplex c) {
        return new BigComplex( c.re, c.im );
    }


    /** Get the zero element.
     * @return 0 as BigComplex.
     */
    public BigComplex getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return 1 as BigComplex.
     */
    public BigComplex getONE() {
        return ONE;
    }


    /** Get a BigComplex element from a BigInteger.
     * @param a BigInteger.
     * @return a BigComplex.
     */
    public BigComplex fromInteger(BigInteger a) {
        return new BigComplex( new BigRational(a) );
    }


    /** Get a BigComplex element from a long.
     * @param a long.
     * @return a BigComplex.
     */
    public BigComplex fromInteger(long a) {
        return new BigComplex( new BigRational( a ) );
    }


    /** The constant 0.
     */
    public static final BigComplex ZERO = 
           new BigComplex();


    /** The constant 1.
     */
    public static final BigComplex ONE = 
           new BigComplex(BigRational.ONE);


    /** The constant i. 
     */
    public static final BigComplex I = 
           new BigComplex(BigRational.ZERO,BigRational.ONE);


    /** Get the real part. 
     * @return re.
     */
    public BigRational getRe() { return re; }


    /** Get the imaginary part. 
     * @return im.
     */
    public BigRational getIm() { return im; }


    /** Get the String representation.
     */
    public String toString() {
        String s = "" + re;
        int i = im.compareTo( BigRational.ZERO );
        //logger.info("compareTo "+im+" ? 0 = "+i);
        if ( i == 0 ) return s;
        s += "i" + im;
        return s;
    }


   /** Complex number zero. 
     * @param A is a complex number. 
     * @return If A is 0 then true is returned, else false.
     */
    public static boolean isCZERO(BigComplex A) {
      if ( A == null ) return false;
      return A.isZERO();
    }


   /** Is Complex number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ZERO );
    }


    /** Complex number one.  
     * @param A is a complex number.  
     * @return If A is 1 then true is returned, else false. 
     */
    public static boolean isCONE(BigComplex A) {
      if ( A == null ) return false;
      return A.isONE();
    }


    /** Is Complex number one.  
     * @return If this is 1 then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return    re.equals( BigRational.ONE )
               && im.equals( BigRational.ZERO );
    }


    /** Is Complex imaginary one.  
     * @return If this is i then true is returned, else false. 
     */
    public boolean isIMAG() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ONE );
    }


    /** Is Complex unit element.
     * @return If this is a unit then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return ( ! isZERO() );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object b) {
        if ( ! ( b instanceof BigComplex ) ) {
           return false;
        }
   BigComplex bc = (BigComplex) b;
        return    re.equals( bc.re ) 
               && im.equals( bc.im );
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return 37 * im.hashCode() + re.hashCode();
    }


    /** since complex numbers are unordered, there is 
     * no compareTo method. 
     * We define the result to be 
     * @return 0 if b is equal to this
     * @return 1 else
     */
    public int compareTo(BigComplex b) {
        if ( this.equals(b) ) { 
            return 0;
        } else {
            return 1;
        }
    }

    /** since complex numbers are unordered, there is 
     * no signum method. 
     * We define the result to be 
     * @return 0 if this is equal to 0;
     *         1 if re > 0, or re == 0 and im > 0;
     *        -1 if re < 0, or re == 0 and im < 0
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
      int s = re.signum();
      if ( s != 0 ) {
          return s;
      }
      return im.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /** Complex number add.  
     * @param B a BigComplex number.
     * @return this+B.
     */
    public BigComplex sum(BigComplex B) {
        return new BigComplex( re.sum( B.re ), 
                               im.sum( B.im ) );
    }

    /** Complex number sum.  
     * @param A and B are complex numbers.  
     * @return A+B. 
     */
    public static BigComplex CSUM(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.sum(B);
    }


    /** Complex number difference.  
     * @param A and B are complex numbers.  
     * @return A-B.
     */
    public static BigComplex CDIF(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.subtract(B);
    }


    /** Complex number subtract.  
     * @param B a BigComplex number.
     * @return this-B.
     */
    public BigComplex subtract(BigComplex B) {
        return new BigComplex( re.subtract( B.re ), 
                               im.subtract( B.im ) );
    }


    /** Complex number negative.  
     * @param A is a complex number.  
     * @return -A
     */
    public static BigComplex CNEG(BigComplex A) {
        if ( A == null ) return null;
        return A.negate();
    }


    /** Complex number negative.  
     * @return -this. 
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigComplex negate() {
        return new BigComplex( re.negate(), 
                               im.negate());
    }


    /** Complex number conjugate.  
     * @param A is a complex number. 
     * @return the complex conjugate of A. 
     */
    public static BigComplex CCON(BigComplex A) {
        if ( A == null ) return null;
        return A.conjugate();
    }

    /* arithmetic operations: conjugate, absolut value 
     */

    /** Complex number conjugate.  
     * @return the complex conjugate of this. 
     */
    public BigComplex conjugate() {
        return new BigComplex(re, im.negate());
    }


    /** Complex number absolute value.  
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2.
     * Note: The square root is not jet implemented.
     */
    public BigComplex abs() {
        BigRational v = re.multiply(re).sum(im.multiply(im));
        logger.error("abs() square root missing");
        // v = v.sqrt();
        return new BigComplex( v );
    }

    /** Complex number absolute value.  
     * @param A is a complex number.  
     * @return the absolute value of A, a rational number. 
     * Note: The square root is not jet implemented.
     */
    public static BigRational CABS(BigComplex A) {
      if ( A == null ) return null;
      return A.abs().re;
    }


    /** Complex number product.  
     * @param A and B are complex numbers.  
     * @return A*B.
     */
    public static BigComplex CPROD(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */


    /** Complex number product.  
     * @param B is a complex number.  
     * @return this*B.
     */
    public BigComplex multiply(BigComplex B) {
        return new BigComplex(
               re.multiply(B.re).subtract(im.multiply(B.im)),
               re.multiply(B.im).sum(im.multiply(B.re)) );
    }


    /** Complex number inverse.  
     * @param A is a non-zero complex number.  
     * @return S with S*A = 1. 
     */
    public static BigComplex CINV(BigComplex A) {
        if ( A == null ) return null;
        return A.inverse();
    }


    /** Complex number inverse.  
     * @return S with S*this = 1. 
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigComplex inverse() {
        BigRational a = re.multiply(re).sum(im.multiply(im));
        return new BigComplex( re.divide(a), 
                               im.divide(a).negate() ); 
    }


    /** Complex number inverse.  
     * @param S is a complex number.  
     * @return 0. 
     */
    public BigComplex remainder(BigComplex S) {
        if ( S.isZERO() ) {
           throw new RuntimeException("division by zero");
        }
        return ZERO;
    }


    /** Complex number quotient.  
     * @param A and B are complex numbers, B non-zero.
     * @return A/B. 
     */
    public static BigComplex CQ(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.divide(B);
    }


    /** Complex number divide.
     * @param B is a complex number, non-zero.
     * @return this/B. 
     */
    public BigComplex divide (BigComplex B) {
        return this.multiply( B.inverse() );
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public BigComplex random(int n) {
        return random(n,random);
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R.
     */
    public BigComplex random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random( n, rnd );
        BigRational i = BigRational.ONE.random( n, rnd );
        return new BigComplex( r, i ); 
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public static BigComplex CRAND(int n) {
        return ONE.random(n,random);
    }


    /** Parse complex number from string.
     * @param s String.
     * @return BigComplex from s.
     */
    public BigComplex parse(String s) {
        return new BigComplex(s);
    }


    /** Parse complex number from Reader.
     * @param r Reader.
     * @return next BigComplex from r.
     */
    public BigComplex parse(Reader r) {
        return ZERO;
    }

}
