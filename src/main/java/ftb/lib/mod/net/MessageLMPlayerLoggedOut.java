package ftb.lib.mod.net;

import ftb.lib.api.*;
import ftb.lib.api.net.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.UUID;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public UUID playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(ForgePlayerMP p)
	{
		playerID = p.getProfile().getId();
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBLibNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		ForgePlayerSP p = ForgeWorldSP.inst.getPlayer(m.playerID);
		p.onLoggedOut();
		p.isOnline = false;
		return null;
	}
}