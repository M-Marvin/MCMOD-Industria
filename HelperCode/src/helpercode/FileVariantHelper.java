package helpercode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import de.m_marvin.commandlineparser.CommandLineParser;

public class FileVariantHelper {
	
	public static void main(String[] args) {
		
		CommandLineParser parser = new CommandLineParser();
		parser.addOption("gen_item_list", "", "Generate item list from texture files in the given folder");
		parser.addOption("ignore_file_endings", "", "List of file endings ignored when generating item name list.");
		parser.addOption("gen_file_variants", "", "Generate file variants from variant list specified in the path");
		parser.addOption("variant_template", "", "Path to an file used for the file varaints as template");
		
		parser.parseInput(args);
		if (!parser.getOption("gen_item_list").isEmpty()) {
			System.out.println("Run gen item list ..." + parser.getOption("gen_item_list"));
			makeItemList(new File(parser.getOption("gen_item_list")), parser.getOption("ignore_file_endings").split(","));
		} else if (!parser.getOption("gen_file_variants").isEmpty()) {
			System.out.println("Run make file varaints ...");
			makeFileVariants(new File(parser.getOption("gen_file_variants")), new File(parser.getOption("variant_template")));
		}
		
	}
	
	public static void makeFileVariants(File variantList, File variantTemplate) {
		
		try {
			
			String template = "${item_name}";
			if (variantTemplate.isFile()) {
				InputStream is = new FileInputStream(variantTemplate);
				template = new String(is.readAllBytes());
				is.close();
			} else {
				System.err.println("No template file specified!");
			}
			
			if (variantList.isFile()) {
				
				InputStream is = new FileInputStream(variantList);
				String[] variants = new String(is.readAllBytes()).split("\n");
				is.close();
				
				File variantFolder = new File(variantList.getParentFile(), "variants");
				variantFolder.mkdir();
				
				String fileEnding = variantTemplate.getName().split("\\.")[1];
				
				for (String variant : variants) {
					
					String[] columns = variant.replace("\r", "").split("\t");
					
					String variantName = columns[1] + "." + fileEnding;
					String variantContent = template;
					for (int i = 0; i < columns.length; i += 2) {
						String name = columns[i];
						String value = columns[i + 1];
						System.out.println("Map " + name + " -> " + value);
						variantContent = variantContent.replace("${" + name + "}", value);
						if (name.equals("file_name")) variantName = value;
					}
					
					OutputStream os = new FileOutputStream(new File(variantFolder, variantName));
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
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			for (String s : itemNames) writer.write(s + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
