package de.m_marvin.languagefileeditor;

import com.google.gson.Gson;

import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.simplelogging.Log;
import de.m_marvin.simplelogging.impl.SynchronizedLogger;

public class LanguageFileEditor {
	
	public static EditorWindow editorWindow = null;
	
	public static final Gson GSON = new Gson();
	
	public static void main(String[] args) {
		
//		Log.setDefaultLogger(new SynchronizedLogger(Log));
		
		GLFWStateManager.initialize(System.out);
		
		Log.defaultLogger().info("Language File Editor Startup");
		
		editorWindow = new EditorWindow();
		editorWindow.start();
		
		while (editorWindow.isOpen()) {
			GLFWStateManager.update();
		}
		
		Log.defaultLogger().info("Language File Editor Exit");
		
		GLFWStateManager.terminate();
		System.exit(0);
		
	}
	
}
