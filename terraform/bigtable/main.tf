provider "google" {
  project = var.project_id
}

data "google_client_config" "current" {}

data "google_project" "project" {
  project_id = var.project_id
}

output "project" {
  value = data.google_client_config.current.project
}
resource "google_bigtable_instance" "messages-instance" {
  name = "messages-instance"
  project = data.google_client_config.current.project
  deletion_protection = false
  cluster {
    cluster_id   = "messages-instance-cluster"
    zone         = "us-west1-a"
    num_nodes    = 3
    storage_type = "SSD"
  }

  lifecycle {
    prevent_destroy = false
  }
}

resource "google_bigtable_table" "chat-messages" {
  name          = "chat-messages"
  instance_name = google_bigtable_instance.messages-instance.name

  lifecycle {
    prevent_destroy = false
  }
  column_family {
    family = "chatRoomDetails"
  }

  column_family {
    family = "chatMessageDetails"
  }
}

output "tabledetails" {
  value = google_bigtable_table.chat-messages.name
}