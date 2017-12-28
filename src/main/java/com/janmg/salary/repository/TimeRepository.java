package com.janmg.salary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.janmg.salary.domain.TimeEntry;

// https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/
@Repository
public interface TimeRepository extends JpaRepository<TimeEntry, Long> {

    public List<TimeEntry> findByPersid(int persid);

    public List<TimeEntry> findByPersidAndMonthyear(int persid, String monthyear);
    
//    @Query("SELECT te FROM TimeEntry te WHERE te.persid = :persid AND te.monthyear = ':monthyear'")
//    public List<TimeEntry> findPersidAndMonth(@Param("persid") int persid, @Param("monthyear") String monthyear);
/*
    // TODO: TemporalType? Storing date as string is bad, but ZonedDataTime and persistancy aren't that great either
    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-part-two-crud/
    @Query("SELECT te FROM TimeEntry te WHERE e.eventsDate BETWEEN :startDate AND :endDate")
    public List<TimeEntry> findBetween(String startDate, String endDate);
*/
}
