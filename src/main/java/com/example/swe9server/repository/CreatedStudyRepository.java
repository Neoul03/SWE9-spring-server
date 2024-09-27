package com.example.swe9server.repository;

import com.example.swe9server.entity.CreatedStudyEntity;
import com.example.swe9server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreatedStudyRepository extends JpaRepository<CreatedStudyEntity, Long> {
    List<CreatedStudyEntity> findByUser(UserEntity user);

    void deleteByStudyId(Long studyId);

    void deleteByUserId(Long userId);
}