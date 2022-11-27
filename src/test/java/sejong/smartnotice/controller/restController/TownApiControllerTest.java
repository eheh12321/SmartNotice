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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.smartnotice.config.TestSecurityConfig;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Town;
import sejong.smartnotice.helper.dto.request.TownRequest.TownCreateRequest;
import sejong.smartnotice.helper.dto.request.TownRequest.TownModifyRequest;
import sejong.smartnotice.helper.dto.response.TownResponse;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;
import sejong.smartnotice.service.UserService;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(TownApiController.class)
@AutoConfigureRestDocs(
        uriHost = "www.smarttownnotice.gq",
        uriScheme = "https",
        uriPort = 443
)
class TownApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private TownService townService;

    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    private static final ObjectMapper mapper = new JsonMapper();

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void POST_마을_등록() throws Exception {
        // Given
        Long townId = 1L;
        Long regionCode = 5L;
        TownCreateRequest createTownRequest = new TownCreateRequest("새 마을 이름", regionCode);
        String content = mapper.writeValueAsString(createTownRequest);

        Town createdTown = Town.builder()
                .id(townId)
                .name("새 마을 이름")
                .region(new Region("상위 지역명", "하위 지역명")).build();
        given(townService.register(any())).willReturn(TownResponse.from(createdTown));

        // When
        ResultActions actions = mvc.perform(
                post("/api/towns")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.create.town", null, Locale.KOREA)))
                .andDo(document(
                        "post-town",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("새 마을 이름"),
                                fieldWithPath("regionCode").description("지역 번호")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("마을 번호"),
                                fieldWithPath("data.name").type(STRING).description("마을 이름"),
                                fieldWithPath("data.parentRegion").type(STRING).description("상위 지역명"),
                                fieldWithPath("data.childRegion").type(STRING).description("하위 지역명"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));

    }

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void PATCH_마을_수정() throws Exception {
        // Given
        Long townId = 1L;
        Long regionCode = 5L;
        TownModifyRequest modifyRequest = new TownModifyRequest("수정된 마을 이름", regionCode);
        String content = mapper.writeValueAsString(modifyRequest);

        Town modifiedTown = Town.builder()
                .id(1L)
                .name("수정된 마을 이름")
                .region(new Region("상위 지역명", "하위 지역명")).build();
        given(townService.modifyTownInfo(anyLong(), any())).willReturn(TownResponse.from(modifiedTown));

        // When
        ResultActions actions = mvc.perform(
                patch("/api/towns/{townId}", townId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.update.town", null, Locale.KOREA)))
                .andDo(document(
                        "patch-town",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("townId").description("마을 번호")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 마을 이름"),
                                fieldWithPath("regionCode").description("수정할 지역 번호")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("마을 번호"),
                                fieldWithPath("data.name").type(STRING).description("마을 이름"),
                                fieldWithPath("data.parentRegion").type(STRING).description("상위 지역명"),
                                fieldWithPath("data.childRegion").type(STRING).description("하위 지역명"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void DELETE_마을_삭제() throws Exception {
        // Given
        Long townId = 1L;
        willDoNothing().given(townService).delete(anyLong());

        // When
        ResultActions actions = mvc.perform(
                delete("/api/towns/{townId}", townId));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.delete.town", null, Locale.KOREA)))
                .andDo(document(
                        "delete-town",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("townId").description("마을 번호")),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터").ignored(),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }
}