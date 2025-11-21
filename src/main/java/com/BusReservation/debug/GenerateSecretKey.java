package com.BusReservation.debug;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class GenerateSecretKey {

    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretKey = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("The secret key is " + secretKey);
    }
}
