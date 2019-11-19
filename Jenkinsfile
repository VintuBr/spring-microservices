openshift.withCluster() {
  env.SERVICE_PROJECTS = "discovery-service,account-service,balance-service,customer-service,hystrix-dashboard-service,gateway-service"
  env.ARTIFACT_FOLDER = "all_targets"
  env.NAMESPACE = openshift.project()
  env.POM_FILE = env.BUILD_CONTEXT_DIR ? "${env.BUILD_CONTEXT_DIR}/pom.xml" : "pom.xml"
  env.APP_NAME = "${JOB_NAME}".replaceAll(/-build.*/, '')
  //env.BUILD = "${env.NAMESPACE}"
  env.DEV_PROJECT = "spring-cloud-demo-dev"
  env.PROD_PROJECT = "spring-cloud-demo"
  echo "Starting Pipeline for ${APP_NAME} - ${env.NAMESPACE} - POM: ${POM_FILE}..."
}

pipeline {
  // Use Jenkins Maven slave
  // Jenkins will dynamically provision this as OpenShift Pod
  // All the stages and steps of this Pipeline will be executed on this Pod
  // After Pipeline completes the Pod is killed so every run will have clean
  // workspace
  agent {
    label 'maven'
  }

  // Pipeline Stages start here
  // Requeres at least one stage
  stages {  
    // Checkout source code
    // This is required as Pipeline code is originally checkedout to
    // Jenkins Master but this will also pull this same code to this slave
    stage('Git Checkout') {
      steps {
        // Turn off Git''s SSL cert check, uncomment if needed
        // sh 'git config --global http.sslVerify false'
        git url: "${APPLICATION_SOURCE_REPO}", branch: "${APPLICATION_SOURCE_REF}"
      }
    }

    // Run Maven build, skipping tests
    stage('Build'){
      steps {
        sh "mvn -B clean install -DskipTests=true -f ${POM_FILE}"
      }
    }

    // Run Maven unit tests
    stage('Unit Test'){
      steps {
        sh "mvn -B test -f ${POM_FILE}"
      }
    }

    stage('Store Artifact'){
      steps{
        script{
            SERVICE_PROJECTS.split(',').each { APPLICATION_NAME ->
                def safeBuildName  = "${APPLICATION_NAME}_${BUILD_NUMBER}",
                    artifactFolder = "${ARTIFACT_FOLDER}",
                    fullFileName   = "${safeBuildName}.tar.gz",
                    applicationZip = "${artifactFolder}/${fullFileName}"
                    applicationDir = ["target/${APPLICATION_NAME}.jar",
                                      "Dockerfile",
                                      ].join(" ");
                def needTargetPath = !fileExists("${artifactFolder}")
                if (needTargetPath) {
                    sh "mkdir ${artifactFolder}"
                }
                sh "tar --directory=${APPLICATION_NAME} -czvf ${applicationZip} ${applicationDir}"
                archiveArtifacts artifacts: "${applicationZip}", excludes: null, onlyIfSuccessful: true         
            }
        }
      }
   }

    stage('Create Image Builder') {
        when {
            expression {
              openshift.withCluster() {
                openshift.withProject(DEV_PROJECT) {
                    openshift.verbose();
                    def services_bc_lst = []
                    services_bc_lst.addAll(SERVICE_PROJECTS.split(','));

                    services_bc_lst.each { svc ->
                        //def svc_bc_name = svc + "-bc";
                        def svc_bc_name = svc;
                        def svc_bc_exists = openshift.selector("bc", svc_bc_name).exists();
                        println("Service BC: [${svc_bc_name}] exists: [${svc_bc_exists}]");
                    }
                
                    services_bc = services_bc_lst.findAll{ !openshift.selector("bc", it).exists() };
                    
                    return services_bc;
                  }
               }
            }
        }
        
        steps {
            script {
                openshift.withCluster() {
                    openshift.verbose();
                    openshift.withProject(DEV_PROJECT) {
                        def services_bc_lst = []
                        services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                    
                        def services_bc = services_bc_lst.findAll{ svc -> !openshift.selector("bc", svc).exists() };
                        println("Step execution: [${services_bc}]");

                        services_bc.each { service ->
                            openshift.newBuild("--name=${service}", "--docker-image=docker.io/java:8-alpine", "--binary=true")
                        }
                    }
                }
            }
        }
    }
    
    stage('Build Image') {
      steps {
          script {
              openshift.withCluster() {
                openshift.verbose();
                openshift.withProject(env.DEV_PROJECT) {
                    def services_bc_lst = []
                    services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                    services_bc_lst.each { APPLICATION_NAME -> 
                        println("Building application: [${APPLICATION_NAME}]");
                        openshift.selector("bc", "${APPLICATION_NAME}").startBuild("--from-archive=${ARTIFACT_FOLDER}/${APPLICATION_NAME}_${BUILD_NUMBER}.tar.gz", "--wait=true")
                    }
                }
              }
          }
      }
    }
    
    stage('Deploy to Dev') {
      when {
          expression {
            openshift.withCluster() {
              openshift.withProject(env.DEV_PROJECT) {
                def services_bc_lst = []
                services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                def services_bc = services_bc_lst.findAll{ svc -> !openshift.selector("dc", svc).exists() };
                println("[When] Deploy to Dev: [${services_bc}]");
                
                return services_bc;
              }
            }
        }
      }
      
      steps {
        script {
            openshift.withCluster() {
                openshift.withProject(env.DEV_PROJECT) {
                    def services_bc_lst = []
                    services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                    def services_bc = services_bc_lst.findAll{ svc -> !openshift.selector("dc", svc).exists() };
                    println("Step execution - Will create DC for: [${services_bc}]");
                    services_bc.each { APPLICATION_NAME -> 
                    println("Deploy application: [${APPLICATION_NAME}]");
                    def app = openshift.newApp("${APPLICATION_NAME}:latest", "-e=DISCOVERY_URL=http://discovery-service:8761");
                    //app.narrow("svc").expose("--port=${PORT}");
                    def dc = openshift.selector("dc", "${APPLICATION_NAME}");
                    while (dc.object().spec.replicas != dc.object().status.availableReplicas) {
                        println("Replicas - spec: [${dc.object().spec.replicas}] - available: [${dc.object().status.availableReplicas}]")
                        sleep 10
                      }
                    }
                 }
              }
          }
      }
    }

    stage('Promote to Production?') {
      steps {
          timeout(time:15, unit:'MINUTES') {
               input message: "Promote to Production?", ok: "Promote"
          }
          script {            
              openshift.withCluster() {
                  def services_bc_lst = []
                  services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                  
                  services_bc_lst.each { APPLICATION_NAME -> 
                    println("Promoting to Production: [${APPLICATION_NAME}] with tag: [latestProd]");
                    openshift.tag("${DEV_PROJECT}/${APPLICATION_NAME}:latest", "${PROD_PROJECT}/${APPLICATION_NAME}:latestProd")
                  }
              }
            }
       }
    }
    
    stage('Rollout to Production') {
      steps {
          script {
                openshift.withCluster() {
                  openshift.withProject(PROD_PROJECT) {
                    def services_bc_lst = []
                    services_bc_lst.addAll(SERVICE_PROJECTS.split(','));
                    services_bc.each { APPLICATION_NAME -> 
                    
                    def dc_exists = openshift.selector('dc', '${APPLICATION_NAME}').exists();
                    println("Promoting to Production: [${APPLICATION_NAME}] - DC exists: [${dc_exists}]");
                    
                    if (openshift.selector('dc', '${APPLICATION_NAME}').exists()) {
                        println("Promoting to Production: [${APPLICATION_NAME}] deleting: [dc][svc][route]");
                        openshift.selector('dc', '${APPLICATION_NAME}').delete()
                        openshift.selector('svc', '${APPLICATION_NAME}').delete()
                        openshift.selector('route', '${APPLICATION_NAME}').delete()
                    }
                    openshift.newApp("${APPLICATION_NAME}:latestProd", "-e=DISCOVERY_URL=http://discovery-service:8761").narrow("svc");
                }
               }
             }
          }
      }
    }
  }
}
