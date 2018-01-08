package com.janmg.salary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.janmg.salary.domain.TimeEntry;

// https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/
@RepositoryRestResource(collectionResourceRel = "rest/timesheet", path = "rest/timesheet")
public interface TimeRepository extends JpaRepository<TimeEntry, Long> {

    public List<TimeEntry> findByPersid(int persid);

    public List<TimeEntry> findByPersidAndMonthyear(int persid, String monthyear);

    @Query("SELECT DISTINCT monthyear FROM TimeEntry")
    public List<String> findDistinctByMonthyear();
}
