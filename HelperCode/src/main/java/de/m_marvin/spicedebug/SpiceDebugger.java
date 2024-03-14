package de.m_marvin.spicedebug;

import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.spicedebug.window.MainWindow;

public class SpiceDebugger {
	
	public static void main(String[] args) {
		
		Logger.setDefaultLogger(new Logger());
		
		new SpiceDebugger().start();
		
	}
	
	private SpiceDebugger instance;
	
	protected MainWindow window;
	protected boolean shouldClose = false;
	
	public SpiceDebugger() {
		instance = this;
	}
	
	public SpiceDebugger getInstance() {
		return instance;
	}
	
	public void start() {
		
		this.window = new MainWindow();
		this.shouldClose = false;
		
		this.window.start();
		
		while (!this.shouldClose && this.window.isOpen()) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		this.shouldClose = true;
		this.window.stop();
		
	}
	
	public void stop() {
		this.shouldClose = true;
	}
	
}
