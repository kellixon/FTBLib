package com.latmod.lib.config;

import com.feed_the_beast.ftbl.api.config.ConfigValueProvider;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigValueProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.util.LMNetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 26.08.2016.
 */
public class PropertyStringList extends PropertyBase
{
    public static final String ID = "string_list";

    @ConfigValueProvider(ID)
    public static final IConfigValueProvider PROVIDER = () -> new PropertyStringList(new ArrayList<>());

    private List<String> value;

    public PropertyStringList(List<String> v)
    {
        value = v;
    }

    public PropertyStringList(String... s)
    {
        this(Arrays.asList(s));
    }

    @Override
    public String getID()
    {
        return ID;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return getStringList();
    }

    public void set(List<String> v)
    {
        value = v;
    }

    public List<String> getStringList()
    {
        return value;
    }

    @Override
    public void writeData(ByteBuf data, boolean extended)
    {
        List<String> list = getStringList();
        data.writeShort(list.size());
        list.forEach(s -> LMNetUtils.writeString(data, s));
    }

    @Override
    public void readData(ByteBuf data, boolean extended)
    {
        int s = data.readUnsignedShort();

        if(s <= 0)
        {
            value.clear();
            set(value);
        }
        else
        {
            value.clear();

            while(--s >= 0)
            {
                value.add(LMNetUtils.readString(data));
            }

            set(value);
        }
    }

    @Override
    public String getString()
    {
        return getStringList().toString();
    }

    @Override
    public boolean getBoolean()
    {
        return !getStringList().isEmpty();
    }

    @Override
    public int getInt()
    {
        return getStringList().size();
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyStringList(new ArrayList<>(getStringList()));
    }

    @Override
    public int getColor()
    {
        return 0xFFAA49;
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagList tagList = new NBTTagList();
        getStringList().forEach(s -> tagList.appendTag(new NBTTagString(s)));
        return tagList;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        value.clear();
        NBTTagList tagList = (NBTTagList) nbt;
        int s = tagList.tagCount();

        for(int i = 0; i < s; i++)
        {
            value.add(tagList.getStringTagAt(i));
        }

        set(value);
    }

    @Override
    public void fromJson(JsonElement json)
    {
        value.clear();
        JsonArray a = json.getAsJsonArray();

        if(a.size() > 0)
        {
            a.forEach(e -> value.add(e.getAsString()));
        }

        set(value);
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonArray a = new JsonArray();
        getStringList().forEach(s -> a.add(new JsonPrimitive(s)));
        return a;
    }
}