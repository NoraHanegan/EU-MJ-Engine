package com.chocohead.eumj;

import buildcraft.api.BCBlocks;
import buildcraft.api.BCItems;
import buildcraft.api.blocks.CustomRotationHelper;
import buildcraft.api.enums.EnumEngineType;
import buildcraft.transport.BCTransportItems;
import com.chocohead.eumj.item.ItemReaderMJ;
import com.chocohead.eumj.te.Engine_TEs;
import com.chocohead.eumj.te.TileEntityEngine;
import com.chocohead.eumj.util.AdvEngineRecipe;
import ic2.api.item.IC2Items;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TeBlockRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.Collection;
import java.util.HashSet;

public class CommonProxy
{
    public static BlockTileEntity engine;
    public static ItemReaderMJ readerMJ;

    public static CreativeTabs EUMJ_TAB;

    public static void register()
    {
        TeBlockRegistry.addAll(Engine_TEs.class, Engine_TEs.IDENTITY);
    }

    public void preInit()
    {
        /* Initialize Blocks */
        engine = TeBlockRegistry.get(Engine_TEs.IDENTITY);

        /* Initialize Items */
        if (Loader.isModLoaded("buildcrafttransport")) {
            readerMJ = new ItemReaderMJ();
        }

        /* Initialize Recipes (Deprecated as of 1.12.1) */
        RecipeSorter.register(EngineMod.MODID + ":shaped", AdvEngineRecipe.class, RecipeSorter.Category.SHAPED, "after:ic2:shaped");
    }

    public void init()
    {
        if (BCBlocks.CORE_ENGINE != null) {
            GameRegistry.addRecipe(new AdvEngineRecipe(engine.getItemStack(Engine_TEs.slow_electric_engine),
                    "B", "E", "C",
                    'B', IC2Items.getItem("re_battery"),
                    'E', new ItemStack(BCBlocks.CORE_ENGINE, 1, EnumEngineType.STONE.ordinal()),
                    'C', IC2Items.getItem("crafting", "circuit")));

            GameRegistry.addRecipe(new AdvEngineRecipe(engine.getItemStack(Engine_TEs.regular_electric_engine),
                    "B", "E", "C",
                    'B', IC2Items.getItem("re_battery"),
                    'E', new ItemStack(BCBlocks.CORE_ENGINE, 1, EnumEngineType.IRON.ordinal()),
                    'C', IC2Items.getItem("crafting", "circuit")));

            GameRegistry.addRecipe(new AdvEngineRecipe(engine.getItemStack(Engine_TEs.fast_electric_engine),
                    "BBB", "EPE", "CPC",
                    'B', IC2Items.getItem("advanced_re_battery"),
                    'E', new ItemStack(BCBlocks.CORE_ENGINE, 1, EnumEngineType.IRON.ordinal()),
                    'P', IC2Items.getItem("crafting", "alloy"),
                    'C', IC2Items.getItem("crafting", "circuit")));

            GameRegistry.addRecipe(new AdvEngineRecipe(engine.getItemStack(Engine_TEs.quick_electric_engine),
                    "BPB", "EEE", "CPC",
                    'B', IC2Items.getItem("energy_crystal"),
                    'E', new ItemStack(BCBlocks.CORE_ENGINE, 1, EnumEngineType.IRON.ordinal()),
                    'P', IC2Items.getItem("crafting", "alloy"),
                    'C', IC2Items.getItem("crafting", "advanced_circuit")));

            GameRegistry.addRecipe(new AdvEngineRecipe(engine.getItemStack(Engine_TEs.adjustable_electric_engine),
                    "BCB", "EEE", "MTM",
                    'B', IC2Items.getItem("lapotron_crystal"),
                    'E', new ItemStack(BCBlocks.CORE_ENGINE, 1, EnumEngineType.IRON.ordinal()),
                    'C', IC2Items.getItem("crafting", "advanced_circuit"),
                    'M', IC2Items.getItem("resource", "advanced_machine"),
                    'T', IC2Items.getItem("te", "hv_transformer")));
        }

        if (readerMJ != null && BCItems.CORE_GEAR_GOLD != null && BCItems.TRANSPORT_PIPE_WOOD_POWER != null) {
            Collection<ItemStack> pipes = new HashSet<>();

            for (Item pipe : new Item[] {BCTransportItems.pipePowerCobble, BCTransportItems.pipePowerStone,
                    BCTransportItems.pipePowerQuartz, BCTransportItems.pipePowerGold, BCTransportItems.pipePowerSandstone}) {
                if (pipe != null) {
                    pipes.add(new ItemStack(pipe));
                }
            }

            if (!pipes.isEmpty()) {
                GameRegistry.addRecipe(new AdvEngineRecipe(new ItemStack(readerMJ),
                        " D ", "PGP", "p p",
                        'D', Items.GLOWSTONE_DUST,
                        'G', BCItems.CORE_GEAR_GOLD,
                        'P', pipes,
                        'p', BCItems.TRANSPORT_PIPE_WOOD_POWER));
            }
        }
    }

    public static void postInit()
    {
        CustomRotationHelper.INSTANCE.registerHandler(engine, (world, pos, state, side) -> {
            TileEntity te = world.getTileEntity(pos);
            return te instanceof TileEntityEngine && ((TileEntityEngine) te).trySpin(side.getOpposite()) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        });
    }
}
