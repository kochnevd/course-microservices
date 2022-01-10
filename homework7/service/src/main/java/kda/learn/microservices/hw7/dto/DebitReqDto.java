package kda.learn.microservices.hw7.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;

/**
 * DebitReqDto
 */
@Validated
public class DebitReqDto {
  @JsonProperty("accountId")
  private Long accountId = null;

  @JsonProperty("sum")
  private BigDecimal sum = null;

  public DebitReqDto accountId(Long accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
   **/
  public Long getAccountId() {
    return accountId;
  }

  public DebitReqDto sum(BigDecimal sum) {
    this.sum = sum;
    return this;
  }

  /**
   * Get sum
   * @return sum
   **/
  public BigDecimal getSum() {
    return sum;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DebitReqDto debitReqDto = (DebitReqDto) o;
    return Objects.equals(this.accountId, debitReqDto.accountId) &&
        Objects.equals(this.sum, debitReqDto.sum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, sum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DebitReqDto {\n");
    
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    sum: ").append(toIndentedString(sum)).append("\n");
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
