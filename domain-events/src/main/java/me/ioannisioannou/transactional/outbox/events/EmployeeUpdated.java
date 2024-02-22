package me.ioannisioannou.transactional.outbox.events;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeUpdated extends DomainEvent {
    private UUID employeeId;
    private String firstName;
    private String lastName;
    private String email;
}
