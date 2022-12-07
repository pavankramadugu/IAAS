properties = {
    "REQUEST_SQS": "https://sqs.us-east-1.amazonaws.com/501410091785/request",
    "RESPONSE_SQS": "https://sqs.us-east-1.amazonaws.com/501410091785/response",
    "REQUEST_S3": "openstack-request",
    "RESPONSE_S3": "openstack-response",
    "ACCESS_KEY": "",
    "SECRET_KEY": "",
    "KEY_PAIR": "546-KeyPair",
    "SG": "sg-083ae66a1b826a1e7",
    "AMI_ID": "ami-0c287ee091fb5747b",
    "MAX_INSTANCES": 20
}


class Config(object):
    def __init__(self):
        self._config = properties

    def get_property(self, property_name):
        if property_name not in self._config.keys():
            return None
        return self._config[property_name]
