package de.m_marvin.industria.core.registries;

import org.valkyrienskies.core.api.attachment.AttachmentRegistration;
import org.valkyrienskies.mod.api.ValkyrienSkies;

import de.m_marvin.industria.core.magnetism.engine.MagneticForceInducer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ContraptionAttachments {
	
	public static void register(FMLJavaModLoadingContext modctx) {
		
		register(MagneticForceInducer.class);
		
	}
	
	private static <T> void register(Class<T> attachment) {
		AttachmentRegistration<T> registration = ValkyrienSkies.api()
				.newAttachmentRegistrationBuilder(attachment)
				.build();
		ValkyrienSkies.api().registerAttachment(registration);
	}
	
}
