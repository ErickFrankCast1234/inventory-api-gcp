Inventory API – Prueba Técnica

API REST para gestión de inventarios, desarrollada con Java 17 + Spring Boot 3, con despliegues en Docker y Google Cloud Run, y con pruebas unitarias, integración y carga.

📦 Tecnologías principales

->Java 17 / Spring Boot 3
->PostgreSQL 15
->Docker + Docker Compose
->Google Cloud Run
->gcloud CLI
->JUnit 5 + Mockito
->Java LoadTest
->Swagger OpenAPI
->Logs JSON estructurados (SLF4J/Logback)

1️⃣ Instrucciones de instalación
🔧 Requisitos previos

->Docker Desktop instalado
->JDK 17
->Maven 3.9+
->Cuenta en Google Cloud
->gcloud CLI instalado y autenticado

🐳 Ejecutar localmente con Docker

1. Construir imagen
    # docker-compose build

2. Levantar los contenedores
    # docker-compose up -d

| Servicio      | Puerto | Descripción   |
| ------------- | ------ | ------------- |
| inventory-api | 8080   | API REST      |
| postgres      | 5432   | Base de datos |

3. Ver logs
    # docker logs -f inventory_api

4. API disponible en:

   http://localhost:8080

   Ejemplo: http://localhost:8080/api/products?page=0&size=5

2️⃣ Documentación de API (Swagger)

Swagger UI disponible en: http://localhost:8080/swagger-ui.html


3️⃣ Decisiones técnicas

✔ Base de datos PostgreSQL

 -> Se eligió PostgreSQL por su solidez y transacciones ACID.

✔ Índices

 Se agregaron índices en:
  -> name
  -> sku
  -> created_at

✔ Transacciones

 Los métodos de servicio usan:

  -> @Transactional

 para operaciones críticas (crear, actualizar, eliminar).

✔ Testing

  -> Tests unitarios → 80%+ cobertura
  -> Tests de integración → usando @SpringBootTest y base de datos real
  -> Tests de carga → script Java (LoadTest.java) enviando 500 req/seg

✔ Docker

 -> Multi-stage build
 -> Imágenes ligeras basadas en eclipse-temurin:17-jdk-alpine

✔ Despliegue en GCP

 -> Se usa Cloud Run, que escala automáticamente y funciona de forma serverless.


4️⃣ Instrucciones de despliegue (GCP)

Se recomienda ampliamente leer el Manual de Despliegue en Docker y GCP antes de ejecutar cualquiera de los comandos en la Google Cloud CLI. Este documento explica paso a paso cada acción del proceso, incluyendo la configuración de Docker, la creación del repositorio en Artifact Registry, la autenticación, el envío de la imagen y el despliegue final en Cloud Run. Revisarlo previamente asegura que el entorno esté correctamente preparado y evita errores durante la ejecución de los comandos.

1. Instalar Google Cloud CLI

  -> https://cloud.google.com/sdk/docs/install

2. Inicializar

   -> gcloud init

   -> Selecciona tu proyecto: inventory-api-prod

3. Activar Cloud Run y Artifact Registry

  -> gcloud services enable run.googleapis.com
  -> gcloud services enable artifactregistry.googleapis.com

4. Crear repositorio Docker en GCP

  -> gcloud artifacts repositories create inventory-api \
  -> --repository-format=docker --location=us-central1

5. Autenticarse con Docker

  -> gcloud auth configure-docker us-central1-docker.pkg.dev

6. Construir y enviar la imagen

  -> docker build -t us-central1-docker.pkg.dev/inventory-api-prod/inventory-api/inventory-api .
  -> docker push us-central1-docker.pkg.dev/inventory-api-prod/inventory-api/inventory-api

7. Desplegar en Cloud Run

  -> gcloud run deploy inventory-api \
  -> --image=us-central1-docker.pkg.dev/inventory-api-prod/inventory-api/inventory-api \
  -> --platform=managed \
  -> --region=us-central1 \
  -> --allow-unauthenticated

8. Obtener URL final

  -> gcloud run services describe inventory-api --region us-central1 --format='value(status.url)'



5️⃣ Script de inicialización de base de datos

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL CHECK (price > 0),
    sku VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE inventory (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    store_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    min_stock INTEGER NOT NULL,

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_inventory_store ON inventory(store_id);
CREATE INDEX idx_inventory_lowstock ON inventory(quantity, min_stock);

CREATE TABLE movements (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    source_store_id VARCHAR(255),
    target_store_id VARCHAR(255),
    quantity INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,

    CONSTRAINT fk_mov_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_mov_product ON movements(product_id);
CREATE INDEX idx_mov_source_store ON movements(source_store_id);
CREATE INDEX idx_mov_target_store ON movements(target_store_id);

6️⃣ Colección de Postman

Adjunto la colección exportada como:
   
    Inventarios - GCP.postman_collection
    Inventarios.postman_collection

