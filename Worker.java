import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Worker extends Thread {

	private Socket sock = null;
	private String userInput = "me hi";
	private PrintWriter out;
	private BufferedReader in;
	private ArrayList<String> msg;

	
	public Worker(Socket socket, ArrayList m) {
        super("Worker");
        this.sock = socket;
        this.msg = m;
    }
	
	
	public void run() {
		try {
            //Socket echoSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            String inputLine, outputLine;
            while (true) {
            	inputLine = in.readLine();
                String split[] = inputLine.split("[ ]");
                attachMsg(inputLine);
              if(inputLine.equals("bye")){ // breaks out of while loop and terminates socket
                	out.println(inputLine);
                	close();
                } else { 
                	out.println(inputLine);
                }
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about this host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to port");
            System.exit(1);
        } 
	}
	
	public synchronized void attachMsg(String s){
		msg.add(s);
	}
	
	public void sendMsg(String userIn) {
		try {
			out.println(userIn);
			in.readLine();
		} catch (Exception e) {
			System.err.println("Error in printing" + e.getMessage());
            System.exit(1);
		}
	}
	
	public void close() throws IOException {  
    	if (sock != null)    sock.close();
    }
}
