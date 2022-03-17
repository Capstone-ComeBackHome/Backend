package com.comebackhome.unit.disease.infrastructure;

import com.comebackhome.disease.domain.Disease;
import com.comebackhome.disease.domain.dto.SimpleDiseaseQueryDto;
import com.comebackhome.disease.infrastructure.repository.DiseaseJpaRepository;
import com.comebackhome.disease.infrastructure.repository.DiseaseQuerydslRepository;
import com.comebackhome.support.QuerydslRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.comebackhome.support.helper.DiseaseGivenHelper.givenDisease;
import static org.assertj.core.api.Assertions.assertThat;

public class DiseaseQuerydslRepositoryTest extends QuerydslRepositoryTest {

    @Autowired DiseaseJpaRepository diseaseJpaRepository;
    @Autowired DiseaseQuerydslRepository diseaseQuerydslRepository;

    @BeforeEach
    void teardown(){
        diseaseJpaRepository.deleteAllInBatch();
    }

    @Test
    void 질병이름으로_DiseaseSimpleQueryDto_찾기() throws Exception{
        //given
        Disease disease = diseaseJpaRepository.save(givenDisease());

        //when
        SimpleDiseaseQueryDto result = diseaseQuerydslRepository.findDiseaseSimpleQueryDtoByName(disease.getName()).get();

        //then
        assertThat(result.getDiseaseId()).isEqualTo(disease.getId());
        assertThat(result.getName()).isEqualTo(disease.getName());
        assertThat(result.getDefinition()).isEqualTo(disease.getDefinition());
        assertThat(result.getRecommendDepartment()).isEqualTo(disease.getRecommendDepartment());
    }

    @Test
    void 없는_질병이름으로_DiseaseSimpleQueryDto_찾기() throws Exception{
        //when
        Optional<SimpleDiseaseQueryDto> result = diseaseQuerydslRepository.findDiseaseSimpleQueryDtoByName("없는 질병");

        //then
        assertThat(result).isEqualTo(Optional.empty());
    }
}
