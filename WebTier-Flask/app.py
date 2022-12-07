from flask import Flask, request
from service import service
from service.scaling import load_balancer
import time
from concurrent.futures import ThreadPoolExecutor

executor = ThreadPoolExecutor(2)

app = Flask(__name__)


def load_balancing_job():
    while True:
        load_balancer.scale_in_and_out()
        time.sleep(3)


@app.route('/')
def get_results():
    return service.get_results()


@app.route('/imageUpload', methods=["POST"])
def imageUpload():
    images = request.files.getlist("images")
    return service.process_images(images)


@app.route('/clearResults')
def clear_results():
    return service.clear_results()


executor.submit(load_balancing_job)

if __name__ == '__main__':
    app.run(reloader=False)
