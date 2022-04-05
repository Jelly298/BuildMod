package me;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;

public class Utils {
    public static boolean hasPlayerNearby()
    {
        for(Entity entity : Minecraft.getMinecraft().theWorld.getLoadedEntityList()){
            if(entity instanceof EntityPlayer && !entity.equals(Minecraft.getMinecraft().thePlayer)){
                if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) < 0.5f)
                    return true;
            }
        }
        return false;
    }

    public static boolean hasSoulSandInInv(){

        for(Slot slot : Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots) {
            if (slot != null) {
                try {
                    if (slot.getStack().getItem().equals(Item.getItemFromBlock(Blocks.soul_sand))) {
                        return true;
                    }
                }catch(Exception e){

                }
            }
        }
        return false;
    }
    public static boolean hasNetherwartInInv(){
        for(Slot slot : Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots) {
            if (slot != null) {
                try {
                    if (slot.getStack().getItem().equals(Items.nether_wart))
                        return true;
                }catch(Exception e){

                }
            }
        }
        return false;
    }
    public static boolean isContainerFull(){
        for(int i  = 0; i < 54; i ++) {
            try {
                if (Minecraft.getMinecraft().thePlayer.openContainer.inventorySlots.get(i).getStack() == null)
                    return false;
            }catch (Exception e){

            }
        }
        return true;

    }
    public static int getFirstSlotWithSoulSand() {
        for (Slot slot : Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots) {
            if (slot != null) {
                if (slot.getStack() != null) {
                    try {
                        if (slot.getStack().getItem().equals(Item.getItemFromBlock(Blocks.soul_sand)))
                            return slot.slotNumber;
                    }catch(Exception e){

                    }
                }
            }

        }
        return 0;

    }
    public static int getFirstSlotWithNetherwart() {
        for (Slot slot : Minecraft.getMinecraft().thePlayer.inventoryContainer.inventorySlots) {
            if (slot != null) {
                if (slot.getStack() != null) {
                    try {
                        if (slot.getStack().getItem().equals(Items.nether_wart))
                            return slot.slotNumber;
                    }catch(Exception e){

                    }
                }
            }

        }
        return 0;

    }

    public static void hardRotate(float yaw) {
        Minecraft mc = Minecraft.getMinecraft();
        if(Math.abs(mc.thePlayer.rotationYaw - yaw) < 0.2f) {
            mc.thePlayer.rotationYaw = yaw;
            return;
        }
        while(mc.thePlayer.rotationYaw > yaw) {
            mc.thePlayer.rotationYaw -= 0.1f;
        }
        while(mc.thePlayer.rotationYaw < yaw) {
            mc.thePlayer.rotationYaw += 0.1f;

        }
    }

}
