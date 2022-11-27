from flask import Flask, request
from service import service
from service.scaling import load_balancer
from flask_apscheduler import APScheduler

app = Flask(__name__)

scheduler = APScheduler()
scheduler.init_app(app)
scheduler.start()


@scheduler.task('interval', id='load_balancing_job', seconds=2)
def load_balancing_job():
    load_balancer.scale_in_and_out()


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


if __name__ == '__main__':
    app.run()
