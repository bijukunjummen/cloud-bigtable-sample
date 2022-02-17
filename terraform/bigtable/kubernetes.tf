module "my-app-workload-identity" {
  source     = "terraform-google-modules/kubernetes-engine/google//modules/workload-identity"
  name       = "cloud-bigtable-sample-sa"
  namespace  = "default"
  project_id = data.google_client_config.current.project
  roles      = ["roles/bigtable.user", "roles/artifactregistry.reader"]
}

module "cluster1" {
  name                    = "cluster1"
  project_id              = data.google_client_config.current.project
  source                  = "terraform-google-modules/kubernetes-engine/google//modules/beta-public-cluster"
  version                 = "17.3.0"
  regional                = false
  region                  = var.kub_region
  network                 = "default"
  subnetwork              = "default"
  ip_range_pods           = ""
  ip_range_services       = ""
  zones                   = var.kub_zones
  release_channel         = "REGULAR"
  cluster_resource_labels = { "mesh_id" : "proj-${data.google_project.project.number}" }
  node_pools = [
    {
      name         = "default-node-pool"
      autoscaling  = false
      auto_upgrade = true

      node_count   = 3
      machine_type = "e2-standard-4"
    },
  ]

}
