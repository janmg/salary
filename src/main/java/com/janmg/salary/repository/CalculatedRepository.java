package com.janmg.salary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.janmg.salary.domain.CalculatedEntry;

@RepositoryRestResource(collectionResourceRel = "rest/totalpay", path = "rest/totalpay")
public interface CalculatedRepository extends JpaRepository<CalculatedEntry, Long> {
    List<Double> findByPersidAndMonthyear(int id, String monthyear);

    List<CalculatedEntry> findByMonthyear(String monthyear);

    @Query("SELECT DISTINCT monthyear FROM CalculatedEntry")
    List<String> findDistinctByMonthyear();
}
