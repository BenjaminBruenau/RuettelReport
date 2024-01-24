# google_client_config and kubernetes provider must be explicitly specified like the following.
# Retrieve an access token as the Terraform runner
data "google_client_config" "default" {}

# GKE cluster
resource "google_container_cluster" "primary" {
  name     = var.cluster-name
  project  = var.project
  location = var.region

  # We can't create a cluster with no node pool defined, but we want to only use
  # separately managed node pools. So we create the smallest possible default
  # node pool and immediately delete it.
  remove_default_node_pool = true
  initial_node_count       = 1



  networking_mode = "VPC_NATIVE"
  ip_allocation_policy {
    cluster_ipv4_cidr_block  = "10.44.0.0/14"
    services_ipv4_cidr_block = "10.48.0.0/20"
  }

  #networking_mode = "VPC_NATIVE"
  #ip_allocation_policy {}
}

# Separately Managed Node Pool
resource "google_container_node_pool" "primary_nodes" {
  project    = var.project
  name       = "${google_container_cluster.primary.name}-node-pool"
  location   = var.region
  cluster    = google_container_cluster.primary.name
  initial_node_count = 3
  autoscaling {
    max_node_count = 3
    min_node_count = 1
  }


  node_config {
    oauth_scopes = [
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/cloud-platform"
    ]

    labels = {
      env = var.project
    }

    preemptible  = true
    disk_size_gb = 20
  }
}

module "gke_auth" {
  source               = "terraform-google-modules/kubernetes-engine/google//modules/auth"
  version              = "24.0.0"
  project_id           = var.project
  cluster_name         = google_container_cluster.primary.name
  location             = var.region
  depends_on           = [
    google_container_cluster.primary
  ]
}