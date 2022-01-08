package com.davenonymous.bonsaitrees3.libnonymous.helper;

public class MathHelper {
	public static int clamp(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}

	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

	public static float clamp(float value, float min, float max) {
		return Math.min(Math.max(value, min), max);
	}
}
