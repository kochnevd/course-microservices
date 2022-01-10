package kda.learn.microservices.hw7.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

/**
 * AccountRespDto
 */
@Validated
public class AccountRespDto {
    @JsonProperty("userId")
    private Long userId = null;

    public AccountRespDto userId(Long userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Get userId
     * @return userId
     **/
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountRespDto accountRespDto = (AccountRespDto) o;
        return Objects.equals(this.userId, accountRespDto.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AccountRespDto {\n");

        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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
