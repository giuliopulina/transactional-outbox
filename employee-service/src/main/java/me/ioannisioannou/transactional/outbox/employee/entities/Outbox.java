package me.ioannisioannou.transactional.outbox.employee.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.ioannisioannou.transactional.outbox.events.DomainEvent;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "outbox")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    private Long id;

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private DomainEvent payload;
}
