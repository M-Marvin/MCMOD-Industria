package de.m_marvin.languagefileeditor;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.JsonObject;

import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.core.layout.GridLayout;
import de.m_marvin.openui.flatmono.WindowFlatMono;
import de.m_marvin.openui.flatmono.components.ButtonComponent;
import de.m_marvin.openui.flatmono.components.GroupBox;
import de.m_marvin.openui.flatmono.components.LabelComponent;
import de.m_marvin.openui.flatmono.components.TextFieldComponent;
import de.m_marvin.simplelogging.Log;
import de.m_marvin.univec.impl.Vec2i;

public class EditorWindow extends WindowFlatMono {

	protected ButtonComponent openRootFileBtn;
	protected ButtonComponent openLangFileBtn;
	protected ButtonComponent saveLangFilesBtn;
	
	protected GroupBox tableArea;
	
	public EditorWindow() {
		super("Language File Editor");
	}

	@Override
	protected void initUI() {
		
		// Menubar
		GroupBox menu = new GroupBox(Color.DARK_GRAY, Color.DARK_GRAY);
		menu.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		menu.setLayout(new GridLayout());
		menu.getSizeMax().x = 1000;
		
		this.openRootFileBtn = new ButtonComponent("Open Root File");
		this.openRootFileBtn.getSizeMin().x = 150;
		this.openRootFileBtn.setLayoutData(new GridLayout.GridLayoutData(0, 0));
		menu.addComponent(this.openRootFileBtn);

		this.openLangFileBtn = new ButtonComponent("Open Lang File");
		this.openLangFileBtn.getSizeMin().x = 150;
		this.openLangFileBtn.setLayoutData(new GridLayout.GridLayoutData(1, 0));
		menu.addComponent(this.openLangFileBtn);

		this.saveLangFilesBtn = new ButtonComponent("Save Lang Files");
		this.saveLangFilesBtn.getSizeMin().x = 150;
		this.saveLangFilesBtn.setLayoutData(new GridLayout.GridLayoutData(2, 0));
		menu.addComponent(this.saveLangFilesBtn);
		
		menu.autoSetMaxAndMinSize();
		menu.getSizeMax().x = 10000;
		getRootComponent().addComponent(menu);
		
		// Editor Area
		this.tableArea = new GroupBox(Color.BLACK);
		this.tableArea.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		this.tableArea.setLayout(new GridLayout());
		this.tableArea.setSizeMax(new Vec2i(10000, 10000));
		getRootComponent().addComponent(this.tableArea);
		
		getRootComponent().setLayout(new BorderLayout());
		getRootComponent().autoSetMaxAndMinSize();
		autoSetMinAndMaxSize();
		
		this.openRootFileBtn.setAction(() -> {
			requestOpenRootFile();
		});
		
		this.openLangFileBtn.setAction(() -> {
			requestOpenLangFile();
		});
		
	}
	
	protected File rootLangFile = null;
	protected List<File> langFiles = new ArrayList<File>();
	
	protected Map<String, String> rootEntries;
	protected Map<File, Map<String, String>> langEntries = new LinkedHashMap<File, Map<String,String>>();
	
	protected File requestFileSelection(File lastFile) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
		
		if (lastFile == null)
			lastFile = new File(".");
		
		JFileChooser selectionDialog = new JFileChooser(lastFile);
		selectionDialog.setFileFilter(new FileNameExtensionFilter("MC Language File", "json"));
		if (selectionDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return selectionDialog.getSelectedFile();
		}
		return null;
		
	}
	
	public void requestOpenRootFile() {
		File file = requestFileSelection(this.rootLangFile);
		if (file != null)
			openRootFile(file);
	}
	
	public void requestOpenLangFile() {
		File file = requestFileSelection(langFiles.isEmpty() ? null : langFiles.getLast());
		if (file != null)
			openLangFile(file);
	}
	
	public void openRootFile(File file) {
		
		this.rootLangFile = file;
		setWindowName("Language File Editor - " + file.getName());
		
		try {
			JsonObject langJson = LanguageFileEditor.GSON.fromJson(new InputStreamReader(new FileInputStream(file)), JsonObject.class);
			this.rootEntries = new LinkedHashMap<String, String>();
			for (var key : langJson.keySet())
				this.rootEntries.put(key, langJson.get(key).getAsString());
			
			updateTable();
			
		} catch (IOException e) {
			Log.defaultLogger().error("unable to load root lang file: %s", file, e);
		}
	}
	
	public void openLangFile(File file) {
		
		this.langFiles.add(file);
		
		try {
			JsonObject langJson = LanguageFileEditor.GSON.fromJson(new InputStreamReader(new FileInputStream(file)), JsonObject.class);
			Map<String, String> entries = new LinkedHashMap<String, String>();
			for (var key : langJson.keySet())
				entries.put(key, langJson.get(key).getAsString());
			this.langEntries.put(file, entries);
			
			updateTable();
			
		} catch (IOException e) {
			Log.defaultLogger().error("unable to load lang file: %s", file, e);
		}
		
	}
	
	public void updateTable() {
		
		// Remove all old entries
		this.tableArea.clearComponents();

		LabelComponent rootLabel = new LabelComponent(this.rootLangFile.getName(), Color.RED);
		rootLabel.setLayoutData(new GridLayout.GridLayoutData(1, 0));
		this.tableArea.addComponent(rootLabel);

		int col = 2;
		for (var lang : this.langFiles) {
			LabelComponent langLabel = new LabelComponent(lang.getName(), Color.RED);
			langLabel.setLayoutData(new GridLayout.GridLayoutData(col, 0));
			this.tableArea.addComponent(langLabel);
			col++;
		}
		
		int row = 1;
		for (String key : this.rootEntries.keySet()) {
			
			LabelComponent keyLabel = new LabelComponent(key, Color.GREEN);
			keyLabel.setFont(new Font("Consolas", 0, 16));
			keyLabel.setLayoutData(new GridLayout.GridLayoutData(0, row));
			keyLabel.setCenterText(false);
			this.tableArea.addComponent(keyLabel);
			
			TextFieldComponent rootLangField = new TextFieldComponent(Color.WHITE, Color.GRAY);
			rootLangField.setLayoutData(new GridLayout.GridLayoutData(1, row));
			rootLangField.setText(this.rootEntries.getOrDefault(key, ""));
			this.tableArea.addComponent(rootLangField);
			
			col = 2;
			for (var lang : this.langFiles) {

				TextFieldComponent langField = new TextFieldComponent(Color.WHITE, Color.GRAY);
				langField.setLayoutData(new GridLayout.GridLayoutData(col, row));
				langField.setText(this.langEntries.get(lang).getOrDefault(key, ""));;
				this.tableArea.addComponent(langField);
				
				col++;
			}
			
			row++;
		}
		
		autoSetMinSize();
		getRootComponent().updateLayout();
		
	}
	
}
