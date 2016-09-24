package com.latmod.lib.config;

import com.feed_the_beast.ftbl.api.config.ConfigValueProvider;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigValueProvider;
import com.feed_the_beast.ftbl.api.config.IGuiEditConfig;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.EnumNameMap;
import com.latmod.lib.util.LMNetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 26.08.2016.
 */
public class PropertyStringEnum extends PropertyBase
{
    @ConfigValueProvider(PropertyEnumAbstract.ID)
    public static final IConfigValueProvider PROVIDER = () -> new PropertyStringEnum(Collections.emptyList(), "");

    private List<String> keys;
    private String value;

    public PropertyStringEnum(List<String> k, String v)
    {
        keys = k;
        value = v;
    }

    @Override
    public String getID()
    {
        return PropertyEnumAbstract.ID;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return getString();
    }

    public void setString(String v)
    {
        value = v;
    }

    @Override
    public String getString()
    {
        return value;
    }

    @Override
    public boolean getBoolean()
    {
        return !value.equals(EnumNameMap.NULL_VALUE);
    }

    @Override
    public int getInt()
    {
        return keys.indexOf(value);
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyStringEnum(keys, getString());
    }

    @Override
    public int getColor()
    {
        return 0x0094FF;
    }

    @Override
    public void onClicked(IGuiEditConfig gui, IConfigKey key, IMouseButton button)
    {
    }

    @Override
    public NBTBase serializeNBT()
    {
        return new NBTTagString(getString());
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        setString(((NBTTagString) nbt).getString());
    }

    @Override
    public void fromJson(JsonElement json)
    {
        setString(json.getAsString());
    }

    @Override
    public JsonElement getSerializableElement()
    {
        return new JsonPrimitive(getString());
    }

    @Override
    public void writeData(ByteBuf data, boolean extended)
    {
        if(extended)
        {
            data.writeShort(keys.size());
            keys.forEach(s -> LMNetUtils.writeString(data, s));
        }

        data.writeShort(getInt());
    }

    @Override
    public void readData(ByteBuf data, boolean extended)
    {
        if(extended)
        {
            keys.clear();
            int s = data.readUnsignedShort();

            while(--s >= 0)
            {
                keys.add(LMNetUtils.readString(data));
            }
        }

        setString(keys.get(data.readShort() & 0xFFFF));
    }
}