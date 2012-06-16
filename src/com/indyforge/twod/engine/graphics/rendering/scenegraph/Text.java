package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Text extends GraphicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text = name();
	private Color fontColor = Color.white;
	private Font font = new Font("Verdana", Font.PLAIN, 1);

	public String text() {
		return text;
	}

	public void text(String text) {
		if (text == null) {
			throw new NullPointerException("test");
		}
		this.text = text;
	}

	public Color fontColor() {
		return fontColor;
	}

	public void fontColor(Color fontColor) {
		if (fontColor == null) {
			throw new NullPointerException("fontColor");
		}
		this.fontColor = fontColor;
	}

	public Font font() {
		return font;
	}

	public void font(Font font) {
		if (font == null) {
			throw new NullPointerException("font");
		}
		this.font = font;
	}

	public void drawCenteredString(String s, Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();

		double x = -fm.stringWidth(s) * 0.5;
		double y = (fm.getAscent() - (fm.getAscent() + fm.getDescent()) * 0.5);

		g.translate(x, y);
		g.drawString(s, 0, 0);
	}

	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {
		super.onRender(original, transformed);

		transformed.setFont(font);
		transformed.setColor(fontColor);
		drawCenteredString(text, transformed);
	}
}
