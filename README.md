# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción
Repositorio del post-contenido de la Unidad 3 del curso **Patrones de Diseño de Software** — Sexto Semestre de Ingeniería de Sistemas en la Universidad Francisco de Paula Santander (UFPS).

Este proyecto comprende una aplicación Spring Boot estándar (`confudes-patrones-estructurales`) que resuelve cuatro necesidades arquitectónicas reales del backend de **ConfUDES** (plataforma institucional de gestión de congresos académicos), organizadas en dos partes complementarias:
1. **Parte 1 — Registro de Asistencia y Emisión de Certificados:** Integración de un SDK externo de validación QR mediante el patrón **Adapter** y unificación del subsistema de emisión de certificados mediante el patrón **Facade**.
2. **Parte 2 — Mejoras Opcionales y Control de Acceso:** Incorporación de mejoras visuales y funcionales combinables sobre el PDF emitido mediante el patrón **Decorator**, y protección de la operación de descarga masiva mediante el patrón **Proxy (Protection Proxy)**.

---

## Cómo ejecutar

Desde la raíz del repositorio:
```bash
# Compilar y empaquetar el proyecto
mvn clean package

# Ejecutar las pruebas unitarias y de integración de ambas partes
mvn test

# Iniciar la aplicación Spring Boot
mvn spring-boot:run
```

---

## Decisiones de diseño

### Necesidad 1 — Registro de asistencia
* **Patrón aplicado:** **Adapter (Adaptador de Objetos)** (`QRCheckAdapter` implementando `ServicioAsistencia`).
* **Problema específico:**
  El controlador REST en producción (`ControladorCheckIn`) depende rígidamente del contrato interno preexistente:
  ```java
  ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR);
  ```
  Por otro lado, el proveedor contratado suministra un SDK de terceros en un JAR inmodificable (`QRCheckClient`) que expone una interfaz totalmente distinta e incompatible:
  ```java
  QRCheckResponse validar(QRCheckRequest request);
  ```
  donde `QRCheckRequest` requiere un `long idEvento` y un `payload` que debe iniciar con el prefijo `"QR-"`, retornando códigos de estado HTTP enteros (200 para éxito, 401 para rechazo).
* **Por qué Adapter y no Facade (Alternativa descartada):**
  Se descartó el patrón **Facade** porque su propósito es proveer una interfaz simplificada de alto nivel para ocultar y orquestar un subsistema complejo integrado por múltiples colaboradores heterogéneos. En este escenario no hay un subsistema con múltiples interfaces complejas que simplificar; hay exactamente **un único colaborador externo** cuya firma y tipos de datos no coinciden con el contrato interno de la aplicación. **Adapter** es el único patrón estructural concebido específicamente para convertir la interfaz de una clase existente en la interfaz esperada por el cliente, permitiendo que dos componentes incompatibles colaboren sin modificar ninguno de los dos.

---

### Necesidad 2 — Emisión de certificados
* **Patrón aplicado:** **Facade (Fachada)** (`FachadaCertificados` implementando `ServicioCertificados`).
* **Problema específico:**
  La versión original de `ControladorCertificados` se encontraba fuertemente acoplada a cuatro servicios de infraestructura independientes: `ValidadorAsistencia`, `GeneradorCertificadoPDF`, `FirmaDigitalService` y `EnvioCorreoService`. Cualquier cambio en el ciclo de vida de la firma criptográfica o en el envío de correos obligaba a modificar el controlador web. Estos cuatro servicios no podían ser fusionados ni alterados porque son utilizados de forma autónoma por otros módulos de la universidad.
* **Por qué Facade y no Adapter (Alternativa descartada):**
  Se descartó el patrón **Adapter** porque ninguno de los cuatro servicios presenta un problema de incompatibilidad de interfaces ni tipos de datos dispares respecto a lo que el sistema espera; cada uno de ellos funciona adecuadamente con su API nativa. El problema real radica en el **alto acoplamiento y exceso de colaboradores** que el cliente debía conocer, instanciar y orquestar en una secuencia procedural estricta (`validar -> generar -> firmar -> enviar`). **Facade** resuelve este problema al encapsular la complejidad de todo el subsistema detrás de un punto de entrada unificado y de alto nivel (`ServicioCertificados.emitir(solicitud)`), reduciendo las dependencias del controlador a un único colaborador inyectado y manteniendo su método `emitir()` en menos de 10 líneas de código limpio.

---

### Necesidad 3 — Mejoras opcionales del certificado
* **Patrón aplicado:** **Decorator (Decorador)** (`CertificadoDecorator`, `MarcaDeAguaDecorator`, `CodigoQRDecorator`, `TraduccionInglesDecorator`).
* **Problema específico:**
  Los organizadores de cada congreso requieren activar tres mejoras opcionales y acumulativas sobre el documento PDF base: marca de agua institucional (`UtilidadesPDF.aplicarMarcaDeAgua`), código QR de verificación web (`UtilidadesPDF.insertarCodigoQR`) y traducción al idioma inglés (`UtilidadesPDF.traducirAIngles`). Cada evento puede requerir ninguna, una, dos o las tres mejoras simultáneas en cualquier orden (8 combinaciones posibles).
