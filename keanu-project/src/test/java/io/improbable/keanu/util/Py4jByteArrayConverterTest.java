package io.improbable.keanu.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Py4jByteArrayConverterTest {

    @Test
    public void canCreateIntegerArrayFromByteArray() {
        byte byte1 = Byte.parseByte("00000110", 2);
        byte byte2 = Byte.parseByte("00000011", 2);
        byte zeroByte = Byte.parseByte("00000000", 2);
        byte[] bytes = new byte[] {
            byte1, zeroByte, zeroByte, zeroByte,
            byte2, zeroByte, zeroByte, zeroByte
        };

        int[] ints = Py4jByteArrayConverter.toIntegerArray(bytes, 4);
        int[] desiredInts = new int[] { 6, 3 };

        assertArrayEquals(ints, desiredInts);
    }

    @Test
    public void canCreateDoubleArrayFromByteArray() {
        byte byte1 = Byte.parseByte("01000000", 2);
        byte byte2 = Byte.parseByte("00011000", 2);
        byte byte3 = Byte.parseByte("00001000", 2);
        byte zeroByte = Byte.parseByte("00000000", 2);
        byte[] bytes = new byte[] {
            zeroByte, zeroByte, zeroByte, zeroByte, zeroByte, zeroByte, byte2, byte1,
            zeroByte, zeroByte, zeroByte, zeroByte, zeroByte, zeroByte, byte3, byte1
        };

        double[] doubles = Py4jByteArrayConverter.toDoubleArray(bytes, 8);
        double[] desiredDoubles = new double[] { 6.0, 3.0 };

        assertArrayEquals(doubles, desiredDoubles, 0.0);
    }

    @Test
    public void canCreateBooleanArrayFromByteArray() {
        byte byte1 = (byte) Integer.parseInt("10101010", 2);
        byte byte2 = (byte) Integer.parseInt("10000000", 2);
        byte[] bytes = new byte[] { byte1, byte2 };

        boolean[] bools = Py4jByteArrayConverter.toBooleanArray(bytes, bytes.length * 8);
        boolean[] desiredBools = new boolean[] {
            true, false, true, false, true, false, true, false,
            true, false, false, false, false, false, false, false
        };

        assertArrayEquals(bools, desiredBools);
    }
}
