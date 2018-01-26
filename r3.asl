// Initial plan of the agent is to get his starting position from the env 
!getPosition(slots).

// Debug line start
//+!getPosition(slots).
// Debug line end

// we need the starting position from the env. "starting(slots)" creates a "starter(X,Y,ZX,ZY)" with the positions
+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots). 

// ZX and ZY are verables that help to find the direction. (their possible values are -1, 0, 1)

// For exable if current position is (X, Y) then:
// (X+ZX,Y+ZY) == the cell in the front side of the agent
// (X-ZY,Y+ZX) == the cell in the left side of the agent 
// (X+ZY,Y-ZX) == the cell in the right side of the agent 
// (X-ZX,Y-ZY) == the cell in the back side of the agent 

+!getPosition(slots) : not finish(r3) & starter(X,Y,ZX,ZY) <- !moveFirst(X,Y,ZX,ZY).

// The agent might spawn in a crossroad, therefor he will start moving without
// marking the first time (or else the algorithm might break
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & not finish(r3) 
	<-  next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & not finish(r3) 
	<- next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!moveFirst(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & not finish(r3) 
	<- next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
	
// There is only one road so follow it
+!move(Xp,Yp,ZXp,ZYp) : 
	not finish(r3) & not crossroad(slots) & empty(_,X,Y,ZX,ZY)
	<- //.print("-Cross, go front"); 
	next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// Agent went in a dead end. Mark it twice and go back
+!move(X,Y,ZX,ZY) : 
	not finish(r3) & not crossroad(slots)
	<- //.print("-Cross, deadR marked, go back"); 
	+mark2(X,Y,-ZX,-ZY); +mark1(X,Y,-ZX,-ZY); next(X-ZX,Y-ZY,-ZX,-ZY); 
	!move(X-ZX,Y-ZY,-ZX,-ZY).

// There are at least 2 roads and from them at least one has no mark.
// So the agent sould go to that road marking it.
// The road that he came has no marks, so mark it once
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go front"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go left"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & not mark1(X,Y,-ZX,-ZY) & not mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR -mark, backR -mark, go right"); 
	+mark1(X,Y,-ZX,-ZY); +mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// There are at least 2 roads and from them at least one has no mark.
// So the agent sould go to that road marking it.
// The road that he came has one mark, so mark it twice
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

// There are multiple possible ways but they are all marked at least once
// The road that the agent came is not marked, so mark it twice and go back
+!move(X,Y,ZX,ZY) : 
	not mark1(X-ZX,Y-ZY,ZX,ZY) & not finish(r3) //	not mark2(X-ZX,Y-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR -mark2, go back"); 
	+mark1(X-ZX,Y-ZY,ZX,ZY);  +mark2(X-ZX,Y-ZY,ZX,ZY); next(X-ZX,Y-ZY,-ZX,-ZY); 
	!move(X-ZX,Y-ZY,-ZX,-ZY).

// There are multiple possible ways but they are all marked at least once
// The road that the agent came is marked once, so mark it twice and go to one
// of the other roads
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(0,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go front"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(1,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go left"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).
+!move(Xp,Yp,ZXp,ZYp) : 
	empty(2,X,Y,ZX,ZY) & mark1(X,Y,-ZX,-ZY) & mark1(Xp-ZXp,Yp-ZYp,ZXp,ZYp) 
	& not mark2(X,Y,-ZX,-ZY) & not finish(r3) 
	<- //.print("+Cross, allR +mark, backR +mark2, go right"); 
	+mark2(X,Y,-ZX,-ZY); next(X,Y,ZX,ZY); !move(X,Y,ZX,ZY).

// The agent is in the finishin cell
+!move(X,Y,ZX,ZY) : 
	finish(r3) <- finished(slots).

// The agent got lost with no posible way to go (his part of the labirinth does
// not connect to the finishing cell.
+!move(X,Y,ZX,ZY) : true <- .print("Bot was lost").


















