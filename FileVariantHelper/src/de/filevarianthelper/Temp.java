package de.filevarianthelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Temp {
	
	public static void main(String... args) {
		
		
		new Temp().start();
	}
	
	public void start() {
		
		File input = new File(this.getClass().getResource("").getPath()).getParentFile().getParentFile().getParentFile();
		File output = new File(input, "/Templates/ggg/file/");
		input = new File(input, "/Templates/ggg/");
		System.out.println(input + " -> " + output);
		
		for (File file : input.listFiles()) {
			
			System.out.println(file);
			
			try {
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = "";
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				reader.close();
				
				String json = sb.toString();
				json = json.replace("minecraft:block/slab", "minecraft:block/slab_top");
				
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output, file.getName().replace("slab.json", "slab_top.json")))));
				writer.write(json);
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
