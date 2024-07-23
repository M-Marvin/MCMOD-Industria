package de.m_marvin;

import java.util.Arrays;

import de.m_marvin.electronflowdebug.ElectronFlowDebug;
import de.m_marvin.filevarianthelper.FileVariantHelper;
import de.m_marvin.spicedebug.SpiceDebugger;

public class UtilityTools {
	
	public static void main(String[] args) {
		
		System.out.println("Industria utility tools");
		
		if (args.length > 0) {
			switch (args[0]) {
				case "variants": FileVariantHelper.main(Arrays.copyOfRange(args, 1, args.length)); break;
				case "spice": SpiceDebugger.main(Arrays.copyOfRange(args, 1, args.length)); break;
				case "electronflow": ElectronFlowDebug.main(Arrays.copyOfRange(args,  1, args.length)); break;
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
