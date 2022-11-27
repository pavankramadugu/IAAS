properties = {
    "REQUEST_SQS": "https://sqs.us-east-1.amazonaws.com/867386276772/request",
    "RESPONSE_SQS": "https://sqs.us-east-1.amazonaws.com/867386276772/response",
    "REQUEST_S3": "openstack-546-request-bucket",
    "RESPONSE_S3": "openstack-546-response-bucket",
    "ACCESS_KEY": "",
    "SECRET_KEY": "",
    "KEY_PAIR": "546-KeyPair",
    "SG": "sg-025612337724c49d7",
    "AMI_ID": "ami-037b9a6ecfacdbc39",
    "MAX_INSTANCES": 20
}


class Config(object):
    def __init__(self):
        self._config = properties

    def get_property(self, property_name):
        if property_name not in self._config.keys():
            return None
        return self._config[property_name]
