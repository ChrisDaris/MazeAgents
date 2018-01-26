// init beliefs
// The agent regards his initial direction as the "right" direction and starts
// counting from 0
counter(0).

// Debug line start
//+!getPosition(slots).
// Debug line end

// we need the starting position from the env. "starting(slots)" creates a "starter(X,Y,ZX,ZY)" with the positions
!getPosition(slots).

// ZX and ZY are verables that help to find the direction. (their possible values are -1, 0, 1)

// For exable if current position is (X, Y) then:
// (X+ZX,Y+ZY) == the cell in the front side of the agent
// (X-ZY,Y+ZX) == the cell in the left side of the agent 
// (X+ZY,Y-ZX) == the cell in the right side of the agent 
// (X-ZX,Y-ZY) == the cell in the back side of the agent 

// The agent broadcasts his starting position to the other agents
+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots). 
+!getPosition(slots) : not finish(r4) & starter(X,Y,ZX,ZY) 
	<- //.print(ZX,ZY); 
	.broadcast(tell,pos(X,Y)); !move(X,Y,ZX,ZY).

// The following comments will be deleted after more testing
// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
//// The agent has counter == 0 so goes front until he finds an obstacle
//+!move(X,Y,ZX,ZY) : 
//	not finish(r4) & empty(X+ZX,Y+ZY) & counter(0) & not pos(X+ZY,Y+ZX)
//	<-// .print("No wall follower");
//	.broadcast(tell,pos(X+ZX,Y+ZY)); next(X+ZX,Y+ZY,ZX,ZY); 
//	.broadcast(untell,pos(X,Y)); !move(X+ZX,Y+ZY,ZX,ZY).

//// The agent finds an obstacle and as he is unable to go further front he turns
//// left so as to follow the "Right wall follower" algorithm, substracting once from
//// the counter
//+!move(X,Y,ZX,ZY) :
//	not finish(r4) & empty(X-ZY,Y+ZX) & counter(0) & not pos(X-ZY,Y+ZX)
//	<-// .print("Right wall follower");
//	.broadcast(tell,pos(X-ZY,Y+ZX)); -+counter(-1); next(X-ZY,Y+ZX,-ZY,ZX); 
//	.broadcast(untell,pos(X,Y)); !move(X-ZY,Y+ZX,-ZY,ZX).
// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

// As the counter is not 0 and the agent is able to go right, he adds 1 to the 
// counter and goes right
+!move(X,Y,ZX,ZY) :
	not finish(r4) & empty(X+ZY,Y-ZX) & counter(N) & N < 0 & not pos(X+ZY,Y-ZX)
	<- .broadcast(tell,pos(X+ZY,Y-ZX)); -+counter(N+1); next(X+ZY,Y-ZX,ZY,-ZX); 
	.broadcast(untell,pos(X,Y)); !move(X+ZY,Y-ZX,ZY,-ZX).

// Front cell is empty and either the right cell is not empty or the counter is 0
+!move(X,Y,ZX,ZY) :
	not finish(r4) & empty(X+ZX,Y+ZY) & counter(N) & not pos(X+ZX,Y+ZY)
	<- .broadcast(tell,pos(X+ZX,Y+ZY)); next(X+ZX,Y+ZY,ZX,ZY); 
	.broadcast(untell,pos(X,Y)); !move(X+ZX,Y+ZY,ZX,ZY).

// Both right and front cells of the agent are not emtpy, so the agent turns left
+!move(X,Y,ZX,ZY) :
	not finish(r4) & counter(N)
	<- -+counter(N-1); next(X,Y,-ZY,ZX); !move(X,Y,-ZY,ZX).

// The agent is in a finishing cell
+!move(X,Y,ZX,ZY) : 
	finish(r4) <- .broadcast(untell,pos(X,Y)); finished(slots).

// This message should never be printed as the algorithm does not know if he is lost
+!move(X,Y,ZX,ZY) : true <- .print("Bot was lost").


















