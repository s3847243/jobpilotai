variable "region" {
  default = "ap-southeast-2"
}

variable "key_name" {
  description = "SSH key pair name"
  type        = string
}

variable "public_key_path" {
  description = "Path to your public SSH key"
  type        = string
}

variable "db_username" {
  type        = string
  description = "RDS username"
}

variable "db_password" {
  type        = string
  sensitive   = true
}

variable "db_name" {
  default     = "jobpilot"
}
