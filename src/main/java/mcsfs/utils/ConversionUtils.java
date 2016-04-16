/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcsfs.utils;

import java.util.Arrays;

public class ConversionUtils {
	
	/**
	 * Converts a byte array to an array consisting of unsigned integers
	 * 
	 * @param input
	 * @return
	 */
	public static int[] byteArrayToIntArray(byte[] input) {
		int[] output = new int[input.length];
		for(int i=0; i<input.length; i++) {
			output[i] = Byte.toUnsignedInt(input[i]);
		}
		
		return output;
	}
	
	/**
	 * Converts an array of unsigned integers to a string which substitutes alphabets for ASCII values
	 * wherever possible.
	 * 
	 * Eg. [133, 118, 152, 88] -> 133v152X
	 * @param input
	 * @return
	 */
	public static String intArrayToMixedString(int[] input) {
		StringBuilder output = new StringBuilder();
		
		for(int i=0; i<input.length; i++) {
    		int val = input[i];
    		if((val >= 'a' && val <= 'z') || (val >= 'A' && val <= 'Z'))
    			output.append((char)val);
    		else
    			output.append(String.format("%03d", val));
    	}
		
		return output.toString();
	}
	
	/**
	 * Converts a mixed string consisting of alphabets and numbers to an integer array
	 * which substitutes ASCII values for alphabets.
	 * 
	 * @param input
	 * @return
	 */
	public static int[] mixedStringToIntArray(String input) {
		int[] output = new int[input.length()];
		int k = 0;
		
		for(int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			int num;
			if((c >= '0' && c <= '9')) {
				num = ((int)(c - '0') * 100) + ((int)(input.charAt(i + 1) - '0') * 10) + (int)(input.charAt(i + 2) - '0');
				i += 2;
			} else {
				num = (int)c;
			}
			output[k++] = num;
		}
		
		return Arrays.copyOf(output, k);
	}
}
