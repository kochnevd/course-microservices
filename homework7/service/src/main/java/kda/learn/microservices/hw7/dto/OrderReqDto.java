package kda.learn.microservices.hw7.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;

/**
 * OrderReqDto
 */
@Validated
public class OrderReqDto {
  @JsonProperty("userId")
  private Integer userId = null;

  @JsonProperty("cost")
  private BigDecimal cost = null;

  @JsonProperty("orderContent")
  private String orderContent = null;

  public OrderReqDto userId(Integer userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   **/
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public OrderReqDto cost(BigDecimal cost) {
    this.cost = cost;
    return this;
  }

  /**
   * Get cost
   * @return cost
   **/
  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public OrderReqDto orderContent(String orderContent) {
    this.orderContent = orderContent;
    return this;
  }

  /**
   * Get orderContent
   * @return orderContent
   **/
  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderReqDto orderBody = (OrderReqDto) o;
    return Objects.equals(this.userId, orderBody.userId) &&
        Objects.equals(this.cost, orderBody.cost) &&
        Objects.equals(this.orderContent, orderBody.orderContent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, cost, orderContent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderReqDto {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    cost: ").append(toIndentedString(cost)).append("\n");
    sb.append("    orderContent: ").append(toIndentedString(orderContent)).append("\n");
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
