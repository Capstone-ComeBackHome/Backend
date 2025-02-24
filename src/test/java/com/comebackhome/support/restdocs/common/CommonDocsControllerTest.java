package com.comebackhome.support.restdocs.common;


import com.comebackhome.common.exception.GlobalExceptionHandler;
import com.comebackhome.config.RestDocsConfig;
import com.comebackhome.support.restdocs.SampleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public class CommonDocsControllerTest {


    MockMvc mockMvc;
    RestDocumentationResultHandler restDocumentationResultHandler;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider provider) {
        this.restDocumentationResultHandler = MockMvcRestDocumentation.document(
                "{class-name}/{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

        this.mockMvc = MockMvcBuilders.standaloneSetup(CommonDocsController.class)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocumentationResultHandler)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    /**
     * 소셜 로그인은 외부 연동이므로 스프링을 띄우면 해당 api 요청 시 화면이 열려버리므로 rest docs 작성 불가능
     * standalone으로 스프링 없이 띄워서 rest docs 전용 컨트롤러를 만들고 문서화 작업 진행
     */

    @Test
    void 소셜_로그인() throws Exception{
        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/oauth2/authorization/{provider}","kakao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("provider").description("소셜 로그인 제공자, [kakao, naver, google]")
                        ),
                        responseFields(
                                fieldWithPath("data.tokenType").type(STRING).description("토큰 타입"),
                                fieldWithPath("data.accessToken").type(STRING).description("권한 인증할 때 사용하는 access 토큰"),
                                fieldWithPath("data.refreshToken").type(STRING).description("access 토큰 만료시 재발급에 사용하는 refresh 토큰"),
                                fieldWithPath("result").type(STRING).description("성공 시 SUCCESS, 실패시 FAIL"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("errors").description("Error 값 배열 값")
                        )
                ))

        ;
    }

    @Test
    void 에러_샘플() throws Exception{
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        SampleRequest sampleRequest = new SampleRequest("backtony");
        String content = objectMapper.writeValueAsString(sampleRequest);
        // when then docs
        mockMvc.perform(RestDocumentationRequestBuilders.get("/docs/error")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(restDocumentationResultHandler.document(
                        PayloadDocumentation.responseFields(
                                fieldWithPath("result").type(STRING).description("성공 시 SUCCESS, 실패시 FAIL"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("errors").description("Error 값 배열 값"),
                                fieldWithPath("errors[0].field").description("에러 필드명").optional(),
                                fieldWithPath("errors[0].value").description("에러 필드값").optional(),
                                fieldWithPath("errors[0].reason").description("에러 이유").optional())
                ));
    }
}
