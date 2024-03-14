package de.m_marvin.spicedebug.window;

import java.awt.Color;

import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.flatmono.Window;
import de.m_marvin.openui.flatmono.components.GroupBox;

public class MainWindow extends Window {

	public MainWindow() {
		super("SPICE Debugger");
	}

	@Override
	protected void initUI() {
		
		getRootComponent().setLayout(new BorderLayout());
		
		// Top
		
		GroupBox topBox = new GroupBox(Color.GRAY, Color.DARK_GRAY);
		topBox.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		
		
		
		//topBox.autoSetMinSize();
		getRootComponent().addComponent(topBox);
		
		// Bottom
		
		GroupBox bottomBox = new GroupBox(Color.GRAY, Color.DARK_GRAY);
		bottomBox.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));
		
		//bottomBox.autoSetMaxSize();
		getRootComponent().addComponent(bottomBox);
		
		// Center
		
		GroupBox centerBox = new GroupBox(Color.GRAY, Color.DARK_GRAY);
		centerBox.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		
		//centerBox.autoSetMinSize();
		getRootComponent().addComponent(centerBox);
		
		autoSetMinSize();
		
	}

}
