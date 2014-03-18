import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;


public class Currency {

	public static int procID = -1;
	public static int numOps = -1;
	public static int clkRate = -1;
	public static ArrayList<String> hostname = new ArrayList<String>();
	public static ArrayList<Integer> port = new ArrayList<Integer>();
	public static ArrayList<String> message = new ArrayList<String>();
	public static int opNum[] = {0,0,0};
	public static Clock c;
	public static int sell = 100;
	public static int buy = 100;
	public static int delX, delY;

	public Currency() {
		
	}
	
	public static void main(String[] args) throws IOException {
		//taking input from user
		if (args.length != 3) {
            System.err.println("Usage: java Currency <proc id> <# operations> <clock rate>");
            System.exit(1);
        }
		//taken from user for each process
		procID = Integer.parseInt(args[0]);
		numOps = Integer.parseInt(args[1]);
		clkRate = Integer.parseInt(args[2]);
		
		//read the info.txt file to get hostname and port number
		readFile(new File("info.txt"));
		
		//some variables
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput = "me hi";
		Worker w1;
		Worker w2;
		String sysTime;
		int count = 0;
		
		//prepare file for logging
		String fileName = "log".concat(Integer.toString(procID)).concat(".txt");
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("[P0]"+hostname.get(0)+":"+port.get(0)+'\n');
		bw.write("[P1]"+hostname.get(1)+":"+port.get(1)+'\n');
		bw.write("[P2]"+hostname.get(2)+":"+port.get(2)+'\n');

		switch(procID){
		//if this is process P0
		case 0:
			System.out.println("P0 created " + port.get(0) + " " + hostname.get(0));
			c = new Clock(clkRate);
			c.start();
			ServerSocket serverSocket = new ServerSocket(port.get(0));
			bw.write("\nP0("+hostname.get(0)+") is listening on "+port.get(0)+" ...\n");
			System.out.println("Server Started ");
			Socket clientSocket1 = serverSocket.accept();
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P0 is connected from P1 ("+hostname.get(1)+")");
			bw.write("\nWaiting for all to be connected...\n");
			w1 = new Worker(clientSocket1, message);
			w1.start();
			Socket clientSocket2 = serverSocket.accept();
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P0 is connected from P2 ("+hostname.get(2)+")");
			bw.write("\nAll connected.\n");
			w2 = new Worker(clientSocket2, message);
			w2.start();
			break;
		
		//if this is process P1
		case 1:
			System.out.println("P1 created " + port.get(1) + " " + hostname.get(1));
			bw.write("\nP1("+hostname.get(1)+") is listening on "+port.get(1)+" ...\n");
			c = new Clock(clkRate);
			c.start();
			Socket echoSocket1 = new Socket(hostname.get(0), port.get(0));
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P1 is connected to P0 ("+hostname.get(0)+")");
			bw.write("\nWaiting for all to be connected...\n");
			w1 = new Worker(echoSocket1, message);
			w1.start();
			ServerSocket serverSocket3 = new ServerSocket(port.get(1));
			System.out.println("Server Started ");
			Socket clientSocket = serverSocket3.accept();
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P1 is connected from P2 ("+hostname.get(2)+")");
			bw.write("\nAll connected.\n");
			w2 = new Worker(clientSocket, message);
			w2.start();
			break;
		
		//if this is process P2
		case 2: 
			System.out.println("P2 created " + port.get(1) + " " + hostname.get(1));
			bw.write("\nP2("+hostname.get(2)+") is active on "+port.get(2)+" ...\n");
			c = new Clock(clkRate);
			c.start();
			Socket echoSocket0 = new Socket(hostname.get(0), port.get(0));
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P2 is connected to P0 ("+hostname.get(0)+")");
			bw.write("\nWaiting for all to be connected...\n");
			w1 = new Worker(echoSocket0, message);
			w1.start();
			Socket echoSocket3 = new Socket(hostname.get(1), port.get(1));
			sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			bw.write("\n[ "+sysTime+" ] P2 is connected to P1 ("+hostname.get(1)+")");
			bw.write("\nAll connected.\n");
			w2 = new Worker(echoSocket3, message);
			w2.start();
			break;
			
		//default case
		default: 
			w1 = null;
			w2 = null;
			System.out.println("improper process number");
			System.exit(1);
			break;
		}//switch ends here
		
		int n=numOps;
		while(true){
			try{
				System.out.println(opNum[0]+" "+opNum[1]+" "+opNum[2]);
				if((opNum[0]>=n)&&(opNum[1]>=n)&&(opNum[2]>=n)){
					break;
				}
				int r = 0;
				while(r==0) {
					r = (int)(Math.random()*1000);
				}
				System.out.println(".....Sleep for "+r+" seconds.....");
				Thread.sleep(r);
				if((message.isEmpty())&&(opNum[procID]<n)){
					userInput = createUpdate(procID);
					w2.sendMsg(userInput);
					w1.sendMsg(userInput);
				}
				else if(!(message.isEmpty())){
					userInput = createACK(procID);
					w2.sendMsg(userInput);
					w1.sendMsg(userInput);
				} else if((message.isEmpty())&&(opNum[procID]>=n)){

				} 
				Thread.sleep(1000-r);
			
				String sp[] = message.get(0).split("[ ]");
				if(message.isEmpty()){
					continue;
				}
				c.setClk(Integer.parseInt(sp[2]));
				System.out.println("update from "+ sp[1] +" was successful");
				opNum[Integer.parseInt(sp[1])]++;
				count++;
				sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
				sell = sell+Integer.parseInt(sp[3]);
				buy = buy+Integer.parseInt(sp[4]);
				bw.write("\n[ "+sysTime+" ] [ OP"+count+" : C"+c.clk+" ] Currency Value is set to ("+sell+","+buy+") by ("+sp[3]+","+sp[4]+").");
				if((opNum[Integer.parseInt(sp[1])])>n-1){
					sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
					bw.write("\n[ "+sysTime+" ] P"+sp[1]+" finished");
				}
				message.clear();
				
			} catch(Exception e){
				e.getMessage();
			}				
		}
	
		sysTime = new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
		bw.write("\n\n[ "+sysTime+" ] All finished. P"+procID+" is terminating...");
		bw.close();
		try {Thread.sleep(500);
		w1.sendMsg("bye");
		w2.sendMsg("bye");
		} catch(Exception e){
			e.getMessage();
		}	
	}//main method ends here
	
	public static String createUpdate(int id) {
		String updt = "null";
		delX = (-80)+(int)(Math.random()*160);
		delY = (-80)+(int)(Math.random()*160);
		updt = "UPDATE "+Integer.toString(id)+" "+c.clk+" "+delX+" "+delY;
		return updt;
	}//createUpdate ends here
	
	public static String createACK(int id){
		String ack = "null";
	
		ack = "ACK "+Integer.toString(id)+" "+c.clk;
		return ack;
	}//createACK ends here
	
	public static void readFile(File f){
		try{
	    	Scanner h = new Scanner(f);        	
	        while (h.hasNext()) {
	            hostname.add(h.next());
	            port.add(Integer.parseInt(h.next()));
	        }
	        
	        h.close();
	      }catch (Exception e){
	          System.err.println("Error: Error in file reading = " + e.getMessage());
	      }
	}//readFile ends here	
}//class ends here