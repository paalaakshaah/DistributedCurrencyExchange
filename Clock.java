
public class Clock extends Thread{

	public int clk = 0;
	public boolean interrupt = true;
	public int tick = -1; 
	
	public Clock(int t) {
		this.tick = t;
	}
	
	public void run() {
		while(interrupt){
			try { 
				Thread.sleep(1000/tick); 
				clk++;
				//System.out.println("clock: " + clk);
			} catch (InterruptedException e) {  
				e.printStackTrace(); 
			} 
		
		}
		
	}
	
	public void setClk(int cNew) {
		if(cNew >= clk) {
			clk = cNew+1;
		}
	}
	
}
