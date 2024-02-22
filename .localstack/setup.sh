#!/bin/sh
REGION=eu-central-1

topic_arn=$(awslocal sns create-topic --name employee_events_topic.fifo --attribute FifoTopic=true,ContentBasedDeduplication=true --output text --region $REGION)
echo "Topic ARN: $topic_arn"

dlq_queue_url_1=$(awslocal sqs create-queue \
                  --queue-name dlq_consumer_1_queue.fifo \
                  --attribute FifoQueue=true \
                  --output text \
                  --region $REGION)
echo "DLQ Queue 1 URL: $dlq_queue_url_1"

dlq_queue_arn_1=$(awslocal sqs get-queue-attributes \
              --queue-url "$dlq_queue_url_1" \
              --attribute-names QueueArn \
              --output text \
              --region $REGION)

dlq_queue_arn_1=$(echo "$dlq_queue_arn_1" | sed s/"ATTRIBUTES"// | tr -d '[:space:]')
echo "DLQ Queue 1 ARN: $dlq_queue_arn_1"

queue_url_1=$(awslocal sqs create-queue \
              --queue-name consumer_1_queue.fifo \
              --attributes DelaySeconds=1,MaximumMessageSize=200000,FifoQueue=true,MessageRetentionPeriod=3600,ReceiveMessageWaitTimeSeconds=5,VisibilityTimeout=60,ContentBasedDeduplication=false,DeduplicationScope=messageGroup,RedrivePolicy="\"{\\\"deadLetterTargetArn\\\":\\\"$dlq_queue_arn_1\\\",\\\"maxReceiveCount\\\":\\\"2\\\"}\""\
              --output text \
              --region $REGION)
#
echo "Queue 1 URL: $queue_url_1"

queue_arn_1=$(awslocal sqs get-queue-attributes \
              --queue-url "$queue_url_1" \
              --attribute-names QueueArn \
              --output text \
              --region $REGION)

queue_arn_1=$(echo "$queue_arn_1" | sed s/"ATTRIBUTES"// | tr -d '[:space:]')
echo "Queue 1 ARN: $queue_arn_1"

subscription_arn_1=$(awslocal sns subscribe \
    --topic-arn "$topic_arn" \
    --protocol sqs \
    --notification-endpoint "$queue_arn_1" \
    --attributes '{"RawMessageDelivery": "true"}' \
    --output text \
    --region $REGION)

echo "Subscription ARN 1: $subscription_arn_1"
