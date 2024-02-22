package me.ioannisioannou.transactional.outbox.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmployeeCreated.class, name = "EMPLOYEE_CREATED"),
        @JsonSubTypes.Type(value = EmployeeUpdated.class, name = "EMPLOYEE_UPDATED"),
        @JsonSubTypes.Type(value = EmployeeDeleted.class, name = "EMPLOYEE_DELETED"),
})

public abstract class DomainEvent implements Serializable {

    protected UUID eventId = UUID.randomUUID();

    public UUID getEventId() {
        return eventId;
    }
}
