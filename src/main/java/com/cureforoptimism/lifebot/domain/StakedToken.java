package com.cureforoptimism.lifebot.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StakedToken {
  @Getter @Setter @Id String id;

  @OneToMany(mappedBy = "stakedToken", orphanRemoval = true)
  @Getter
  @Setter
  private Set<Token> tokens = new LinkedHashSet<>();

  @Getter @Setter private Integer quantity;

  @ManyToOne
  @Getter
  @Setter
  @JoinColumn(name = "life_form_id")
  private LifeForm lifeForm;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    StakedToken that = (StakedToken) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
