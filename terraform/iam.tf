resource "aws_iam_role_policy_attachment" "nginx-terraform" {
  count = length(local.role_policy_arns)

  policy_arn = element(local.role_policy_arns, count.index)
  role       = aws_iam_role.nginx-terraform.name
}

resource "aws_iam_instance_profile" "nginx-terraform" {
  name = "nginx-terraform"
  role = aws_iam_role.nginx-terraform.name
}

resource "aws_iam_role" "nginx-terraform" {
  name = "nginx-terraform"
  path = "/"
  assume_role_policy = jsondecode({
    "Version" : "2012-10-17",
    "Statement" : [{
      "Action" : "sts:AssumeRole",
      "Principal" : {
        "Service" : "ec2,anazibaws,cin"
      },
      "Effect" : "Allow"
    }]

  })
}