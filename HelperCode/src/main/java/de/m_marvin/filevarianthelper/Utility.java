package de.m_marvin.filevarianthelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class Utility {
	
	public static void replaceImageColors(File colorReplaceList, File imageFolder) {
		
		if (colorReplaceList.isFile() && imageFolder.isDirectory()) {
			
			try {

				InputStream is = new FileInputStream(colorReplaceList);
				Map<Integer, Integer> colorMap = new HashMap<>();
				Stream.of(new String(is.readAllBytes()).split("\n")).map(s -> s.replace("\r", "").split("\t")).forEach(s -> colorMap.put((int) Long.parseLong(s[0], 16), (int) Long.parseLong(s[1], 16)));
				is.close();
				
				File outputFolder = new File(imageFolder.getParentFile(), "image_output");
				outputFolder.mkdir();
				
				System.out.println("Found " + colorMap.size() + " colors");
				
				for (File imageFile : imageFolder.listFiles()) {
					
					System.out.println("processing " + imageFile.getName() + " ...");
					BufferedImage image = ImageIO.read(imageFile);
					int colorsReplaced = 0;
					int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
					
					for (int i = 0; i < colors.length; i++) {
						if (colorMap.containsKey(colors[i])) {
							colors[i] = colorMap.get(colors[i]);
							colorsReplaced++;
						}
					}
					
					BufferedImage image2 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
					image2.setRGB(0, 0, image2.getWidth(), image2.getHeight(), colors, 0, image2.getWidth());
					ImageIO.write(image2, "png", new File(outputFolder, imageFile.getName()));
					
					System.out.println("Colors replaced: " + colorsReplaced);
					
				}
				
				System.out.println("Processed " + imageFolder.listFiles().length + " images");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
}
