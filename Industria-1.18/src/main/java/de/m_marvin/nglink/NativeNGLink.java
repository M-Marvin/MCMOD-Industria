package de.m_marvin.nglink;

public class NativeNGLink {
	
//	public native int initNGLink(); // TODO
//	
//	public void callbackLogFunc() {
//		
//	}
//	
//	public void callbackLog
	
	public static record NGComplex(double real, double imag) {};
	public static record VectorInfo(String name, int type, short flags, double[] realdata, NGComplex complexdata, int length) {};
	
	public native int initNGSPice(String libName);
	public native int detachNGSpice();
	public native int execCommand(String command);
	public native int execList(String commandList);
	public native boolean isRunning();
	public native String[] listPlots();
	public native String getCurrentPlot();
	public native String[] getVecs(String plotName);
	public native VectorInfo getVec(String vecName);
	
}

