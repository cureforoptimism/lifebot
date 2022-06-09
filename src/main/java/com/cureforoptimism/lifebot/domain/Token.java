package com.cureforoptimism.lifebot.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Token {
  @Getter @Setter @Id Long tokenId;

  String name;

  @ManyToOne
  @JoinColumn(name = "staked_token_id")
  private StakedToken stakedToken;

  public StakedToken getStakedToken() {
    return stakedToken;
  }

  public void setStakedToken(StakedToken stakedToken) {
    this.stakedToken = stakedToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Token token = (Token) o;
    return tokenId != null && Objects.equals(tokenId, token.tokenId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
