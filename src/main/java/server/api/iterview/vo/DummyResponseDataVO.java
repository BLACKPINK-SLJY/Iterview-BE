package server.api.iterview.vo;

import lombok.Getter;

@Getter
public class DummyResponseDataVO {

    public static final String ANSWER_DUMMY = "{\n" +
            "    \"questionId\": 37,\n" +
            "    \"question\": \"DI에 대해서 설명해 주세요.\",\n" +
            "    \"level\": 1,\n" +
            "    \"tags\": [\n" +
            "      \"스프링\",\n" +
            "      \"웹\"\n" +
            "    ],\n" +
            "    \"bookmarked\": \"N\",\n" +
            "    \"transcription\": \"자세히 보아야 예쁘다. 오래 보아야 사랑스럽다. 너도 그렇다. 스프링은 @@잡아언어를@@ 기반으로 한다. \",\n" +
            "    \"score\": 0,\n" +
            "    \"feedback\": \"이 답변은 DI(Dependency Injection)에 대한 내용이 아닌 것으로 보입니다. DI에 대한 설명이 없으며 스프링 프레임워크에 대한 부적절하고 모호한 언급만 있습니다. DI에 대해 정확하고 명확한 설명이 필요합니다.\",\n" +
            "    \"bestAnswer\": \"DI(Dependency Injection)은 객체 간의 의존성을 관리하기 위한 디자인 패턴입니다. 의존성은 객체 생성 및 관계를 설정하는 것이 아닌 외부에서 강제적으로 주입되는 방식으로 처리됩니다. 이를 통해 의존성에 대한 변경 없이도 유연한 코드 작성이 가능하며, 객체 간의 결합도를 낮추어 유지보수성과 테스트 용이성을 높입니다. 스프링 프레임워크에서는 DI를 지원하여 스프링 컨테이너가 객체를 생성하고 관리하며, 의존성을 주입해줍니다.\",\n" +
            "    \"created\": \"Y\"\n" +
            "  }";

    public static final String ANSWER_REPLAY_DUMMY = "{\n" +
            "    \"questionId\": 37,\n" +
            "    \"question\": \"DI에 대해서 설명해 주세요.\",\n" +
            "    \"level\": 1,\n" +
            "    \"tags\": [\n" +
            "      \"스프링\",\n" +
            "      \"웹\"\n" +
            "    ],\n" +
            "    \"bookmarked\": \"N\",\n" +
            "    \"category\": \"BE\",\n" +
            "    \"date\": \"2023.07.13 목요일\",\n" +
            "    \"url\": \"https://iterview-bucket.s3.ap-northeast-2.amazonaws.com/9fb02c54-29a5-437e-aaf0-25f7f2ae1b58/37?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230713T145023Z&X-Amz-SignedHeaders=host&X-Amz-Expires=599&X-Amz-Credential=AKIA4MFRH54OCAMCQVXR%2F20230713%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=5281965b5739b918201e84ef47e1774d1249df974cd74be6120b61e1cf5ec5eb\",\n" +
            "    \"length\": null,\n" +
            "    \"results\": {\n" +
            "      \"transcripts\": [\n" +
            "        {\n" +
            "          \"transcript\": \"자세히 보아야 예쁘다. 오래 보아야 사랑스럽다. 너도 그렇다. 스프링은 @@잡아언어를@@ 기반으로 한다. \"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"start_time\": \"0.4\",\n" +
            "          \"end_time\": \"1.68\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"자세히 보아야 예쁘다. \"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.12\",\n" +
            "          \"end_time\": \"3.61\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"오래 보아야 사랑스럽다. \"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.03\",\n" +
            "          \"end_time\": \"4.97\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"너도 그렇다. \"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.37\",\n" +
            "          \"end_time\": \"7.75\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"스프링은 @@잡아언어를@@ 기반으로 한다. \"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    \"created\": \"Y\"\n" +
            "  }";
    public static final String TRANSCRIPTION_DUMMY = "{\n" +
            "    \"category\": \"IOS\",\n" +
            "    \"date\": \"2023.06.27 화요일\",\n" +
            "    \"url\": \"https://iterview-bucket.s3.ap-northeast-2.amazonaws.com/9fb02c54-29a5-437e-aaf0-25f7f2ae1b58/5?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230626T170430Z&X-Amz-SignedHeaders=host&X-Amz-Expires=599&X-Amz-Credential=AKIA4MFRH54OCAMCQVXR%2F20230626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=c7090a333f7ccdab2f81f91aa01674fa4d35d039befa429705af550e78a40df7\",\n" +
            "    \"length\": null,\n" +
            "    \"results\": {\n" +
            "      \"transcripts\": [\n" +
            "        {\n" +
            "          \"transcript\": \"자세히 보아야 예쁘다. 오래 보아야 사랑스럽다. 너도 그렇다. 스프링은 잡아언어를 기반으로 한다.\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"start_time\": \"0.4\",\n" +
            "          \"end_time\": \"1.68\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"자세히 보아야 예쁘다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.12\",\n" +
            "          \"end_time\": \"3.61\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"오래 보아야 사랑스럽다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.03\",\n" +
            "          \"end_time\": \"4.97\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"너도 그렇다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.37\",\n" +
            "          \"end_time\": \"7.75\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"스프링은 잡아언어를 기반으로 한다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }";
}
