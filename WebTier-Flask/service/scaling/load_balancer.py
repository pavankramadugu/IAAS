from helper import EC2Helper, SQSHelper
from config import Config

properties = Config()


def scale_in_and_out():
    running_app_instance_count = EC2Helper.get_instances_count()
    print("Number of Running App Instances: " + str(running_app_instance_count))
    count = SQSHelper.get_SQS_message_count()
    if count:
        message_count = int(count)
        print("Number of Messages in the Queue: " + count)
        max_possible_instances = properties.get_property("MAX_INSTANCES") - running_app_instance_count
        if message_count > 0 and message_count > running_app_instance_count and max_possible_instances > 0:
            num_of_instances_to_create = min(max_possible_instances, message_count - running_app_instance_count)
            print("Delta is Greater than 0, Creating " + str(num_of_instances_to_create)
                  + " app instances to process images.")
            EC2Helper.create_instances(num_of_instances_to_create)
