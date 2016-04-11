# JsonJsonApplication


Instead of IIB REST API, This one is IIB Application. Created using IBM Integration Bus 10.0.0.3. The REST API needs swagger specification. Application can create a REST API without swagger definition and with more flexibility of input output formats. Swagger specifications can be created independently.

The functionality of this application is same as https://github.com/sanketsw/HttpRequestRestAPI.git (more details in its readme file)

the comparable REST API implementation in IIB is **JsonJsonRESTAPI**:  https://github.com/sanketsw/JsonJsonRestAPI. 

### How to import the project in IIB
In IIB toolkit, goto Window -> open perspective -> Git. If you dont see Git Repositories view, then goto Window -> Show view -> Git Repositories

In your git repositories view select -> **clone a git repository** -> Enter credentials -> After import is finished, right click on the repository and **import projects** -> import as exisitng projects -> select all projects.
 
you may have to run maven clean package install on **TransformJava project (right click pom.xml -> run as maven build-> enter goal as `clean package install`)

Right click and refresh the projects. If dependencies in Java project are not resolved, goto Project -> clean

Deploy the IIB application to default node.

