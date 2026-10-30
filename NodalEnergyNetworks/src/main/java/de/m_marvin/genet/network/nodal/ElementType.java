package de.m_marvin.genet.network.nodal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.m_marvin.tvnlnna.nodal.INodalElement;
import net.minecraft.resources.ResourceLocation;

public class ElementType {
	
	private final ResourceLocation name;
	private final INodalElement definition;
	private final List<String> groundables;
	
	public ElementType(ResourceLocation name, INodalElement definition, List<String> groundables) {
		this.name = name;
		this.definition = definition;
		this.groundables = Collections.unmodifiableList(new ArrayList<String>(groundables));
	}
	
	public ResourceLocation getName() {
		return name;
	}
	
	public INodalElement getDefinition() {
		return definition;
	}
	
	public List<String> getGroundables() {
		return groundables;
	}
	
}
