<br />
<div align="center">
<h2 align="center">Image Recognition as a Service</h2>


</div>

Using the Hybrid cloud, we created an elastic application that can automatically scale out and in on-demand and cost-effectively. Our cloud app will provide image recognition services to users by utilizing cloud resources to perform deep learning on images provided by the users.

### Built With

The application is powered by Flask as a WebTier Server, OpenStack for WebTier, and Amazon EC2 for AppTier as a cloud resource to deploy/scale as needed.
<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

* Java 11
  ```sh
  sudo apt-get install openjdk-11-jdk
  ```
* Gradle
  [Install Gradle](https://gradle.org/install/)
* Python3
    ```sh
  sudo apt-get install python3
  ```

### Installation

Steps to install and set up the app.

1. Clone the repo
   ```sh
   git clone git@github.com:pavankramadugu/IAAS.git
   ```

2. Add AWS account access and secret keys here at,  

   `AppTier`: [AppTierProperties](https://github.com/pavankramadugu/IAAS/blob/master/AppTier/src/main/java/com/cc/app/properties/AppTierProperties.java), `WebTier`: [Config.py](https://github.com/pavankramadugu/IAAS/blob/feature/flask-webTier/WebTier-Flask/config.py)


3. Build the AppTier Project by clean building using the gradle package manager
   ```sh
   cd AppTier
   ./gradlew clean build
   ```
3. Build the WebTier Project by clean building using the pip package manager
   ```sh
   cd WebTier
   pip install -r requirements.txt
   ```
4. To start the flask server use the following command
   ```
   nohup python3 -m flask run > output.log &
   ```
   
5. Tail the server logs using, ```tail -f output.log```


## OpenStack Setup

We installed OpenStack on Ubuntu with the use of DevStack. Devstack is a series of extensible scripts, which is used to set up an OpenStack environment with ease.

1. We need to ensure that our system is updated, for that run following command:
    ```
   sudo apt-get update && sudo apt-get upgrade -y
   ```
2. Creating stack user with Sudo privileges.
    ```
   sudo useradd -s /bin/bash -d /opt/stack -m stack
   
   echo "stack ALL=(ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/stack
   ```
3. Downloading Devstack.
    ```
   cd devstack
   
   vim local.conf
   ```
   and paste the following content –
    ```
   [[local|localrc]]
   ADMIN_PASSWORD=StrongAdminSecret
   DATABASE_PASSWORD=$ADMIN_PASSWORD
   RABBIT_PASSWORD=$ADMIN_PASSWORD
   SERVICE_PASSWORD=$ADMIN_PASSWORD
   ```
4. Installing Openstack with Devstack
    ```
   ./stack.sh
   ```
5. Accessing OpenStack using a web browser.
    ```
   https://server-ip/dashboard
   ```
   
After Installing Openstack, We need to create an Instance. Following steps are to be done to create an instance.

1. Create a Private Network.
   To create a private network, begin by navigating to Project -> Network -> Networks.
   Load the form to create a network, by navigating to Create Network near the top right.
   ![Image1](https://creodias.eu/documents/20195/74031/a3.png/5f391ced-2d9e-4a44-9902-69db1928df43?t=1552980724603)
    Define your Network Name and tick two checkboxes: Enable Admin State and Create Subnet. Go to Next.
   ![Image2](https://openmetal.io/docs/manuals/assets/images/network-form-6286d9b525c127740fdae8e1da6041be.png)
   Next, move on to the Subnet tab of this form.    Define your Subnet name. Assign a valid network address with mask presented as a prefix. (This number determines how many bytes are being destined for network address) Define Gateway IP for your Router. Normally it’s the first available address in the subnet.
   ![Image3](https://openmetal.io/docs/manuals/assets/images/network-form-subnet-e3c25600ef39acabe452f8cfe1d8c0ab.png)
   In Subnet Details you are able to turn on DHCP server, assign DNS servers to your network and set up basic routing. In the end, confirm the process with “Create” button.
   ![Image4](https://creodias.eu/documents/20195/74031/test_network_dhcp_dns.png/54e8c260-42a9-4f40-a8ba-10c0b3d28374?t=1578483149443)
   Click on the Routers tab. Click on the “Create Router” button. Name your device and assign the only available network → external. Finish by choosing “Create Router” blue button.
   ![Image5](https://creodias.eu/documents/20195/74031/router_config.png/9fd6b849-bec6-4e72-9c11-40cf50496c44?t=1578483133820)
   Click on your newly created Router (e.g called “Router_1”). Choose Interfaces. Choose + Add Interface button. Assign a proper subnet and fill in IP Address. (It’s the gateway for our network). Submit the process.
   ![Image6](https://creodias.eu/documents/20195/74031/router_interface.png/d1791a40-fefe-4557-af0a-f606956a5ed9?t=1578483137665)
   The instance created previously is associated with a private network. Presently, the only way to access this instance is to connect to it from with the cloud's hardware nodes. Another option for connecting is to use a floating IP. Next, load the form to allocate a floating IP by pressing Allocate IP to Project.
   ![Image7](https://openmetal.io/docs/manuals/assets/images/allocate-floating-ip-a752155aa5bf66b873fd4ca498ff2298.png)

2. Next, We need to create a security group. Security groups allow control of network traffic to and from instances. Allow TCP(5000), ICMP and SSH rules.
   ![Image7](https://openmetal.io/docs/manuals/assets/images/add-ssh-rule-fa4aac4e7d1b17194061b26ecd6d8721.png)

3. We need to specify an SSH public key to inject into the instance. We can create a Key-Pair and save the Private key to connect to the instance.

After Setting up connectivity, we need to import a source image to create the instance. I used Ubuntu 20.04 Focal QCoW2 Cloud image to create an instance.

1. Create Instance, To create the first instance, begin by navigating to Project -> Compute -> Instances. Pull up the form to create an instance by navigating to Launch Instance near the top right.
   ![Image8](https://openmetal.io/docs/manuals/assets/images/instances-be8f61308554034db9c53d662d6e4214.png)
2. Next, move to the Source tab allowing you to specify an operating system image.
   ![Image9](https://openmetal.io/docs/manuals/assets/images/instance-source-8c9ee13036059ca30c502a55427817a2.png)
3. Then Select the Ubuntu Image and Move to Flavours.
4. Flavors are a way to define the VCPUs, RAM, and Disk space used by an instance. Pre-built flavors are available for you. For this step, select an appropriate flavor from the options under the Available heading.
5. Then, add the private network, security group and SSH KeyPair created into the instance.
6. Launch the instance with the created configuration.

Using the floating IP, SSH into the instance and Launch the WebTier server.


<!-- USAGE EXAMPLES -->
## Usage

The app should take images received from users as input and perform image recognition on these images using the provided deep learning model. It should also return the recognition result (the top 1 result from the provided model) as output to the users. The input is a .png file, and the output is the prediction result. For example, the user uploads an image named “test_0.JPEG”. For the above request, the output should be “bathtub” in plain text.

To facilitate the testing, a standard image dataset is provided at: [imagenet-100-updated.zip](https://canvas.asu.edu/courses/128579/files/51752784/download).

Upload the images to ```http://<floating-ip>:5000/imageUpload```. Please wait for the model to respond.

For load-testing use following endpoint as url :- `http://<floating-ip>:5000/imageUpload`

```yml
sqs:
  queue:
    request: https://sqs.us-east-1.amazonaws.com/501410091785/request
    response: https://sqs.us-east-1.amazonaws.com/501410091785/response

s3:
  bucket:
    request: openstack-request
    response: openstack-response

elasticIP: 34.85.189.64
```


## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://pavankramadugu.github.io/"><img src="https://avatars.githubusercontent.com/u/73785007?v=4" width="100px;" alt=""/><br /><sub><b>Pavan K Ramadugu</b></td>
      <td align="center"><a href="https://github.com/snehalchaudhari98"><img src="https://avatars.githubusercontent.com/u/31732637?v=4" width="100px;" alt=""/><br /><sub><b>Snehal Chaudhari</b></td>
      <td align="center"><a href="https://github.com/JaydeepBhoite"><img src="https://avatars.githubusercontent.com/u/112657685?v=4" width="100px;" alt=""/><br /><sub><b>Jaydeep Bhoite</b></a></td>
    </tr>

  </tbody>
</table>


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.
