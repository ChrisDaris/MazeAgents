
!getPosition(slots).

+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots). 
+!getPosition(slots) : not finish(r3) & starter(X,Y,ZX,ZY) <- !moveFirst(X,Y,ZX,ZY).

// might spawn in crossroad, so dont put mark first time
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & not finish(r3) 
	<-  next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & not finish(r3) 
	<- next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & not finish(r3) 
	<- next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
	
// no crossroad, go on the only empty cell without marking
+!move(Xp,Yp,ZXp,ZYp) : 
	not finish(r3) & not crossroad(slots) & empty(_,X,Y,ZX,ZY)
	<- //.print("-Cross, go front"); 
	next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// no crossroad but end of road. return back
+!move(X,Y,ZX,ZY) : 
	not finish(r3) & not crossroad(slots)
	<- //.print("-Cross, deadR marked, go back"); 
	+mark2(X,Y,-ZX,-ZY); +mark1(X,Y,-ZX,-ZY); next(X-ZX,Y-ZY,-ZX,-ZY); !move(X-ZX,Y-ZY,-ZX,-ZY).

// crossroad, with at least a road with no mark
// the road that we came is not marked so mark it once
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go front"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go left"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go right"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// crossroad, with at least a road with no mark
// the road that we came is marked once so mark it twice
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR +mark, go front"); 
	+mark1(X,Y,-ZX,-ZY); +mark2(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR +mark, go left"); 
	+mark1(X,Y,-ZX,-ZY); +mark2(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR +mark, go right"); 
	+mark1(X,Y,-ZX,-ZY); +mark2(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// crossroad, but all roads are marked at least once, so go back
+!move(X,Y,ZX,ZY) : 
	not mark1(X-ZX,Y-ZY,ZX,ZY) & not finish(r3) //	not mark2(X-ZX,Y-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR -mark2, go back"); 
	+mark1(X-ZX,Y-ZY,ZX,ZY);  +mark2(X-ZX,Y-ZY,ZX,ZY); next(X-ZX,Y-ZY,-ZX,-ZY); !move(X-ZX,Y-ZY,-ZX,-ZY).

// crossroad,  but all roads are marked at least once, and the road that we came
// is marked twice
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go front"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go left"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) & not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go right"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

+!move(X,Y,ZX,ZY) : 
	finish(r3) <- finished(slots).

+!move(X,Y,ZX,ZY) : true <- .print("Bot was lost").


















