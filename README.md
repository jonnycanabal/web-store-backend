# web-store-backend

para iniciar la prueba se debe ejecutar en un terminal
- docker-compose up

Para realizar las pruebas el collection de postman esta en la carpeta principal web-store en:
 - /PostmanCollection

Usuario y contraseña por defecto:
 - username: "Admin"
 - password: "12345"

En el postman collection se debe realizar el login primero, está en la carpeta:
 - Login/login
 - endpoint: "localhost:8080/login"

Recordar copiar el token de autenticación generado con JWT Token y
pegarlo en las solicitudes correspondientes en donde dice:
 - Authorization
 - Type
 - Bearer Token
 - toke: "Pegar el token"

# web-store-backend

To initiate the test, run the following command in a terminal:
- docker-compose up

The Postman collection for testing is located in the main web-store folder at:
- /PostmanCollection

Default username and password:
- username: "Admin"
- password: "12345"

In the Postman collection, login must be performed first, found in the folder:
- Login/login
- endpoint: "localhost:8080/login"

Remember to copy the authentication token generated with JWT Token and
paste it into the corresponding requests where it says:
- Authorization
- Type
- Bearer Token
- toke: "Paste el token"

