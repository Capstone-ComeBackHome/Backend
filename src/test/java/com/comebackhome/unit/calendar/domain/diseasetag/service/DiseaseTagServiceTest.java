package com.comebackhome.unit.calendar.domain.diseasetag.service;

import com.comebackhome.calendar.domain.diseasetag.DiseaseType;
import com.comebackhome.calendar.domain.diseasetag.repository.DiseaseTagRepository;
import com.comebackhome.calendar.domain.diseasetag.service.DiseaseTagService;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DefaultTypeDiseaseTagListResponseDto;
import com.comebackhome.calendar.domain.diseasetag.service.dto.DiseaseTagQueryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.comebackhome.support.helper.CalendarGivenHelper.givenDiseaseTagQueryDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DiseaseTagServiceTest {

    @InjectMocks DiseaseTagService diseaseTagService;
    @Mock DiseaseTagRepository diseaseTagRepository;

    @Test
    void getDiseaseTagExcludeCustomType() throws Exception{
        //given
        List<DiseaseTagQueryDto> diseaseTagList = createDiseaseTagList();
        given(diseaseTagRepository.findAllDiseaseTagExceptDiseaseType(any())).willReturn(diseaseTagList);

        //when
        DefaultTypeDiseaseTagListResponseDto result = diseaseTagService.getDiseaseTagExceptCustomType();

        //then
        assertThat(result.getHead().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getHead().getDiseaseTypeDescription()).isEqualTo(DiseaseType.HEAD.getDescription());
        assertThat(result.getBronchus().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getBronchus().getDiseaseTypeDescription()).isEqualTo(DiseaseType.BRONCHUS.getDescription());
        assertThat(result.getChest().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getChest().getDiseaseTypeDescription()).isEqualTo(DiseaseType.CHEST.getDescription());
        assertThat(result.getStomach().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getStomach().getDiseaseTypeDescription()).isEqualTo(DiseaseType.STOMACH.getDescription());
        assertThat(result.getLimb().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getLimb().getDiseaseTypeDescription()).isEqualTo(DiseaseType.LIMB.getDescription());
        assertThat(result.getSkin().getDiseaseTagNameList().size()).isEqualTo(1);
        assertThat(result.getSkin().getDiseaseTypeDescription()).isEqualTo(DiseaseType.SKIN.getDescription());

    }

    private List<DiseaseTagQueryDto> createDiseaseTagList() {
        List<DiseaseTagQueryDto> diseaseTagQueryDtoList = new ArrayList<>();
        diseaseTagQueryDtoList.addAll(List.of(
                givenDiseaseTagQueryDto(DiseaseType.HEAD,"두통"),
                givenDiseaseTagQueryDto(DiseaseType.BRONCHUS,"코막힘"),
                givenDiseaseTagQueryDto(DiseaseType.CHEST,"가슴 통증"),
                givenDiseaseTagQueryDto(DiseaseType.STOMACH,"공복감"),
                givenDiseaseTagQueryDto(DiseaseType.LIMB,"관절통"),
                givenDiseaseTagQueryDto(DiseaseType.SKIN,"여드름")
        ));
        return diseaseTagQueryDtoList;
    }

}
