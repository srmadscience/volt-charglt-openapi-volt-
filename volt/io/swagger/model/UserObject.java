/*
 * 
 * 
 *
 * 
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.TimestampType;
import io.swagger.model.UserObjectUsage;
import io.swagger.model.UserRecentTransactions;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * UserObject
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2024-02-19T21:28:12.651360Z[Europe/Dublin]")
public class UserObject   {
  @JsonProperty("userId")
  private Long userId = null;

  @JsonProperty("statusCode")
  private Integer statusCode = null;

  @JsonProperty("lockId")
  private Long lockId = null;

  @JsonProperty("userLastSeen")
  private TimestampType userLastSeen = null;

  @JsonProperty("userSoftlockExpiry")
  private TimestampType userSoftlockExpiry = null;

  @JsonProperty("balance")
  private Long balance = null;

  @JsonProperty("userUsage")
  private List<UserObjectUsage> userUsage = null;

  @JsonProperty("recentTransactions")
  private List<UserRecentTransactions> recentTransactions = null;

  public UserObject userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   **/
  @JsonProperty("userId")
  @Schema(description = "")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public UserObject statusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Get statusCode
   * @return statusCode
   **/
  @JsonProperty("statusCode")
  @Schema(description = "")
  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public UserObject lockId(Long lockId) {
    this.lockId = lockId;
    return this;
  }

  /**
   * Get lockId
   * @return lockId
   **/
  @JsonProperty("lockId")
  @Schema(description = "")
  public Long getLockId() {
    return lockId;
  }

  public void setLockId(Long lockId) {
    this.lockId = lockId;
  }

  public UserObject userLastSeen(TimestampType userLastSeen) {
    this.userLastSeen = userLastSeen;
    return this;
  }

  /**
   * Get userLastSeen
   * @return userLastSeen
   **/
  @JsonProperty("userLastSeen")
  @Schema(description = "")
  @Valid
  public TimestampType getUserLastSeen() {
    return userLastSeen;
  }

  public void setUserLastSeen(TimestampType userLastSeen) {
    this.userLastSeen = userLastSeen;
  }

  public UserObject userSoftlockExpiry(TimestampType userSoftlockExpiry) {
    this.userSoftlockExpiry = userSoftlockExpiry;
    return this;
  }

  /**
   * Get userSoftlockExpiry
   * @return userSoftlockExpiry
   **/
  @JsonProperty("userSoftlockExpiry")
  @Schema(description = "")
  @Valid
  public TimestampType getUserSoftlockExpiry() {
    return userSoftlockExpiry;
  }

  public void setUserSoftlockExpiry(TimestampType userSoftlockExpiry) {
    this.userSoftlockExpiry = userSoftlockExpiry;
  }

  public UserObject balance(Long balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
   **/
  @JsonProperty("balance")
  @Schema(description = "")
  public Long getBalance() {
    return balance;
  }

  public void setBalance(Long balance) {
    this.balance = balance;
  }

  public UserObject userUsage(List<UserObjectUsage> userUsage) {
    this.userUsage = userUsage;
    return this;
  }

  public UserObject addUserUsageItem(UserObjectUsage userUsageItem) {
    if (this.userUsage == null) {
      this.userUsage = new ArrayList<UserObjectUsage>();
    }
    this.userUsage.add(userUsageItem);
    return this;
  }

  /**
   * Get userUsage
   * @return userUsage
   **/
  @JsonProperty("userUsage")
  @Schema(description = "")
  @Valid
  public List<UserObjectUsage> getUserUsage() {
    return userUsage;
  }

  public void setUserUsage(List<UserObjectUsage> userUsage) {
    this.userUsage = userUsage;
  }

  public UserObject recentTransactions(List<UserRecentTransactions> recentTransactions) {
    this.recentTransactions = recentTransactions;
    return this;
  }

  public UserObject addRecentTransactionsItem(UserRecentTransactions recentTransactionsItem) {
    if (this.recentTransactions == null) {
      this.recentTransactions = new ArrayList<UserRecentTransactions>();
    }
    this.recentTransactions.add(recentTransactionsItem);
    return this;
  }

  /**
   * Get recentTransactions
   * @return recentTransactions
   **/
  @JsonProperty("recentTransactions")
  @Schema(description = "")
  @Valid
  public List<UserRecentTransactions> getRecentTransactions() {
    return recentTransactions;
  }

  public void setRecentTransactions(List<UserRecentTransactions> recentTransactions) {
    this.recentTransactions = recentTransactions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserObject userObject = (UserObject) o;
    return Objects.equals(this.userId, userObject.userId) &&
        Objects.equals(this.statusCode, userObject.statusCode) &&
        Objects.equals(this.lockId, userObject.lockId) &&
        Objects.equals(this.userLastSeen, userObject.userLastSeen) &&
        Objects.equals(this.userSoftlockExpiry, userObject.userSoftlockExpiry) &&
        Objects.equals(this.balance, userObject.balance) &&
        Objects.equals(this.userUsage, userObject.userUsage) &&
        Objects.equals(this.recentTransactions, userObject.recentTransactions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, statusCode, lockId, userLastSeen, userSoftlockExpiry, balance, userUsage, recentTransactions);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserObject {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
    sb.append("    lockId: ").append(toIndentedString(lockId)).append("\n");
    sb.append("    userLastSeen: ").append(toIndentedString(userLastSeen)).append("\n");
    sb.append("    userSoftlockExpiry: ").append(toIndentedString(userSoftlockExpiry)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    userUsage: ").append(toIndentedString(userUsage)).append("\n");
    sb.append("    recentTransactions: ").append(toIndentedString(recentTransactions)).append("\n");
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