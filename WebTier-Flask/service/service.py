from helper import S3Helper, SQSHelper

image_result_map = dict()


def process_images(images):
    image_names = []
    try:
        print("Uploading Images to S3 and SQS")
        for image in images:
            image_name = S3Helper.upload_file_to_s3(file=image)
            SQSHelper.publish_message(image_name)
            image_names.append(image_name)
        print("Uploaded Images to S3 and SQS")
        return get_results_from_response_queue(image_names)

    except Exception as e:
        print(e)
        return {}


def get_results_from_response_queue(image_names):
    while True:
        print("Waiting to read classification response from Response Queue")
        result_messages = SQSHelper.read_messages_from_SQS()

        for message in result_messages:
            values = message['Body'].split(',')
            image_result_map[values[0]] = values[1]
            SQSHelper.delete_message(message)

        if all(item in list(image_result_map.keys()) for item in image_names):
            return {key: image_result_map[key] for key in image_names}


def clear_results():
    print("Clearing Result Map")
    image_result_map.clear()
    return "Cleared Result Map"


def get_results():
    return image_result_map
