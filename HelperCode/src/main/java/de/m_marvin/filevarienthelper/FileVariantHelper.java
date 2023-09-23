package de.m_marvin.filevarienthelper;

import java.io.File;

import de.m_marvin.commandlineparser.CommandLineParser;

public class FileVariantHelper {
	
	public static void main(String[] args) {
		
		CommandLineParser parser = new CommandLineParser();
		parser.addOption("gen_item_list", "", "Generate item list from texture files in the given folder");
		parser.addOption("ignore_file_endings", "", "List of file endings ignored when generating item name list.");
		parser.addOption("gen_file_variants", "", "Generate file variants from variant table list specified in the path");
		parser.addOption("gen_mass_file", "", "Generates a json file for the VS mass configuration");
		parser.addOption("gen_tag_files", "", "Generates tag files from an tag table");
		
		parser.addOption("replace_image_colors", "", "Replaces the colors in the images in the specified folder with the ones specified in the given file");
		parser.addOption("image_folder", "", "Specifies the image folder");
		parser.addOption("help", "", "Show help info");
		
		parser.parseInput(args);
		
		if (parser.getFlag("help")) {
			System.out.println(parser.printHelp());
			return;
		}
		
		if (!parser.getOption("gen_item_list").isEmpty()) {
			System.out.println("Run gen item list ..." + parser.getOption("gen_item_list"));
			MinecraftItems.makeItemList(new File(parser.getOption("gen_item_list")), parser.getOption("ignore_file_endings").split(","));
		} else if (!parser.getOption("gen_file_variants").isEmpty()) {
			System.out.println("Run make file varaints ...");
			MinecraftItems.makeFileVariants(new File(parser.getOption("gen_file_variants")));
		} else if (!parser.getOption("gen_mass_file").isEmpty()) {
			System.out.println("Run gen mass file ...");
			MinecraftItems.makeVSMasses(new File(parser.getOption("gen_mass_file")));
		} else if (!parser.getOption("gen_tag_files").isEmpty()) {
			System.out.println("Run gen tag files ...");
			MinecraftItems.makeTagFiles(new File(parser.getOption("gen_tag_files")));
		} else if (!parser.getOption("replace_image_colors").isEmpty()) {
			System.out.println("Run replace image colors ...");
			Utility.replaceImageColors(new File(parser.getOption("replace_image_colors")), new File(parser.getOption("image_folder")));
		}
		
	}
	
}
