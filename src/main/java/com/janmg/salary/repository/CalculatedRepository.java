package com.janmg.salary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.janmg.salary.domain.CalculatedEntry;
import com.janmg.salary.domain.Employee;

@RepositoryRestResource(collectionResourceRel = "rest/totalpay", path = "rest/totalpay")
public interface CalculatedRepository extends JpaRepository<CalculatedEntry, Long> {
    List<Double> findByPersid(int id);
}

