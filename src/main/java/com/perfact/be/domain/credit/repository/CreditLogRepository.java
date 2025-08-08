package com.perfact.be.domain.credit.repository;

import com.perfact.be.domain.credit.entity.CreditLog;
import com.perfact.be.domain.credit.entity.enums.CreditLogType;
import com.perfact.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLogRepository extends JpaRepository<CreditLog, Long> {

  @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CreditLog c " +
      "WHERE c.user = :user " +
      "AND c.type = :type " +
      "AND DATE(c.createdAt) = CURRENT_DATE")
  Long sumTodayUsedCreditByUserAndType(
      @Param("user") User user,
      @Param("type") CreditLogType type
  );

  @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CreditLog c " +
      "WHERE c.user = :user " +
      "AND c.type = :type " +
      "AND FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m') = :yearMonth")
  Long sumMonthlyUsedCreditByUserAndType(
      @Param("user") User user,
      @Param("type") CreditLogType type,
      @Param("yearMonth") String yearMonth
  );



}
