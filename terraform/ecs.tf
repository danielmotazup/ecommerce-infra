resource "aws_ecs_cluster" "nginx-terraform" {
  name = "nginx-terraform"
}

resource "aws_ecs_task_definition" "nginx-terraform" {
  family                = "nginx-terraform"
  container_definitions = jsondecode([{
    name = "nginx"
    image = "rmnobarra/nginx:blue"
    cpu = 10
    memory = 128
    essential = true
    portMappings = [{
      containerPort = 80
      hostPort = 80
    }]
  }])
  depends_on = [aws_instance.nginx-terraform]
}

resource "aws_ecs_service" "nginx-terraform" {
  name = "nginx-terraform"
  cluster = aws_ecs_cluster.nginx-terraform.id
  task_definition = aws_ecs_task_definition.nginx-terraform.arn
  desired_count = 1

  ordered_placement_strategy {
    type = "binpack"
    field = "cpu"
  }
}