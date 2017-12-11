package com.janmg.salary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.janmg.salary.domain.Employee;

@RepositoryRestResource(collectionResourceRel = "rest/employee", path = "rest/employee")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	List<Employee> findByPersid(int id);
}
