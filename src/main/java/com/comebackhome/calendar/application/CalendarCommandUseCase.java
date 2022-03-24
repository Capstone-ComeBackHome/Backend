package com.comebackhome.calendar.application;


import com.comebackhome.calendar.application.dto.ScheduleModifyRequestDto;
import com.comebackhome.calendar.application.dto.ScheduleSaveRequestDto;

public interface CalendarCommandUseCase {

    void saveMySchedule(ScheduleSaveRequestDto scheduleSaveRequestDto);

    void deleteSchedule(Long scheduleId, Long userId);

    void modifyMySchedule(Long scheduleId, Long userId, ScheduleModifyRequestDto scheduleModifyRequestDto);
}
