package com.nosiphus.furniture.core;

import com.mojang.math.Vector3f;
import com.nosiphus.furniture.NosiphusFurnitureMod;
import com.nosiphus.furniture.fluid.BaseFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {

    public static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
    public static final ResourceLocation SOAP_OVERLAY = new ResourceLocation(NosiphusFurnitureMod.MOD_ID, "block/soapy_water");
    public static final ResourceLocation SOAP_RENDER_OVERLAY = new ResourceLocation(NosiphusFurnitureMod.MOD_ID, "textures/misc/soapy_water.png");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NosiphusFurnitureMod.MOD_ID);

    public static final RegistryObject<FluidType> SOAPY_WATER_FLUID_TYPE = registerSoap("soapy_water",
            FluidType.Properties.create().lightLevel(2).density(15).viscosity(7).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK));

    private static RegistryObject<FluidType> registerSoap(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL, WATER_FLOW, SOAP_OVERLAY, SOAP_RENDER_OVERLAY,
                0xA11FA0FF, new Vector3f(31f / 255f, 160f / 255f, 255f / 255f), properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }

}