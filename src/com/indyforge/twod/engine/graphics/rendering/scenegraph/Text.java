package com.indyforge.twod.engine.graphics.rendering.scenegraph;

import java.awt.Color;
import java.awt.Font;
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

	@Override
	protected void onRender(Graphics2D original, Graphics2D transformed) {
		super.onRender(original, transformed);

		transformed.setFont(font);
		transformed.setColor(fontColor);
		//transformed.scale(1f / text.length(), 1f / text.length());
		transformed.drawString(text, 0, 0);
	}
}
