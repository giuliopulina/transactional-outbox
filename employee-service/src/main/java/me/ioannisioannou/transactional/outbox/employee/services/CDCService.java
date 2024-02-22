package me.ioannisioannou.transactional.outbox.employee.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsHeaders;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import me.ioannisioannou.transactional.outbox.employee.entities.Outbox;
import me.ioannisioannou.transactional.outbox.employee.repositories.OutboxRepository;
import me.ioannisioannou.transactional.outbox.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CDCService {

    private static final Logger logger = LoggerFactory.getLogger(CDCService.class);

    private final OutboxRepository outboxRepository;

    private final SnsOperations snsOperations;

    private final SnsTemplate snsTemplate;

    private final ObjectMapper objectMapper;

    @Value("${cdc.sns_topic}")
    private String snsTopic;
    @Value("${cdc.batch_size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${cdc.polling_ms}")
    public void forwardEventsToSNS() {

        List<Outbox> entities = outboxRepository.findAllByOrderByIdAsc(Pageable.ofSize(batchSize)).toList();
        try {

            entities.forEach(entity -> {

                try {
                    String payloadValue = objectMapper.writeValueAsString(entity.getPayload());
                    logger.info("Publishing " + payloadValue + " to topic " + snsTopic);
                    /*var notification = SnsNotification.builder(payloadValue)
                            .groupId(String.format("%s-%s", entity.getAggregateType(), entity.getAggregateId()))
                            .headers(Map.of("eventType", entity.getEventType()))
                            .build();

                    snsOperations.sendNotification(snsTopic, notification);*/

                    snsTemplate.convertAndSend(snsTopic, entity.getPayload(),
                            Map.of(SnsHeaders.MESSAGE_GROUP_ID_HEADER, String.format("%s-%s", entity.getAggregateType(), entity.getAggregateId()),
                                    "eventType", entity.getEventType()));

                    logger.info("Publishing to topic " + snsTopic + " completed");
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        outboxRepository.deleteAllInBatch(entities);

    }
}
