#!/bin/sh
REGION=eu-central-1

topic_arn=$(awslocal sns create-topic --name employee_events_topic.fifo --attribute FifoTopic=true,ContentBasedDeduplication=true --output text --region $REGION)
echo "Topic ARN: $topic_arn"

queue_url_1=$(awslocal sqs create-queue --queue-name consumer_1_queue.fifo --attribute FifoQueue=true --output text --region $REGION)
echo "Queue 1 URL: $queue_url_1"

queue_arn_1=$(awslocal sqs get-queue-attributes --queue-url "$queue_url_1" --attribute-names QueueArn --output text --region $REGION)
queue_arn_1=$(echo "$queue_arn_1" | sed s/"ATTRIBUTES"// | sed 's/ *$//g')
echo "Queue 1 ARN: $queue_arn_1"

queue_url_2=$(awslocal sqs create-queue --queue-name consumer_2_queue.fifo --attribute FifoQueue=true --output text --region $REGION)
echo "Queue 2 URL: $queue_url_2"

queue_arn_2=$(awslocal sqs get-queue-attributes --queue-url "$queue_url_2" --attribute-names QueueArn --output text --region $REGION)
queue_arn_2=$(echo "$queue_arn_2" | sed s/"ATTRIBUTES"// | sed 's/ *$//g')
echo "Queue 2 ARN: $queue_arn_2"

subscription_arn_1=$(awslocal sns subscribe \
    --topic-arn "$topic_arn" \
    --protocol sqs \
    --notification-endpoint "$queue_arn_1" \
    --attributes '{"FilterPolicy":"{\"eventType\":[\"EmployeeCreated\", \"EmployeeUpdated\"]}", "RawMessageDelivery": "true"}' \
    --output text \
    --region $REGION)



echo "Subscription ARN 1: $subscription_arn_1"

subscription_arn_2=$(awslocal sns subscribe \
    --topic-arn "$topic_arn" \
    --protocol sqs \
    --notification-endpoint "$queue_arn_2" \
    --attributes '{"FilterPolicy":"{\"eventType\":[\"EmployeeDeleted\"]}", "RawMessageDelivery": "true"}' \
    --output text \
    --region $REGION)

echo "Subscription ARN 2: $subscription_arn_2"