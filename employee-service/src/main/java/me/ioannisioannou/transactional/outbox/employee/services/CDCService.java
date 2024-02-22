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

import static io.awspring.cloud.sns.core.SnsHeaders.MESSAGE_DEDUPLICATION_ID_HEADER;
import static io.awspring.cloud.sns.core.SnsHeaders.MESSAGE_GROUP_ID_HEADER;

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
                    final DomainEvent payload = entity.getPayload();
                    String payloadValue = objectMapper.writeValueAsString(payload);
                    logger.info("Publishing " + payloadValue + " to topic " + snsTopic);

                    snsTemplate.convertAndSend(snsTopic, payload,
                            Map.of(MESSAGE_GROUP_ID_HEADER, buildMessageGroupIdHeader(entity),
                                    MESSAGE_DEDUPLICATION_ID_HEADER, payload.getEventId().toString(),
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

    private static String buildMessageGroupIdHeader(Outbox entity) {
        return String.format("%s-%s", entity.getAggregateType(), entity.getAggregateId());
    }
}
