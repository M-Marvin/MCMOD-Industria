package de.m_marvin.industria.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalPrefixFormater {

	private DecimalPrefixFormater() {}
	
	public static String format(String msg, Number... args) {
		Object[] formatted = new String[args.length];
		for (int i = 0; i < args.length; i++)
			formatted[i] =  formatNumber(args[i]);
		return String.format(msg, formatted);
	}
	
	public static String formatNumber(Number n) {
		if (n instanceof Double d)
			return formatDouble(d);
		if (n instanceof Float d)
			return formatFloat(d);
		if (n instanceof Long d)
			return formatLong(d);
		if (n instanceof Integer d)
			return formatInt(d);
		return "NaN";
	}
	
	public static String formatDouble(double d) {
		int exp = 0;
		if (d != 0 && Double.isFinite(d)) {
			while (Math.abs(d) >= 1000.0) {
				d /= 1000;
				exp += 3;
			}
			while (Math.abs(d) < 1.0) {
				d *= 1000;
				exp += 3;
			}
		}
		return String.format("%3.03f%c", d, prefix(exp));
	}

	public static String formatFloat(float d) {
		int exp = 0;
		if (d != 0 && Float.isFinite(d)) {
			while (Math.abs(d) >= 1000.0) {
				d /= 1000;
				exp += 3;
			}
			while (Math.abs(d) < 1.0) {
				d *= 1000;
				exp += 3;
			}
		}
		return String.format("%3.03f%c", d, prefix(exp));
	}

	public static String formatLong(long d) {
		int exp = 0;
		while (Math.abs(d) >= 1000.0) {
			d /= 1000;
			exp += 3;
		}
		return String.format("%3.03f%c", d, prefix(exp));
	}

	public static String formatInt(int d) {
		int exp = 0;
		while (Math.abs(d) >= 1000.0) {
			d /= 1000;
			exp += 3;
		}
		return String.format("%3.03f%c", d, prefix(exp));
	}
	
	public static char prefix(int exp) {
		switch (exp) {
		case -30: return 'q';
		case -27: return 'r';
		case -24: return 'y';
		case -21: return 'z';
		case -18: return 'a';
		case -15: return 'f';
		case -12: return 'p';
		case -9: return 'n';
		case -6: return 'u';
		case -3: return 'm';
		case 0: return ' ';
		case 3: return 'k';
		case 6: return 'M';
		case 9: return 'G';
		case 12: return 'T';
		case 15: return 'P';
		case 18: return 'E';
		case 21: return 'Z';
		case 24: return 'Y';
		case 27: return 'R';
		case 30: return 'Q';
		default: return '?';
		}
	}
	
	private static final Pattern PPAT = Pattern.compile("([\\d,.]{1,}) ?([A-Za-z]?)");
	
	public static double parseDouble(String s) {
		Matcher m = PPAT.matcher(s);
		if (!m.matches())
			throw new NumberFormatException("not a valid decimal prefixed number: " + s);
		double d = Double.parseDouble(m.group(1));
		char p = (m.group(2).isEmpty()) ? ' ' : m.group(2).charAt(0);
		return d * Math.pow(10, exponent(p));
	}

	public static double parseFloat(String s) {
		Matcher m = PPAT.matcher(s);
		if (!m.matches())
			throw new NumberFormatException("not a valid decimal prefixed number: " + s);
		float d = Float.parseFloat(m.group(1));
		char p = (m.group(2) == null) ? ' ' : m.group(2).charAt(0);
		return d * Math.pow(10, exponent(p));
	}

	public static double parseLong(String s) {
		Matcher m = PPAT.matcher(s);
		if (!m.matches())
			throw new NumberFormatException("not a valid decimal prefixed number: " + s);
		long d = Long.parseLong(m.group(1));
		char p = (m.group(2) == null) ? ' ' : m.group(2).charAt(0);
		return d * Math.pow(10, exponent(p));
	}
	
	public static double parseInt(String s) {
		Matcher m = PPAT.matcher(s);
		if (!m.matches())
			throw new NumberFormatException("not a valid decimal prefixed number: " + s);
		int d = Integer.parseInt(m.group(1));
		char p = (m.group(2) == null) ? ' ' : m.group(2).charAt(0);
		return d * Math.pow(10, exponent(p));
	}

	public static int exponent(char prefix) {
		switch (prefix) {
		case 'Q': return 30;
		case 'R': return 27;
		case 'Y': return 24;
		case 'Z': return 21;
		case 'E': return 18;
		case 'P': return 15;
		case 'T': return 12;
		case 'G': return 9;
		case 'M': return 6;
		case 'k': return 3;
		case ' ': return 0;
		case 'm': return -3;
		case 'u': return -6;
		case 'n': return -9;
		case 'p': return -12;
		case 'f': return -15;
		case 'a': return -18;
		case 'z': return -21;
		case 'y': return -24;
		case 'r': return -27;
		case 'q': return -30;
		default: return 0;
		}
	}
	

}
