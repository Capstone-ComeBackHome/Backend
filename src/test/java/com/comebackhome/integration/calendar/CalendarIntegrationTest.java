package com.comebackhome.integration.calendar;

import com.comebackhome.authentication.application.TokenProvider;
import com.comebackhome.calendar.domain.DiseaseType;
import com.comebackhome.calendar.domain.ScheduleDiseaseTag;
import com.comebackhome.calendar.domain.repository.DiseaseTagRepository;
import com.comebackhome.calendar.domain.repository.ScheduleRepository;
import com.comebackhome.calendar.infrastructure.repository.DiseaseTagJpaRepository;
import com.comebackhome.calendar.infrastructure.repository.ScheduleDiseaseTagJpaRepository;
import com.comebackhome.calendar.presentation.dto.ScheduleSaveRequest;
import com.comebackhome.support.IntegrationTest;
import com.comebackhome.user.domain.User;
import com.comebackhome.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.comebackhome.calendar.domain.DiseaseType.*;
import static com.comebackhome.support.helper.CalendarGivenHelper.*;
import static com.comebackhome.support.helper.UserGivenHelper.givenUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CalendarIntegrationTest extends IntegrationTest {

    private final String URL = "/api/v1/calendars";
    private final String TOKEN_TYPE = "Bearer ";

    @Autowired UserRepository userRepository;
    @Autowired TokenProvider tokenProvider;
    @Autowired DiseaseTagRepository diseaseTagRepository;
    @Autowired ScheduleDiseaseTagJpaRepository scheduleDiseaseTagJpaRepository;
    @Autowired ScheduleRepository scheduleRepository;
    @Autowired DiseaseTagJpaRepository diseaseTagJpaRepository;

    @Test
    void 이미_존재하는_diseaseTag로_스케줄_저장하기() throws Exception{
        // given
        diseaseTagRepository.saveAll(List.of(
                givenDiseaseTag(HEAD,"두통"),
                givenDiseaseTag(SKIN,"여드름"),
                givenDiseaseTag(CUSTOM,"교통사고")));

        ScheduleSaveRequest scheduleSaveRequest = givenScheduleSaveRequest();
        scheduleSaveRequest.setDiseaseTagRequestList(List.of(
                givenDiseaseTagRequest(DiseaseType.HEAD,"두통"),
                givenDiseaseTagRequest(DiseaseType.SKIN,"여드름"),
                givenDiseaseTagRequest(DiseaseType.CUSTOM,"교통사고")
        ));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION,TOKEN_TYPE + createAccessToken())
                .content(createJson(scheduleSaveRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;

        List<ScheduleDiseaseTag> result = scheduleDiseaseTagJpaRepository.findAll();
        Set<Long> scheduleIdSet = result.stream().map(scheduleDiseaseTag -> scheduleDiseaseTag.getSchedule().getId()).collect(Collectors.toSet());

        assertThat(result.size()).isEqualTo(3);
        assertThat(scheduleIdSet.size()).isEqualTo(1);
    }

    @Test
    void 존재하지_않는_customType_diseaseTag로_스케줄_저장하면_CustomType_diseaseTag를_save하고_스케줄을_저장한다() throws Exception{
        // given
        diseaseTagRepository.saveAll(List.of(
                givenDiseaseTag(HEAD,"두통"),
                givenDiseaseTag(SKIN,"여드름"),
                givenDiseaseTag(CUSTOM,"교통사고")));


        ScheduleSaveRequest scheduleSaveRequest = givenScheduleSaveRequest();
        scheduleSaveRequest.setDiseaseTagRequestList(List.of(
                givenDiseaseTagRequest(DiseaseType.HEAD,"두통"),
                givenDiseaseTagRequest(DiseaseType.SKIN,"여드름"),
                givenDiseaseTagRequest(DiseaseType.CUSTOM,"교통사고"),
                givenDiseaseTagRequest(DiseaseType.CUSTOM,"허리디스크")
        ));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION,TOKEN_TYPE + createAccessToken())
                .content(createJson(scheduleSaveRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;

        List<ScheduleDiseaseTag> result = scheduleDiseaseTagJpaRepository.findAll();
        Set<Long> scheduleIdSet = result.stream().map(scheduleDiseaseTag -> scheduleDiseaseTag.getSchedule().getId()).collect(Collectors.toSet());

        assertThat(result.size()).isEqualTo(4);
        assertThat(scheduleIdSet.size()).isEqualTo(1);
    }

    @Test
    void customType이_없이_스케줄_저장() throws Exception{
        // given
        diseaseTagRepository.saveAll(List.of(
                givenDiseaseTag(HEAD,"두통"),
                givenDiseaseTag(SKIN,"여드름")
                ));


        ScheduleSaveRequest scheduleSaveRequest = givenScheduleSaveRequest();
        scheduleSaveRequest.setDiseaseTagRequestList(List.of(
                givenDiseaseTagRequest(DiseaseType.HEAD,"두통"),
                givenDiseaseTagRequest(DiseaseType.SKIN,"여드름")
        ));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION,TOKEN_TYPE + createAccessToken())
                .content(createJson(scheduleSaveRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;

        List<ScheduleDiseaseTag> result = scheduleDiseaseTagJpaRepository.findAll();
        Set<Long> scheduleIdSet = result.stream().map(scheduleDiseaseTag -> scheduleDiseaseTag.getSchedule().getId()).collect(Collectors.toSet());

        assertThat(result.size()).isEqualTo(2);
        assertThat(scheduleIdSet.size()).isEqualTo(1);
    }

    @Test
    void 스케줄_삭제() throws Exception{
        // given
        User user = userRepository.save(givenUser());
        Long scheduleId = saveSchedule(user);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.delete(URL+"/"+scheduleId)
                .header(HttpHeaders.AUTHORIZATION,TOKEN_TYPE + createAccessToken(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    private Long saveSchedule(User user) {
        Long scheduleId = scheduleRepository.save(givenSchedule(user));
        Long diseaseTagId1 = diseaseTagJpaRepository.save(givenDiseaseTag(HEAD, "두통")).getId();
        Long diseaseTagId2 = diseaseTagJpaRepository.save(givenDiseaseTag(SKIN, "여드름")).getId();
        scheduleDiseaseTagJpaRepository.save(ScheduleDiseaseTag.of(scheduleId,diseaseTagId1));
        scheduleDiseaseTagJpaRepository.save(ScheduleDiseaseTag.of(scheduleId,diseaseTagId2));
        return scheduleId;
    }

    @Test
    void 특정_월의_나의_스케줄_조회() throws Exception{
        // given
        User user = userRepository.save(givenUser());
        Long diseaseTagId1 = diseaseTagJpaRepository.save(givenDiseaseTag(HEAD, "두통")).getId();
        Long diseaseTagId2 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "디스크")).getId();
        Long diseaseTagId3 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "교통사고")).getId();

        Long scheduleId1 = scheduleRepository.save(givenSchedule(user));
        scheduleDiseaseTagJpaRepository.saveAll(List.of(
                ScheduleDiseaseTag.of(scheduleId1,diseaseTagId1),
                ScheduleDiseaseTag.of(scheduleId1,diseaseTagId2),
                ScheduleDiseaseTag.of(scheduleId1,diseaseTagId3)
        ));

        Long scheduleId2 = scheduleRepository.save(givenSchedule(user));
        scheduleDiseaseTagJpaRepository.saveAll(List.of(
                ScheduleDiseaseTag.of(scheduleId2,diseaseTagId1),
                ScheduleDiseaseTag.of(scheduleId2,diseaseTagId2)
        ));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(URL+"?yearMonth="+ YearMonth.now())
                .header(HttpHeaders.AUTHORIZATION,TOKEN_TYPE + createAccessToken(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.simpleScheduleResponseList[0].scheduleId").value(scheduleId1))
                .andExpect(jsonPath("$.simpleScheduleResponseList[0].localDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.simpleScheduleResponseList[0].diseaseTagCount").value(3))
                .andExpect(jsonPath("$.simpleScheduleResponseList[1].scheduleId").value(scheduleId2))
                .andExpect(jsonPath("$.simpleScheduleResponseList[1].localDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.simpleScheduleResponseList[1].diseaseTagCount").value(2))
        ;
    }


}
