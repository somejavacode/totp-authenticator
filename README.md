# TOTP Generator 

A minimal command line version of
https://en.wikipedia.org/wiki/Google_Authenticator

Which is in fact a specific TOTP according to
http://www.ietf.org/rfc/rfc6238.txt

## usage hints

```
mvn clean test
set CLASSPATH=target/classes
java OtpMain "aaaa bbbb cccc dddd eeee ffff gggg hhhh"
or
java OtpMain "aaaa bbbb cccc dddd eeee ffff gggg hhhh" 0
```
