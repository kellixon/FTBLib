package com.feed_the_beast.ftbl.net;

import com.feed_the_beast.ftbl.api.config.ConfigContainer;
import com.feed_the_beast.ftbl.api.config.ConfigGroup;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbl.gui.GuiEditConfig;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.JsonHelper;
import com.google.gson.JsonObject;
import com.latmod.lib.io.ByteIOStream;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageEditConfig extends MessageToClient<MessageEditConfig> // MessageEditConfigResponse
{
    public ConfigGroup group;
    public NBTTagCompound extraNBT;
    public ITextComponent title;

    public MessageEditConfig()
    {
    }

    public MessageEditConfig(NBTTagCompound nbt, ConfigContainer c)
    {
        group = c.createGroup();
        extraNBT = nbt;
        title = c.getConfigTitle();

        if(FTBLib.DEV_ENV)
        {
            FTBLib.dev_logger.info("TX Send: " + group);
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBLibNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        extraNBT = readTag(io);
        title = JsonHelper.deserializeICC(readJsonElement(io));
        int s = io.readInt();
        byte[] b = new byte[s];
        io.readBytes(b, 0, s);
        ByteIOStream bios = new ByteIOStream();
        bios.setCompressedData(b);
        group = new ConfigGroup();
        group.readData(bios, true);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        writeTag(io, extraNBT);
        writeJsonElement(io, JsonHelper.serializeICC(title));
        int s = io.readInt();
        ByteIOStream bios = new ByteIOStream();
        group.writeData(bios, true);
        byte[] b = bios.toCompressedByteArray();
        io.writeInt(b.length);
        io.writeBytes(b, 0, b.length);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(final MessageEditConfig m, Minecraft mc)
    {
        if(FTBLib.DEV_ENV)
        {
            FTBLib.dev_logger.info("RX Send: " + m.group);
        }

        new GuiEditConfig(m.extraNBT, new ConfigContainer()
        {
            @Override
            public ConfigGroup createGroup()
            {
                return m.group;
            }

            @Override
            public ITextComponent getConfigTitle()
            {
                return m.title;
            }

            @Override
            public void saveConfig(ICommandSender sender, NBTTagCompound nbt, JsonObject json)
            {
                new MessageEditConfigResponse(nbt, json).sendToServer();
            }
        }).openGui();
    }
}