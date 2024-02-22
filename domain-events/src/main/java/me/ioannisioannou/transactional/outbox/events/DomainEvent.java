package me.ioannisioannou.transactional.outbox.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmployeeCreated.class, name = "EMPLOYEE_CREATED"),
        @JsonSubTypes.Type(value = EmployeeUpdated.class, name = "EMPLOYEE_UPDATED"),
        @JsonSubTypes.Type(value = EmployeeDeleted.class, name = "EMPLOYEE_DELETED"),
})
@ToString
public abstract class DomainEvent implements Serializable {

    protected UUID eventId = UUID.randomUUID();

    public UUID getEventId() {
        return eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEvent that = (DomainEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
