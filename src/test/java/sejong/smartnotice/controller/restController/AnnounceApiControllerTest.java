package sejong.smartnotice.controller.restController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.smartnotice.config.TestSecurityConfig;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.domain.TownAnnounce;
import sejong.smartnotice.domain.announce.Announce;
import sejong.smartnotice.domain.announce.AnnounceCategory;
import sejong.smartnotice.domain.announce.AnnounceStatus;
import sejong.smartnotice.domain.announce.AnnounceType;
import sejong.smartnotice.helper.dto.request.AnnounceRequest;
import sejong.smartnotice.helper.dto.response.AnnounceResponse;
import sejong.smartnotice.service.AnnounceService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(TestSecurityConfig.class)
@WebMvcTest(AnnounceApiController.class)
@AutoConfigureRestDocs(
        uriHost = "www.smarttownnotice.gq",
        uriScheme = "https",
        uriPort = 443
)
class AnnounceApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private AnnounceService announceService;

    private static final ObjectMapper mapper = new JsonMapper();

    @Test
    @WithUserDetails
    void GET_방송_조회() throws Exception {
        // Given
        long announceId = 1L;
        String directory = "storage" + File.separator;
        LocalDateTime announceTime = LocalDateTime.now();
        Announce announce = createAnnounce(announceId, announceTime, directory);
        given(announceService.findById(announceId)).willReturn(announce);

        // When
        ResultActions actions = mvc.perform(
                get("/api/announces/{announceId}", announceId)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.get.announce", null, Locale.KOREA)))
                .andDo(document(
                        "get-announce",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("announceId").description("방송 ID")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("방송 ID"),
                                fieldWithPath("data.title").type(STRING).description("방송 제목"),
                                fieldWithPath("data.producer").type(STRING).description("방송자"),
                                fieldWithPath("data.src").type(STRING).description("방송 요청 경로"),
                                fieldWithPath("data.content").type(STRING).description("방송 내용 (문자 방송시)"),
                                fieldWithPath("data.time").type(STRING).description("방송 시각"),
                                fieldWithPath("data.status").type(STRING).description("방송 상태 (방송 준비중 / 청취 가능 / 불가능 / 실패)"),
                                fieldWithPath("data.category").type(STRING).description("방송 종류 (일반/재난)"),
                                fieldWithPath("data.townList").type(ARRAY).description("방송 대상 마을 목록"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));

    }

    @Test
    @WithUserDetails
    void POST_방송_등록() throws Exception {
        // Given
        long adminId = 1L;
        List<Long> townIdList = List.of(1L, 2L);
        AnnounceRequest announceRequest = new AnnounceRequest(adminId,
                AnnounceCategory.NORMAL,
                AnnounceType.TEXT,
                townIdList,
                "방송 제목",
                "방송 내용",
                "방송 데이터");
        String content = mapper.writeValueAsString(announceRequest);

        AnnounceResponse response = AnnounceResponse.builder()
                .id(1L)
                .title("방송 제목")
                .producer("방송자")
                .src("/storage/fileName.mp3")
                .content("방송 내용")
                .time("2022/11/27 21:55:00")
                .status(AnnounceStatus.READY.getName())
                .category(AnnounceCategory.NORMAL.getName())
                .townList(List.of("마을 1", "마을 2"))
                .build();

        given(announceService.registerAnnounce(any())).willReturn(response);

        // When
        ResultActions actions = mvc.perform(
                post("/api/announces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.create.announce", null, Locale.KOREA)))
                .andDo(document(
                        "post-announce",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("adminId").description("관리자 ID"),
                                fieldWithPath("category").description("방송 카테고리 (NORMAL/DISASTER)"),
                                fieldWithPath("type").description("방송 타입 (TEXT/VOICE)"),
                                fieldWithPath("townId").description("방송 대상 마을 ID 목록"),
                                fieldWithPath("title").description("방송 제목"),
                                fieldWithPath("textData").description("방송 내용"),
                                fieldWithPath("voiceData").description("음성 파일 데이터")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("방송 ID"),
                                fieldWithPath("data.title").type(STRING).description("방송 제목"),
                                fieldWithPath("data.producer").type(STRING).description("방송자"),
                                fieldWithPath("data.src").type(STRING).description("방송 저장 위치"),
                                fieldWithPath("data.content").type(STRING).description("방송 내용"),
                                fieldWithPath("data.time").type(STRING).description("방송 시각"),
                                fieldWithPath("data.status").type(STRING).description("방송 상태"),
                                fieldWithPath("data.category").type(STRING).description("방송 카테고리(문자/음성)"),
                                fieldWithPath("data.townList").type(ARRAY).description("방송 대상 마을 이름"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
        
    }

    @Test
    @WithUserDetails
    void DELETE_방송_삭제() throws Exception {
        // Given
        long announceId = 1L;

        // When
        ResultActions actions = mvc.perform(
                delete("/api/announces/{announceId}", announceId));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(0))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.delete.announce", null, Locale.KOREA)))
                .andDo(document(
                        "delete-announce",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("announceId").description("관리자 번호")),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    private static Announce createAnnounce(long announceId, LocalDateTime announceTime, String directory) {
        Town town = Town.builder()
                .name("방송 대상 마을 이름").build();

        Announce announce = Announce.builder()
                .id(announceId)
                .producer("방송자")
                .time(announceTime)
                .category(AnnounceCategory.NORMAL)
                .type(AnnounceType.TEXT)
                .status(AnnounceStatus.READY)
                .title("방송 제목")
                .contents("방송 내용")
                .directory(directory)
                .fileName("test-file-name")
                .townAnnounceList(new ArrayList<>()).build();

        TownAnnounce townAnnounce = new TownAnnounce(1L, town, announce);
        announce.getTownAnnounceList().add(townAnnounce);
        return announce;
    }
}