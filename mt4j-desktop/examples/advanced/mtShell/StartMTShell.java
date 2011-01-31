package advanced.mtShell;

import org.mt4j.MTDesktopApplication;


public class StartMTShell extends MTDesktopApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new MTShellScene(this, "Multi-Touch Shell Scene"));
	}
	
}
