package de.redtec.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaInterpreter {
	
	public static final int MAX_TIMEOUT = 20;
	
	protected String crashMessage;
	protected Thread executionThread;
	protected ILuaThreadViolating violation;
	protected OutputStream outputStream;
	protected boolean isExecuted;
	protected TwoArgFunction[] librarys;
	
	public LuaInterpreter(ILuaThreadViolating violation, OutputStream outputStream, TwoArgFunction... librarys) {
		this.violation = violation;
		this.outputStream = outputStream;
		this.librarys = librarys;
	}
	
	/**
	 * Starts executing of an Script as own Thread
	 * 
	 * @param stringCode The LUA Script
	 */
	public void executeCode(String stringCode) {
		
		try {
			
			Globals globals = JsePlatform.standardGlobals();
			for (TwoArgFunction lib : this.librarys) {
				globals.load(lib);
			}
			globals.STDOUT = new PrintStream(outputStream);
			LuaValue luaCode = globals.load(new StringReader(stringCode), "BOOTDRIVE");
			
			this.executionThread = new Thread("LuaInterpreterExecutionThread") {
				@Override
				public void run() {
					try {
						luaCode.call();
						LuaInterpreter.this.isExecuted = true;
					} catch (ThreadDeath e) {
					} catch (LuaError e) {
						LuaInterpreter.this.isExecuted = true;
						LuaInterpreter.this.crashMessage = e.getMessage();
					}
				}
			};
			
			this.isExecuted = false;
			this.crashMessage = null;
			this.executionThread.start();
			
		} catch (LuaError e) {
			this.crashMessage = e.getMessage();
			stopExecuting();
		}
		
	}
	
	/**
	 * Checks the response of the Script
	 * -1 The Script has crashed
	 * 0 the Script has reached the end 
	 * 1 the Script is running
	 * @return An integer for the response state
	 */
	public int checkExecutationState() {
		if (isCodeRunning()) {
			if (!this.violation.isViolating()) {
				stopExecuting();
			} else if (this.isExecuted) {
				stopExecuting();
				return 0;
			}
			return 1;
		} else {
			return this.crashMessage != null ? -1 : 0;
		}
	}
	
	/**
	 * Checks if a LuaThread is open
	 * @return true if an Thread is open
	 */
	public boolean isCodeRunning() {
		return this.executionThread != null;
	}
	
	/**
	 * Stops the actually executed code an closes the Thread
	 */
	@SuppressWarnings("deprecation")
	public void stopExecuting() {
		if (this.executionThread != null) {
			this.executionThread.interrupt();
			this.executionThread.stop();
		}
		this.executionThread = null;
	}
	
	/**
	 * Get the CrashMessage that was thrown last
	 * @return
	 */
	public String getCrashMessage() {
		return crashMessage;
	}

	public static interface ILuaThreadViolating {
		public boolean isViolating();
	}
	
}
