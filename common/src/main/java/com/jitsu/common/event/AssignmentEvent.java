package com.jitsu.common.event;

import com.jitsu.common.model.Assignment;

public class AssignmentEvent extends BaseEvent {
    private Assignment assignment;
    
    public AssignmentEvent() {}
    
    public AssignmentEvent(String eventId, String userId, EventType eventType, Assignment assignment) {
        super(eventId, userId, eventType);
        this.assignment = assignment;
    }
    
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
}