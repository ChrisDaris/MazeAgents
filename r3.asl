/*
!getPosition(slots).

+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots). 
+!getPosition(slots) : not finish(r3) & starter(X,Y,ZX,ZY) <- !move(X,Y,ZX,ZY).

+!move(X,Y,ZX,ZY) : 
	not finish(r3) & not markA(X+ZX,Y+ZY) || not markA(X-ZY,Y+ZX) || not markA(X+ZY,Y-ZX) 
	<- choose(X+ZX,Y+ZY,ZX,ZY); !moveA(X+ZX,Y+ZY,ZX,ZY).

+!moveA(X,Y,ZX,ZY) : 
	not markA(X+ZX,Y+ZY)
	<- next(X+ZX,Y+ZY,ZX,ZY); !move(X+ZX,Y+ZY,ZX,ZY).
+!moveA(X,Y,ZX,ZY) : 
	not markA(X-ZY,Y+ZX)
	<- next(X-ZY,Y+ZX,-ZY,ZX); !move(X-ZY,Y+ZX,-ZY,ZX).
+!moveA(X,Y,ZX,ZY) : 
	not markA(X+ZY,Y-ZX)
	<- next(X+ZY,Y-ZX,ZY,-ZX); !move(X+ZY,Y-ZX,ZY,-ZX).
	
+!move
*/
