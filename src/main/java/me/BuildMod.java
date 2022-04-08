package me;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mod(modid = BuildMod.MODID, version = BuildMod.VERSION)
public class BuildMod {
    Minecraft mc = Minecraft.getMinecraft();
    public static final String MODID = "keystroke";
    public static final String VERSION = "1.0";

    boolean enabled;
    volatile boolean process1 = false;
    volatile boolean process2 = false;
    volatile boolean process3 = false;
    volatile boolean process4 = false;
    volatile boolean process5 = false;
    boolean dig1 = false;
    boolean lockdig1 = false;
    boolean dig2 = false;
    boolean dig3 = false;
    boolean lockdig3 = false;
    boolean dig4 = false;
    boolean startedSlowDig = false;
    boolean slowDig = false;
    boolean slowDig2 = false;
    boolean placeBlock1 = false;
    boolean placeBlock2 = false;
    boolean placeSoulSandBlock1 = false;
    boolean placeSoulSandBlock2 = false;
    boolean initializingPath = false;
    boolean noSoulSand = false;
    boolean refilling = false;
    boolean setZ = false;
    boolean setX = false;
    boolean moved = false;
    boolean changedX = false;
    boolean firstBlock = false; //place soul sand 2 first block
    boolean reached = false; // reached place soul sand 1 end
    boolean placedFirstBlock = false; // placed soul sand 2 first block
    boolean positiveX = false; // Place soul sand 1 whether player posX is +ve
    boolean positiveZ = false;
    boolean leftSide = false; // whether player ended digging on left side
    boolean sameSide = false;
    boolean placeNWGoingLeft = false;
    boolean placingNetherwart = false;
    boolean buyingNetherwart = false;
    boolean startedBuyingNetherwart = false;
    static boolean rotated = false;

    int corner1x = 0;
    int corner1y = 0;
    int corner1z = 0;
    int corner2x = 0;
    int corner2y = 0;
    int corner2z = 0;
    int originalZ = 0;
    int originalX = 0;
    int lengthX = 0;
    int lengthZ = 0;
    int digAngle = 0;


    MouseHelper mouseHelper = new MouseHelper();

    String process = "";

    Block blockunderfeet;
    Block blockin;
    int setmode = 0;

