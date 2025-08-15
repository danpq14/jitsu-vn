package com.jitsu.assignment.repository;

import com.jitsu.common.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByZone(String zone);
    List<Assignment> findByTargetDate(LocalDate targetDate);
    List<Assignment> findByClaimedByDriverId(Long driverId);
    List<Assignment> findByZoneAndTargetDateAndClaimedByDriverIdIsNull(String zone, LocalDate targetDate);
    List<Assignment> findByClaimedByDriverIdIsNull();
    List<Assignment> findByZoneInAndClaimedByDriverIdIsNull(List<String> zones);
}