/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.wastelesscorp.platform.app.security.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.Period;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

// TODO refactor this nasty code

/**
 * A service to create JWT objects, this one is used when an exchange provides basic authentication.
 * If authentication is successful, a token is added in the response
 */
public class JwtTokenService {
    private final JWSVerifier jwsVerifier;
    private final JWSSigner jwsSigner;

    JwtTokenService(@Value("${secrets.jwt.signer}") String secret) {
        try {
            this.jwsSigner = new MACSigner(secret);
        } catch (KeyLengthException e) {
            throw new IllegalStateException(e);
        }
        try {
            this.jwsVerifier = new MACVerifier(secret);
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
    }
    /**
     * Create and sign a JWT object using information from the current authenticated principal
     *
     * @param subject Name of current principal
     * @param credentials Credentials of current principal
     * @param authorities A collection of granted authorities for this principal
     * @return String representing a valid token
     */
    public String generateToken(
            String subject,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        SignedJWT signedJWT;
        JWTClaimsSet claimsSet;
        claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(subject)
                        .issuer("rapha.io")
                        .expirationTime(new Date(getExpiration()))
                        .claim(
                                "roles",
                                authorities.stream()
                                        .map(GrantedAuthority.class::cast)
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.joining(",")))
                        .build();

        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(jwsSigner);
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return signedJWT.serialize();
    }

    public Mono<SignedJWT> verifyToken(String token) {
        return Mono.justOrEmpty(createJWS(token))
                .filter(token1 -> getExpirationDate(token1).after(Date.from(Instant.now())))
                .filter(
                        token2 -> {
                            try {
                                return token2.verify(this.jwsVerifier);
                            } catch (JOSEException e) {
                                e.printStackTrace();
                                return false;
                            }
                        });
    }

    private SignedJWT createJWS(String token) {
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date getExpirationDate(SignedJWT token) {
        try {
            return token.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a millisecond time representation 24hrs from now to be used as the time the currently
     * token will be valid
     *
     * @return Time representation 24 from now
     */
    private static long getExpiration() {
        return new Date().toInstant().plus(Period.ofDays(1)).toEpochMilli();
    }
}
