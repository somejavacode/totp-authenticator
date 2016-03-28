import org.junit.Assert;
import org.junit.Test;

public class OtpTest {

    /*
    removed code that was testing original RFC6238 TOTP code.
    @Test
    public void testRFC6238() throws Exception {
        // see Appendix B
        String key = "3132333435363738393031323334353637383930";
        // these are really "smart" parameters. using String for any type?
        String password1 = TOTP.generateTOTP(key, "0000000000000001", "8");
        Assert.assertEquals("94287082", password1);
        String password2 = TOTP.generateTOTP(key, "00000000023523EC", "8");
        Assert.assertEquals("07081804", password2);
        String password3 = TOTP.generateTOTP(key, "00000000023523ED", "8");
        Assert.assertEquals("14050471", password3);
    }
    */

    @Test
    public void testRFC6238WithSHA1() throws Exception {
        // see Appendix B of RFC6238
        byte[] key = "12345678901234567890".getBytes("ASCII"); // 20 byte
        OtpGenerator generator = new OtpGenerator(key, OtpGenerator.HMAC_SHA1, 8);
        String password1 = generator.getPassword(59 / 30);
        Assert.assertEquals("94287082", password1);
        String password2 = generator.getPassword(1111111109L / 30);
        Assert.assertEquals("07081804", password2);
        String password3 = generator.getPassword(1111111111L / 30);
        Assert.assertEquals("14050471", password3);
    }

    @Test
    public void testRFC6238WithSHA256() throws Exception {
        // see Appendix B of RFC6238
        byte[] key = "12345678901234567890123456789012".getBytes("ASCII"); // 32 byte
        OtpGenerator generator = new OtpGenerator(key, OtpGenerator.HMAC_SHA256, 8);
        String password1 = generator.getPassword(59 / 30);
        Assert.assertEquals("46119246", password1);
        String password2 = generator.getPassword(1111111109L / 30);
        Assert.assertEquals("68084774", password2);
        String password3 = generator.getPassword(1111111111L / 30);
        Assert.assertEquals("67062674", password3);
    }

    @Test
    public void testRFC6238WithSHA512() throws Exception {
        // see Appendix B of RFC6238
        byte[] key = "1234567890123456789012345678901234567890123456789012345678901234".getBytes("ASCII"); // 64 byte
        OtpGenerator generator = new OtpGenerator(key, OtpGenerator.HMAC_SHA512, 8);
        String password1 = generator.getPassword(59 / 30);
        Assert.assertEquals("90693936", password1);
        String password2 = generator.getPassword(1111111109L / 30);
        Assert.assertEquals("25091201", password2);
        String password3 = generator.getPassword(1111111111L / 30);
        Assert.assertEquals("99943326", password3);
    }

    @Test
    public void testGoogleApp() throws Exception {
        // enter this key in google authenticator app.
        // menu - add account - add account manually
        // name: testing, key: see next line, "time based"
        String keyB32 = "aaaa bbbb cccc dddd eeee ffff gggg hhhh";
        byte[] key = Base32.decode(keyB32);
        OtpGenerator generator = new OtpGenerator(key);
        long now = System.currentTimeMillis();
        long counter = OtpGenerator.getCounter(now);
        for (int i = 0; i < 3; i++) {
            generator.showPassword(counter + i);
        }
        // compare three values to be identical (takes up to 90s)
        // tested OK:
        // - Authenticator Open Source 2.21 via f-droid
        // - Authenticator 4.44 (why closed source? why access identity?) via play store
    }

}
