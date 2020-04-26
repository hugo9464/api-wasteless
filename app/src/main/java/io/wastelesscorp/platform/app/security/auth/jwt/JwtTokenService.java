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

import static io.wastelesscorp.platform.support.exceptions.ExceptionUtils.defaultOnError;
import static java.time.Period.ofDays;
import static java.util.stream.Collectors.joining;

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
import java.time.Clock;
import java.util.Collection;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

/**
 * A service to create JWT objects, this one is used when an exchange provides basic authentication.
 * If authentication is successful, a token is added in the response
 */
public class JwtTokenService {
  private final JWSVerifier jwsVerifier;
  private final JWSSigner jwsSigner;
  private final Clock clock;

  JwtTokenService(@Value("${secrets.jwt.signer}") String secret, Clock clock) {
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
    this.clock = clock;
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
      String subject, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer("wasteless.io")
            .expirationTime(new Date(getExpiration()))
            .claim(
                "roles",
                authorities.stream()
                    .map(GrantedAuthority.class::cast)
                    .map(GrantedAuthority::getAuthority)
                    .collect(joining(",")))
            .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    try {
      signedJWT.sign(jwsSigner);
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
    return signedJWT.serialize();
  }

  public Mono<SignedJWT> verifyToken(String token) {
    return Mono.fromCallable(() -> SignedJWT.parse(token))
        .filter(
            defaultOnError(
                t -> t.getJWTClaimsSet().getExpirationTime().after(Date.from(clock.instant())),
                false))
        .filter(defaultOnError(t -> t.verify(this.jwsVerifier), false))
        .onErrorResume(t -> Mono.empty());
  }
  /**
   * Returns a millisecond time representation 24hrs from now to be used as the time the currently
   * token will be valid
   *
   * @return Time representation 24 from now
   */
  private long getExpiration() {
    return clock.instant().plus(ofDays(1)).toEpochMilli();
  }
}
