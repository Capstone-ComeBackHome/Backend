package com.comebackhome.support.helper;

import com.comebackhome.calendar.domain.diseasetag.DiseaseTag;
import com.comebackhome.calendar.domain.diseasetag.DiseaseType;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DefaultTypeDiseaseTagListResponseDto;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DiseaseTagListResponseDto;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DiseaseTagQueryDto;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DiseaseTagResponseDto;
import com.comebackhome.calendar.domain.schedule.PainType;
import com.comebackhome.calendar.domain.schedule.Schedule;
import com.comebackhome.calendar.domain.schedule.ScheduleDiseaseTag;
import com.comebackhome.calendar.domain.schedule.repository.dto.BubbleQueryDto;
import com.comebackhome.calendar.domain.schedule.repository.dto.LineQueryDto;
import com.comebackhome.calendar.domain.schedule.service.dto.request.ScheduleModifyRequestDto;
import com.comebackhome.calendar.domain.schedule.service.dto.request.ScheduleSaveRequestDto;
import com.comebackhome.calendar.domain.schedule.service.dto.response.BubbleResponseDto;
import com.comebackhome.calendar.domain.schedule.service.dto.response.LineResponseDto;
import com.comebackhome.calendar.domain.schedule.service.dto.response.ScheduleResponseDto;
import com.comebackhome.calendar.presentation.dto.request.DiseaseTagRequest;
import com.comebackhome.calendar.presentation.dto.request.ScheduleModifyRequest;
import com.comebackhome.calendar.presentation.dto.request.ScheduleSaveRequest;
import com.comebackhome.calendar.presentation.dto.response.ScheduleResponse;
import com.comebackhome.user.domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.comebackhome.calendar.domain.diseasetag.DiseaseType.*;

public class CalendarGivenHelper {

    public static DiseaseTag givenDiseaseTag(DiseaseType diseaseType, String name){
        return DiseaseTag.builder()
                .diseaseType(diseaseType)
                .name(name)
                .build();
    }

    public static DefaultTypeDiseaseTagListResponseDto givenDefaultTypeDiseaseTagListResponseDto(){
        return DefaultTypeDiseaseTagListResponseDto.builder()
                .head(givenDiseaseTagListResponseDto(List.of("두통"), HEAD))
                .bronchus(givenDiseaseTagListResponseDto(List.of("코막힘"), BRONCHUS))
                .chest(givenDiseaseTagListResponseDto(List.of("가슴 통증"), CHEST))
                .stomach(givenDiseaseTagListResponseDto(List.of("공복감"), STOMACH))
                .limb(givenDiseaseTagListResponseDto(List.of("관절통"), LIMB))
                .skin(givenDiseaseTagListResponseDto(List.of("여드름"), SKIN))
                .build();
    }

    public static DiseaseTagListResponseDto givenDiseaseTagListResponseDto(List<String> nameList,DiseaseType diseaseType){
        return DiseaseTagListResponseDto.builder()
                .diseaseTagNameList(nameList)
                .diseaseTypeDescription(diseaseType.getDescription())
                .build();
    }

    public static ScheduleSaveRequestDto givenScheduleSaveRequestDto(Long userId){
        return givenScheduleSaveRequest().toScheduleSaveRequestDto(userId);
    }

    public static ScheduleSaveRequest givenScheduleSaveRequest(){
        return ScheduleSaveRequest.builder()
                .diseaseTagRequestList(List.of(
                        givenDiseaseTagRequest(HEAD,"두통"),
                        givenDiseaseTagRequest(SKIN,"여드름"),
                        givenDiseaseTagRequest(CUSTOM,"교통사고")
                ))
                .dailyNote("오늘은 조금 괜찮아진 것 같네요.")
                .scheduleDate(LocalDate.now())
                .painType(PainType.GOOD)
                .build();
    }

    public static DiseaseTagRequest givenDiseaseTagRequest(DiseaseType diseaseType, String name) {
        return DiseaseTagRequest.builder()
                .diseaseType(diseaseType)
                .name(name)
                .build();
    }

