Feature: API Testing de Restful-Booker

  Background: Configurar la URI base
    Given la URI base es "https://restful-booker.herokuapp.com"

  Scenario: Crear un token de autenticacion exitosamente
    When envio una peticion POST a "/auth" con credenciales "admin" y "password123"
    Then el status code de la respuesta debe ser 200
    And la respuesta debe contener un "token" no nulo

  Scenario: Crear una nueva reserva y validar la estructura de la respuesta
    When envio una peticion POST a "/booking" con el nombre "Shantal", apellido "Espinoza" y precio 150
    Then el status code de la respuesta debe ser 200
    And la respuesta de creacion debe contener el nombre "Shantal"
    And se debe generar un "bookingid"

  Scenario: Obtener una reserva existente por ID
    Given que existe una reserva previamente creada con el nombre "Shantal"
    When envio una peticion GET al endpoint "/booking/" con ese ID
    Then el status code de la respuesta debe ser 200
    And el cuerpo de la respuesta debe coincidir con el nombre "Shantal" y apellido "Espinoza"

  Scenario: Actualizar una reserva existente mediante PUT
    Given que genero un token de autenticacion con "admin" y "password123"
    And que existe una reserva previamente creada con el nombre "Shantal"
    When envio una peticion PUT para actualizar el nombre a "Maria" y apellido "Perez" con precio 200
    Then el status code de la respuesta debe ser 200
    And el cuerpo de la respuesta debe mostrar el nombre actualizado "Maria"

  Scenario: Eliminar una reserva y validar que ya no existe
    Given que genero un token de autenticacion con "admin" y "password123"
    And que existe una reserva previamente creada con el nombre "Eliminar"
    When envio una peticion DELETE para borrar esa reserva
    Then el status code de la respuesta debe ser 201
    And si envio un GET a ese ID, el status code debe ser 404