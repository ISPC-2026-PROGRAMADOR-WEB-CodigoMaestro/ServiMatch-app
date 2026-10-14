🔧 ServiMatch - El servicio ideal, con la confianza que necesitás

Código Maestro - Programador de Aplicaciones para Móviles - Segundo Cuatrimestre 2026

🧾 Descripción del proyecto

ServiMatch es una aplicación móvil desarrollada para facilitar la búsqueda de trabajadores de oficios en la provincia de Córdoba, Argentina, tales como electricistas, carpinteros, pintores, plomeros, gasistas y otros profesionales independientes.

Actualmente, muchas personas que residen en distintas localidades de Córdoba enfrentan dificultades para encontrar prestadores de servicios responsables, con experiencia y que generen confianza al momento de contratarlos.

En muchos casos, la búsqueda se realiza mediante recomendaciones informales, redes sociales o publicaciones que no siempre ofrecen información suficiente sobre los profesionales.

Frente a esta problemática, ServiMatch propone una solución tecnológica que permite conectar usuarios con trabajadores de diferentes oficios dentro de la provincia de Córdoba, facilitando el acceso a información sobre sus especialidades y ubicaciones.

La aplicación busca ofrecer una experiencia sencilla, organizada y accesible desde dispositivos Android, contribuyendo a mejorar la búsqueda de servicios y promoviendo oportunidades laborales para los profesionales cordobeses.

🎯 Objetivo del proyecto

Desarrollar una aplicación móvil que facilite la búsqueda y consulta de profesionales de diferentes oficios en la provincia de Córdoba, mediante una plataforma intuitiva que permita acceder a información actualizada y organizada.

Asimismo, implementar herramientas de administración para gestionar usuarios, oficios y ubicaciones, integrando la aplicación con una API REST y una base de datos remota.



📱 Funcionalidades de la aplicación

🔐 Autenticación de usuarios

-Inicio de sesión mediante correo electrónico y contraseña.

-Identificación de usuarios según su rol.

-Acceso a diferentes funcionalidades según los permisos correspondientes.



👤 Usuario estándar

-Acceso a la pantalla principal.

-Visualización del listado de profesionales disponibles.

-Visualización de la ciudad y provincia de los profesionales.


🛠️ Administrador

El administrador dispone de un panel de gestión que permite administrar los datos del sistema.

Gestión de usuarios (CRUD):

Registrar nuevos usuarios.

Consultar usuarios existentes.

Modificar información de usuarios.

Eliminar usuarios.

Gestión de oficios (CRUD):

Registrar nuevos oficios.

Consultar los oficios disponibles.

Actualizar información de oficios.

Eliminar oficios.

Gestión de ubicaciones (CRUD):

Registrar nuevas ubicaciones.

Consultar ciudades y provincias.

Modificar ubicaciones.

Eliminar ubicaciones.

🚀 Tecnologías utilizadas

Android Studio: entorno de desarrollo de la aplicación móvil.

Java: lenguaje de programación.

XML: diseño de interfaces.

Retrofit: comunicación con la API REST.

JSON: intercambio de información entre aplicación y servidor.

Django y Django REST Framework: desarrollo del backend.

MySQL: gestión de la base de datos.

Railway: alojamiento del backend.

Git y GitHub: control de versiones y trabajo colaborativo.


🏗️ Arquitectura de la aplicación

La aplicación móvil utiliza una arquitectura cliente-servidor.

El usuario interactúa con la aplicación Android.

La aplicación realiza solicitudes HTTP mediante Retrofit.

La API REST desarrollada con Django procesa las solicitudes.

El backend consulta o modifica la información almacenada en MySQL.

La API devuelve respuestas en formato JSON.

La aplicación muestra la información correspondiente al usuario.

La comunicación con el backend desplegado en Railway permite consultar y administrar información de manera remota.

🔒 Seguridad

La aplicación incorpora mecanismos de seguridad para proteger el acceso a las funcionalidades del sistema.

Autenticación mediante JWT.

Diferenciación de permisos según roles.

Gestión de sesiones de usuario.

Comunicación con el backend mediante HTTPS.


📥 Instalación de la aplicación

Para instalar ServiMatch en un dispositivo Android:

Acceder a la sección Releases del repositorio.

Descargar el archivo APK disponible.

Abrir el archivo desde el dispositivo.

Autorizar la instalación desde esa fuente si Android lo solicita.

Completar la instalación y abrir ServiMatch.

👥 Integrantes del equipo

Integrante


Agustin Exequiel Gimenez Benitez  - Scrum Master

Guadalupe Magali Turri Sánchez  - Developer

Franco Daniel Gimenez Benitez  - Developer

Emilce Agustina Torres - Developer

Kevin Cristofer Lorea Tannfeld  - Developer

Fernando Guillermo Cazon  - Developer