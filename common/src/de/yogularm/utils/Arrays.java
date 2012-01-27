package de.yogularm.utils;

// These methods are not implemented in android sdk 8

import java.lang.reflect.Array;

/**
 * This class contains various methods for manipulating arrays (such as
 * sorting and searching). This class also contains a static factory
 * that allows arrays to be viewed as lists.
 *
 * <p>The methods in this class all throw a {@code NullPointerException},
 * if the specified array reference is null, except where noted.
 *
 * <p>The documentation for the methods contained in this class includes
 * briefs description of the <i>implementations</i>. Such descriptions should
 * be regarded as <i>implementation notes</i>, rather than parts of the
 * <i>specification</i>. Implementors should feel free to substitute other
 * algorithms, so long as the specification itself is adhered to. (For
 * example, the algorithm used by {@code sort(Object[])} does not have to be
 * a MergeSort, but it does have to be <i>stable</i>.)
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * 
 * <p>Note: This class (de.yogularm.utils.Arrays) only holds a fraction of the original class
 * java.util.Arrays.</p>
 *
 * @author Josh Bloch
 * @author Neal Gafter
 * @author John Rose
 * @since  1.2
 */
public class Arrays {

  // Cloning

  /**
   * Copies the specified array, truncating or padding with nulls (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>null</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   * The resulting array is of exactly the same class as the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with nulls
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] copyOf(T[] original, int newLength) {
      return (T[]) copyOf(original, newLength, original.getClass());
  }

  /**
   * Copies the specified array, truncating or padding with nulls (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>null</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   * The resulting array is of the class <tt>newType</tt>.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @param newType the class of the copy to be returned
   * @return a copy of the original array, truncated or padded with nulls
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @throws ArrayStoreException if an element copied from
   *     <tt>original</tt> is not of a runtime type that can be stored in
   *     an array of class <tt>newType</tt>
   * @since 1.6
   */
  public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
      @SuppressWarnings("unchecked")
      T[] copy = ((Object)newType == (Object)Object[].class)
          ? (T[]) new Object[newLength]
          : (T[]) Array.newInstance(newType.getComponentType(), newLength);
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>(byte)0</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static byte[] copyOf(byte[] original, int newLength) {
      byte[] copy = new byte[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>(short)0</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static short[] copyOf(short[] original, int newLength) {
      short[] copy = new short[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>0</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static int[] copyOf(int[] original, int newLength) {
      int[] copy = new int[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>0L</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static long[] copyOf(long[] original, int newLength) {
      long[] copy = new long[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with null characters (if necessary)
   * so the copy has the specified length.  For all indices that are valid
   * in both the original array and the copy, the two arrays will contain
   * identical values.  For any indices that are valid in the copy but not
   * the original, the copy will contain <tt>'\\u000'</tt>.  Such indices
   * will exist if and only if the specified length is greater than that of
   * the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with null characters
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static char[] copyOf(char[] original, int newLength) {
      char[] copy = new char[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>0f</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static float[] copyOf(float[] original, int newLength) {
      float[] copy = new float[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with zeros (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>0d</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with zeros
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static double[] copyOf(double[] original, int newLength) {
      double[] copy = new double[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified array, truncating or padding with <tt>false</tt> (if necessary)
   * so the copy has the specified length.  For all indices that are
   * valid in both the original array and the copy, the two arrays will
   * contain identical values.  For any indices that are valid in the
   * copy but not the original, the copy will contain <tt>false</tt>.
   * Such indices will exist if and only if the specified length
   * is greater than that of the original array.
   *
   * @param original the array to be copied
   * @param newLength the length of the copy to be returned
   * @return a copy of the original array, truncated or padded with false elements
   *     to obtain the specified length
   * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static boolean[] copyOf(boolean[] original, int newLength) {
      boolean[] copy = new boolean[newLength];
      System.arraycopy(original, 0, copy, 0,
                       Math.min(original.length, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>null</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   * <p>
   * The resulting array is of exactly the same class as the original array.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with nulls to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] copyOfRange(T[] original, int from, int to) {
      return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>null</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   * The resulting array is of the class <tt>newType</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @param newType the class of the copy to be returned
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with nulls to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @throws ArrayStoreException if an element copied from
   *     <tt>original</tt> is not of a runtime type that can be stored in
   *     an array of class <tt>newType</tt>.
   * @since 1.6
   */
  public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      @SuppressWarnings("unchecked")
      T[] copy = ((Object)newType == (Object)Object[].class)
          ? (T[]) new Object[newLength]
          : (T[]) Array.newInstance(newType.getComponentType(), newLength);
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>(byte)0</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static byte[] copyOfRange(byte[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      byte[] copy = new byte[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>(short)0</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static short[] copyOfRange(short[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      short[] copy = new short[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>0</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static int[] copyOfRange(int[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      int[] copy = new int[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>0L</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static long[] copyOfRange(long[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      long[] copy = new long[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>'\\u000'</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with null characters to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static char[] copyOfRange(char[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      char[] copy = new char[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>0f</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static float[] copyOfRange(float[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      float[] copy = new float[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>0d</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with zeros to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static double[] copyOfRange(double[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      double[] copy = new double[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }

  /**
   * Copies the specified range of the specified array into a new array.
   * The initial index of the range (<tt>from</tt>) must lie between zero
   * and <tt>original.length</tt>, inclusive.  The value at
   * <tt>original[from]</tt> is placed into the initial element of the copy
   * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
   * Values from subsequent elements in the original array are placed into
   * subsequent elements in the copy.  The final index of the range
   * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
   * may be greater than <tt>original.length</tt>, in which case
   * <tt>false</tt> is placed in all elements of the copy whose index is
   * greater than or equal to <tt>original.length - from</tt>.  The length
   * of the returned array will be <tt>to - from</tt>.
   *
   * @param original the array from which a range is to be copied
   * @param from the initial index of the range to be copied, inclusive
   * @param to the final index of the range to be copied, exclusive.
   *     (This index may lie outside the array.)
   * @return a new array containing the specified range from the original array,
   *     truncated or padded with false elements to obtain the required length
   * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
   *     or {@code from > original.length}
   * @throws IllegalArgumentException if <tt>from &gt; to</tt>
   * @throws NullPointerException if <tt>original</tt> is null
   * @since 1.6
   */
  public static boolean[] copyOfRange(boolean[] original, int from, int to) {
      int newLength = to - from;
      if (newLength < 0)
          throw new IllegalArgumentException(from + " > " + to);
      boolean[] copy = new boolean[newLength];
      System.arraycopy(original, from, copy, 0,
                       Math.min(original.length - from, newLength));
      return copy;
  }
}
