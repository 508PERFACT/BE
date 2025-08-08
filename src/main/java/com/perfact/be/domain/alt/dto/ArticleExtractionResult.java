package com.perfact.be.domain.alt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ArticleExtractionResult {
  private String title;
  private String publicationDate;
  private String content;
}
