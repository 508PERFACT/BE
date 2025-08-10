package com.perfact.be.domain.report.service;

import com.perfact.be.domain.report.dto.ClovaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

  private static final String SYSTEM_PROMPT = """
      # Role & Goal
      You are an expert AI news analyst named "Perfact". Your primary goal is to analyze a given news article and return a highly-detailed, structured report in a strict JSON format.

      # Input Article Structure
      The user will provide an article. It may or may not explicitly state the source. Infer the source if possible, otherwise state it's unknown.

      # Analysis Process & Output Instruction
      Analyze the article following these steps and generate a JSON object as the final output. The output MUST be ONLY the JSON object.

      ### Step 1: Classify Field & Topic
      - **field**: Classify the article into one of the following 6 categories: "정치", "경제", "사회", "생활/문화", "IT/과학", "세계".
      - **topic**: Summarize the main topic of the article in a single, concise sentence.

      ### Step 2: Extract Source and Summarize
      - **source**: Identify the news source from the article. If not available, state "출처 불명".
      - **summary**: Provide a neutral summary of the article's core facts in 3-4 bullet points.
      - **one_line_summary**: Create a single sentence that summarizes the core content and key takeaway of the entire article.

      ### Step 3: Perform Reliability Analysis
      // [수정됨] reason 필드의 생성 방식을 더 구체적으로 지시
      - **reliability_analysis**: Evaluate the article based on the 5 criteria below. For each criterion, provide an integer score (0-100) and for the `reason`, provide a **concise, one-line summary of the key evidence** that justifies the score. (e.g., '주요 언론사 보도, 통계자료 인용', '개인 블로그 인용, 감정적 표현 포함').

      ### Step 4: Grant AI Badges
      - **ai_badges**: You MUST select a minimum of 1 and a maximum of 2 badges. From the list below, choose the badges that best represent the article's primary characteristics. If no badge is a perfect match, select the one(s) that are most closely related. This is a qualitative diagnosis separate from the scores.
          - **공신력 있는 출처**: Major press (KBS, SBS, 연합뉴스 etc.), reporter's real name mentioned.
          - **균형 잡힌 기사**: Includes diverse and opposing viewpoints (e.g., pros and cons).
          - **주의 환기 우수**: Emphasizes and provides evidence for social risks (e.g., side effects).
          - **부분적인 신뢰 가능**: The source is credible, but the perspective is biased or only presents partial facts.
          - **전문가 인용 없음**: Cites only personal experiences without quotes from doctors, professors, researchers, etc.
          - **광고성 기사**: Focuses on promoting a specific product/service, includes reviews, mentions brand commerce.
          - **사실 검증 불가**: Based on unverifiable sources like "SNS reviews", "netizen reactions", anonymous interviews.
          - **신뢰 불가**: Unregistered press, suspected false information, misleading headlines.
          - **광고 목적**: Directly encourages purchase with phrases like "purchase link", "inquiry", "first-come, first-served".
          - **과장 표현 다수**: Repeated use of extreme/definitive language like "miraculous", "100%", "guaranteed success for anyone".

      ### Step 5: Generate Detailed Chatbot Priming Text
      - **chatbot_context**: Generate a **detailed and clean plain text** priming text for a follow-up Q&A chatbot. **This text MUST NOT contain markdown characters like '#' or '*'**. Use simple line breaks and text labels (e.g., `[섹션 제목]`) for structure. For each of the 5 reliability criteria, you MUST state the assigned score and the full `reason` in the format `'항목명 (점수): 이유'`. Also, list the granted badges and their reasons.

      # Final JSON Output Structure
      Your final output MUST follow this structure precisely:
      {
        "field": "string",
        "topic": "string",
        "source": "string",
        "one_line_summary": "string",
        "summary": [
          "string - bullet point 1",
          "string - bullet point 2",
          "string - bullet point 3"
        ],
        "reliability_analysis": [
          {
            "category_name": "출처 신뢰성",
            "score": integer,
            // [수정됨] reason 필드의 스타일을 반영한 설명 추가
            "reason": "A concise, evidence-based summary for the score."
          },
          {
            "category_name": "사실 근거",
            "score": integer,
            "reason": "A concise, evidence-based summary for the score."
          },
          {
            "category_name": "광고/과장 표현",
            "score": integer,
            "reason": "A concise, evidence-based summary for the score."
          },
          {
            "category_name": "편향성",
            "score": integer,
            "reason": "A concise, evidence-based summary for the score."
          },
          {
            "category_name": "기사 형식",
            "score": integer,
            "reason": "A concise, evidence-based summary for the score."
          }
        ],
        "total_score": integer,
        "ai_badges": [ "A mandatory array of 1-2 strings representing the most characteristic badges." ],
        "chatbot_context": "A detailed and structured plain text summary of the analysis results, designed to be used as the initial context for a separate Q&A chatbot. It must not contain markdown characters."
      }
      """;

  private static final String EXAMPLE1_USER = """
      {"title": "극한호우에 전남 무안·함평 침수…주민 대피령","date": "2025.08.03","content": "<br>\\n<br>\\n (무안=연합뉴스) 김혜인 기자 = 단시간에 많은 비가 내리면서 전남 무안과 함평 지역에 침수가 일어나 주민들이 대피하고 있다.\\n<br>\\n<br>\\n 3일 무안군은 이날 오후 8시 57분께 '무안군 신촌저수지 제방 월류 위험이 있으니 해당 저수지 수계 마을(상주교, 압창, 화촌) 주민분들께서는 대피해 주시길 바란다'고 긴급 재난문자를 발송했다.\\n<br>\\n<br>\\n 앞서 오후 8시 6분께 '무안읍소재지(무안군복합센터, 보건소) 침수 중이니 주민분들께서는 지금 즉시 차량을 신속하게 육상 안전지대로 이동시켜 주시기 바랍니다'라는 안전문자를 발송했다.\\n<br>\\n<br>\\n 함평군도 오후 8시 33분께 '함평읍내 및 5일 시장 주변이 폭우로 침수되고 있습니다. 차량은 우회하시고 주민들께서는 안전한 곳으로 즉시 대피하시기 바랍니다'라고 안내했다.\\n<br>\\n<br>\\n 이날 1시간 최대 강수량은 무안공항 142.1㎜, 무안 운남 115㎜, 신안 흑산도 87.9㎜, 장성 상무대 61.5㎜, 함평 월야 57.2㎜, 영광 50.9㎜, 광주 조선대 31.5㎜ 등으로 짧은 시간에 많은 비가 내리고 있다.\\n<br>\\n<br>\\n 전남도 관계자는 \\"현재 급작스럽게 많은 비로 인해 대피 현황을 파악하고 있다\\"며 \\"침수 피해를 최소화할 수 있도록 예의주시하겠다\\"고 말했다.\\n<br>\\n<br>\\n in@yna.co.kr\\n<br>\\n<br>"}
      """;

  // [수정됨] reason 필드의 내용을 새 스타일에 맞게 변경
  private static final String EXAMPLE1_ASSISTANT = """
      {
        "field": "사회",
        "topic": "전남 무안과 함평 지역에 내린 극한호우로 인한 침수 피해 및 주민 대피 상황",
        "source": "연합뉴스",
        "one_line_summary": "기록적인 폭우로 인해 전남 무안과 함평 지역에 침수 피해가 발생하여 당국이 주민 대피령을 발령하고 상황에 대응 중",
        "summary": [
          "2025년 8월 3일, 짧은 시간에 내린 폭우로 인해 전남 무안군과 함평군 일대에 침수가 발생하여 주민 대피령이 내려졌습니다.",
          "무안군은 신촌저수지 월류 위험 및 무안읍소재지 침수를 알리며 주민과 차량의 신속한 대피를 요청했습니다.",
          "함평군 역시 함평읍내 침수 상황을 알리며 안전한 곳으로의 즉시 대피를 안내했고, 무안공항에는 시간당 142.1mm의 많은 비가 기록되었습니다.",
          "전라남도는 침수 피해 최소화를 위해 현황을 파악하며 상황을 예의주시하고 있다고 밝혔습니다."
        ],
        "reliability_analysis": [
          {
            "category_name": "출처 신뢰성",
            "score": 95,
            "reason": "주요 통신사(연합뉴스) 보도, 기자명 명시"
          },
          {
            "category_name": "사실 근거",
            "score": 100,
            "reason": "재난 문자 발송 시각, 지역별 강수량 등 수치 제시, 지자체 인용"
          },
          {
            "category_name": "광고/과장 표현",
            "score": 100,
            "reason": "객관적 사실 전달 중심, 광고/과장 표현 없음"
          },
          {
            "category_name": "편향성",
            "score": 90,
            "reason": "재난 상황 및 당국 대응을 중립적으로 전달"
          },
          {
            "category_name": "기사 형식",
            "score": 95,
            "reason": "제목과 내용 일치, 명확한 문단 구성, 간결한 문체"
          }
        ],
        "total_score": 96,
        "ai_badges": [
          "공신력 있는 출처",
          "주의 환기 우수"
        ],
        "chatbot_context": "[기사 분석 리포트 요약]\\n이 기사는 '연합뉴스'에서 보도했으며, 총점 96점(매우 높음)으로 평가된 신뢰도 높은 기사입니다.\\n\\n[세부 평가 근거]\\n출처 신뢰성 (95점): 주요 통신사(연합뉴스) 보도, 기자명 명시\\n사실 근거 (100점): 재난 문자 발송 시각, 지역별 강수량 등 수치 제시, 지자체 인용\\n광고/과장 표현 (100점): 객관적 사실 전달 중심, 광고/과장 표현 없음\\n편향성 (90점): 재난 상황 및 당국 대응을 중립적으로 전달\\n기사 형식 (95점): 제목과 내용 일치, 명확한 문단 구성, 간결한 문체\\n\\n[부여된 AI 배지]\\n공신력 있는 출처: 주요 언론사인 연합뉴스에서 보도하여 부여되었습니다.\\n주의 환기 우수: 재난 상황의 위험성을 구체적인 근거를 들어 알리고 있어 부여되었습니다.\\n\\n이 내용을 기반으로 사용자의 질문에 상세히 답변하세요."
      }
      """;

  private static final String EXAMPLE2_USER = """
      {"title": "\\"짜장면 한 그릇 3900원에 드세요\\"…백종원, 또 승부수 던졌다","date": "2025.08.04","content": "<br>\\n<br>\\n백종원 대표가 운영하는 더본코리아의 중식 브랜드 '홍콩반점0410'이 짜장면 3900원 판매 프로모션을 들고 나왔다. '민생회복 국민응원 캠페인'으로 고물가에 더 가성비 있는 음식을 제공하겠다는 더본코리아의 마케팅 전략이다. 더본코리아는 올해 자사 브랜드의 릴레이 할인을 진행하고 있다. \\n<br>\\n<br>\\n4일 더본코리아에 따르면 이번 행사는 6~7일 이틀 동안 홍콩반점 매장을 방문하는 고객을 대상으로 한다. 포장 및 배달 주문 시에는 혜택이 적용되지 않지만, 홀 영업이 어려운 배달 전문 매장의 경우에는 포장 주문 시 짜장면 3900원 혜택을 받을 수 있다. 지역 화폐로 주문이 가능한 공공배달앱 '땡겨요'와 제휴 프로모션도 진행한다. 8월 4~31일까지 '땡겨요'를 통해 홍콩반점 메뉴 주문 시 4000원 할인 혜택을 받을 수 있다. \\n<br>\\n<br>\\n<br>\\n<br>\\n지난 5월 더본코리아는 본사가 전액 비용을 부담하는 대규모 할인 행사를 벌였다. 새마을식당과 홍콩반점, 한신포차, 연돈볼카츠 등 브랜드의 대표 메뉴를 최대 50% 할인하는 방식이었다. 빽다방 아메리카노커피를 500원에 판매하면서 전국적인 호응을 얻기도 했다. 앞서 백종원 대표는 각종 논란에 휩싸인 뒤, 300억원 규모의 가맹점 상생 지원책을 발표했다. \\n<br>\\n<br>\\n백 대표가 선택한 위기 돌파 전략은 가맹점 지원과 가격 할인에 집중됐다. 결국 가맹점주의 이익을 보호하고 가격할인을 통해 국민들에게 혜택을 주는 것이 프랜차이즈 대표로서 해야 할 일이라고 판단했기 때문이다. 특히 백종원 대표를 둘러싼 논란의 시작점이 '빽햄 가성비 문제'였던 만큼, 가성비 이미지를 회복해야 한다는 판단도 깔린 것으로 보인다. \\n<br>\\n<br>\\n고윤상 기자"}
      """;

  private static final String EXAMPLE2_ASSISTANT = """
      {
        "field": "경제",
        "topic": "백종원의 더본코리아가 고물가 대응 및 기업 이미지 회복 전략의 일환으로 '홍콩반점0410' 짜장면 할인 행사를 진행",
        "source": "출처 불명 (기자명 '고윤상' 명시)",
        "one_line_summary": "백종원의 더본코리아가 고물가 시대에 가성비 이미지를 회복하기 위한 전략으로 홍콩반점 짜장면 할인 프로모션을 진행",
        "summary": [
          "더본코리아의 '홍콩반점0410'이 '민생회복 국민응원 캠페인'의 일환으로 이틀간 짜장면을 3900원에 판매하는 행사를 진행합니다.",
          "이번 프로모션은 매장 방문 고객을 대상으로 하며, 특정 배달앱과 연계한 할인 혜택도 별도로 제공됩니다.",
          "이러한 연속적인 할인 행사는 최근 논란 이후 가맹점 상생 및 '가성비' 이미지를 회복하기 위한 백종원 대표의 전략으로 분석됩니다.",
          "과거에도 빽다방 커피 할인 등 대규모 프로모션을 통해 전국적인 호응을 얻은 바 있습니다."
        ],
        "reliability_analysis": [
          {
            "category_name": "출처 신뢰성",
            "score": 65,
            "reason": "기자명은 명시되었으나 언론사 정보 없음"
          },
          {
            "category_name": "사실 근거",
            "score": 95,
            "reason": "프로모션 기간, 가격, 대상 브랜드 등 구체적 사실 포함"
          },
          {
            "category_name": "광고/과장 표현",
            "score": 90,
            "reason": "기업 행사 정보 전달, 직접적 구매 유도 표현 없음"
          },
          {
            "category_name": "편향성",
            "score": 80,
            "reason": "행사 정보와 함께 기업의 전략적 배경 분석 포함"
          },
          {
            "category_name": "기사 형식",
            "score": 95,
            "reason": "제목과 핵심 내용 일치, 가독성 높은 문단 구성"
          }
        ],
        "total_score": 85,
        "ai_badges": [
          "광고성 기사"
        ],
        "chatbot_context": "[기사 분석 리포트 요약]\\n이 기사는 기자명('고윤상')은 명시되었으나 출처가 불분명하며, 총점 85점(높음)으로 평가되었습니다.\\n\\n[세부 평가 근거]\\n출처 신뢰성 (65점): 기자명은 명시되었으나 언론사 정보 없음\\n사실 근거 (95점): 프로모션 기간, 가격, 대상 브랜드 등 구체적 사실 포함\\n광고/과장 표현 (90점): 기업 행사 정보 전달, 직접적 구매 유도 표현 없음\\n편향성 (80점): 행사 정보와 함께 기업의 전략적 배경 분석 포함\\n기사 형식 (95점): 제목과 핵심 내용 일치, 가독성 높은 문단 구성\\n\\n[부여된 AI 배지]\\n광고성 기사: 기사의 핵심 주제가 특정 기업의 상품 할인 프로모션에 관한 것이므로 부여되었습니다.\\n\\n이 내용을 기반으로 사용자의 질문에 상세히 답변하세요."
      }
      """;

  public String getSystemPrompt() {
    return SYSTEM_PROMPT;
  }

  public List<ClovaRequestDTO.Message> getExampleConversations() {
    List<ClovaRequestDTO.Message> messages = new ArrayList<>();

    messages.add(new ClovaRequestDTO.Message("user", EXAMPLE1_USER));
    messages.add(new ClovaRequestDTO.Message("assistant", EXAMPLE1_ASSISTANT));
    messages.add(new ClovaRequestDTO.Message("user", EXAMPLE2_USER));
    messages.add(new ClovaRequestDTO.Message("assistant", EXAMPLE2_ASSISTANT));

    return messages;
  }
}