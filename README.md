# Sunbird-Cloud-Storage-SDK-Demo

### Prerequisites
1. JDK 11 
2. Maven : https://maven.apache.org/install.html\

### Setup
1. #### Clone the Repository:
Open a terminal or command prompt and navigate to the directory where you want to clone the project. Use the git clone command to clone the repository.

```
git clone git@github.com:abhishekpnt/cloud-storage-sdk-demo.git
```

2. #### Navigate to the Project Directory:
Change your working directory to the cloned project's directory:
  ```
cd cloud-storage-sdk-demo
```

3. #### Set CSP related configuration:
In `application.properties` file in `src/main/resources/` provide the required configuration for respective Cloud Provider

| Generalized keys |             Azure            |             AWS            |              GCP              |              OCI              | 
|:----------------:|:----------------------------:|:--------------------------:|:-----------------------------:|:-----------------------------:|
|     provider     |            `azure`           |            `aws`           |            `gcloud`           |            `oci`              |
|     storageKey     |      Azure Account Name      |       AWS Access Key       |        GCP Client Email       |        OCI S3 Access Key      |
|    storageSecret    |       Azure Account Key      |       AWS Secret Key       |        GCP Private Key        |        OCI S3 Secret Key      |
|      storageRegion      |              --              |         AWS Region         |               --              |              OCI Region       |
|     storageEndpoint     |              --              |             --             |               --              |        OCI S3 endpoint        |

---
4. #### Build the Project:
Use Maven to build the project. Run the following command in the project directory:

```
mvn clean install
```
This will download the project's dependencies and build the project. Ensure that Maven is installed on your system before running this command.

5. #### Run the Application:
After a successful build, you can run the application using the Spring Boot Maven plugin. Run the following command:

```
mvn spring-boot:run
```
This command will start the Spring Boot application.

---
### Usage
### Run Curl commands provided below in Postman to get the response

#### Use respective curl commands for each method
1. `getSignedURL` - To get the downloadable signedURL of the file

```
curl --location 'http://localhost:8080/getUrl?container=<container>&objectKey=<filepath>'
```
where
- container: container in which blob exist
- filepath : path of the file in the container

Eg:
```
curl --location 'http://localhost:8080/getUrl?container=reports&objectKey=test/sample.pdf'
```
---

2. `upload` - To upload a file 
```
curl --location 'http://localhost:8080/upload?container=<container>&objectKey=<filekey>&filePath=<filepath>'
```
where
- container: container in which blob has to be uploaded
- filekey : The path on the container where the file has to be created
- filepath : The local absolute path of the file to be uploaded

Eg:
```
curl --location 'http://localhost:8080/upload?container=reports&objectKey=test/sample.pdf&filePath=/Users/abc/sample.pdf'
```
---

3. `download` - To download a file 
```
curl --location 'http://localhost:8080/download?container=<container>&objectKey=<filekey>&filePath=<filepath>'
```
where
- container: container in which blob has to be uploaded
- filekey : The file path on the container where the file exists
- filepath : The local path where the file has to be downloaded

Eg:
```
curl --location 'http://localhost:8080/download?container=reports&objectKey=test/sample.pdf&filePath=/Users/abc/Downloads/'
```
---
4. `delete` - To delete a file 
```
curl --location 'http://localhost:8080/delete?container=<container>&objectKey=<filekey>'
```
where
- container: container in which blob has to be uploaded
- filekey : The file path on the container where the file exists

Eg:
```
curl --location 'http://localhost:8080/delete?container=reports&objectKey=test/sample.pdf'
```
---