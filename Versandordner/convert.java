import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

static final Pattern unicode = Pattern.compile("\\\\u([0-9A-Fa-f]{4,4})");

public static void main(String[] args) throws IOException {
	
	File inputFile = new File(args[0]);
	File outputFile = new File(args[1]);
	
	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
	OutputStream output = new FileOutputStream(outputFile);
	
	String line;
	while ((line = reader.readLine()) != null) {
		
		Matcher m = unicode.matcher(line);
		String replaced = m.replaceAll(mr -> String.valueOf(Character.valueOf((char) (int) Integer.valueOf(mr.group(1), 16))));
		output.write((replaced + "\n").getBytes());
		
	}
	
	reader.close();
	output.close();
	
}