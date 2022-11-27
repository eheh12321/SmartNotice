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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.smartnotice.config.TestSecurityConfig;
import sejong.smartnotice.domain.member.AdminType;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminModifyRequest;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminRegisterRequest;
import sejong.smartnotice.helper.dto.response.AdminResponse;
import sejong.smartnotice.helper.dto.response.TownResponse;
import sejong.smartnotice.helper.validator.AdminModifyValidator;
import sejong.smartnotice.helper.validator.UserAccountRegisterValidator;
import sejong.smartnotice.service.AdminService;
import sejong.smartnotice.service.TownService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(AdminApiController.class)
@AutoConfigureRestDocs(
        uriHost = "www.smarttownnotice.gq",
        uriScheme = "https",
        uriPort = 443
)
class AdminApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private AdminService adminService;

    @MockBean
    private TownService townService;

    @MockBean
    private AdminModifyValidator adminModifyValidator;

    @MockBean
    UserAccountRegisterValidator userAccountRegisterValidator;

    private static final ObjectMapper mapper = new JsonMapper();

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void GET_관리자_목록_조회() throws Exception {
        // Given
        List<AdminResponse> responseList = List.of(
                new TestAdminResponse(1L, "최고 관리자 1", "111-1111-0001", AdminType.SUPER.getName(), 1),
                new TestAdminResponse(2L, "마을 관리자 1", "111-1111-0002", AdminType.ADMIN.getName(), 1));
        given(adminService.findAll()).willReturn(responseList);

        // When
        ResultActions actions = mvc.perform(
                get("/api/admin")
                        .content(MediaType.APPLICATION_JSON_VALUE)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("최고 관리자 1"))
                .andExpect(jsonPath("$.data[1].tel").value("111-1111-0002"))
                .andExpect(jsonPath("$.data[1].type").value("마을 관리자"))
                .andExpect(jsonPath("$.data[1].manageTownList.size()").value(1))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.get.admin", null, Locale.KOREA)))
                .andDo(document(
                        "get-admin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("결과 데이터"),
                                fieldWithPath("data[].id").type(NUMBER).description("관리자 번호"),
                                fieldWithPath("data[].name").type(STRING).description("관리자 이름"),
                                fieldWithPath("data[].tel").type(STRING).description("관리자 전화번호"),
                                fieldWithPath("data[].type").type(STRING).description("관리자 권한(최고 관리자/마을 관리자)"),
                                fieldWithPath("data[].manageTownList").type(ARRAY).description("관리 마을 목록"),
                                fieldWithPath("data[].manageTownList[].id").type(NUMBER).description("관리 마을 ID"),
                                fieldWithPath("data[].manageTownList[].name").type(STRING).description("관리 마을 이름"),
                                fieldWithPath("data[].manageTownList[].parentRegion").type(STRING).description("관리 마을 상위 지역명"),
                                fieldWithPath("data[].manageTownList[].childRegion").type(STRING).description("관리 마을 하위 지역명"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void GET_마을별_관리자_목록_조회() throws Exception {
        // Given
        String townId = "1";
        List<AdminResponse> responseList = List.of(
                new TestAdminResponse(1L, "최고 관리자 1", "111-1111-0001", AdminType.SUPER.getName(), 1),
                new TestAdminResponse(2L, "마을 관리자 1", "111-1111-0002", AdminType.ADMIN.getName(), 1));
        given(adminService.findAdminByTown(anyLong())).willReturn(responseList);

        // When
        ResultActions actions = mvc.perform(
                get("/api/admin")
                        .queryParam("townId", townId)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("최고 관리자 1"))
                .andExpect(jsonPath("$.data[1].tel").value("111-1111-0002"))
                .andExpect(jsonPath("$.data[1].type").value("마을 관리자"))
                .andExpect(jsonPath("$.data[1].manageTownList.size()").value(1))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.get.admin", null, Locale.KOREA)))
                .andDo(document(
                        "get-townAdmin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("townId").description("마을 번호")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("결과 데이터"),
                                fieldWithPath("data[].id").type(NUMBER).description("관리자 번호"),
                                fieldWithPath("data[].name").type(STRING).description("관리자 이름"),
                                fieldWithPath("data[].tel").type(STRING).description("관리자 전화번호"),
                                fieldWithPath("data[].type").type(STRING).description("관리자 권한(최고 관리자/마을 관리자)"),
                                fieldWithPath("data[].manageTownList").type(ARRAY).description("관리 마을 목록"),
                                fieldWithPath("data[].manageTownList[].id").type(NUMBER).description("관리 마을 ID"),
                                fieldWithPath("data[].manageTownList[].name").type(STRING).description("관리 마을 이름"),
                                fieldWithPath("data[].manageTownList[].parentRegion").type(STRING).description("관리 마을 상위 지역명"),
                                fieldWithPath("data[].manageTownList[].childRegion").type(STRING).description("관리 마을 하위 지역명"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @WithUserDetails
    void POST_관리자_등록() throws Exception {
        // Given
        AdminRegisterRequest registerRequest = new AdminRegisterRequest(
                "관리자 이름",
                "010-1234-1234",
                "loginId",
                "loginPw",
                AdminType.ADMIN);
        TestAdminResponse response = new TestAdminResponse(1L, "관리자 이름", "010-1234-1234", AdminType.ADMIN.getName(), 0);
        String content = mapper.writeValueAsString(registerRequest);

        willDoNothing().given(userAccountRegisterValidator).validate(any(), any());
        given(adminService.register(any())).willReturn(response);

        // When
        ResultActions actions = mvc.perform(
                post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("관리자 이름"))
                .andExpect(jsonPath("$.data.tel").value("010-1234-1234"))
                .andExpect(jsonPath("$.data.type").value("마을 관리자"))
                .andExpect(jsonPath("$.data.manageTownList.size()").value(0))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.create.admin", null, Locale.KOREA)))
                .andDo(document(
                        "post-admin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("관리자 이름"),
                                fieldWithPath("tel").description("관리자 전화번호"),
                                fieldWithPath("loginId").description("관리자 로그인 ID"),
                                fieldWithPath("loginPw").description("관리자 로그인 PW"),
                                fieldWithPath("type").description("관리자 권한")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("결과 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("관리자 번호"),
                                fieldWithPath("data.name").type(STRING).description("관리자 이름"),
                                fieldWithPath("data.tel").type(STRING).description("관리자 전화번호"),
                                fieldWithPath("data.type").type(STRING).description("관리자 권한(최고 관리자/마을 관리자)"),
                                fieldWithPath("data.manageTownList").type(ARRAY).description("관리 마을 목록"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @WithUserDetails
    void PATCH_관리자_수정() throws Exception {
        // Given
        Long adminId = 1L;
        Long manageTownId = 1L;
        AdminModifyRequest modifyRequest = new AdminModifyRequest("바뀐 관리자 이름", "010-1234-1234", List.of(manageTownId));
        String content = mapper.writeValueAsString(modifyRequest);

        TestAdminResponse response = new TestAdminResponse(1L, "바뀐 관리자 이름", "010-1234-1234", AdminType.ADMIN.getName(), 1);
        willDoNothing().given(adminModifyValidator).validate(any(), any());
        given(adminService.modifyAdminInfo(anyLong(), any())).willReturn(response);
        willDoNothing().given(townService).modifyAdminManagedTownList(anyLong(), any());

        // When
        ResultActions actions = mvc.perform(
                patch("/api/admin/{adminId}", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("바뀐 관리자 이름"))
                .andExpect(jsonPath("$.data.tel").value("010-1234-1234"))
                .andExpect(jsonPath("$.data.type").value("마을 관리자"))
                .andExpect(jsonPath("$.data.manageTownList.size()").value(1))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.update.admin", null, Locale.KOREA)))
                .andDo(document(
                        "patch-admin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("adminId").description("관리자 번호")
                        ),
                        requestFields(
                                fieldWithPath("name").description("관리자 이름"),
                                fieldWithPath("tel").description("관리자 전화번호"),
                                fieldWithPath("townIdList").description("관리자 관리 마을 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("관리자 번호"),
                                fieldWithPath("data.name").type(STRING).description("관리자 이름"),
                                fieldWithPath("data.tel").type(STRING).description("관리자 전화번호"),
                                fieldWithPath("data.type").type(STRING).description("관리자 권한(최고 관리자/마을 관리자)"),
                                fieldWithPath("data.manageTownList").type(ARRAY).description("관리 마을 목록"),
                                fieldWithPath("data.manageTownList[].id").type(NUMBER).description("관리 마을 ID"),
                                fieldWithPath("data.manageTownList[].name").type(STRING).description("관리 마을 이름"),
                                fieldWithPath("data.manageTownList[].parentRegion").type(STRING).description("관리 마을 상위 지역명"),
                                fieldWithPath("data.manageTownList[].childRegion").type(STRING).description("관리 마을 하위 지역명"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void DELETE_관리자_삭제() throws Exception {
        // Given
        Long adminId = 1L;

        // When
        ResultActions actions = mvc.perform(
                delete("/api/admin/{adminId}", adminId));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("api.delete.admin", null, Locale.KOREA)))
                .andDo(document(
                        "delete-admin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("adminId").description("관리자 번호")),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    // private Admin ID는 AutoIncrement로 만들어지는데 Slice test에다가 id에 대한 Setter가 없어서 어쩔 수 없이 ID를 넣어주기 위한 방법으로 생성
    private static class TestAdminResponse extends AdminResponse {
        public TestAdminResponse(Long id, String name, String tel, String type, int manageTownCnt) {
            super(id, name, tel, type, new ArrayList<>());
            for(int i = 1; i <= manageTownCnt; i++) {
                this.getManageTownList().add(TownResponse.builder()
                        .id((long)i)
                        .name("관리자 관리 마을" + i)
                        .parentRegion("관리 마을 상위 지역명")
                        .childRegion("관리 마을 하위 지역명").build()
                );
            }
        }
    }
}