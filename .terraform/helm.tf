provider "helm" {
  #kubernetes {
   # host                   = "https://${google_container_cluster.primary.endpoint}"
   # token                  = data.google_client_config.default.access_token

    #client_certificate     = base64decode(google_container_cluster.primary.master_auth.0.client_certificate)
    #client_key             = base64decode(google_container_cluster.primary.master_auth.0.client_key)
    #cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth.0.cluster_ca_certificate)

  #}
  kubernetes {
    cluster_ca_certificate = module.gke_auth.cluster_ca_certificate
    host                   = module.gke_auth.host
    token                  = module.gke_auth.token
  }

}

#resource "helm_release" "example" {
#  name  = "test-chart"
#  chart = "../.helm/ruettel-chart"

  #wait for cluster to be created
#  depends_on = [
#    google_container_cluster.primary
#  ]
#}

#resource "helm_release" "release" {
#  chart            = "../.helm/ruettel-report-backend"
#  name             = "ruettel-backend"
#  namespace        = "ruettel-backend"
#  create_namespace = true
#  depends_on = [
#    google_container_cluster.primary
#  ]

#values = [
#  templatefile("../app-chart/values.yaml", {
#    project_id  = var.project_id
#    bucket_name = google_storage_bucket.main.name
#  })
#]
#}

#resource "helm_release" "example" {
#  name  = "test-chart"
#  chart = "../.helm/ruettel-report-backend"

  # wait for cluster to be created
#  depends_on = [
#    google_container_cluster.primary
#  ]
#}
