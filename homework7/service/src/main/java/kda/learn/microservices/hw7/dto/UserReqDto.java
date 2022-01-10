package kda.learn.microservices.hw7.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

/**
 * UserReqDto
 */
@Validated
public class UserReqDto {
  @JsonProperty("login")
  private String login = null;

  @JsonProperty("firstName")
  private String firstName = null;

  @JsonProperty("lastName")
  private String lastName = null;

  @JsonProperty("email")
  private String email = null;

  public UserReqDto login(String login) {
    this.login = login;
    return this;
  }

  /**
   * Get login
   * @return login
   **/
  public String getLogin() {
    return login;
  }

  public UserReqDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   **/
  public String getFirstName() {
    return firstName;
  }

  public UserReqDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   **/
  public String getLastName() {
    return lastName;
  }

  public UserReqDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   **/
  public String getEmail() {
    return email;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserReqDto userBody = (UserReqDto) o;
    return Objects.equals(this.login, userBody.login) &&
        Objects.equals(this.firstName, userBody.firstName) &&
        Objects.equals(this.lastName, userBody.lastName) &&
        Objects.equals(this.email, userBody.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login, firstName, lastName, email);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserReqDto {\n");
    
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