    public static Schedule givenSchedule(User user) {
        return Schedule.builder()
                .user(user)
                .scheduleDate(LocalDate.now())
                .scheduleDiseaseTagList(List.of(ScheduleDiseaseTag.of(1L,1L)))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD)
                .build();
    }

    public static Schedule givenScheduleBeforeMonth(User user, int month) {
        return Schedule.builder()
                .user(user)
                .scheduleDate(LocalDate.now().minusMonths(month))
                .scheduleDiseaseTagList(List.of(ScheduleDiseaseTag.of(1L,1L)))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD)
                .build();
    }

    public static Schedule givenScheduleBeforeDay(User user, int day) {
        return Schedule.builder()
                .user(user)
                .scheduleDate(LocalDate.now().minusDays(day))
                .scheduleDiseaseTagList(List.of(ScheduleDiseaseTag.of(1L,1L)))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD)
                .build();
    }

    public static Schedule givenSchedule(User user, LocalDate localDate) {
        return Schedule.builder()
                .user(user)
                .scheduleDate(localDate)
                .scheduleDiseaseTagList(List.of(ScheduleDiseaseTag.of(1L,1L)))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD)
                .build();
    }

    public static Schedule givenSchedule() {
        User user = User.builder().id(1L).build();
        ScheduleDiseaseTag scheduleDiseaseTag = ScheduleDiseaseTag.builder()
                .id(1L)
                .diseaseTag(givenDiseaseTag(DiseaseType.HEAD, "두통"))
                .schedule(givenSchedule(user))
                .build();
        Schedule schedule = Schedule.builder()
                .user(user)
                .scheduleDate(LocalDate.now())
                .scheduleDiseaseTagList(List.of(scheduleDiseaseTag))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD)
                .build();
        return schedule;
    }


    public static ScheduleResponseDto givenScheduleResponseDto() {
        return ScheduleResponseDto.builder()
                .scheduleId(1L)
                .scheduleDate(LocalDate.now())
                .diseaseTagResponseDtoList(List.of(
                        givenDiseaseTagResponseDto(HEAD,"두통"),
                        givenDiseaseTagResponseDto(CUSTOM,"디스크")
                ))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD.name())
                .build();
    }

    public static ScheduleResponseDto givenScheduleResponseDto(Long scheduleId, LocalDate localDate) {
        return ScheduleResponseDto.builder()
                .scheduleId(scheduleId)
                .scheduleDate(localDate)
                .diseaseTagResponseDtoList(List.of(
                        givenDiseaseTagResponseDto(HEAD,"두통"),
                        givenDiseaseTagResponseDto(CUSTOM,"디스크")
                ))
                .dailyNote("오늘은 조금 괜찮아요.")
                .painType(PainType.GOOD.name())
                .build();
    }

    public static ScheduleResponse givenScheduleResponse() {
        return ScheduleResponse.from(givenScheduleResponseDto());
    }

    public static DiseaseTagResponseDto givenDiseaseTagResponseDto(DiseaseType diseaseType, String name){
        return DiseaseTagResponseDto.builder()
                .diseaseType(diseaseType.name())
                .name(name)
                .build();
    }

    public static DiseaseTagQueryDto givenDiseaseTagQueryDto(DiseaseType diseaseType, String name){
        return DiseaseTagQueryDto.builder()
                .diseaseType(diseaseType)
                .name(name)
                .build();
    }

    public static ScheduleModifyRequest givenScheduleModifyRequest(){
        return ScheduleModifyRequest.builder()
                .dailyNote("오늘은 조금 아프네요.")
                .painType(PainType.BAD)
                .diseaseTagRequestList(List.of(
                        givenDiseaseTagRequest(HEAD,"두통"),
                        givenDiseaseTagRequest(CUSTOM,"디스크")
                ))
                .build();
    }

    public static ScheduleModifyRequestDto givenScheduleModifyRequestDto(){
        return givenScheduleModifyRequest().toScheduleModifyRequestDto();
    }

    public static List<BubbleResponseDto> givenBubbleResponseDtoList(){
        List<BubbleResponseDto> arr = new ArrayList<>();
        for (DiseaseType type : values()) {
            if (type.equals(CUSTOM))
                continue;
            arr.add(new BubbleResponseDto(type,1,3.3));
        }
        return arr;
    }

    public static List<BubbleQueryDto> givenBubbleQueryDtoList(){
        List<BubbleQueryDto> arr = new ArrayList<>();
        arr.add(new BubbleQueryDto(PainType.WORST, HEAD));
        arr.add(new BubbleQueryDto(PainType.BAD, HEAD));
        arr.add(new BubbleQueryDto(PainType.NORMAL, SKIN));
        return arr;
    }

    public static LineResponseDto givenLineResponseDto(){
        List<List<LineQueryDto>> list = new ArrayList<>();
        list.add(List.of(
                new LineQueryDto(LocalDate.now().minusDays(3),PainType.BAD,"두통"),
                new LineQueryDto(LocalDate.now().minusDays(2),PainType.BAD,"두통"),
                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"두통")
        ));
        list.add(List.of(
                new LineQueryDto(LocalDate.now().minusDays(2),PainType.BAD,"코막힘"),
                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"코막힘")
        ));
        list.add(List.of(
                new LineQueryDto(LocalDate.now(),PainType.BAD,"인후염")
        ));
        return LineResponseDto.from(list);
    }

    public static List<LineQueryDto> givenLineQueryDtoList(){
        return List.of(
                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"두통"),
                new LineQueryDto(LocalDate.now().minusDays(2),PainType.BAD,"두통"),
                new LineQueryDto(LocalDate.now().minusDays(3),PainType.BAD,"두통"),
                new LineQueryDto(LocalDate.now().minusDays(4),PainType.BAD,"두통"),

                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"코막힘"),
                new LineQueryDto(LocalDate.now().minusDays(2),PainType.BAD,"코막힘"),
                new LineQueryDto(LocalDate.now().minusDays(3),PainType.BAD,"코막힘"),

                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"인후염"),
                new LineQueryDto(LocalDate.now().minusDays(2),PainType.BAD,"인후염"),

                new LineQueryDto(LocalDate.now().minusDays(1),PainType.BAD,"재채기")
        );
    }

}
