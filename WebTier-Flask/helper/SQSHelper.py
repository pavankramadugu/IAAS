import boto3
from config import Config

properties = Config()


def get_SQS_message_count():
    sqs = boto3.client(
        'sqs',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )
    try:
        print("Getting Message Count from Request Queue")
        queue_attributes = sqs.get_queue_attributes(
            QueueUrl=properties.get_property('REQUEST_SQS'),
            AttributeNames=[
                'All'
            ]
        )
        sqs.close()
        return queue_attributes['Attributes']['ApproximateNumberOfMessages']

    except Exception as e:
        print(e)
        return e


def publish_message(message):
    sqs = boto3.client(
        'sqs',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )
    try:
        sqs.send_message(
            QueueUrl=properties.get_property('REQUEST_SQS'),
            MessageBody=message,
        )
        print("Sent Image details to Queue: " + message)

    except Exception as e:
        print(e)
        return e

    finally:
        sqs.close()


def read_messages_from_SQS():
    sqs = boto3.client(
        'sqs',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )

    try:
        response = sqs.receive_message(
            QueueUrl=properties.get_property('RESPONSE_SQS'),
            MaxNumberOfMessages=10,
            WaitTimeSeconds=15
        )
        return response.get('Messages', [])

    except Exception as e:
        print(e)
        return e

    finally:
        sqs.close()


def delete_message(message):
    sqs = boto3.client(
        'sqs',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )

    try:
        sqs.delete_message(
            QueueUrl=properties.get_property('RESPONSE_SQS'),
            ReceiptHandle=message['ReceiptHandle']
        )

    except Exception as e:
        print(e)

    finally:
        sqs.close()
