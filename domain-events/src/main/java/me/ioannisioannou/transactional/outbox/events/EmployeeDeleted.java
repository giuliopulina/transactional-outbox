package me.ioannisioannou.transactional.outbox.events;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EmployeeDeleted extends DomainEvent {
    private UUID employeeId;
}
