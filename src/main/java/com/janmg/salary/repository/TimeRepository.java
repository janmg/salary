package com.janmg.salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.janmg.salary.domain.TimeEntry;

// https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/
@Repository
public interface TimeRepository extends JpaRepository<TimeEntry, Long> {
    //List<TimeEntry> findDistinctByPersid();
}
