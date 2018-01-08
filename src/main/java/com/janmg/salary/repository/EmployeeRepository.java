package com.janmg.salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.janmg.salary.domain.Employee;

@RepositoryRestResource(collectionResourceRel = "rest/employee", path = "rest/employee")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Employee findByPersid(int id);
	
	@Modifying
	@Query("update Employee emp set emp.hourlyrate = :rate where emp.persid = :persid")
	void setRateByPersid(int persid, double rate);
}
