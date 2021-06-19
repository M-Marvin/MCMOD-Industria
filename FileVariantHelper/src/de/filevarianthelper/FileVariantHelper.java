package de.filevarianthelper;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FileVariantHelper extends JFrame {
	
	private static final long serialVersionUID = 7584388304358038238L;
	
	protected JTextArea variableTabel;
	protected JButton generateButton;
	protected JButton openTemplateButton;
	
	protected File selecetedFile;
	
	public static void main(String... args) {
		
		new Temp().start();
		//new FileVariantHelper().start();
		
	}
	
	public void start() {
		
		this.setTitle("FileVariantHelper");
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		this.variableTabel = new JTextArea();
		this.variableTabel.setText("%$1\ntemplate");
		panel.add(variableTabel);
		this.variableTabel.setBounds(10, 10, 300, 300);
		
		this.generateButton = new JButton("Erzeugen");
		this.generateButton.addActionListener(this::onGenerateClicket);
		panel.add(generateButton);
		this.generateButton.setBounds(10, 320, 140, 30);

		this.openTemplateButton = new JButton("Vorlage ...");
		this.openTemplateButton.addActionListener(this::onOpenTemplateClicked);
		panel.add(openTemplateButton);
		this.openTemplateButton.setBounds(170, 320, 140, 30);
		
		this.add(panel);
		this.setSize(340, 400);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	public void onOpenTemplateClicked(ActionEvent e) {
		
		System.out.println("Open template ...");
		
		File templateFile = new File(this.getClass().getResource("").getPath());
		
		JFileChooser dialog = new JFileChooser("Vorlage wählen");
		dialog.setDialogType(JFileChooser.OPEN_DIALOG);
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dialog.setCurrentDirectory(templateFile);
		
		int result = dialog.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			this.selecetedFile = dialog.getSelectedFile();
			
			File variableFile = new File(this.selecetedFile.getParent(), "/" + this.selecetedFile.getName() + "_variables.txt");
			if (variableFile.exists()) {
				this.variableTabel.setText("");
				try {
					BufferedReader reader = new BufferedReader(new FileReader(variableFile));
					String line = "";
					while ((line = reader.readLine()) != null) {
						this.variableTabel.setText(variableTabel.getText() + line + "\n");
					}
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	public void onGenerateClicket(ActionEvent e) {
		
		if (this.selecetedFile != null) {
			
			System.out.println("Save template variabels");
			
			try {
				String variabelText = this.variableTabel.getText();
				BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(this.selecetedFile.getParent(), "/" + this.selecetedFile.getName() + "_variables.txt")));
				writer1.write(variabelText);
				writer1.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			System.out.println("Generate ...");
			
			//Load variable values
			HashMap<Integer, String[]> variants = new HashMap<Integer, String[]>();
			
			Integer currentId = -1;
			List<String> entrys = new ArrayList<String>();
			for (String line : this.variableTabel.getText().split("\n")) {
				if (line.startsWith("%$")) {
					int variableId = Integer.parseInt(line.substring(2));
					if (currentId != -1) {
						variants.put(currentId, entrys.toArray(new String[entrys.size()]));
					}
					currentId = variableId;
					entrys.clear();
				} else {
					entrys.add(line);
				}
			}
			variants.put(currentId, entrys.toArray(new String[entrys.size()]));
			
			//Get output folder
			File outputFolder = new File(this.selecetedFile.getParentFile(), "/Files");
			outputFolder.mkdir();
			System.out.println("Save files in " + outputFolder);
			
			//Write files
			try {
				
				int[] variableMask = new int[variants.keySet().size()];		
				int lastVariable = variableMask.length;
				int lastEntryCount = variants.get(lastVariable).length;
				
				while (variableMask[lastVariable - 1] < lastEntryCount) {
					
					String fileName = solveVariables(this.selecetedFile.getName(), variableMask, variants);
					
					System.out.println("Write " + fileName);
					
					BufferedReader reader = new BufferedReader(new FileReader(this.selecetedFile));
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFolder, fileName)));
					
					String line;
					while ((line = reader.readLine()) != null) {
						String newLine = solveVariables(line, variableMask, variants);
						writer.write(newLine + "\n");
					}
					
					reader.close();
					writer.close();
					
					incraseVariableMask(variableMask, variants);
					
				}
				
			} catch (FileNotFoundException e1) {
				System.out.println("Template " + this.selecetedFile + " not found!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		System.out.println("Done");
		
	}
	
	protected String solveVariables(String keyString, int[] variantMask, HashMap<Integer, String[]> variables) {
		String[] stringKeys = keyString.split("\\%\\$");
		String newString = stringKeys[0];
		for (int i = 1; i < stringKeys.length; i++) {
			Integer id = Integer.parseInt(stringKeys[i].substring(0, 1));
			String part = stringKeys[i].substring(1);
			String[] keys = variables.get(id);
			newString += keys[variantMask[id - 1]] + part;
		}
		return newString;
	}
	
	protected void incraseVariableMask(int[] variantMask, HashMap<Integer, String[]> variables) {
		incrase0(variantMask, 1, variables);
	}
	
	private void incrase0(int[] list, int id, HashMap<Integer, String[]> variables) {
		list[id - 1]++;
		if (list[id - 1] >= variables.get(id).length && id < list.length) {
			list[id - 1] = 0;
			incrase0(list, id + 1, variables);
		}
	}
	
}
