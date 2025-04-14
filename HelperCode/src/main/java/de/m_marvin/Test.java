package de.m_marvin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
	
	public static void main(String[] args) {
		
		File input = new File("oute.mp4");
		File output = new File("outd,mp4");
		
		List<String> cmd = new ArrayList<String>();
		cmd.addAll(Arrays.asList("ffmpeg", "-decryption_key", "76a6c65c5ea762046bd749a2e632ccbB", "-i", input.getAbsolutePath(), output.getAbsolutePath()));
		
		long keyL = 0;
		long keyH = 0;
		
		while (true) {
			
			String key =String.format("%016x%016x", keyL, keyH);
			cmd.set(2, key);
			
			try {
				Process process = new ProcessBuilder(cmd).inheritIO().start();
				int result = process.waitFor();
				
				System.out.println("exit code: " + result);
				
				Thread.sleep(1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
}
