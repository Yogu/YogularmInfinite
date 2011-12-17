package de.yogularm.utils;

public class Numbers {
	public static boolean isPowerOfTwo(int value) {
		// http://en.wikipedia.org/wiki/Power_of_two#Fast_algorithm_to_check_if_a_positive_number_is_a_power_of_two
		return value > 0 && (value & (value - 1)) == 0;
	}
}
