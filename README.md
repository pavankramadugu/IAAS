<br />
<div align="center">
<h2 align="center">Image Recognition as a Service</h2>


</div>

Using the IaaS cloud, we created an elastic application that can automatically scale out and in on-demand and cost-effectively. Our cloud app will provide image recognition services to users by utilizing cloud resources to perform deep learning on images provided by the users.

### Built With

The application is powered by Spring Boot as a WebTier Server and Amazon EC2 as a cloud resource to deploy/scale as needed.

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

### Installation

Steps to install and set up the app.

1. Clone the repo
   ```sh
   git clone git@github.com:pavankramadugu/IAAS.git
   ```

2. Add AWS account access and secret keys here at,  

   `AppTier`: [AppTierProperties](https://github.com/pavankramadugu/IAAS/blob/master/AppTier/src/main/java/com/cc/app/properties/AppTierProperties.java), `WebTier`: [Application.yml](https://github.com/pavankramadugu/IAAS/blob/master/WebTier/src/main/resources/application.yml)


3. Build the Project by clean building using the gradle package manager
   ```sh
   ./gradlew clean build
   ```
4. Find the executable Jar in the `WebTier/build/libs` folder, to start the server use the following command
   ```
   nohup java -jar <name of the jar> > output.log &
   ```
   
5. Tail the server logs using, ```tail -f output.log```


<!-- USAGE EXAMPLES -->
## Usage

The app should take images received from users as input and perform image recognition on these images using the provided deep learning model. It should also return the recognition result (the top 1 result from the provided model) as output to the users. The input is a .png file, and the output is the prediction result. For example, the user uploads an image named “test_0.JPEG”. For the above request, the output should be “bathtub” in plain text.

To facilitate the testing, a standard image dataset is provided at: [imagenet-100-updated.zip](https://canvas.asu.edu/courses/128579/files/51752784/download).

Upload the images to ```localhost:8080```. Please wait for the model to respond.

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
