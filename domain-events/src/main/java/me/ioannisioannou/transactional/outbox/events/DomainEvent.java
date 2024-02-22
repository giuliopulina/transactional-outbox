package me.ioannisioannou.transactional.outbox.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmployeeCreated.class, name = "EMPLOYEE_CREATED"),
        @JsonSubTypes.Type(value = EmployeeUpdated.class, name = "EMPLOYEE_UPDATED"),
        @JsonSubTypes.Type(value = EmployeeDeleted.class, name = "EMPLOYEE_DELETED"),
})

public abstract class DomainEvent implements Serializable {
}
