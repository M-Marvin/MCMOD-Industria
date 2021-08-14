package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.packet.CCRecipeCreator;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScreenRecipeCreator extends ContainerScreen<ContainerRecipeCreator> {

public static final ResourceLocation WORKBENCH_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/recipe_creator.png");
	
	protected Button createRecipeBtn;
	protected Button shapelessBtn;
	protected boolean shapeless;
	
	public ScreenRecipeCreator(ContainerRecipeCreator screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	      this.titleLabelX = 88;
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(WORKBENCH_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
	}
	
	@Override
	protected void init() {
		super.init();
		
    	int i = this.width / 2 - 100;
    	int j = this.height / 2 - 128;
    	
		this.createRecipeBtn = this.addButton(new Button(i + 140, j + 106, 40, 20, new StringTextComponent("Create"), this::createRecipe));
		this.shapelessBtn = this.addButton(new Button(i + 98, j + 106, 40, 20, new StringTextComponent(this.shapeless ? "Shapeless" : "Shaped"), this::toggleShapeless));
		
	}
	
	protected void createRecipe(Button button) {
		Industria.NETWORK.sendToServer(new CCRecipeCreator(this.menu.pos, this.shapeless, this.menu.inventory));
	}
	
	protected void toggleShapeless(Button button) {
		this.shapeless = !this.shapeless;
		button.setMessage(new StringTextComponent(this.shapeless ? "Shplss" : "Shaped"));
	}
	
}