* **Por qué se descartaron la herencia y los parámetros booleanos:**
  1. *Herencia (subclases estáticas):* Modelar combinaciones mediante herencia provocaría una **explosión combinatoria de clases**: para $n=3$ mejoras se requerirían $2^3 = 8$ subclases (`CertificadoConMarca`, `CertificadoConQR`, `CertificadoConMarcaYQR`, etc.), y para $n=5$ se elevaría a 32 clases, haciendo el código inmanejable y violando el principio OCP.
  2. *Parámetros booleanos en `emitir()`:* Añadir banderas (`boolean conMarca, boolean conQR, boolean traducir`) violaría el principio de responsabilidad única (SRP) y obligaría a modificar la interfaz cada vez que surja una nueva mejora, saturando la clase base con bloques condicionales `if/else`.
* **Por qué el patrón de la Necesidad 4 (Proxy) no serviría aquí:**
  El patrón **Proxy** está diseñado para controlar o restringir el acceso a un objeto; no está pensado para la composición acumulativa y recursiva de responsabilidades aditivas. Si se empleara Proxy para aplicar mejoras visuales, no se dispondría de un mecanismo limpio para anidar múltiples mejoras en tiempo de ejecución (`new TraduccionInglesDecorator(new CodigoQRDecorator(new MarcaDeAguaDecorator(base, "..."), "..."))`).

---

### Necesidad 4 — Control de acceso a la descarga masiva
* **Patrón aplicado:** **Proxy (Protection Proxy / Proxy de Protección)** (`CertificadosSecurityProxy` implementando `ServicioCertificados`).
* **Problema específico:**
  La descarga masiva de certificados de un congreso completo implica un alto costo computacional y está sujeta a una cuota estricta del proveedor de firma digital (máximo 60 firmas por minuto). Solo los usuarios con rol `ORGANIZADOR` o `ADMIN` tienen autorización para disparar este proceso. El resto de clientes del sistema (incluyendo el portal de participantes) debe continuar consumiendo la interfaz `ServicioCertificados` sin necesidad de gestionar roles ni restricciones de infraestructura.
* **Por qué se descartó Decorator (Comparación sutil con la Necesidad 3):**
  Tanto Decorator como Proxy implementan la misma interfaz que envuelven (`ServicioCertificados`), pero sus intenciones arquitectónicas son diametralmente opuestas:
  * **Decorator** añade funcionalidad *aditiva* al resultado y **siempre delega** la ejecución en el objeto envuelto antes o después de aplicar su transformación. Si usáramos Decorator para control de acceso, se ejecutaría la operación costosa de firma y generación de certificados antes de poder abortar, o se pervertiría la semántica del patrón convirtiéndolo en un interceptor condicional.
  * **Proxy**, por definición, actúa como un *gatekeeper* (guardián de acceso): evalúa las precondiciones (`ContextoUsuario.rolActual()`) y **decide si delega o no** en el objeto real. Si el usuario tiene rol `PARTICIPANTE`, el Proxy arroja inmediatamente una excepción (`SecurityException`), impidiendo de forma tajante que la lógica costosa llegue a ejecutarse.

---

### Reflexión — Composite y Flyweight (Estructurales Complementarios)
* **Composite:** Encajaría de manera natural en el módulo de **administración de la agenda del congreso**, cuya estructura es un árbol jerárquico de partes (Congreso -> Tracks temáticos -> Sesiones -> Actividades individuales). Definir una interfaz común `ElementoAgenda` con métodos como `getDuracionMinutos()` o `renderizarPrograma()` permitiría al sistema manipular tanto las actividades individuales (hojas) como los tracks completos (compuestos) de forma polimórfica y uniforme.
* **Flyweight:** **No resulta aplicable ni justificado** para las miles de credenciales QR generadas para los asistentes. Aunque el volumen de objetos es elevado, cada credencial QR almacena un estado extrínseco o datos intrínsecos irrepetibles y únicos por participante (identificador de participante, hash criptográfico único del QR, fecha y hora de emisión). No existe un bloque significativo de estado intrínseco compartido e inmutable entre credenciales que justifique la sobrecarga de un gestor de estados compartidos (*Flyweight Factory*).

---

## Herramientas Utilizadas
* **Lenguaje:** Java 17 / OpenJDK 21
* **Framework:** Spring Boot 3.2.0 (Spring Web, Spring Test)
* **Gestor de Dependencias y Construcción:** Apache Maven 3.8+
* **Framework de Pruebas:** JUnit 5 (Jupiter API)
* **Control de Versiones:** Git 2.x y GitHub

---

## Conclusiones
La ejecución de este post-contenido permitió constatar que la idoneidad de los patrones de diseño estructurales reside fundamentalmente en la **intención arquitectónica** que persiguen, aun cuando compartan estructuras UML similares basadas en composición e interfaces comunes. En la Parte 1, la comparación entre **Adapter** y **Facade** demostró que mientras Adapter resuelve una incompatibilidad de contrato punto a punto con un proveedor externo, Facade desacopla al cliente de la orquestación de un subsistema complejo preexistente. En la Parte 2, el contraste sutil entre **Decorator** y **Protection Proxy** puso en evidencia que, aunque ambos envuelven la misma interfaz (`ServicioCertificados`), Decorator enriquece dinámicamente el resultado siempre delegando en el objeto base, mientras que Proxy asume el control del acceso interceptando y abortando la ejecución costosa antes de que se consuman recursos críticos. Esta discriminación reflexiva garantiza arquitecturas mantenibles, extensibles y respetuosas de los principios SOLID.
