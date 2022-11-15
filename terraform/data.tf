data "aws_ami" "ecs_ami" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn-ami-*-amazon-ecs-optimized"]
  }
}

data "template_file" "init" {
  template = file("userdata.tpl")

  vars = {
    ecs_cluster_name = "${aws_ecs_cluster.nginx-terraform.name}"
  }
}