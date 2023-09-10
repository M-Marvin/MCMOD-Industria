package de.m_marvin.filevarienthelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.m_marvin.commandlineparser.CommandLineParser;

public class FileVariantHelper {
	
	public static void main(String[] args) {
		
		CommandLineParser parser = new CommandLineParser();
		parser.addOption("gen_item_list", "", "Generate item list from texture files in the given folder");
		parser.addOption("ignore_file_endings", "", "List of file endings ignored when generating item name list.");
		parser.addOption("gen_file_variants", "", "Generate file variants from variant table list specified in the path");
		parser.addOption("gen_mass_file", "", "Generates a json file for the VS mass configuration");
		parser.addOption("gen_tag_files", "", "Generates tag files from an tag table");
		
		parser.parseInput(args);
		if (!parser.getOption("gen_item_list").isEmpty()) {
			System.out.println("Run gen item list ..." + parser.getOption("gen_item_list"));
			makeItemList(new File(parser.getOption("gen_item_list")), parser.getOption("ignore_file_endings").split(","));
		} else if (!parser.getOption("gen_file_variants").isEmpty()) {
			System.out.println("Run make file varaints ...");
			makeFileVariants(new File(parser.getOption("gen_file_variants")));
		} else if (!parser.getOption("gen_mass_file").isEmpty()) {
			System.out.println("Run gen mass file ...");
			makeVSMasses(new File(parser.getOption("gen_mass_file")));
		} else if (!parser.getOption("gen_tag_files").isEmpty()) {
			System.out.println("Run gen tag files ...");
			makeTagFiles(new File(parser.getOption("gen_tag_files")));
		}
		
	}
	
	public static void makeTagFiles(File tagList) {
		
		try {
			
			if (tagList.isFile()) {
				
				InputStream is = new FileInputStream(tagList);
				String[] entries = new String(is.readAllBytes()).split("\n");
				is.close();
				
				File outputFolder = new File(tagList.getParentFile(), "tags");
				outputFolder.mkdir();
				
				Map<String, Set<String>> tag2itemMap = new HashMap<>();
				
				for (String itemEntry : entries) {
					
					String[] columns = itemEntry.replace("\r", "").split("\t");
					String itemName = columns[0];
					
					for (int i = 1; i < columns.length; i++) {
						
						String tag = columns[i];
						
						Set<String> itemSet = tag2itemMap.get(tag);
						if (itemSet == null) itemSet = new HashSet<>();
						itemSet.add(itemName);
						if (!tag2itemMap.containsKey(tag)) tag2itemMap.put(tag, itemSet);
						
					}
					
				}
				
				for (Entry<String, Set<String>> tagEntry : tag2itemMap.entrySet()) {
					
					String tag = tagEntry.getKey();
					String namespace = tag.split(":")[0];
					String path = tag.substring(namespace.length() + 1) + ".json";

					System.out.println("Make tag " + path);
					
					StringBuilder tagBuilder = new StringBuilder();
					
					tagBuilder.append("{\n	\"replace\": false,\n	\"values\": [");
					
					boolean first = true;
					for (String item : tagEntry.getValue()) {
						tagBuilder.append(first ? "\n" : ",\n").append("		\"industria:" + item + "\"");
						first = false;
					}
					
					tagBuilder.append("\n	]\n}");
					
					File tagFile = new File(outputFolder, namespace + "/" + path);
					tagFile.getParentFile().mkdirs();
					OutputStream os = new FileOutputStream(tagFile);
					os.write(tagBuilder.toString().getBytes());
					os.close();
					
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void makeVSMasses(File massList) {
		
		try {
			
			if (massList.isFile()) {
				
				InputStream is = new FileInputStream(massList);
				String[] entries = new String(is.readAllBytes()).split("\n");
				is.close();
				
				StringBuilder massFile = new StringBuilder();
				massFile.append("[");
				
				boolean first = true;
				for (String entry : entries) {
					
					String[] columns = entry.split("\t");
					String blockName = columns[0];
					int mass = Integer.parseInt(columns[1].replace("\r", ""));
					
					if (mass >= 0) {
						System.out.println("Append mass " + mass + " for block " + blockName);
						massFile.append(first ? "\n" : ",\n").append("\t{\n\t\t\"block\": \"industria:").append(blockName).append("\",\n\t\t\"mass\": ").append(mass).append("\n\t}");
						first = false;
					}
					
				}
				
				massFile.append("\n]");
				
				OutputStream os = new FileOutputStream(new File(massList.getParentFile(), "vs_masses.json"));
				os.write(massFile.toString().getBytes());
				os.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void makeFileVariants(File variantList) {
		
		try {
			
			if (variantList.isFile()) {
				
				InputStream is = new FileInputStream(variantList);
				String[] variants = new String(is.readAllBytes()).split("\n");
				is.close();
				
				File variantFolder = new File(variantList.getParentFile(), "variants");
				variantFolder.mkdir();
				
				for (String variant : variants) {
					
					String[] columns = variant.replace("\r", "").split("\t");
					
					String variantName = columns[1];
					String variantContent = "no_template";
					String fileEnding = ".txt";
					
					for (int i = 0; i < columns.length; i += 2) {
						
						String name = columns[i];
						String value = columns[i + 1];
						
						if (name.equals("template")) {
							File templateFile = new File(variantList.getParentFile(), value);
							fileEnding = templateFile.getName().split("\\.")[1];
							if (templateFile.isFile()) {
								InputStream is2 = new FileInputStream(templateFile);
								variantContent = new String(is2.readAllBytes());
								is2.close();
								break;
							} else {
								System.err.println("No template " + value + " found!");
								return;
							}
						}
						
					}
					
					for (int i = 0; i < columns.length; i += 2) {
						
						String name = columns[i];
						String value = columns[i + 1];
						
						if (name.equals("file_name")) variantName = value;
						
						System.out.println("Map " + name + " -> " + value);
						variantContent = variantContent.replace("${" + name + "}", value);
						
					}
					
					OutputStream os = new FileOutputStream(new File(variantFolder, variantName + "." + fileEnding));
					os.write(variantContent.getBytes());
					os.close();
					
				}
				
				System.out.println("Created " + variants.length + " files");
				
			} else {
				System.err.println("No variant list found!");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void makeItemList(File fileFolder, String[] ignoredEndings) {
		
		File outputFile = new File(fileFolder.getParent(), "/item_list.txt");
		Set<String> itemNames = new HashSet<>();
		File[] files = fileFolder.listFiles();
		
		for (File file : files) {
			String fileName = file.getName().split("\\.")[0];
			
			for (String s : ignoredEndings) {
				if (fileName.endsWith(s)) {
					fileName = fileName.substring(0, fileName.length() - s.length());
					break;
				}
			}
			
			itemNames.add(fileName);
		}
		
		System.out.println("Found " + itemNames.size() + " item names in " + files.length + " files");
		
		List<String> itemsSorted = sortItemNames(itemNames);
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			for (String s : itemsSorted) writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<String> sortItemNames(Set<String> itemNames) {
		
		return itemNames
			.stream()
			.map(s -> {
				byte[] b = new byte[s.length()];
				for (int i = 0; i < s.length(); i++) b[b.length - 1 - i] = s.getBytes()[i];
				return new String(b);
			})
			.sorted(String::compareTo)
			.map(s -> {
				byte[] b = new byte[s.length()];
				for (int i = 0; i < s.length(); i++) b[b.length - 1 - i] = s.getBytes()[i];
				return new String(b);
			})
			
			.toList();
		
	}
	
}
