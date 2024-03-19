
String s = "ingame-level-circuit\n\r" + 
	"\n\r" + 
	"* Component Block{industria:electro_magnetic_coil} BlockPos{x=3, y=-60, z=0}\n\r" + 
	"\n\r" + 
	"R0GND null 0 1\n\r" + 
	".end\n\r" + 
	"";

System.out.println(s.lines().filter(l -> !l.startsWith("*") && !l.isBlank()).toList());
