package com.chocohead.eumj;

import static com.chocohead.eumj.EngineMod.MODID;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import buildcraft.api.mj.MjAPI;

import ic2.api.event.TeBlockFinalCallEvent;

@Mod
(
	modid 		 = EngineMod.MODID,
	name		 = EngineMod.MODNAME,
	dependencies = EngineMod.DEPENDENCIES,
	version		 = EngineMod.VERSION
)

public final class EngineMod {

	/* Meta Data */
	public static final String MODID 		= "eu-mj_engine";
	public static final String MODNAME 		= "EU-MJ Engine";
	public static final String DEPENDENCIES = "required-after:ic2;required-after:buildcraftenergy@[7.99.7];after:buildcrafttransport";
	public static final String VERSION 		= "@VERSION@";

	/* Proxy */
	@SidedProxy(clientSide = "com.chocohead.eumj.ClientProxy", serverSide = "com.chocohead.eumj.ServerProxy")
	public static CommonProxy proxy;

	/** Initialization Events **/

	@EventHandler
	public void construction(FMLConstructionEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void register(TeBlockFinalCallEvent event) {
		proxy.register();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		loadConfig(event.getSuggestedConfigurationFile());
		event.getModLog().info("Running with "+Conversion.MJperEU / MjAPI.MJ+" MJ per EU or "+MjAPI.MJ / Conversion.MJperEU+" EU per MJ");

		//Blocks
		engine = TeBlockRegistry.get(Engine_TEs.IDENTITY);
		engine.setCreativeTab(TAB);
		//Items
		if (BCModules.TRANSPORT.isLoaded()) {
			readerMJ = new ItemReaderMJ();
		}

		if (event.getSide().isClient()) {
			if (readerMJ != null) readerMJ.registerModels(null);
		}
	}

	/* Import/Load and evaluate Configuration File */
	private void loadConfig(File file) {
		Configuration config = new Configuration(file);

		try {
			config.load();

			Conversion.MJperEU = MjAPI.MJ * config.getFloat("MJperEU", "balance", 2F / 5F, 1F / 100, 100F, "The number of MJ per EU");
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception loading config!", e);
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}
}
