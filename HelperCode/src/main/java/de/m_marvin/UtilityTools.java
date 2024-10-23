package de.m_marvin;

import java.util.Arrays;

import de.m_marvin.filevarianthelper.FileVariantHelper;

public class UtilityTools {
	
	public static void main(String[] args) {
		
		System.out.println("Industria utility tools");
		
		if (args.length > 0) {
			switch (args[0]) {
				case "variants": FileVariantHelper.main(Arrays.copyOfRange(args, 1, args.length)); break;
				default:
					System.err.println("Unknown tool!");
			}
		} else {
			System.out.println("variants -> File variant helper");
			System.out.println("spice -> SPICE debugger");
			System.out.println("electronflow -> ElectronFlow debugger");
		}
		
	}
	
}
