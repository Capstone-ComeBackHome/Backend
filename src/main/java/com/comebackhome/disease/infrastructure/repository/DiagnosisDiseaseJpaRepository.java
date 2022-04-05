package com.comebackhome.disease.infrastructure.repository;

import com.comebackhome.disease.domain.DiagnosisDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiagnosisDiseaseJpaRepository extends JpaRepository<DiagnosisDisease,Long> {


    @Modifying(clearAutomatically = true)
    @Query("delete from DiagnosisDisease dd where dd.diagnosis.id =:id")
    void deleteByDiagnosisId(@Param("id") Long diagnosisId);
}
