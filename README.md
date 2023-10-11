// Carga esta biblioteca directamente desde GitHub: el '_' al final importa todas las funciones

pipeline {
  stages {

    stage('Ejemplo Simple'){
      steps {
        // Llama a cualquier función de esta biblioteca por su nombre de archivo en vars/... sin la extensión .groovy
        //
        // Mira cada archivo vars/<función>.groovy para ver los argumentos
        //
        // Llama a vars/printEnv.groovy
        printEnv()

        // Ejecuta inicios de sesión para cualquier servicio para el que tengas secretos/tokens de variables de entorno,
        // incluyendo AWS, GCP, DockerHub, GHCR, ECR, GCR, GAR, ACR, GitLab, Quay
        // Mira ejemplos de funciones de inicio de sesión de servicios individuales en la siguiente Etapa
        login()

        // Muestra todos los sistemas en la nube a los que has iniciado sesión y con qué usuario has iniciado sesión
        printAuth()

        // Usa el gestor de paquetes disponible: portátil, utilizado por otras funciones también
        installPackages(['curl', 'unzip'])

        // Lanza un trabajo de compilación en la nube de GCP, por defecto contra tu cloudbuild.yaml si no se dan argumentos
        gcpCloudBuild()

        // Descarga herramientas en $HOME/bin
        downloadTerraform('1.2.3')
        downloadJenkinsCLI()

        // Solicita la aprobación de un clic humano antes de pasar al siguiente paso, es decir, la implementación en producción
        approval()

        // GitOps actualiza la versión de la imagen de Docker para la aplicación 1 y la aplicación 2 en Kustomize de Kubernetes
        gitKustomizeImage(['mi-repo/app1', 'mi-repo/app2'])

        // Activa la implementación de ArgoCD en Kubernetes para la aplicación 'mi-app'
        argoDeploy('mi-app')

        // Mira los archivos Groovy en vars/ para obtener más documentación, detalles y muchas más funciones útiles
      }
    }
  }

  // Envía notificaciones sobre compilaciones fallidas y recuperaciones
  post {
    failure {
      // Encuentra a los autores de Git que rompieron la compilación,
      // resuelve sus IDs de usuario de Slack y
      // los notifica activamente con etiquetas @usuario1 @usuario2
      slackNotify()
    }
    fixed {
      // Llama a una o más funciones de notificación para enviar mensajes de Slack, correos electrónicos, etc.
      // como slackNotify()
      // Use Notify() en lugar de múltiples llamadas a diferentes funciones de notificación
      Notify()
    }
  }
}


gcpDeployKubernetesPipeline(
  project: 'mi-proyecto-de-gcp',
  region: 'europe-west2',
  app: 'mi-app',
  env: 'producción-uce',
  images: [
    "mi-app-webapp",
    "mi-app-sidecar",
  ],
  gcr_registry: 'eu.gcr.io',
  gcp_serviceaccount_key: 'llave-de-cuenta-de-servicio-gcp-de-jenkins',  // ID de credencial de Jenkins
  cloudflare_email: 'mi-cuenta-cicd@dominio.com',       // opcional, activa la purga de la caché de Cloudflare
  cloudflare_zone_id: '12a34b5c6d7ef8a901b2c3def45ab6c7', // si ambos están configurados y la credencial 'cloudflare-api-key' de Jenkins está disponible
)


Opción 1 - Hashref
Importa la biblioteca como se muestra arriba directamente desde este repositorio, reemplazando @master con @<hashref> para fijarlo en una versión inmutable (las etiquetas no son inmutables). Esta es una mejor práctica de seguridad de GitHub para CI/CD, como se ve en este documento.

Opción 2 - Fork Público (totalmente automatizado)
Realiza un fork de este repositorio para tener un mayor control y visibilidad sobre todas las actualizaciones.

Habilita el flujo de trabajo fork-sync de GitHub Actions en tu fork para mantener sincronizada la rama principal cada pocas horas.

Luego puedes crear etiquetas o ramas de entorno para organizar las actualizaciones en los entornos de desarrollo, pruebas y producción.

Si utilizas ramas de entorno, habilita el flujo de trabajo fork-update-pr de GitHub Actions para generar automáticamente solicitudes de extracción de GitHub para tus ramas de entorno con el fin de auditar, autorizar y controlar las actualizaciones.

Opción 3 - Copia Privada (semi-automatizada)
Descarga las funciones que desees en tu repositorio privado de biblioteca compartida de Jenkins.

Puedes utilizar el script vars/download.sh para ayudarte a descargar archivos *.groovy específicos y ejecutarlo periódicamente para obtener actualizaciones de estas funciones previamente descargadas.

Serás responsable de realizar confirmaciones y reconciliar cualquier diferencia en tus copias locales.