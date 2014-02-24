package com.dsatab.data;

import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.dsatab.util.Debug;
import com.dsatab.util.Util;

public class Dice {

	public static class DiceRoll {
		public int dice;
		public int result;

		public DiceRoll(int dice, int result) {
			this.dice = dice;
			this.result = result;
		}
	}

	private static Random rnd = new SecureRandom();

	public int diceCount = 1;
	public int diceType = 6;
	public int constant = 0;

	private static Pattern p = Pattern.compile("(\\d*)WA?(\\d*)([+-]?\\d*)", Pattern.CASE_INSENSITIVE);

	public static Dice parseDice(String tp) {

		Matcher m = p.matcher(tp);
		Dice dice = null;

		if (m.matches()) {
			dice = new Dice();

			try {
				String s = m.group(1);
				if (!TextUtils.isEmpty(s)) {
					dice.diceCount = Integer.parseInt(s);
				}
				s = m.group(2);
				if (!TextUtils.isEmpty(s)) {
					dice.diceType = Integer.parseInt(s);
				}
				s = m.group(3);
				if (!TextUtils.isEmpty(s)) {
					dice.constant = Util.parseInteger(s);
				}
			} catch (IllegalStateException e) {
				Debug.error("unable to parse " + tp, e);
				return null;
			} catch (NumberFormatException e) {
				Debug.error("unable to parse " + tp, e);
				return null;
			}
		}

		return dice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(diceCount);
		sb.append("W");
		if (diceType != 6)
			sb.append(diceType);

		if (constant != 0)
			sb.append(Util.toProbe(constant));

		return sb.toString();
	}

	public static DiceRoll diceRoll(int max) {
		return new DiceRoll(max, dice(max));
	}

	/**
	 * Returns a value between 1 and max (incl)
	 * 
	 * @param max
	 * @return
	 */
	public static int dice(int max) {
		return rnd.nextInt(max) + 1;
	}
}
