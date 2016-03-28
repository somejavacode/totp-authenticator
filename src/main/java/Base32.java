// https://en.wikipedia.org/wiki/Base32
// https://tools.ietf.org/html/rfc4648
// NOTE: could use apache commons codec but avoiding dependency.
public class Base32 {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * decode base32 string to byte array
     * @param input encoded string
     * @return decoded byte array
     */
    public static byte[] decode(String input) {

        // use uppercase, remove blanks
        input = input.toUpperCase().replaceAll(" ", "");

        // check for padding
        if (input.contains("=")) {
             throw new RuntimeException("padding not supported.");
        }
        // check size
        if (input.length() % 8 != 0) {
            // not aligned: 8 chars -> 5 byte
            throw new RuntimeException("currently only aligned (8 chars) blocks supported.");
        }
        int length = input.length() * 5 / 8;
        byte[] result = new byte[length];

        int bitPos = 0;
        for (char c : input.toUpperCase().toCharArray()) {
            int cValue = ALPHABET.indexOf(c); // 5 bit value (0..31)
            if (cValue == -1) {
                throw new RuntimeException("invalid base32 character: " + c);
            }
            // each bit is mapped like this.
            // definition: "cxy" = char index x bit y
            // definition: "bxy" = byte index x bit y
            // input char bits:  c04 c03 c02 c01 c00 c14 c13 c12 c11 c10 ...
            // output byte bits: b07 b06 b05 b04 b03 b02 b01 b00 b17 b16 ...
            // bitPos starts with 0 (c04 -> b07) from the left side.
            for (int bit = 0; bit < 5; bit++) {
                if ((cValue & (1 << (4 - bit))) > 0) { // is input bit set?
                    result[bitPos / 8] |= 1 << (7 - bitPos % 8); // set output bit
                }
                bitPos++;
            }
        }
        return result;
    }

    /**
     * encode byte array to base32 string
     * @param bytes byte array
     * @return encoded string
     */
    public static String encode(byte[] bytes) {
        // check size
        if (bytes.length % 5 != 0) {
            // not aligned: 5 bytes -> 8 chars
            throw new RuntimeException("currently only aligned (5 bytes) blocks supported.");
        }
        int length = bytes.length * 8 / 5;
        byte[] result = new byte[length];
        int bitPos = 0;
        // see according comment in "decode"
        for (byte b : bytes) {
            for (int bit = 0; bit < 8; bit++) {
                if ((b & (1 << (7 - bit))) > 0) {
                    result[bitPos / 5] |= 1 << (4 - bitPos % 5);
                }
                bitPos++;
            }
        }
        // convert bytes according to alphabet
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET.charAt(result[i]);
        }
        return new String(chars);
    }

}
