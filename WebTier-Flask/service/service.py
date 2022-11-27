from helper import S3Helper, SQSHelper

image_result_map = dict()


def process_images(images):
    image_names = S3Helper.upload_images_to_S3(images)

    try:
        for image in image_names:
            SQSHelper.publish_message(image)
        return get_results_from_response_queue(image_names)

    except Exception as e:
        print(e)
        return {}


def get_results_from_response_queue(image_names):
    while not set(image_names).issubset(image_result_map.keys()):
        print("Waiting to read classification response from Response Queue")
        result_messages = SQSHelper.read_messages_from_SQS()

        for message in result_messages:
            values = message['Body'].split(',')
            image_result_map[values[0]] = values[1]
            SQSHelper.delete_message(message)

    return {key: image_result_map[key] for key in image_names}


def clear_results():
    print("Clearing Result Map")
    image_result_map.clear()
    return "Cleared Result Map"


def get_results():
    return image_result_map
