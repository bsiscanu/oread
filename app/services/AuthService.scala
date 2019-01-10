package services

import java.util.Date
import javax.inject.Singleton
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


@Singleton
class AuthService {

  // move it to the config file
  val secret: String = "SECRET!"

  def sign(user: String): String = {
    Jwts.builder
      .setSubject(user)
      .setIssuer("@domy.io")
      .setAudience("premium") // if it's premium show it
      .setIssuedAt(new Date())
      .signWith(SignatureAlgorithm.HS256, secret)
      .compact()
  }


  def check(jws: String, user: String): Boolean = {
    Jwts.parser.setSigningKey(secret).parseClaimsJws(jws).getBody.getSubject == user
  }
}
