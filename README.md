# Academy Java - Proyecto Final Globant

## Resumen
Microservicio para una tienda de videojuegos que permite crear, actualizar, borrar y listar videojuegos de un sitio, además que permita realizar la reserva de éstos a los usuarios.


## Comenzando 🚀

_Estas instrucciones te permitirán obtener una copia del proyecto en funcionamiento en tu máquina local para propósitos de desarrollo y pruebas._

Mira **Deployment** para conocer como desplegar el proyecto.


### Pre-requisitos 📋

_Que cosas necesitas para instalar el software y como instalarlas_

```
Clonar el proyecto en tú computador mediante el siguiente comando:  
    git clone https://github.com/javacoboleros/proyectoJava.git
```
```
En archivo src/main/resources/application.properties, debes completar los siguientes datos de conexión username y password para la conexión a la base de datos local MySQL:

spring.datasource.url=jdbc:mysql://localhost:3306/GamesStore
spring.datasource.username=root
spring.datasource.password=root

```
## Ejecutando las pruebas ⚙️

Con la siguiente URL se puede visualizar la documentación de las Apis mediante Swagger
```
http://localhost:8080/swagger-ui.html
```

### Métodos POST

Crear un videojuego
```
http://localhost:8080/games/create
```
Body
```
{
 "title": "ttitle one",
 "console":"console one",
 "creationDate":"2020-01-01",
 "copies":2,
 "status":"AVAILABLE"
}
```

Crear una resserva

```
http://localhost:8080/reservation
```
Body
```
{
  "gameId":1,
  "name":"Juanito",
  "lastName":"perez",
  "documentNumber":"18235982-4",
   "email":"juanito@gmail.com"
}
```

### Métodos PUT

Modificar un videojuego

```
http://localhost:8080/games/update/1
```
Body
```
{
 "title": "ttitle one",
 "console":"console one",
 "creationDate":"2020-01-01",
 "copies":9,
 "status":"AVAILABLE"
}
```

Modificar una reserva
```
http://localhost:8080/reservation/1
```

Body
```
{
  "gameId":1,
  "name":"Juanito",
  "lastName":"Lopez",
  "documentNumber":"18235982-4",
   "email":"juanito@gmail.com"
}
```

### Métodos DELETE

Eliminar un videojuego por Id

```
http://localhost:8080/games/delete/1
```

Eliminar una reserva por Id

```
http://localhost:8080/reservation/1
```

Eliminar reserva por Id de viedojuego

```
http://localhost:8080/reservation/delete/1
```

### Métodos GET

Obtener todos los juegos
```
http://localhost:8080/games/getAll
```

Obtener juegos por Id
```
http://localhost:8080/games/getById/1
```

Obtener Juegos por estado (Available/Reserved)

```
http://localhost:8080/games/getStatus/available
```

Obtener todas las reservas

 ```
http://localhost:8080/reservation
```

Obtener una reserva por Id
  ```
http://localhost:8080/reservation/1
```

Obtener reservas por game Id
  ```
http://localhost:8080/reservation/game/1
```

## Construido con 🛠️

Herramientas que se utilizaron para crear el proyecto:

* Spring Boot
* Java 11
* Maven
* JUnit
* Mockito
* Swagger


## Integrantes del proyecto✒️


* **Camilo Andrés González López**
* **Diego Nicolás Gonzáles López**
* **Maria Cecilia Reyes**
* **Valentina Alejandra Contreras Valenzuela**

Tutor:
* **Julio Cesar Guerra Lema**
