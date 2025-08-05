package com.perfact.be.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleResponse {
  private String title;
  private String date;
  private String content;
}