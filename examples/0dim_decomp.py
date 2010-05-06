#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import Ring, PolyRing
from jas import terminate
from jas import startLog

from jas import QQ, DD

# polynomial examples: zero dimensional ideals prime and primary decomposition

#r = Ring( "Rat(x) L" );
#r = Ring( "Q(x) L" );
r = PolyRing(QQ(),"x,y,z",PolyRing.lex);

print "Ring: " + str(r);
print;

[one,x,y,z] = r.gens();

f1 = (x**2 - 5)**2;
f2 = y**2 - x**2;
f3 = z**3 - y * x;

print "f1 = ", f1;
print "f2 = ", f2;
print "f3 = ", f3;
print;

F = r.ideal( list=[f1,f2,f3] );

print "F = ", F;
print;

startLog();

t = System.currentTimeMillis();
P = F.primeDecomp();
t1 = System.currentTimeMillis() - t;
print "P = ", P;

t = System.currentTimeMillis();
R = F.primaryDecomp();
t = System.currentTimeMillis() - t;

print "P = ", P;
print;
print "prime decomp time =", t1, "milliseconds";
print;
print "R = ", R;
print;
print "primary decomp time =", t, "milliseconds";
print;

print "F = ", F;
print;

#startLog();
terminate();
