package me;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;

import java.util.Random;

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

    public static void hardRotateTo(float yaw) {
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

    public static float get360RotationYaw(){
        return Minecraft.getMinecraft().thePlayer.rotationYaw > 0?
                (Minecraft.getMinecraft().thePlayer.rotationYaw % 360) :
                (Minecraft.getMinecraft().thePlayer.rotationYaw < 360f ? 360 - (-Minecraft.getMinecraft().thePlayer.rotationYaw % 360)  :  360 + Minecraft.getMinecraft().thePlayer.rotationYaw);
    }

    public static void smoothRotateTo(final int rotation360, final boolean clockwise){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (get360RotationYaw() != rotation360) {
                    if(Math.abs(get360RotationYaw() - rotation360) < 1f) {
                        if(clockwise)
                            Minecraft.getMinecraft().thePlayer.rotationYaw = Math.round(Minecraft.getMinecraft().thePlayer.rotationYaw + Math.abs(get360RotationYaw() - rotation360));
                        else
                            Minecraft.getMinecraft().thePlayer.rotationYaw = Math.round(Minecraft.getMinecraft().thePlayer.rotationYaw - Math.abs(get360RotationYaw() - rotation360));
                        return;
                    }
                    if(clockwise)
                        Minecraft.getMinecraft().thePlayer.rotationYaw += 0.2f + nextInt(3)/10.0f;
                    else
                        Minecraft.getMinecraft().thePlayer.rotationYaw -= 0.2f + nextInt(3)/10.0f;

                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    public static void smoothRotateClockwise(final int rotationClockwise360){
        new Thread(new Runnable() {
            @Override
            public void run() {

                int targetYaw = (Math.round(get360RotationYaw()) + rotationClockwise360) % 360;
                while (get360RotationYaw() != targetYaw) {
                    if(Math.abs(get360RotationYaw() - targetYaw) < 1f) {
                        Minecraft.getMinecraft().thePlayer.rotationYaw = Math.round(Minecraft.getMinecraft().thePlayer.rotationYaw + Math.abs(get360RotationYaw() - targetYaw));
                        return;
                    }
                    Minecraft.getMinecraft().thePlayer.rotationYaw += 0.2f + nextInt(3)/10.0f;
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }
    public static int nextInt(int upperbound){
        Random r = new Random();
        return r.nextInt(upperbound);
    }
    public static float getActualRotationYaw(){ //f3
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer.rotationYaw > 0?
                (mc.thePlayer.rotationYaw % 360 > 180 ? -(180 - (mc.thePlayer.rotationYaw % 360 - 180)) :  mc.thePlayer.rotationYaw % 360  ) :
                (-mc.thePlayer.rotationYaw % 360 > 180 ? (180 - (-mc.thePlayer.rotationYaw % 360 - 180))  :  -(-mc.thePlayer.rotationYaw % 360));
    }


}
