import boto3
from config import Config
from werkzeug.utils import secure_filename

properties = Config()


def upload_file_to_s3(file):
    secure_filename(file.filename)
    s3 = boto3.client(
        's3',
        aws_access_key_id=properties.get_property("ACCESS_KEY"),
        aws_secret_access_key=properties.get_property("SECRET_KEY"),
        region_name='us-east-1'
    )
    try:
        print("Uploading Image: " + file.filename)
        s3.upload_fileobj(
            file,
            properties.get_property("REQUEST_S3"),
            file.filename,
        )
        print("Successfully Uploaded Image: " + file.filename)
        return file.filename

    except Exception as e:
        print(e)
        return e

    finally:
        s3.close()


def upload_images_to_S3(images):
    print("Uploading Images to S3")
    file_list = []
    for image in images:
        upload_file_to_s3(file=image)
        file_list.append(image.filename)
    print("Successfully Uploaded Images to S3")

    return file_list
