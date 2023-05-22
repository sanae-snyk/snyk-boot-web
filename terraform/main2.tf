

resource "aws_iam_account_password_policy" "strict" {
  minimum_password_length        = 8
  require_lowercase_characters   = true
  require_numbers                = true
  require_uppercase_characters   = true
  allow_users_to_change_password = true
  password_reuse_prevention      = 24
  max_password_age                = 3
  require_symbols = true
}

module "vpc" {
  source = "./modules/vpc"
}

module "subnet"  {
  source = "./modules/subnet"
  vpc_id = module.vpc.vpc_id
}

module "storage" {
  source = "./modules/storage"

  acl = var.s3_acl
  db_password = "supersecret"
  db_username = "snyk"
  environment = "dev"
  private_subnet = [module.subnet.subnet_id]
  vpc_id = module.vpc.vpc_id
}

module "instance" {
  source                 = "git@github.com:terraform-aws-modules/terraform-aws-ec2-instance.git"
  ami                    = var.ami
  instance_type          = "t2.micro"
  name                   = "example-server"
  instance_count         = 1

  vpc_security_group_ids = [module.vpc.vpc_sg_id]
  subnet_id              = module.subnet.subnet_id

  tags = {
    Terraform            = "true"
    Environment          = "dev"
  }
}
