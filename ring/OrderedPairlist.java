/*
 * $Id$
 */

package edu.jas.ring;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.TermOrder;

import edu.jas.ring.Reduction;

/**
 * Pair list management.
 * Implemented using OrderedPolynomial, TreeMap and BitSet.
 * @author Heinz Kredel
 */

public class OrderedPairlist {

    private final ArrayList P;
    private final TreeMap pairlist;
    private final ArrayList red;
    private final TermOrder torder;

    private static Logger logger = Logger.getLogger(OrderedPairlist.class);

    /**
     * Constructor for OrderedPairlist
     */

    public OrderedPairlist(TermOrder to) {
	 torder = to;
         P = new ArrayList();
         pairlist = new TreeMap( to.getAscendComparator() );
         red = new ArrayList();
    }

    /**
     * subclass to hold pairs of polynomials
     */

    class Pair implements Serializable {
	public final OrderedPolynomial pi;
	public final OrderedPolynomial pj;
	public final int i;
	public final int j;

	Pair(Object a, OrderedPolynomial b, int i, int j) {
	    this( (OrderedPolynomial)a, b, i, j); 
	}

	Pair(OrderedPolynomial a, OrderedPolynomial b, int i, int j) {
	    pi = a; 
            pj = b; 
            this.i = i; 
            this.j = j;
	}
    }

    public synchronized void put(OrderedPolynomial p) { 
           Pair pair;
           ExpVector e; 
           ExpVector f; 
           ExpVector g; 
           OrderedPolynomial pj; 
	   BitSet redi;
           Object x;
           LinkedList xl;
           e = p.leadingExpVector();
	   int l = P.size();
           for ( int j = 0; j < l; j++ ) {
	       pj = (OrderedPolynomial) P.get(j);
               f = pj.leadingExpVector(); 
               g = ExpVector.EVLCM( e, f );
               pair = new Pair( pj, p, j, l);
	       // redi = (BitSet)red.get(j);
	       ///if ( j < l ) redi.set( l );
	       // System.out.println("bitset."+j+" = " + redi );  

	       //multiple pairs under same keys -> list of pairs
               x = pairlist.get( g );
               if ( x == null ) xl = new LinkedList();
	          else xl = (LinkedList) x; 

               xl.addLast( pair); // first or last ?
               pairlist.put( g, xl );
	   }
	   // System.out.println("pairlist.keys@put = " + pairlist.keySet() );  
           P.add(  p );
	   redi = new BitSet();
           if ( l > 0 ) { // redi.set( 0, l ) jdk 1.4
	       for ( int i=0; i<l; i++ ) redi.set(i);
	   }
	   red.add( redi );
    }


    public synchronized Pair removeNext() { 
	//System.out.println("pairlist.keys@remove = " + pairlist.keySet() );  

       Set pk = pairlist.entrySet();
       Iterator ip = pk.iterator();

       Pair pair = null;
       boolean c = false;
       int i, j;

       while ( !c & ip.hasNext() )  {

           Map.Entry me = (Map.Entry) ip.next();

           ExpVector g = (ExpVector) me.getKey();
           LinkedList xl =(LinkedList) me.getValue();
           if ( logger.isInfoEnabled() )
	      logger.info("g  = " + g);
	   pair = null;

	   while ( !c & xl.size() > 0 ) {
                 pair = (Pair) xl.removeFirst();
                 // xl is also modified in pairlist 
                 i = pair.i; 
                 j = pair.j; 
                 //System.out.println("pair(" + j + "," +i+") ");
                 c = Reduction.GBCriterion4( pair.pi, pair.pj, g ); 
                 //System.out.println("c4  = " + c);  
		 if ( c ) {
                    c = GBCriterion3( i, j, g );
                    //System.out.println("c3  = " + c); 
		 }
                 ((BitSet)red.get( j )).clear(i); // set(i,false) jdk1.4
	   }
           if ( xl.size() == 0 ) ip.remove(); 
              // = pairlist.remove( g );
       }
       if ( ! c ) pair = null;
       return pair; 
    }

    public boolean hasNext() { 
          return pairlist.size() > 0;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */

    public boolean GBCriterion3(int i, int j, ExpVector eij) {  
	// assert i < j;
	boolean s;
        s = ((BitSet)red.get( j )).get(i); 
	if ( ! s ) { 
           logger.warn("c3.s false for " + j + " " + i); 
           return s;
	}
	s = true;
	boolean m;
	OrderedPolynomial A;
        ExpVector ek;
	for ( int k = 0; k < P.size(); k++ ) {
	    A = (OrderedPolynomial) P.get( k );
            ek = A.leadingExpVector();
            m = ExpVector.EVMT(eij,ek);
	    if ( m ) {
		if ( k < i ) {
		   // System.out.println("k < i "+k+" "+i); 
                   s =    ((BitSet)red.get( i )).get(k) 
                       || ((BitSet)red.get( j )).get(k); 
		}
		if ( i < k && k < j ) {
		   // System.out.println("i < k < j "+i+" "+k+" "+j); 
                   s =    ((BitSet)red.get( k )).get(i) 
                       || ((BitSet)red.get( j )).get(k); 
		}
		if ( j < k ) {
		    //System.out.println("j < k "+j+" "+k); 
                   s =    ((BitSet)red.get( k )).get(i) 
                       || ((BitSet)red.get( k )).get(j); 
		}
                //System.out.println("s."+k+" = " + s); 
		if ( ! s ) return s;
	    }
	}
        return true;
    }


}
