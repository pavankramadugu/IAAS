import boto3
import datetime
from config import Config

properties = Config()

USERDATA_SCRIPT = '''#!/bin/bash
su ubuntu -c "cd /home/ubuntu/classifier && java -jar AppTier-1.0-SNAPSHOT.jar"'''


def get_instances_count():
    ec2_client = boto3.client(
        'ec2',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )
    try:
        print("Getting Running Instances Count")

        response = ec2_client.describe_instances(
            Filters=[{'Name': 'instance-state-name', 'Values': ['running', 'pending']}])
        count = 0
        for reservation in response["Reservations"]:
            for _ in reservation["Instances"]:
                count += 1
        return count
    except Exception as e:
        print(e)
    finally:
        ec2_client.close()


def create_instances(instances_count):
    for _ in range(instances_count):
        now = datetime.datetime.now()
        instance_name = 'AppTier-' + str(now.hour) + str(now.minute) + str(now.second)
        print("Starting EC2 Instance with Name: " + instance_name)
        start_instance(instance_name=instance_name)


def start_instance(instance_name):
    ec2_client = boto3.client(
        'ec2',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )
    try:
        ec2_client.run_instances(ImageId=properties.get_property("AMI_ID"),
                                 InstanceType='t2.micro',
                                 KeyName=properties.get_property('KEY_PAIR'),
                                 UserData=USERDATA_SCRIPT,
                                 MinCount=1,
                                 MaxCount=1,
                                 SecurityGroupIds=[properties.get_property('SG')],
                                 InstanceInitiatedShutdownBehavior='terminate',
                                 TagSpecifications=[
                                     {
                                         'ResourceType': 'instance',
                                         'Tags': [
                                             {
                                                 'Key': 'Name',
                                                 'Value': instance_name
                                             }
                                         ]
                                     }
                                 ])
        print("Started EC2 Instance with Name: " + instance_name)
    except Exception as e:
        print(e)
    finally:
        ec2_client.close()
