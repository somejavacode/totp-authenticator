import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * generates time based one time passwords according to RFC6238
 * http://www.ietf.org/rfc/rfc6238.txt
 */
public class OtpGenerator {

    public static final int TIME_STEP = 30 * 1000; // time step of 30s (in milliseconds)
    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_SHA512 = "HmacSHA512";
    private Mac mac;
    private int digits;

    /**
     * create OtpGenerator with SHA1 and password length 6.
     * This values are used by google authenticator
     * @param key the secret key (20 bytes)
     */
    public OtpGenerator(byte[] key) throws Exception {
        this(key, HMAC_SHA1, 6);
    }

    /**
     * create OtpGenerator with according key, algorithm and digit count
     * @param algorithm specific HMAC algorithm
     * @param digits number of digits in password (max 8)
     * @param key the secret key
     */
    public OtpGenerator(byte[] key, String algorithm, int digits) throws Exception {
        mac = Mac.getInstance(algorithm);
        // Note: valid key algorithm parameter is "any not null string". this is odd.
        mac.init(new SecretKeySpec(key, "anything?"));
        this.digits = digits;
    }

    /**
     * get counter based on java time
     * @param time java time (milliseconds since 1.1.1970)
     * @return counter value
     */
    public static long getCounter(long time) {
        return time / TIME_STEP;
    }

    /**
     * get start time of according time interval
     * @param counter counter value
     * @return start of time interval
     */
    public static long getTime(long counter) {
        return counter * TIME_STEP;
    }

    /**
     * print password and start of time interval to console
     * @param counter counter value
     */
    public void showPassword(long counter) {
        long time = getTime(counter);
        // output in local time
        String date = new SimpleDateFormat("HH:mm:ss").format(new Date(time));
        System.out.println(date + " -> " + getPassword(counter));
    }

    /**
     * get password for counter
     * @param counter counter value
     * @return n digit password string
     */
    public synchronized String getPassword(long counter) { // synchronized as mac is reused
        // reset mac
        mac.reset();
        // encode "message" as bytes...
        byte[] messageBytes = ByteBuffer.allocate(8).putLong(counter).array();
        // calculate hash
        byte[] hash = mac.doFinal(messageBytes);
        // offset is last nibble
        int offset = hash[hash.length - 1] & 0xF;
        // create integer from 4 hash bytes with according offset, mask MSB for positive value
        int truncatedHash = ByteBuffer.wrap(hash, offset, 4).getInt() & 0x7FFFFFFF;
        // get last n digits from integer
        int modulo = (int) Math.pow(10, digits);
        int pinValue = truncatedHash % modulo;
        // format with leading zeros
        return String.format("%0" + digits + "d", pinValue);
    }

}
