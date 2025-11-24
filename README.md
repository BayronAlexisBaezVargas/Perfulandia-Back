🌿 Perfulandia Backend

Este repositorio contiene el backend de Perfulandia, un e-commerce especializado en la venta de perfumes. Está desarrollado utilizando Java 17 y Spring Boot 3, siguiendo una arquitectura REST para la comunicación con el frontend.

🚀 Descripción del Proyecto

El objetivo de este backend es gestionar toda la lógica de negocio de la tienda, asegurando la integridad de los datos, la seguridad de las transacciones y la administración eficiente del inventario.

Funcionalidades Principales:

Gestión de Productos (CRUD):

Permite listar, crear, editar y eliminar productos.

Implementa "Borrado Lógico" (Soft Delete): Los productos no se eliminan físicamente de la base de datos para preservar el historial de ventas, sino que se marcan como "inactivos".

Control de Stock: Impide la eliminación de productos que forman parte de pedidos pendientes.

Gestión de Usuarios y Seguridad:

Autenticación: Registro e inicio de sesión de usuarios.

Roles: Distinción entre usuarios normales ("user") y administradores ("admin").

Seguridad: Las contraseñas se almacenan encriptadas utilizando BCrypt.

Gestión de Pedidos:

Creación de nuevos pedidos con múltiples productos.

Transaccionalidad: Si falla el stock de un producto, se cancela toda la orden.

Historial de compras por usuario.

Cambio de estado de pedidos (Pendiente, Enviado, Completado, Cancelado) por parte del administrador.

Documentación API:

Integración con Swagger / OpenAPI 3 para visualizar y probar los endpoints directamente desde el navegador.

🛠️ Tecnologías Utilizadas

Lenguaje: Java 17

Framework: Spring Boot 3.5.7

Base de Datos: PostgreSQL (Desplegada en AlwaysData)

ORM: Spring Data JPA (Hibernate)

Seguridad: Spring Security (BCrypt)

Documentación: SpringDoc OpenAPI (Swagger UI)

Build Tool: Maven

Despliegue: Docker & Render

📂 Estructura del Proyecto

El código está organizado en paquetes según su responsabilidad:

model: Entidades JPA que representan las tablas de la base de datos (Producto, Usuario, Pedido, DetallePedido).

repository: Interfaces que extienden JpaRepository para interactuar con la base de datos.

controller: Controladores REST que exponen los endpoints de la API.

config: Configuraciones globales como WebConfig (CORS), SecurityConfig (Seguridad) y SwaggerConfig.

🔌 Endpoints Principales

Una vez que la aplicación esté corriendo, puedes ver la documentación completa en:
https://tu-dominio.onrender.com/swagger-ui/index.html

Algunos endpoints clave:

Productos

GET /api/productos: Listar todos los productos.

GET /api/productos/{id}: Obtener un producto.

POST /api/productos: Crear producto (Admin).

PUT /api/productos/{id}: Actualizar producto (Admin).

DELETE /api/productos/{id}: Desactivar producto (Admin).

Usuarios / Autenticación

POST /api/auth/login: Iniciar sesión.

POST /api/auth/register: Registrar usuario.

GET /api/usuarios: Listar usuarios (Admin).

Pedidos

POST /api/pedidos: Crear un nuevo pedido.

GET /api/pedidos: Listar todos los pedidos (Admin).

GET /api/pedidos/usuario/{id}: Historial de un usuario.

PUT /api/pedidos/{id}/estado: Cambiar estado (Admin).

🔧 Instalación y Ejecución Local

Clonar el repositorio:

git clone [https://github.com/tu-usuario/perfulandia-back.git](https://github.com/tu-usuario/perfulandia-back.git)
cd perfulandia-back


Configurar Base de Datos:
Asegúrate de tener las credenciales de tu base de datos PostgreSQL en src/main/resources/application.properties:

spring.datasource.url=jdbc:postgresql://tu-host:5432/tu-bd
spring.datasource.username=tu-usuario
spring.datasource.password=tu-password


Ejecutar la aplicación:

./mvnw spring-boot:run


O en Windows:

mvnw spring-boot:run


🐳 Despliegue con Docker

El proyecto incluye un Dockerfile optimizado para el despliegue en la nube (como Render).

Construir la imagen:

docker build -t perfulandia-back .


Correr el contenedor:
