/**
 *  simple command line version of google authenticator<br/>
 *  it is in fact a "Time-Based One-Time Password" with SHA1, T0 = 0, time step = 30s and 6 digits based on
 *  https://tools.ietf.org/html/rfc6238
 */
public class OtpMain {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.print("Usage: OtpMain \"<key>\" [count]");
            System.out.print(" default count 5 will show next 5 passwords. count 0 will show endless password on time.");
            return;
        }
        String keyB32 = args[0];

        int intervals = 5; // 0 = endless
        if (args.length > 1) {
            intervals = Integer.parseInt(args[1]);
        }

        byte[] key = Base32.decode(keyB32);

        OtpGenerator generator = new OtpGenerator(key);

        if (intervals == 0) {
            while (true) {  // endless...
                long now = System.currentTimeMillis();
                long counter = OtpGenerator.getCounter(now);
                generator.showPassword(counter);
                long next = (counter + 1) * OtpGenerator.TIME_STEP;
                Thread.sleep(next - now + 20); // add 20ms to be in next interval for sure
            }
        }

        long now = System.currentTimeMillis();
        long counter = OtpGenerator.getCounter(now);

        for (int i = 0; i < intervals; i++) {
            generator.showPassword(counter + i);
        }
    }

}
