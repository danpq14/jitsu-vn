package com.jitsu.assignment.controller;

import com.jitsu.assignment.service.AssignmentService;
import com.jitsu.common.dto.AdminClaimAssignmentRequest;
import com.jitsu.common.dto.ClaimAssignmentRequest;
import com.jitsu.common.dto.DriverIdRequest;
import com.jitsu.common.model.Assignment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.jitsu.common.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@Tag(name = "Assignments", description = "Assignment Management API")
public class AssignmentController {
    
    @Autowired
    private AssignmentService assignmentService;
    
    @GetMapping
    @Operation(summary = "Get all assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.findById(id));
    }
    
    @GetMapping("/zone/{zone}")
    @Operation(summary = "Get assignments by zone")
    public ResponseEntity<List<Assignment>> getAssignmentsByZone(@PathVariable String zone) {
        return ResponseEntity.ok(assignmentService.findByZone(zone));
    }
    
    @GetMapping("/driver")
    @Operation(summary = "Get assignments claimed by current driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<Assignment>> getAssignmentsForCurrentDriver() {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(assignmentService.findByDriverId(driverId));
    }
    
    @GetMapping("/available/for-driver")
    @Operation(summary = "Get assignments available for current driver's ticket zones")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<Assignment>> getAvailableAssignmentsForCurrentDriver() {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(assignmentService.findAvailableForDriver(driverId));
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get all available (unclaimed) assignments")
    public ResponseEntity<List<Assignment>> getAvailableAssignments() {
        return ResponseEntity.ok(assignmentService.findAvailable());
    }
    
    @GetMapping("/available/zone/{zone}")
    @Operation(summary = "Get available assignments by zone and date")
    public ResponseEntity<List<Assignment>> getAvailableAssignmentsByZoneAndDate(
            @PathVariable String zone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return ResponseEntity.ok(assignmentService.findAvailableByZoneAndDate(zone, targetDate));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new assignment (Admin only)")
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.create(assignment));
    }
    
    // ========= DRIVER APIs for claim/unclaim assignments =========
    
    @PostMapping("/{assignmentId}/claim")
    @Operation(summary = "Claim assignment (Driver)", description = "Driver claims an assignment using their booked ticket")
    public ResponseEntity<Assignment> claimAssignment(
            @PathVariable Long assignmentId, 
            @RequestBody ClaimAssignmentRequest request) {
        Long driverId = AuthUtils.getCurrentUserId();
        Long ticketId = request.getTicketId();
        
        return ResponseEntity.ok(assignmentService.claimAssignment(assignmentId, driverId, ticketId));
    }
    
    @PostMapping("/{assignmentId}/unclaim")
    @Operation(summary = "Unclaim assignment (Driver)", description = "Driver unclaims their assignment")
    public ResponseEntity<Assignment> unclaimAssignment(@PathVariable Long assignmentId) {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(assignmentService.unclaimAssignment(assignmentId, driverId));
    }
    
    // ========= ADMIN APIs to manage driver assignments =========
    
    @PostMapping("/{assignmentId}/admin-claim")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin claim assignment for driver", description = "Admin claims assignment on behalf of driver")
    public ResponseEntity<Assignment> adminClaimAssignment(
            @PathVariable Long assignmentId, 
            @RequestBody AdminClaimAssignmentRequest request) {
        Long driverId = request.getDriverId();
        Long ticketId = request.getTicketId();
        
        return ResponseEntity.ok(assignmentService.claimAssignment(assignmentId, driverId, ticketId));
    }
    
    @PostMapping("/{assignmentId}/admin-unclaim")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin unclaim assignment for driver", description = "Admin unclaims assignment on behalf of driver")
    public ResponseEntity<Assignment> adminUnclaimAssignment(
            @PathVariable Long assignmentId, 
            @RequestBody DriverIdRequest request) {
        Long driverId = request.getDriverId();
        
        return ResponseEntity.ok(assignmentService.unclaimAssignment(assignmentId, driverId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete assignment (Admin only)")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.ok().build();
    }
}