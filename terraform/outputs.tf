output "jenkins_server_public_ip" {
  description = "Public IP address of Jenkins server"
  value       = aws_instance.jenkins_server.public_ip
}

output "application_server_public_ip" {
  description = "Public IP address of Application server"
  value       = aws_instance.application_server.public_ip
}

output "jenkins_ssh_command" {
  description = "SSH command to connect to Jenkins server"
  value       = "ssh -i <your-key>.pem ubuntu@${aws_instance.jenkins_server.public_ip}"
}

output "application_ssh_command" {
  description = "SSH command to connect to Application server"
  value       = "ssh -i <your-key>.pem ubuntu@${aws_instance.application_server.public_ip}"
}

output "jenkins_url" {
  description = "Jenkins web interface URL"
  value       = "http://${aws_instance.jenkins_server.public_ip}:8080"
}

output "application_url" {
  description = "Application health check URL"
  value       = "http://${aws_instance.application_server.public_ip}:8081/actuator/health"
}
