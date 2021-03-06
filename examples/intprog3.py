#
# jython for jas example 3 integer programming.
# $Id$
#
# CLO2, p374,a,b 
# 3 A + 2 B + C + D = 10
# 4 A +   B + C     = 5
#
# max: 2 A + 3 B + C + 5 D
#

import sys;

from jas import Ring

#r = Ring( "Rat(w1,w2,w3,w4,z1,z2) W( (0,0,0,0,1,1),(-2,-3,-1,-5,0,0) )" );
#r = Ring( "Rat(w1,w2,w3,w4,z1,z2) W( (0,0,0,0,1,1),( 7, 3, 2, 1,0,0)*6 )" );
r = Ring( "Rat(w1,w2,w3,w4,z1,z2)  W( (0,0,0,0,1,1),(40,15,11, 1,0,0)   )" );
print "Ring: " + str(r);
print;


ps = """
( 
 ( z1^3 z2^4 - w1 ),
 ( z1^2 z2   - w2 ),
 ( z1   z2   - w3 ),
 ( z1        - w4 )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

rg = f.GB();
print "seq Output:", rg;
print;


pf = """
( 
 ( z1^10 z2^5 ),
 ( z1^20 z2^14 )
) 
""";

fp = r.ideal( pf );
print "Ideal: " + str(fp);
print;

nf = fp.NF(rg);
print "NFs: " + str(nf);
print;

