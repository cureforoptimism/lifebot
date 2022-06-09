package com.cureforoptimism.lifebot.domain;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifeForm {
  @Getter @Setter @Id String id;

  Date creationTimestamp;

  @Getter @Setter LifeFormClass lifeFormClass;

  @Getter @Setter Integer treasureBoost;

  @OneToMany(mappedBy = "lifeForm", orphanRemoval = true)
  @Getter
  @Setter
  private Set<StakedToken> stakedTokens = new LinkedHashSet<>();
}
