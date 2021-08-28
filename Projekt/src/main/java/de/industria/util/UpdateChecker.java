package de.industria.util;

import java.util.Optional;

import de.industria.Industria;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;

public class UpdateChecker {
	
	protected CheckResult result;
	
	public UpdateChecker() {

		Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(Industria.MODID);
		if (modContainer.isPresent()) {
			
			this.result = VersionChecker.getResult(modContainer.get().getModInfo());
			
//			URL updateURL = modContainer.get().getModInfo().getUpdateURL();
//			
//			try {
//				
//				InputStream inputStream = updateURL.openStream();
//				InputStreamReader inputReader = new InputStreamReader(inputStream);
//				BufferedReader reader = new BufferedReader(inputReader);
//				
//				StringBuilder jsonBuilder = new StringBuilder();
//				String line;
//				while ((line = reader.readLine()) != null) {
//					jsonBuilder.append(line).append("\n");
//				}
//				
//				reader.close();
//				
//				String jsonData = jsonBuilder.toString();
//				
//				Gson gson = new Gson();
//				JsonElement json = gson.fromJson(jsonData, JsonElement.class);
//				
//				PlayerEntity player = Minecraft.getInstance().player;
//				
//				player.sendMessage(new StringTextComponent(result.status.name()), player.getUUID());
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
	}
	
	public IFormattableTextComponent getCheckResultText() {
		
		if (this.result == null) return new TranslationTextComponent("industria.updatecheck.failed.info");
		switch (this.result.status) {
		default: return new TranslationTextComponent("industria.updatecheck.failed.info");
		case BETA_OUTDATED: return new TranslationTextComponent("industria.updatecheck.beta_outdated.info");
		case OUTDATED: return new TranslationTextComponent("industria.updatecheck.outdated.info");
		case UP_TO_DATE: return new TranslationTextComponent("industria.updatecheck.up_to_date.info");
		case AHEAD: return new TranslationTextComponent("industria.updatecheck.ahead.info");
		case BETA: return new TranslationTextComponent("industria.updatecheck.beta.info");
		}
		
	}
	
	public String getCheckResultString() {
		return getCheckResultText().getString();
	}
	
}
