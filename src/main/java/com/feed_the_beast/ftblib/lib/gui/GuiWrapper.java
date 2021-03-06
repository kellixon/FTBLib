package com.feed_the_beast.ftblib.lib.gui;

import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiWrapper extends GuiScreen implements IGuiWrapper
{
	private GuiBase wrappedGui;
	private List<String> tempTextList = new ArrayList<>();

	public GuiWrapper(GuiBase g)
	{
		wrappedGui = g;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		wrappedGui.initGui();
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return wrappedGui.doesGuiPauseGame();
	}

	@Override
	protected final void mouseClicked(int mouseX, int mouseY, int button) throws IOException
	{
		wrappedGui.updateMouseOver(mouseX, mouseY);

		if (button == MouseButton.BACK.id)
		{
			wrappedGui.onBack();
		}
		else
		{
			wrappedGui.mousePressed(MouseButton.get(button));
			super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button)
	{
		wrappedGui.updateMouseOver(mouseX, mouseY);
		wrappedGui.mouseReleased(MouseButton.get(button));
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	protected void keyTyped(char keyChar, int key) throws IOException
	{
		if (wrappedGui.keyPressed(key, keyChar))
		{
			return;
		}
		else if (key == Keyboard.KEY_BACK)
		{
			wrappedGui.onBack();
			return;
		}
		else if (wrappedGui.onClosedByKey(key))
		{
			wrappedGui.closeGui(false);
			return;
		}

		super.keyTyped(keyChar, key);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int scroll = Mouse.getEventDWheel();
		if (scroll != 0)
		{
			wrappedGui.mouseScrolled(scroll);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException
	{
		if (!Keyboard.getEventKeyState())
		{
			wrappedGui.keyReleased(Keyboard.getEventKey());
		}
		else
		{
			super.handleKeyboardInput();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if (wrappedGui.fixUnicode)
		{
			GuiHelper.setFixUnicode(true);
		}

		wrappedGui.updateGui(mouseX, mouseY);
		drawDefaultBackground();
		GuiHelper.setupDrawing();
		int x = wrappedGui.getX();
		int y = wrappedGui.getY();
		int w = wrappedGui.width;
		int h = wrappedGui.height;
		Theme theme = wrappedGui.getTheme();
		wrappedGui.draw(theme, x, y, w, h);
		wrappedGui.drawForeground(theme, x, y, w, h);

		if (wrappedGui.contextMenu != null)
		{
			wrappedGui.contextMenu.addMouseOverText(tempTextList);
		}
		else
		{
			wrappedGui.addMouseOverText(tempTextList);
		}

		GuiUtils.drawHoveringText(tempTextList, mouseX, Math.max(mouseY, 18), wrappedGui.getScreen().getScaledWidth(), wrappedGui.getScreen().getScaledHeight(), 0, theme.getFont());
		tempTextList.clear();

		if (wrappedGui.fixUnicode)
		{
			GuiHelper.setFixUnicode(false);
		}
	}

	@Override
	public void drawDefaultBackground()
	{
		if (wrappedGui.drawDefaultBackground())
		{
			super.drawDefaultBackground();
		}
	}

	@Override
	public GuiBase getGui()
	{
		return wrappedGui;
	}
}