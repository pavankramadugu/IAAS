import sys
import requests
import os
import argparse
import _thread
import datetime
import subprocess
from collections import deque
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import as_completed

# req_id = 1
parser = argparse.ArgumentParser(description='Upload images')
parser.add_argument('--num_request', type=int, help='one image per request')
parser.add_argument('--url', type=str, help='URL to the backend server, e.g. http://3.86.108.221/xxxx.php')
parser.add_argument('--image_folder', type=str, help='the path of the folder where images are saved on your local machine')

args = parser.parse_args()
url = args.url

print('Snehal')

def send_one_request(image_path):
    # Define http payload, "myfile" is the key of the http payload
    #On line 24, be wary that you change the http request key from 'myfile' to whatever suits your we tier
    # global req_id
    msg ="sample result"
    file = {"images": open(image_path,'rb')} 
    headers = {'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36'}
    print("sending req. for file : ",image_path)
    r = requests.post(url+"imageUpload", files=file, headers = headers)
    # Print error message if failed
    # print(" Response :: status code ",r.status_code)
    if r.status_code != 200:
        print('sendErr: '+r.url + ' ' + r.text)
        # return "Error in upload"
    else :         
        path = image_path.split('\\')
        image_msg = path[len(path)-1] + ' uploaded!'
        msg = image_msg + '  Classification result: ' + r.text
        # print("Fetched result: ",msg)
    # req_id = req_id + 1
    return msg

def clear_res():
    r = requests.get(url+"clearResults")
    if r.status_code != 200:
        print('sendErr: '+r.url + ' ' + r.text)
        # return "Error in upload"
    else :         
        print(' Response: ' + r.text)

start_time = datetime.datetime.now()
print("start_time : ", start_time )
num_request = args.num_request
image_folder = args.image_folder
num_max_workers = 100
image_path_list = []
for i, name in enumerate(os.listdir(image_folder)):
    if i == num_request:
        break
    image_path_list.append(str(image_folder)+"\\"+name)

print("image_list",image_path_list)
clear_res()
with ThreadPoolExecutor(max_workers = num_max_workers) as executor:
    for result in executor.map(send_one_request, image_path_list):
        print(result)

end_time =  datetime.datetime.now()
print("end_time : ",end_time)
print("Tota time : ", end_time - start_time)
