Program Structure and Important Files, Classes and Methods:
[BEWARE] The process runs for (3*Numof Operatios) seconds as explained in point [3] of report. That is, a code with 30 operations per process runs for 90 seconds.
	     Therefore at first, please test it with small number of Operations (5 -15) to see the logfile.
		 
This Project keeps replicas of currency exchange values in 3 VMS using lamports totally ordered multicast algorithm.

Console:
	This project takes arguments - procID, number of operations and clock ticks - from the user.
	/*** This code assumes that each process has the same number of messages to send. It will allow user to input different values for number of messages, 
		 but it will not act correctly if that happens ***/

info.txt:
	This project reads the hostnames and port numbers of these messages from a file called info.txt


The following are the main classes of this project
The worker class:
	The worker class extends the Thread class.
	Its constructor takes in a socket as input and creates input and output stream buffers for that socket conection.
	run() :  This method constantly listens to input buffer for new messages.
	attachMsg() : When a message arrives, it puts it in the queue (ArrayList msg) that is shared by all three threads.
	sendMsg() : This method takes a string as input and puts it on the output buffer.
	
The Clock Class:
	The clock class extends the Thread class and creates a thread that increments the local clock concurrently with the main execution.
	It has a method setClk() which takes in a new clock value and if it is greater than the local clock, assignes  (new Clock+1) to local clock.

The Currency class: 
	This class has the main function
			Message:
	There are to types of messages: UPDATE and ACK. There is a method to create each of them.
	createUpdate():
		This class takes ProcID of process creating the message and returns a string containing the Type, procID, Local Clock and Payload.
	createACK():
		This method takes procID of process  creating the ACK and returns a string containing the Type, procID and Local Clock. 
		A process only sends ack if it has an update message in it's queue. It doesn't need to send the identification for the message 
		because this algorithm does not allow a new update message to be added to queue unless the previous one is acknowledged and committed.
	
	A Process:
		The currency class creates a process. There are 3 processes, each running on one VM. The processes are called P0, P1 and P2. 
		Each process has a message queue(ArrayList message) and local clock(Clock c).
		Currency exchange rates (sellRate, buyRate) are defined and initialized to (100,100) in each of the processes.

		Establishing connections:
			In the beginning, the three process act differently. The switch function in the code allows this : 
			Process 0 listens on a port and waits for connectios from Process 1 and Process 2.
			Process 1 initializes connection with Process 0 and then waits for connection from Process 2.
			Process 2 initializes connection with both Process 0 and Process 1.

		Algorithm Loop:
			After the initial connections are established, the algorithm enters a infinite while loop 
			which can only be exited when all processes have finished sending messages.
			At a random time between (0,100] not including 0, every process sends an update message to everyone.
			If a process sees an update on it's local queue, it sends ack for it.
			When a message has received all the acks, it is flushed from the queue(along with the acks).
			When a message sees that the queue is empty, only then does it send a new message so as to preserve the rules of the algorithm.
			
		Termination:
			Only when all processes are done executing the number of operations it was assigned, they send "bye" messages to each other.
			The Worker class recognizes this and closes the socket.
			Any attempt to contact a socket after that results in error.
			
log.txt:
	Every process keeps a log of it's activities in a file called log#.txt where # stands for the procID of that process.
	When a process has finished sending the number of messages assigned to it, it makes an entry saying I've finished. Thus, differet processes finish at different times.
	It is clear that all the processes receive the updates to (sell,buy) in the same order.
	The local clock of each process only shows ascending times, without a single faulty value.(eg. no value is smaller than the previous one)
	Thus, from the log file, we can verify that the algorithm is working correctly i.e. the currency value is consistent and clock times are totally ordered
	