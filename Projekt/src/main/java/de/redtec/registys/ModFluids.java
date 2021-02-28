
package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.fluids.FluidChemicalWater;
import de.redtec.fluids.FluidDestilledWater;
import de.redtec.fluids.FluidNatronLye;
import de.redtec.fluids.FluidOreAluminiumSolution;
import de.redtec.fluids.FluidOreCopperSolution;
import de.redtec.fluids.FluidOreIronSolution;
import de.redtec.fluids.FluidOreTinSolution;
import de.redtec.fluids.FluidOreWolframSolution;
import de.redtec.fluids.FluidRawOil;
import de.redtec.fluids.FluidSteam;
import de.redtec.fluids.FluidSulfuricAcid;
import de.redtec.fluids.util.BlockGasFluid;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {
	
	public static final FluidDestilledWater FLOWING_DESTILLED_WATER = register("flowing_destilled_water", new FluidDestilledWater.Flow());
	public static final FluidDestilledWater DESTILLED_WATER = register("destilled_water", new FluidDestilledWater.Still());
	public static final FluidSteam STEAM = register("steam", new FluidSteam());
	public static final FluidSulfuricAcid SULFURIC_ACID = register("sulfuric_acid", new FluidSulfuricAcid.Still());
	public static final FluidSulfuricAcid FLOWING_SULFURIC_ACID = register("flowing_sulfuric_acid", new FluidSulfuricAcid.Flow());
	public static final FluidNatronLye NATRON_LYE = register("natron_lye", new FluidNatronLye.Still());
	public static final FluidNatronLye FLOWING_NATRON_LYE = register("flowing_natron_lye", new FluidNatronLye.Flow());
	public static final FluidOreIronSolution FLOWING_IRON_SOLUTION = register("flowing_iron_solution", new FluidOreIronSolution.Flow());
	public static final FluidOreIronSolution IRON_SOLUTION = register("iron_solution", new FluidOreIronSolution.Still());
	public static final FluidOreCopperSolution FLOWING_COPPER_SOLUTION = register("flowing_copper_solution", new FluidOreCopperSolution.Flow());
	public static final FluidOreCopperSolution COPPER_SOLUTION = register("copper_solution", new FluidOreCopperSolution.Still());
	public static final FluidOreAluminiumSolution FLOWING_ALUMINIUM_SOLUTION = register("flowing_aluminium_solution", new FluidOreAluminiumSolution.Flow());
	public static final FluidOreAluminiumSolution ALUMINIUM_SOLUTION = register("aluminium_solution", new FluidOreAluminiumSolution.Still());
	public static final FluidOreWolframSolution FLOWING_WOLFRAM_SOLUTION = register("flowing_wolfram_solution", new FluidOreWolframSolution.Flow());
	public static final FluidOreWolframSolution WOLFRAM_SOLUTION = register("wolfram_solution", new FluidOreWolframSolution.Still());
	public static final FluidOreTinSolution FLOWING_TIN_SOLUTION = register("flowing_tin_solution", new FluidOreTinSolution.Flow());
	public static final FluidOreTinSolution TIN_SOLUTION = register("tin_solution", new FluidOreTinSolution.Still());
	public static final FluidChemicalWater FLOWING_CHEMICAL_WATER = register("flowing_chemical_water", new FluidChemicalWater.Flow());
	public static final FluidChemicalWater CHEMICAL_WATER = register("chemical_water", new FluidChemicalWater.Still());
	public static final FluidRawOil FLOWING_RAW_OIL = register("flowing_raw_oil", new FluidRawOil.Flow());
	public static final FluidRawOil RAW_OIL = register("raw_oil", new FluidRawOil.Still());
	
	private static <T extends Fluid> T register(String key, T p_215710_1_) {
		p_215710_1_.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.FLUIDS.register(p_215710_1_);
		return p_215710_1_;
	}
	
	public static boolean isFluidBlock(Block block) {
		return block instanceof FlowingFluidBlock || block instanceof BlockGasFluid;
	}
	
}
