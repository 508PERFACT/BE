package com.perfact.be.domain.credit.entity.enums;

public enum CreditLogType {
  DAILY_RESET("자정 크레딧 초기화"),
  REPORT_CREATE("레포트 생성시 차감"),
  ADMIN_ADJUST("관리자 수동 조정"),

  ;

  private final String description;

  CreditLogType(String description) {
    this.description = description;
  }

  public String getDescription(){
    return description;
  }

}
