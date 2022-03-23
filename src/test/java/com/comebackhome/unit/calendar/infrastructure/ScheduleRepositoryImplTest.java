package com.comebackhome.unit.calendar.infrastructure;

import com.comebackhome.calendar.domain.DiseaseTag;
import com.comebackhome.calendar.domain.Schedule;
import com.comebackhome.calendar.domain.ScheduleDiseaseTag;
import com.comebackhome.calendar.domain.dto.SimpleScheduleQueryDto;
import com.comebackhome.calendar.infrastructure.repository.DiseaseTagJpaRepository;
import com.comebackhome.calendar.infrastructure.repository.ScheduleDiseaseTagJpaRepository;
import com.comebackhome.calendar.infrastructure.repository.ScheduleJpaRepository;
import com.comebackhome.calendar.infrastructure.repository.ScheduleRepositoryImpl;
import com.comebackhome.support.QuerydslRepositoryTest;
import com.comebackhome.user.domain.User;
import com.comebackhome.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static com.comebackhome.calendar.domain.DiseaseType.CUSTOM;
import static com.comebackhome.calendar.domain.DiseaseType.HEAD;
import static com.comebackhome.support.helper.CalendarGivenHelper.givenDiseaseTag;
import static com.comebackhome.support.helper.CalendarGivenHelper.givenSchedule;
import static com.comebackhome.support.helper.UserGivenHelper.givenUser;
import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleRepositoryImplTest extends QuerydslRepositoryTest {

    @Autowired ScheduleRepositoryImpl scheduleRepository;
    @Autowired UserJpaRepository userJpaRepository;
    @Autowired ScheduleJpaRepository scheduleJpaRepository;
    @Autowired ScheduleDiseaseTagJpaRepository scheduleDiseaseTagJpaRepository;
    @Autowired DiseaseTagJpaRepository diseaseTagJpaRepository;
    @Autowired EntityManager em;

    @Test
    void schedule_저장() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());

        //when
        scheduleRepository.save(givenSchedule(user));

        //then
        List<Schedule> result = scheduleJpaRepository.findAll();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void schedule_id로_찾기() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());
        Schedule schedule = scheduleJpaRepository.save(givenSchedule(user));

        //when
        Schedule result = scheduleJpaRepository.findById(schedule.getId()).get();

        //then
        assertThat(result.getId()).isEqualTo(schedule.getId());
    }

    @Test
    void schedule_id로_제거() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());
        Schedule schedule = scheduleJpaRepository.save(givenSchedule(user));

        //when
        scheduleRepository.deleteById(schedule.getId());

        //then
        Optional<Schedule> result = scheduleJpaRepository.findById(schedule.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void yearMonth와_userId로_유저의_해당_월에_포함된_모든_스케줄_찾기() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());
        DiseaseTag diseaseTag1 = diseaseTagJpaRepository.save(givenDiseaseTag(HEAD, "두통"));
        DiseaseTag diseaseTag2 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "디스크"));
        DiseaseTag diseaseTag3 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "교통사고"));

        Schedule schedule1 = scheduleJpaRepository.save(givenSchedule(user));
        scheduleDiseaseTagJpaRepository.saveAll(List.of(
                ScheduleDiseaseTag.of(schedule1.getId(),diseaseTag1.getId()),
                ScheduleDiseaseTag.of(schedule1.getId(),diseaseTag2.getId()),
                ScheduleDiseaseTag.of(schedule1.getId(),diseaseTag3.getId())
        ));

        Schedule schedule2 = scheduleJpaRepository.save(givenSchedule(user));
        scheduleDiseaseTagJpaRepository.saveAll(List.of(
                ScheduleDiseaseTag.of(schedule2.getId(),diseaseTag1.getId()),
                ScheduleDiseaseTag.of(schedule2.getId(),diseaseTag2.getId())
        ));

        //when
        List<SimpleScheduleQueryDto> result
                = scheduleRepository.findByYearMonthAndUserId(YearMonth.now(), user.getId());

        //then
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).getScheduleId()).isEqualTo(schedule1.getId());
        assertThat(result.get(0).getLocalDate()).isEqualTo(schedule1.getLocalDate());
        assertThat(result.get(0).getDiseaseTagCount()).isEqualTo(3);

        assertThat(result.get(1).getScheduleId()).isEqualTo(schedule2.getId());
        assertThat(result.get(1).getLocalDate()).isEqualTo(schedule2.getLocalDate());
        assertThat(result.get(1).getDiseaseTagCount()).isEqualTo(2);
    }

    @Test
    void scheduleId와_userId로_스케줄_찾기() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());
        DiseaseTag diseaseTag1 = diseaseTagJpaRepository.save(givenDiseaseTag(HEAD, "두통"));
        DiseaseTag diseaseTag2 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "디스크"));
        DiseaseTag diseaseTag3 = diseaseTagJpaRepository.save(givenDiseaseTag(CUSTOM, "교통사고"));

        Schedule schedule = scheduleJpaRepository.save(givenSchedule(user));
        scheduleDiseaseTagJpaRepository.saveAll(List.of(
                ScheduleDiseaseTag.of(schedule.getId(),diseaseTag1.getId()),
                ScheduleDiseaseTag.of(schedule.getId(),diseaseTag2.getId()),
                ScheduleDiseaseTag.of(schedule.getId(),diseaseTag3.getId())
        ));
        em.flush();
        em.clear();

        //when
        Schedule result = scheduleRepository.findWithScheduleDiseaseTagByIdAndUserId(user.getId(), schedule.getId()).get();

        //then
        assertThat(result.getId()).isEqualTo(schedule.getId());
        assertThat(result.getUser().getId()).isEqualTo(user.getId());
        assertThat(result.getScheduleDiseaseTagList().size()).isEqualTo(3);
    }

    @Test
    void scheduleId와_userId로_스케줄이_존재하는지_확인하기() throws Exception{
        //given
        User user = userJpaRepository.save(givenUser());
        Long scheduleId = scheduleRepository.save(givenSchedule(user));

        //when
        boolean result = scheduleRepository.existsByIdAndUserId(scheduleId, user.getId());

        //then
        assertThat(result).isTrue();
    }

}


