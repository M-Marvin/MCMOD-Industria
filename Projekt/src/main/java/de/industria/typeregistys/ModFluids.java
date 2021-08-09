
package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.fluids.FluidBiogas;
import de.industria.fluids.FluidChemicalWater;
import de.industria.fluids.FluidCompressedAir;
import de.industria.fluids.FluidDestilledWater;
import de.industria.fluids.FluidFuelGas;
import de.industria.fluids.FluidHydrofluoricAcid;
import de.industria.fluids.FluidLiquidConcrete;
import de.industria.fluids.FluidNatronLye;
import de.industria.fluids.FluidOreAluminiumSolution;
import de.industria.fluids.FluidOreCopperSolution;
import de.industria.fluids.FluidOreIronSolution;
import de.industria.fluids.FluidOreTinSolution;
import de.industria.fluids.FluidOreVanadiumSolution;
import de.industria.fluids.FluidOreWolframSolution;
import de.industria.fluids.FluidRawOil;
import de.industria.fluids.FluidSteam;
import de.industria.fluids.FluidSulfuricAcid;
import de.industria.fluids.FluidTar;
import de.industria.fluids.util.BlockGasFluid;
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
	public static final FluidHydrofluoricAcid HYDROFLUORIC_ACID = register("hydrofluoric_acid", new FluidHydrofluoricAcid.Still());
	public static final FluidHydrofluoricAcid FLOWING_HYDROFLUORIC_ACID = register("flowing_hydrofluoric_acid", new FluidHydrofluoricAcid.Flow());
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
	public static final FluidOreVanadiumSolution FLOWING_VANADIUM_SOLUTION = register("flowing_vanadium_solution", new FluidOreVanadiumSolution.Flow());
	public static final FluidOreVanadiumSolution VANADIUM_SOLUTION = register("vanadium_solution", new FluidOreVanadiumSolution.Still());
	public static final FluidChemicalWater FLOWING_CHEMICAL_WATER = register("flowing_chemical_water", new FluidChemicalWater.Flow());
	public static final FluidChemicalWater CHEMICAL_WATER = register("chemical_water", new FluidChemicalWater.Still());
	public static final FluidRawOil FLOWING_RAW_OIL = register("flowing_raw_oil", new FluidRawOil.Flow());
	public static final FluidRawOil RAW_OIL = register("raw_oil", new FluidRawOil.Still());
	public static final FluidCompressedAir COMPRESSED_AIR = register("compressed_air", new FluidCompressedAir());
	public static final FluidLiquidConcrete FLOWING_LIQUID_CONCRETE = register("flowing_liquid_concrete", new FluidLiquidConcrete.Flow());
	public static final FluidLiquidConcrete LIQUID_CONCRETE = register("liquid_concrete", new FluidLiquidConcrete.Still());
	public static final FluidBiogas BIOGAS = register("biogas", new FluidBiogas());
	public static final FluidFuelGas FUEL_GAS = register("fuel_gas", new FluidFuelGas());
	public static final FluidTar FLOWING_TAR = register("flowing_tar", new FluidTar.Flow());
	public static final FluidTar TAR = register("tar", new FluidTar.Still());
	
	private static <T extends Fluid> T register(String key, T p_215710_1_) {
		p_215710_1_.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.FLUIDS.register(p_215710_1_);
		return p_215710_1_;
	}
	
	public static boolean isFluidBlock(Block block) {
		return block instanceof FlowingFluidBlock || block instanceof BlockGasFluid;
	}
	
}
