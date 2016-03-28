import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class Base32Test {

    private static final long SEED = 123231L;

    @Test
    public void testRandom() {
        Random random = new Random(SEED);
        for (int size = 0; size < 5; size++) {
            byte[] input = new byte[size * 5];
            for (int i = 0; i < 1000; i++) {
                // fill random bytes
                random.nextBytes(input);
                String encoded = Base32.encode(input);
                byte[] decoded = Base32.decode(encoded);
                Assert.assertArrayEquals(input, decoded);
            }
        }
    }

    @Test
    public void testFail() {
        try {
            Base32.encode(new byte[] {0, 0, 0});
            Assert.fail();
        }
        catch (Exception e) {
            System.out.println("expected: " + e);
        }
        try {
            Base32.decode("MZXW6===");
            Assert.fail();
        }
        catch (Exception e) {
            System.out.println("expected: " + e);
        }
    }

    @Test
    public void testRFC4648() throws Exception {
        // known values from RFC4648 (only one without padding)
        byte[] bytes = "fooba".getBytes("ASCII");
        String encoded = "MZXW6YTB";
        Assert.assertEquals(encoded, Base32.encode(bytes));
        Assert.assertArrayEquals(bytes, Base32.decode(encoded));
    }
}