    int keyBindForward = mc.gameSettings.keyBindForward.getKeyCode();
    int keyBindBackward = mc.gameSettings.keyBindBack.getKeyCode();
    int keyBindRight = mc.gameSettings.keyBindRight.getKeyCode();
    int keyBindLeft = mc.gameSettings.keyBindLeft.getKeyCode();
    int keyBindUseItem = mc.gameSettings.keyBindUseItem.getKeyCode();
    int keyBindAttack = mc.gameSettings.keyBindAttack.getKeyCode();
    int keyBindSneak = mc.gameSettings.keyBindSneak.getKeyCode();
    int keyBindJump = mc.gameSettings.keyBindJump.getKeyCode();


    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new BuildMod());
    }

    @SubscribeEvent
    public void renderText(RenderGameOverlayEvent e) {
        if (e.type == RenderGameOverlayEvent.ElementType.TEXT) {

            Gui.drawRect(4, 28, 140, 84, 0x70000000);
            mc.fontRendererObj.drawString(EnumChatFormatting.BLUE + "--- INFORMATION ---", 16, 32, -1);
            mc.fontRendererObj.drawString(EnumChatFormatting.GREEN + "Corner 1 : "
                    + EnumChatFormatting.GRAY + "X : " + corner1x + "  Z : " + corner1z, 8, 42, -1);
            mc.fontRendererObj.drawString(EnumChatFormatting.GREEN + "Corner 2 : "
                    + EnumChatFormatting.GRAY + "X : " + corner2x + "  Z : " + corner2z, 8, 52, -1);
            mc.fontRendererObj.drawString(EnumChatFormatting.GREEN + "X/Y/Z : "
                    + EnumChatFormatting.GRAY + (int)mc.thePlayer.posX + "/" + (int)mc.thePlayer.posY + "/" + (int)mc.thePlayer.posZ, 8, 62, -1);
            mc.fontRendererObj.drawString(EnumChatFormatting.GREEN + "Pitch/Yaw : "
                    + EnumChatFormatting.GRAY + (int)Utils.getActualRotationYaw() + "/" + (int)mc.thePlayer.rotationPitch, 8, 72, -1);



        }
    }


    @SubscribeEvent
    public void onTickPlayer(TickEvent.ClientTickEvent e) {
        //0- builder's wand 1-> inf-dirt 2-> pouch 3 ->  shovel 4 - 7 ->soul sand

        if (mc.thePlayer != null && mc.theWorld != null && enabled) {

            blockunderfeet = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock();
            blockin = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
            double dx = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
            double dz = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
            lengthX = Math.abs(corner2x - corner1x);
            lengthZ = Math.abs(corner2z - corner1z);

            if (placeBlock1) {
                setHotbarIndex(1);
                if (corner2z - corner1z > 0)
                    setrot(180);
                else
                    setrot(0);

                setpitch(82);
                setKeyBindState(keyBindUseItem, dx == 0 && dz == 0);
                setKeyBindState(keyBindBackward, true);
                setKeyBindState(keyBindSneak, true);

                if ((int) mc.thePlayer.posZ == corner2z && blockunderfeet != Blocks.air) {
                    placeBlock1 = false;
                    placeBlock2 = true;
                    setKeyBindState(keyBindUseItem, false);
                    setKeyBindState(keyBindBackward, false);
                    setKeyBindState(keyBindSneak, false);
                }
            } else if (placeBlock2) {
                process = "Placing dirt (2)";
                setHotbarIndex(0);
                if (corner2x - corner1x > 0)
                    setrot(90);
                else
                    setrot(-90);

                setpitch(82);
                setKeyBindState(keyBindUseItem, dx == 0 && dz == 0);
                setKeyBindState(keyBindBackward, true);
                setKeyBindState(keyBindSneak, true);

                if ((int) mc.thePlayer.posX == corner2x && blockunderfeet != Blocks.air) {
                    placeBlock1 = false;
                    placeBlock2 = false;
                    setKeyBindState(keyBindSneak, false);
                    setKeyBindState(keyBindBackward, false);
                    setKeyBindState(keyBindUseItem, false);
                    mc.thePlayer.sendChatMessage("/setspawn");
                    ScheduleRunnable(WarpHub, 1500, TimeUnit.MILLISECONDS);
                }
            }

            if (placeSoulSandBlock1) {
                process = "Placing Soul Sand (1)";
                if (corner2x - corner1x > 0)
                    setrot(-90);
                else
                    setrot(90);

                setpitch(60);
                setKeyBindState(keyBindSneak, true);

                if (!setX) {
                    originalX = (int) mc.thePlayer.posX;
                    setX = true;
                }

                if (mc.thePlayer.inventory.currentItem <= 3) setHotbarIndex(4);

                if (mc.thePlayer.inventory.currentItem < 8) {
                    setHotbarIndex(mc.thePlayer.getCurrentEquippedItem() == null ?
                            mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem);
                }

                // 1. separate corner 2x - corner 1x
                // 2. filter 0 case
                // 3. separate posX +ve or -ve
                if (corner2x - corner1x > 0) {

                    if (originalX == 0 && positiveX) {
                        if (mc.thePlayer.posX - originalX < -0.5f && !moved && !reached)
                            moveback();
                        else if (mc.thePlayer.posX - originalX > -0.5f && !reached)
                            setKeyBindState(keyBindBackward, true);

                    } else {
                        if (mc.thePlayer.posX < 0) {
                            if (mc.thePlayer.posX - originalX + 0.5f < -1 && !moved && !reached)
                                moveback();
                            else if (mc.thePlayer.posX - originalX + 0.5f > -1 && !reached)
                                setKeyBindState(keyBindBackward, true);

                        } else {
                            if (mc.thePlayer.posX - originalX - 0.5f < -1 && !moved && !reached)
                                moveback();
                            else if (mc.thePlayer.posX - originalX - 0.5f > -1 && !reached)
                                setKeyBindState(keyBindBackward, true);

                        }
                    }
                } else {
                    if (originalX == 0 && !positiveX) {
                        if (mc.thePlayer.posX - originalX > 0.5f && !moved && !reached)
                            moveback();
                        else if (mc.thePlayer.posX - originalX < 0.5f && !reached)
                            KeyBinding.setKeyBindState(keyBindBackward, true);

                    } else {

                        if (mc.thePlayer.posX < 0) {
                            if (mc.thePlayer.posX - originalX + 0.5f > 1 && !moved && !reached)
                                moveback();
                            else if (mc.thePlayer.posX - originalX + 0.5f < 1 && !reached)
                                KeyBinding.setKeyBindState(keyBindBackward, true);

                        } else {
                            if (mc.thePlayer.posX - originalX - 0.5f > 1 && !moved && !reached)
                                moveback();
                            else if (mc.thePlayer.posX - originalX - 0.5f < 1 && !reached)
                                KeyBinding.setKeyBindState(keyBindBackward, true);

                        }
                    }
                }
                if (corner2x - corner1x > 0) {
                    if ((int) (mc.thePlayer.posX + 0.5f) == corner1x && !changedX)
                        changeSoulSandPlaceProcess();
                } else {
                    if ((int) (mc.thePlayer.posX - 0.5f) == corner1x && !changedX)
                        changeSoulSandPlaceProcess();
                }

            } else if (placeSoulSandBlock2) {

                if (corner2z - corner1z > 0)
                    setrot(0);
                else
                    setrot(180);
                process = "Placing Soul Sand (2)";
                if (firstBlock)
                    setpitch(60);
                else
                    setpitch(34);

                setKeyBindState(keyBindSneak, true);
                if (!firstBlock)
                    setHotbarIndex(0);

                if (!setZ) {
                    originalZ = (int) mc.thePlayer.posZ;
                    setZ = true;
                }

                if (corner2z - corner1z > 0) {
                    if (originalZ == 0 && positiveZ) {
                        if (mc.thePlayer.posZ - originalZ < -0.5f && !moved)
                            moveback();
                        else if (mc.thePlayer.posZ - originalZ > -0.5f)
                            setKeyBindState(keyBindBackward, true);

                    } else {
                        if (mc.thePlayer.posZ < 0) {
                            if (mc.thePlayer.posZ - originalZ + 0.5f < -1 && !moved) {
                                if (!placedFirstBlock) {
                                    KeyBinding.onTick(keyBindUseItem);
                                    placedFirstBlock = true;
                                }
                                moveback();
                            } else if (mc.thePlayer.posZ - originalZ + 0.5f > -1)
                                KeyBinding.setKeyBindState(keyBindBackward, true);

                        } else {
                            if (mc.thePlayer.posZ - originalZ - 0.5f < -1 && !moved) {
                                if (!placedFirstBlock) {
                                    KeyBinding.onTick(keyBindUseItem);
                                    placedFirstBlock = true;
                                }
                                moveback();
                            } else if (mc.thePlayer.posZ - originalZ - 0.5f > -1)
                                setKeyBindState(keyBindBackward, true);
                        }
                    }
                } else {

                    if (originalZ == 0 && !positiveZ) {
                        if (mc.thePlayer.posZ - originalZ > 0.5f && !moved)
                            moveback();
                        else if (mc.thePlayer.posZ - originalZ < 0.5f)
                            setKeyBindState(keyBindBackward, true);
                    } else {
                        if (mc.thePlayer.posZ < 0) {
                            if (mc.thePlayer.posZ - originalZ + 0.5f > 1 && !moved) {
                                if (!placedFirstBlock) {
                                    KeyBinding.onTick(keyBindUseItem);
                                    placedFirstBlock = true;
                                }
                                moveback();
                            } else if (mc.thePlayer.posZ - originalZ + 0.5f < 1)
                                setKeyBindState(keyBindBackward, true);
                        } else {
                            if (mc.thePlayer.posZ - originalZ - 0.5f > 1 && !moved) {
                                if (!placedFirstBlock) {
                                    KeyBinding.onTick(keyBindUseItem);
                                    placedFirstBlock = true;
                                }
                                moveback();
                            } else if (mc.thePlayer.posZ - originalZ - 0.5f < 1)
                                KeyBinding.setKeyBindState(keyBindBackward, true);

                        }
                    }
                }

                if (corner2z - corner1z > 0) {
                    if ((int) (mc.thePlayer.posZ + 0.5f) == corner1z)
                        endSoulSandPlaceProcess();
                } else {
                    if ((int) (mc.thePlayer.posZ - 0.5f) == corner1z)
                        endSoulSandPlaceProcess();
                }

            } else if (noSoulSand) {
                process = "Detected no blocks";
                setKeyBindState(keyBindBackward, false);
                setKeyBindState(keyBindSneak, false);
                setKeyBindState(keyBindUseItem, false);
                ScheduleRunnable(Refill, 1, TimeUnit.SECONDS);
                refilling = true;
                noSoulSand = false;
            }

            if (initializingPath) {
                if (corner2x > corner1x) {
                    if (corner2z > corner1z) {
                        setKeyBindState(keyBindLeft, true);
                        if (mc.thePlayer.posX > 0) {
                            if (mc.thePlayer.posX - corner1x > 1.5f)
                                prepareDig(false);

                        } else {
                            if (mc.thePlayer.posX - corner1x > 0.5f)
                                prepareDig(false);

                        }
                    } else {
                        setKeyBindState(keyBindRight, true);
                        if (mc.thePlayer.posX > 0) {
                            if (mc.thePlayer.posX - corner1x > 1.5f)
                                prepareDig(true);

                        } else { // tested
                            if (mc.thePlayer.posX - corner1x > 0.5f)
                                prepareDig(true);

                        }

                    }
                } else { // 2x < 1x
                    if (corner2z > corner1z) {
                        setKeyBindState(keyBindRight, true);
                        if (mc.thePlayer.posX > 0) {
                            if (mc.thePlayer.posX - corner1x < -0.5f)
                                prepareDig(true);
                        } else {
                            if (mc.thePlayer.posX - corner1x < -1.5f)
                                prepareDig(true);
                        }

                    } else {
                        setKeyBindState(keyBindLeft, true);
                        if (mc.thePlayer.posX > 0) {
                            if (mc.thePlayer.posX - corner1x < -0.5f)
                                prepareDig(false);
                        } else {
                            if (mc.thePlayer.posX - corner1x < -1.5f)
                                prepareDig(false);
                        }

                    }
                }
            }
            if (dig1) {
                setHotbarIndex(3);
                if (!lockdig1) {
                    lockdig1 = true;
                    ScheduleRunnable(Dig1, 500, TimeUnit.MILLISECONDS);
                }
            } else if (dig2) {

                if (slowDig && !startedSlowDig) {
                    ScheduleRunnable(DigSlow, 100, TimeUnit.MILLISECONDS);
                    startedSlowDig = true;

                } else if (!slowDig) {
                    setrot(digAngle);
                    setKeyBindState(keyBindForward, true);
                    if (Math.abs(corner2x - corner1x) - 7 <= Math.abs(originalX - (int) mc.thePlayer.posX) && !startedSlowDig) {
                        setKeyBindState(keyBindAttack, false);
                        startedSlowDig = true;
                        ScheduleRunnable(Dig2Slow2, 100, TimeUnit.MILLISECONDS);
                    } else if (Math.abs(corner2x - corner1x) - 11 <= Math.abs(originalX - (int) mc.thePlayer.posX) && !startedSlowDig) {
                        setKeyBindState(keyBindAttack, false);
                    } else if (!startedSlowDig) {
                        setKeyBindState(keyBindAttack, true);
                        mc.thePlayer.rotationPitch = 15;
                    }
                }
            } else if (dig3) {
                if (!lockdig3) {
                    lockdig3 = true;
                    ScheduleRunnable(Dig3, 500, TimeUnit.MILLISECONDS);
                }
            } else if (dig4) {
                if (slowDig && !startedSlowDig) {
                    ScheduleRunnable(DigSlow, 100, TimeUnit.MILLISECONDS);
                    startedSlowDig = true;

                } else if (!slowDig) {
                    setrot(digAngle);
                    setKeyBindState(keyBindForward, true);
                    if (Math.abs(corner2x - corner1x) - 7 <= Math.abs(originalX - (int) mc.thePlayer.posX) && !startedSlowDig) {
                        setKeyBindState(keyBindAttack, false);
                        startedSlowDig = true;
                        ScheduleRunnable(Dig4Slow2, 100, TimeUnit.MILLISECONDS);
                    } else if (Math.abs(corner2x - corner1x) - 11 <= Math.abs(originalX - (int) mc.thePlayer.posX) && !startedSlowDig) {
                        setKeyBindState(keyBindAttack, false);
                    } else if (!startedSlowDig) {
                        setKeyBindState(keyBindAttack, true);
                        setpitch(15);
                    }
                }
            }


            if (placingNetherwart) {
                setHotbarIndex(2);
                setpitch(58);
                if (leftSide)
                    placeNWGoingLeft = !sameSide;
                else
                    placeNWGoingLeft = sameSide;

                if (!buyingNetherwart && !startedBuyingNetherwart) {
                    setKeyBindState(keyBindSneak, true);
                    setKeyBindState(keyBindUseItem, true);

                    if (placeNWGoingLeft)
                        setKeyBindState(keyBindLeft, true);
                    else
                        setKeyBindState(keyBindRight, true);

                    if(!setZ){
                        originalZ = (int)mc.thePlayer.posZ;
                    }
                    if(Math.abs(originalZ - mc.thePlayer.posZ) > 50*64/lengthX){//
                        buyingNetherwart = true;
                    }


                } else if (!startedBuyingNetherwart) {
                    setKeyBindState(keyBindSneak, false);
                    setKeyBindState(keyBindUseItem, false);
                    setKeyBindState(keyBindLeft, false);
                    setKeyBindState(keyBindRight, false);
                    ScheduleRunnable(BuyNetherwart, 500, TimeUnit.MILLISECONDS);
                    startedBuyingNetherwart = true;
                }


            }
        }


    }
    Runnable Baritone = new Runnable() {
        @Override
        public void run() {
            if(enabled) {
                resetKeyBindState();

                try { //-2 -69
                    Utils.smoothRotateTo(90, false);
                    Thread.sleep(3000);
                    process1 = true;
                    while (process1) {
                        setKeyBindState(keyBindForward, true);
                        if ((int) mc.thePlayer.posX == -11) {
                            setKeyBindState(keyBindForward, false);
                            Utils.smoothRotateTo(0, false);
                            Thread.sleep(3000);
                            process1 = false;
                            process2 = true;
                        }
                    }
                    while (process2) {
                        setKeyBindState(keyBindForward, true);
                        if ((int) mc.thePlayer.posZ == -47) {
                            setKeyBindState(keyBindForward, false);
                            Utils.smoothRotateTo(90, true);
                            Thread.sleep(3000);
                            process2 = false;
                            process3 = true;
                        }
                    }
                    while (process3) {
                        setKeyBindState(keyBindForward, true);
                        if ((int) mc.thePlayer.posX == -50) {
                            setKeyBindState(keyBindForward, false);
                            Utils.smoothRotateTo(0, false);
                            Thread.sleep(3000);
                            process3 = false;
                            process4 = true;
                        }
                    }
                    while (process4) {
                        setKeyBindState(keyBindForward, true);
                        if ((int) mc.thePlayer.posZ == -29) {
                            setKeyBindState(keyBindForward, false);
                            Thread.sleep(3000);
                            process4 = false;
                            process5 = true;
                        }

                    }
                    while (process5) {
                        setKeyBindState(keyBindForward, false);
                        if(!Utils.hasPlayerNearby()) {
                            ScheduleRunnable(openBuilderShop, 1, TimeUnit.SECONDS);
                            process5 = false;
                        } else {

                            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                    "[Build Helper] : Detected players nearby"));
                            Thread.sleep(500);
                            mc.thePlayer.sendChatMessage("/warp home");
                            Thread.sleep(3000);
                            mc.thePlayer.sendChatMessage("/hub");
                            ScheduleRunnable(Baritone, 3, TimeUnit.SECONDS);
                            process5 = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Runnable BuyNetherwart = new Runnable() {
        @Override
        public void run() {
            if(enabled) {

                try {
                    mc.thePlayer.sendChatMessage("/bz");
                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 0, 0, 0);
                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 31, 0, 0);
                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 12, 0, 0);
                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 10, 0, 0);
                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 14, 0, 0);
                    Thread.sleep(1000);
                    mc.thePlayer.closeScreen();
                    ScheduleRunnable(openPouch, 1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Runnable openPouch = new Runnable() {
        @Override
        public void run() {
            if(enabled) {
                setHotbarIndex(2);
                KeyBinding.onTick(keyBindAttack);
                ScheduleRunnable(PutNetherwartInPouch, 1, TimeUnit.SECONDS);
            }
        }
    };

    Runnable PutNetherwartInPouch = new Runnable() {
        @Override
        public void run() {

            if(enabled) {
                try {
                    if (Utils.hasNetherwartInInv()) {

                        clickWindow(mc.thePlayer.openContainer.windowId, 45 + Utils.getFirstSlotWithNetherwart(), 0, 1);
                        if (Utils.hasNetherwartInInv()) {
                            if (!Utils.isContainerFull())
                                ScheduleRunnable(PutNetherwartInPouch, 500, TimeUnit.MILLISECONDS);
                            else {
                                startedBuyingNetherwart = false;
                                buyingNetherwart = false;
                                mc.thePlayer.closeScreen();
                            }

                        } else {
                            mc.thePlayer.closeScreen();
                            ScheduleRunnable(BuyNetherwart, 1, TimeUnit.SECONDS);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Runnable Dig1 = new Runnable() {
        @Override
        public void run() {

            try {
                digAngle = (corner2z - corner1z > 0)? 0 : 180;
                setrot(digAngle);
                setKeyBindState(keyBindForward, true);
                setpitch(45);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                originalX = (int) mc.thePlayer.posX;
                if(corner2x < corner1x)
                    digAngle = corner2z > corner1z ? digAngle + 90 : digAngle - 90;
                else
                    digAngle = corner2z > corner1z ? digAngle - 90 : digAngle + 90;

                dig1 = false;
                lockdig1 = false;
                dig2 = true;
                slowDig = true;


            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable DigSlow = new Runnable() {
        @Override
        public void run() {

            try {
                setrot(digAngle);
                setKeyBindState(keyBindForward, false);
                mc.thePlayer.rotationPitch = 45;
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                mc.thePlayer.rotationPitch = 40;
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                mc.thePlayer.rotationPitch = 30;
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);

                slowDig = false;
                startedSlowDig = false;
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable Dig2Slow2 = new Runnable() {
        @Override
        public void run() {

            try {

                setrot(digAngle);
                setKeyBindState(keyBindForward, true);
                setpitch(45);

                while(!(Math.abs(corner2x - corner1x) - 2 <= Math.abs(originalX - (int) mc.thePlayer.posX))) {
                    KeyBinding.onTick(keyBindAttack);
                    Thread.sleep(500);
                }

                if (Math.abs(mc.thePlayer.posZ - corner2z) < 6) {
                    dig1 = false;
                    dig2 = false;
                    dig3 = false;
                    dig4 = false;
                    setKeyBindState(keyBindForward, false);
                    setKeyBindState(keyBindAttack, false);
                    ScheduleRunnable(prepPlaceNW, 100, TimeUnit.MILLISECONDS);
                    sameSide = false;
                } else {
                    dig2 = false;
                    setKeyBindState(keyBindForward, false);
                    setKeyBindState(keyBindAttack, false);
                    dig3 = true;
                    slowDig = false;
                    startedSlowDig = false;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable Dig4Slow2 = new Runnable() {
        @Override
        public void run() {

            try {
                setrot(digAngle);
                KeyBinding.setKeyBindState(keyBindForward, true);
                mc.thePlayer.rotationPitch = 45;

                while(!(Math.abs(corner2x - corner1x) - 2 <= Math.abs(originalX - (int) mc.thePlayer.posX))) {
                    KeyBinding.onTick(keyBindAttack);
                    Thread.sleep(500);
                }

                if (Math.abs(mc.thePlayer.posZ - corner2z) < 6) {
                    dig1 = false;
                    dig2 = false;
                    dig3 = false;
                    dig4 = false;
                    setKeyBindState(keyBindForward, false);
                    setKeyBindState(keyBindAttack, false);
                    ScheduleRunnable(prepPlaceNW, 100, TimeUnit.MILLISECONDS);
                    sameSide = false;
                } else {
                    dig4 = false;
                    setKeyBindState(keyBindForward, false);
                    setKeyBindState(keyBindAttack, false);
                    dig1 = true;
                    slowDig = false;
                    startedSlowDig = false;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable Dig3 = new Runnable() {
        @Override
        public void run() {

            try {
                digAngle = (corner2z - corner1z > 0)? 0 : 180;
                setrot(digAngle);
                setKeyBindState(keyBindForward, true);
                mc.thePlayer.rotationPitch = 45;
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                KeyBinding.onTick(keyBindAttack);
                Thread.sleep(500);
                originalX = (int) mc.thePlayer.posX;
                if(corner2x < corner1x)
                    digAngle = corner2z > corner1z ? digAngle - 90 : digAngle + 90;
                else
                    digAngle = corner2z > corner1z ? digAngle + 90 : digAngle - 90;

                dig3 = false;
                lockdig3 = false;
                dig4 = true;
                slowDig = true;


            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable prepPlaceNW = new Runnable() {
        @Override
        public void run() {

            try {
                setZ = false;

                Thread.sleep(100);
                setKeyBindState(keyBindForward, true);
                setKeyBindState(keyBindJump, true);
                Thread.sleep(100);
                setKeyBindState(keyBindJump, false);
                Thread.sleep(300);
                setKeyBindState(keyBindForward, false);
                digAngle = digAngle + 180;
                setrot(digAngle);
                ScheduleRunnable(clearInventory, 500, TimeUnit.MILLISECONDS);


            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable clearInventory = new Runnable() {
        @Override
        public void run() {


            try {
                setHotbarIndex(8);
                Thread.sleep(100);
                KeyBinding.onTick(keyBindUseItem);
                Thread.sleep(800);
                clickWindow(mc.thePlayer.openContainer.windowId, 22, 0, 0);
                Thread.sleep(800);
                while(Utils.hasSoulSandInInv()) {
                    clickWindow(mc.thePlayer.openContainer.windowId, 45 + Utils.getFirstSlotWithSoulSand(), 0, 0);
                    Thread.sleep(600);
                }
                mc.thePlayer.closeScreen();
                placingNetherwart = true;
                buyingNetherwart = true;

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable ChangeSoulSandPlaceProcess = new Runnable() {
        @Override
        public void run() {

            try {
                Thread.sleep(500);
                firstBlock = true;
                placeSoulSandBlock1 = false;
                placeSoulSandBlock2 = true;

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable MoveBack = new Runnable() {
        @Override
        public void run() {

            try {
                if(!firstBlock)
                KeyBinding.onTick(keyBindUseItem);

                positiveX = mc.thePlayer.posX > 0;
                positiveZ = mc.thePlayer.posZ > 0;
                firstBlock = false;
                setZ = false;
                setX = false;
                moved = false;

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable backToIslandPrep = new Runnable() {
        @Override
        public void run() {
            try {
                KeyBinding.setKeyBindState(keyBindSneak, true);
                Thread.sleep(1000);
                KeyBinding.setKeyBindState(keyBindSneak, false);
                KeyBinding.onTick(keyBindUseItem);
                Thread.sleep(500);
                placeSoulSandBlock2 = true;
                refilling = false;
                setFocus();
            } catch(Exception e){
                e.printStackTrace();
            }


        }
    };


    Runnable Refill = new Runnable() {
        @Override
        public void run() {

            mc.thePlayer.sendChatMessage("/setspawn");
            ScheduleRunnable(WarpHub, 2, TimeUnit.SECONDS);
        }
    };

    Runnable WarpHub = new Runnable() {
        @Override
        public void run() {
            mc.thePlayer.sendChatMessage("/hub");
            ScheduleRunnable(toggleBaritone, 4000, TimeUnit.MILLISECONDS);
        }
    };

    Runnable toggleBaritone = new Runnable() {
        @Override
        public void run() {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Toggling baritone"));
            setFocus();
            process = "Buying dirt";
            ScheduleRunnable(Baritone, 1, TimeUnit.SECONDS);
        }
    };

    Runnable openBuilderShop = new Runnable() {
        @Override
        public void run() {
            if(enabled) {
                try {
                    setrot(13);
                    Thread.sleep(500);
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    Thread.sleep(2000);
                    if (mc.currentScreen instanceof GuiChest) {
                        GuiChest currentContainer = (GuiChest) mc.currentScreen;
                        Slot slot = currentContainer.inventorySlots.inventorySlots.get(16);
                        if (slot != null) {
                            if (slot.getStack() != null) {
                                clickWindow(currentContainer.inventorySlots.windowId, 16, 1, 0);
                                Thread.sleep(1000);
                                clickWindow(mc.thePlayer.openContainer.windowId, 10, 1, 0);
                                Thread.sleep(1000);
                                ScheduleRunnable(buySoulSand, 2, TimeUnit.SECONDS);
                            }
                        }
                    }


                } catch (Exception e) {

                }
            }
        }
    };
    Runnable buySoulSand = new Runnable() {
        @Override
        public void run() {
            if(enabled) {

                try {
                    clickWindow(mc.thePlayer.openContainer.windowId, 24, 0, 0);

                    if (mc.thePlayer.inventory.getFirstEmptyStack() == -1) {
                        mc.thePlayer.closeScreen();
                        ScheduleRunnable(openBuilderWand, 1, TimeUnit.SECONDS);
                    } else {
                        ScheduleRunnable(buySoulSand, 700, TimeUnit.MILLISECONDS);
                    }
                }catch(Exception e){

                }

            }
        }
    };

    Runnable openBuilderWand = new Runnable() {
        @Override
        public void run() {
            if(enabled) {
                mc.thePlayer.inventory.currentItem = 0;
                setrot(60);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                KeyBinding.onTick(keyBindAttack);
                ScheduleRunnable(PlaceSoulSandInWand, 1500, TimeUnit.MILLISECONDS);
            }
        }
    };

    Runnable PlaceSoulSandInWand = new Runnable() {
        @Override
        public void run() {

            if(enabled) {
                if (Utils.hasSoulSandInInv()) {

                    try {
                        clickWindow(mc.thePlayer.openContainer.windowId, 45 + Utils.getFirstSlotWithSoulSand(), 0, 1);

                        if (Utils.hasSoulSandInInv()) {
                            if (!Utils.isContainerFull())
                                ScheduleRunnable(PlaceSoulSandInWand, 500, TimeUnit.MILLISECONDS);
                            else {
                                mc.thePlayer.closeScreen();
                                mc.thePlayer.sendChatMessage("/warp home");
                                if (refilling) {
                                    ScheduleRunnable(backToIslandPrep, 3, TimeUnit.SECONDS);
                                } else {
                                    ScheduleRunnable(prepPlaceBlock, 3, TimeUnit.SECONDS);
                                }

                            }
                        } else {
                            mc.thePlayer.closeScreen();
                            ScheduleRunnable(openBuilderShop, 1, TimeUnit.SECONDS);
                        }
                    }catch(Exception e){

                    }
                }
            }
        }
    };

    Runnable prepPlaceBlock = new Runnable() {
        @Override
        public void run() {

            if (enabled) {

                try {
                    setFocus();
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Preparing to place soul sand"));
                    mc.thePlayer.inventory.currentItem = 0;
                    KeyBinding.onTick(keyBindAttack);

                    Thread.sleep(1000);
                    clickWindow(mc.thePlayer.openContainer.windowId, 1, 0, 1);
                    Thread.sleep(500);
                    clickWindow(mc.thePlayer.openContainer.windowId, 2, 0, 1);
                    Thread.sleep(500);
                    clickWindow(mc.thePlayer.openContainer.windowId, 3, 0, 1);
                    Thread.sleep(500);
                    clickWindow(mc.thePlayer.openContainer.windowId, 4, 0, 1);
                    Thread.sleep(500);
                    clickWindow(mc.thePlayer.openContainer.windowId, 5, 0, 1);
                    Thread.sleep(500);

                    mc.thePlayer.closeScreen();


                    KeyBinding.setKeyBindState(keyBindSneak, true);
                    Thread.sleep(1000);

                    KeyBinding.setKeyBindState(keyBindSneak, false);


                    mc.thePlayer.inventory.currentItem = 3;
                    mc.thePlayer.rotationPitch = 89;

                    Thread.sleep(1500);

                    placeSoulSandBlock1 = true;
                    placeSoulSandBlock2 = false;
                } catch (Exception e) {

                }
            }
        }
    };


    void setrot(int targetRotationYaw){
        Utils.hardRotateTo(targetRotationYaw);
    }
    void setpitch(int targetPitch){
        mc.thePlayer.rotationPitch = targetPitch;
    }
    void setKeyBindState(int keyCode, boolean pressed){
        KeyBinding.setKeyBindState(keyCode, pressed);
    }
    void setHotbarIndex(int index){
        mc.thePlayer.inventory.currentItem = index;
    }
    void moveback(){
        moved = true;
        KeyBinding.setKeyBindState(keyBindBackward, false);
        ScheduleRunnable(MoveBack, 500, TimeUnit.MILLISECONDS);
    }

    void clickWindow(int windowID, int slotID, int mouseButtonClicked, int mode) throws Exception{
       if(mc.thePlayer.openContainer instanceof ContainerChest || mc.currentScreen instanceof  GuiInventory)
           mc.playerController.windowClick(windowID, slotID, mouseButtonClicked, mode, mc.thePlayer);
       else {
           mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                   "[Build Helper] : Didn't open window! This shouldn't happen and the script has been disabled. Please immediately report to the developer."));
           enabled = false;
           throw new Exception();

       }
    }
    void changeSoulSandPlaceProcess(){
        setKeyBindState(keyBindBackward, false);
        reached = true;
        changedX = true;
        ScheduleRunnable(ChangeSoulSandPlaceProcess, 1, TimeUnit.SECONDS);
     }
     void endSoulSandPlaceProcess(){
         setKeyBindState(keyBindBackward, false);
         placeSoulSandBlock2 = false;
         initializingPath = true;
     }
     void prepareDig(boolean onLeftSide){
         leftSide = onLeftSide;
         setKeyBindState(keyBindLeft, false);
         setKeyBindState(keyBindRight, false);
         setKeyBindState(keyBindSneak, false);
         initializingPath = false;
         digAngle = (corner2z - corner1z > 0) ? 0 : 180;
         dig1 = true;
     }
     void resetKeyBindState(){
         KeyBinding.setKeyBindState(keyBindBackward, false);
         KeyBinding.setKeyBindState(keyBindUseItem, false);
         KeyBinding.setKeyBindState(keyBindForward, false);
         KeyBinding.setKeyBindState(keyBindRight, false);
         KeyBinding.setKeyBindState(keyBindLeft, false);
         KeyBinding.setKeyBindState(keyBindSneak, false);
         KeyBinding.setKeyBindState(keyBindAttack, false);
     }

    void ScheduleRunnable(Runnable runnable, int delay, TimeUnit timeUnit){
        ScheduledExecutorService temp = new ScheduledThreadPoolExecutor(1);
        temp.schedule(runnable, delay, timeUnit);
        temp.shutdown();
    }
    void setFocus(){
        mc.inGameHasFocus = true;
        mouseHelper.grabMouseCursor();
        mc.displayGuiScreen((GuiScreen)null);
        Field f = null;
        f = FieldUtils.getDeclaredField(mc.getClass(), "leftClickCounter",true);
        try {
            f.set(mc, 10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static float get360RotationYaw(){
        return Minecraft.getMinecraft().thePlayer.rotationYaw > 0?
                (Minecraft.getMinecraft().thePlayer.rotationYaw % 360) :
                (Minecraft.getMinecraft().thePlayer.rotationYaw < 360f ? 360 - (-Minecraft.getMinecraft().thePlayer.rotationYaw % 360)  :  360 + Minecraft.getMinecraft().thePlayer.rotationYaw);
    }


    void stop(){
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Disabling script"));
        resetKeyBindState();
        enabled = false;
        process1 = false;
        process2 = false;
        process3 = false;
        process4 = false;
        process5 = false;
        dig1 = false;
        dig2 = false;
        dig3 = false;
        dig4 = false;
        lockdig1 = false;
        startedSlowDig = false;
        placeBlock1 = false;
        placeBlock2 = false;
        placeSoulSandBlock1 = false;
        placeSoulSandBlock2 = false;
        noSoulSand = false;
        refilling = false;
        setZ = false;
        setX = false;
        moved = false;
        changedX = false;
        firstBlock = false;
        reached = false;
        positiveX = false;
        positiveZ = false;
        placedFirstBlock = false;
        initializingPath = false;
        lockdig3 = false;
        leftSide = false;
        sameSide = false;
        placeNWGoingLeft = false;
        placingNetherwart = false;
        slowDig2 = false;
        buyingNetherwart = false;
        startedBuyingNetherwart = false;
        rotated = false;

    }





    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e){
        if(Keyboard.isKeyDown(Keyboard.KEY_F)){
            if(!enabled) {

                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Enabling script"));
               enable();
                 noSoulSand = false;
                 refilling = false;
                 setZ = false;
                 moved = false;
                 placeBlock1 = true;
            } else {
                stop();
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_G)){
            if(!enabled) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Enabling script (Buying Soul Sand)"));
                enable();
                ScheduleRunnable(Baritone, 1, TimeUnit.SECONDS);

            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_H)){
            if(!enabled) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Enabling script (Placing Soul Sand 1)"));
                enable();
                noSoulSand = false;
                refilling = false;
                setZ = false;
                moved = false;
                ScheduleRunnable(prepPlaceBlock, 1, TimeUnit.SECONDS);
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_J)){
            if(!enabled) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Enabling script (Digging soul sand)"));
                enable();
                noSoulSand = false;
                refilling = false;
                setZ = false;
                moved = false;
                endSoulSandPlaceProcess();
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_K)){
            if(!enabled) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Enabling script (Buying Netherwart)"));
                enable();
                noSoulSand = false;
                refilling = false;
                setZ = false;
                moved = false;
                startedBuyingNetherwart = false;
                placingNetherwart = true;
                buyingNetherwart = true;
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_P)){
            if(setmode == 0) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Set 1st corner"));
                corner1x = (int)mc.thePlayer.posX;
                corner1y = (int)mc.thePlayer.posY - 1;
                corner1z = (int)mc.thePlayer.posZ;
                setmode = 1 - setmode;
            }
            else {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[Build Helper] : Set 2nd corner"));
                corner2x = (int)mc.thePlayer.posX;
                corner2y = (int)mc.thePlayer.posY - 1;
                corner2z = (int)mc.thePlayer.posZ;
                setmode = 1 - setmode;
            }
        }
    }

    void enable(){
        if(corner1x != 0 && corner1z != 0 && corner2x != 0 && corner2z != 0)
        enabled = true;
        else
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                    "[Build Helper] : Set the corners!"));

    }




    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent e){
        if(e.message.getUnformattedText().contains("You don't have that many")){
            placeSoulSandBlock2 = false;
            noSoulSand = true;
        }
        if(e.message.getUnformattedText().contains("You have sent a trade request")){
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                    "[Build Helper] : Clicked on some other players! The script has been disabled. Please do /warp home and /hub and press G or H."));
            enabled = false;
        }
    }




}
