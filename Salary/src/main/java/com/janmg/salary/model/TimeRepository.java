package com.janmg.salary.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeRepository extends JpaRepository<TimeEntry, Long> {

    TimeEntry findByName(String pk);
}
