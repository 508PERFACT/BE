package com.perfact.be.domain.credit.repository;

import com.perfact.be.domain.credit.entity.CreditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLogRepository extends JpaRepository<CreditLog, Long> {


}
