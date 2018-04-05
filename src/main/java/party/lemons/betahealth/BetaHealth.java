package party.lemons.betahealth;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Sam on 6/04/2018.
 */
@Mod(modid="betahealth", name= "Beta Health", version = "1.0.0")
@Mod.EventBusSubscriber
public class BetaHealth
{
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ForgeRegistries.ITEMS.forEach(i -> {
			i.setMaxStackSize(1);
		});
	}

	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent event)
	{
		if(event instanceof PlayerInteractEvent.LeftClickBlock || event instanceof PlayerInteractEvent.LeftClickEmpty)
			return;

		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();

		if(!stack.isEmpty() && stack.getItem() instanceof ItemFood)
		{
			Item item = stack.getItem();

			if(item instanceof ItemSeedFood)
			{
				if(event.getFace() != null && event.getPos() != null && event.getFace() == EnumFacing.UP)
				{
					Block block = player.world.getBlockState(event.getPos()).getBlock();
					if(block == ((ItemSeedFood)item).soilId)
					{
						return;
					}
				}
			}

			int amt = ((ItemFood)item).getHealAmount(stack);

			eat(amt, player);
			((ItemFood)item).onFoodEaten(stack, player.world, player);
			stack.shrink(1);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		//cake
		Block block = event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock();
		if(block == Blocks.CAKE)
		{
			eat(2, event.getEntityPlayer());
		}
	}

	public static void eat(int amt, EntityPlayer player)
	{
		player.heal(amt);
		player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
	}

	@SubscribeEvent
	public static void setHunger(TickEvent.PlayerTickEvent event)
	{
		event.player.getFoodStats().setFoodLevel(10);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onHud(RenderGameOverlayEvent.Pre event)
	{
		GuiIngameForge.renderFood = false;
	}
}
