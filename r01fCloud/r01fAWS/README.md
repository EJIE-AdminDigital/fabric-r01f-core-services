# Instalación MinIO S3 en local

## Obtención de licencia y descarga

1.  Ir a la página de MinIO: [https://www.min.io/pricing](https://www.min.io/pricing)

2.  Obtener la licencia gratuita y guardarla en `/develop/s3-server/minio/minio.license`.

3.  Descargar MinIO (Server) Ej. Para Windows desde: [https://www.min.io/download/aistor-server?platform=windows](https://www.min.io/download/aistor-server?platform=windows)

## Instalación y configuración (Ejemplo para Windows)

1.  Descargar `minio.exe` y moverlo a `/develop/s3-server`.

2.  Ejecutar PowerShell como administrador.

3.  Navegar al directorio de instalación:
    `cd /develop/s3-server`
    
4.  Iniciar el servidor MinIO, especificando la ruta de la licencia:
    `minio.exe server /develop/s3-server/minio --license /develop/s3-server/minio/minio.license`
    
    **Nota:** Se puede crear un script o CMS para simplificar el arranque del servidor.
    
5.  Crear el bucket `cms` una vez que el servidor esté en funcionamiento.

# Configuración API S3

## Ejemplo para r01plateawebconfigbyenv/r01PLATEAWebCOREConfigByEnv

Fichero `\r01t\r01t.cms.platea.properties.xml`. 

Uso del filestore S3 con la configuración:

- Ejemplo de configuración para MinIO:
	```xml
	<fileStore impl="S3">
		 <rootPath>r01/</rootPath> 
		 <moduleConfigForS3>
				<moduleConfigForBucket>
					<defaultBucket>cms</defaultBucket>
					<nameStrategy>ExtensionForFileIncluded</nameStrategy>
				</moduleConfigForBucket>
				<s3ClientConfig>
					<aws>
						<s3>
							<endPoint>http://localhost:9000/</endPoint>
							<accessKey>minioadmin</accessKey>
							<accessSecret>minioadmin</accessSecret>
						</s3>
					</aws>
				</s3ClientConfig>
			</moduleConfigForS3>
	</fileStore>
	```
- Ejemplo de configuración para Scality:
	```xml
	<fileStore impl="S3">
		 <rootPath>r01/</rootPath> 
		 <moduleConfigForS3>
			    <moduleConfigForBucket>
					<defaultBucket>02-des-1e5b28d71b22b0d09c5220a2b24bcb49</defaultBucket>
				</moduleConfigForBucket>
				<s3ClientConfig>
					<aws>
						<s3>
							<httpSettings disableCertChecking='true' />
							<endPoint>http://s3.itbatera.euskadi.eus</endPoint>
							<accessKey>6VPW0XG22OBYLSV0LWCK</accessKey>
							<accessSecret>Tnn6nzTQU7VdOI7Re/TyjsFDiwZqB2zIYgSaMjs0</accessSecret>
						</s3>
					</aws>
				</s3ClientConfig>
			</moduleConfigForS3>
	</fileStore>
	```
A continuación, se detallan las propiedades clave utilizadas en la configuración del fileStore para S3:
	
- `<fileStore impl="S3">`: Define que la implementación del almacenamiento de archivos es S3.
- `<rootPath>`r01/</rootPath>: Especifica la ruta raíz dentro del bucket S3 donde se almacenarán los archivos.
- `<moduleConfigForS3>`: Contenedor para la configuración específica del módulo S3.
- `<moduleConfigForBucket>`: Contenedor para la configuración relacionada con el bucket.
- `<defaultBucket>`: Nombre del bucket S3 por defecto que se utilizará para el almacenamiento.
- `<nameStrategy>`: Estrategia para nombrar los archivos dentro del bucket.
- `<s3ClientConfig>`: Contenedor para la configuración del cliente S3.
- `<aws>`: Contenedor para la configuración específica de AWS S3 (aunque se use con MinIO o Scality, la API es compatible con AWS S3).
- `<s3>`: Contenedor para las propiedades del servicio S3.
- `<endPoint>`: http://localhost:9000/</endPoint>: URL del endpoint del servidor S3 (MinIO, Scality, etc.).
- `<accessKey>`: Clave de acceso para autenticarse con el servidor S3.
- `<accessSecret>`: Clave secreta asociada a la clave de acceso.
- `<httpSettings>`: (Solo en Scality) Configuración HTTP que deshabilita la verificación de certificados SSL/TLS.