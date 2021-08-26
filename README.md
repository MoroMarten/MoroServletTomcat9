Project can be built by just building module MoroServletTomcat9

You can use Maven - Plugins - war:war to create .war file after building.

The output will be in target folder
and it is enough to move .war file into
Tomcat9/webapps folder
to deploy the application.

The server will be accessible from
localhost:[PORT]/MoroServletTomcat-9/

If Javax library is missing, it can be found in src/main/webapp/WEB-INF/lib