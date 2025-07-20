package de.m_marvin.colors;

import java.io.File;

import de.m_marvin.animator.CommandLineParser;
import de.m_marvin.univec.impl.Vec2i;

public class ColorCopier {
	
	public static void main(String... args) {

		CommandLineParser parser = new CommandLineParser();
		parser.addOption("gen_random", "", "");
		
		parser.addOption("width", "16", "");
		parser.addOption("height", "16", "");
		parser.addOption("image_folder", "", "");
		parser.addOption("help", false, "Show help info");
		
		parser.parseInput(args);
		
		if (parser.getFlag("help")) {
			System.out.println(parser.printHelp());
			return;
		}
		
		if (!parser.getOption("gen_random").isEmpty()) {
			System.out.println("Run gen random list ..." + parser.getOption("gen_item_list"));
			genRandom(new File(parser.getOption("image_folder")), new Vec2i(Integer.parseInt(parser.getOption("width")), Integer.parseInt(parser.getOption("height"))));
		}
		
		File palette = new File(args[0]);
		File imageIn = new File(args[1]);
		
		
		
	}
	
}
