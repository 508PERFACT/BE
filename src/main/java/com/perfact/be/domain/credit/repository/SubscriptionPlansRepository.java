package com.perfact.be.domain.credit.repository;

import com.perfact.be.domain.credit.entity.SubscriptionPlans;
import com.perfact.be.domain.credit.entity.enums.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, Long> {
  Optional<SubscriptionPlans> findByName(PlanType name);

}
