package com.chocohead.eumj;

import buildcraft.lib.client.guide.GuideManager;
import buildcraft.lib.client.guide.PageLine;
import buildcraft.lib.client.guide.loader.XmlPageLoader;
import buildcraft.lib.client.guide.parts.GuideText;
import buildcraft.lib.gui.GuiStack;
import buildcraft.lib.gui.ISimpleDrawable;
import com.chocohead.eumj.te.Engine_TEs;
import ic2.core.util.StackUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

/** Client Proxy
 *
 * Description: All code that falls under this class
 *              is executed on Client side only
 *
 */
public class ClientProxy extends CommonProxy
{
    public void preInit()
    {
        super.preInit();

        /* Custom CreativeTabs entry/tab
         * Overrides common EUMJ_TAB from CommonProxy */
        EUMJ_TAB = new CreativeTabs("EU-MJ Engine") {
            private ItemStack[] items;
            private int ticker;

            @Override
            public ItemStack getIconItemStack() {
                if (++ticker >= 500) {
                    ticker = 0;
                }

                if (items == null) {
                    items = new ItemStack[5];

                    for (int i = 0; i < Engine_TEs.VALUES.length; i++) {
                        items[i] = engine.getItemStack(Engine_TEs.VALUES[i]);
                    }
                }

                assert ticker / 100 < items.length;
                return items[ticker / 100];
            }

            @Override
            public ItemStack getTabIconItem() {
                return null; //Only normally called from getIconItemStack
            }

            @Override
            public String getTranslatedTabLabel() {
                return EngineMod.MODID + ".creative_tab";
            }
        };

        engine.setCreativeTab(EUMJ_TAB);

        MinecraftForge.EVENT_BUS.register(MagicModelLoader.class);
        readerMJ.registerModels(null);
    }

    public void init()
    {
        super.init();

        /* Load dummyTe for each tile entity */
        Engine_TEs.buildDummies(true);

        /* BuildCraft Lib loads pages in post-init, but it also loads first, so we do this here */
        XmlPageLoader.TAG_FACTORIES.put("engineLink", tag -> {
            ItemStack stack = XmlPageLoader.loadItemStack(tag);

            PageLine line;
            if (StackUtil.isEmpty(stack)) {
                line = new PageLine(1, "Missing item: "+tag, false);
            } else {
                ISimpleDrawable icon = new GuiStack(stack);
                line = new PageLine(icon, icon, 1, stack.getDisplayName(), true);
            }

            return Collections.singletonList(gui -> new GuideText(gui, line) {
                @Override
                public PagePosition handleMouseClick(int x, int y, int width, int height, PagePosition current, int index, int mouseX, int mouseY) {
                    if (line.link && (wasHovered || wasIconHovered)) {
                        gui.openPage(GuideManager.INSTANCE.getPageFor(stack).createNew(gui));
                    }

                    return renderLine(current, text, x, y, width, height, -1);
                }
            });
        });
    }
}
