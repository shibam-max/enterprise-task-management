-- Sample data for development
INSERT INTO tasks (id, title, description, status, priority, assignee_id, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Setup Development Environment', 'Configure local development environment with all required tools', 'DONE', 'HIGH', '550e8400-e29b-41d4-a716-446655440101', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'Implement User Authentication', 'Add JWT-based authentication system', 'IN_PROGRESS', 'HIGH', '550e8400-e29b-41d4-a716-446655440101', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Create Task Management API', 'Develop REST APIs for task CRUD operations', 'IN_PROGRESS', 'MEDIUM', '550e8400-e29b-41d4-a716-446655440102', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440004', 'Design Database Schema', 'Create optimized database schema for tasks', 'DONE', 'HIGH', '550e8400-e29b-41d4-a716-446655440101', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440005', 'Setup CI/CD Pipeline', 'Configure automated testing and deployment', 'TODO', 'MEDIUM', '550e8400-e29b-41d4-a716-446655440103', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440006', 'Implement Caching Layer', 'Add Redis caching for improved performance', 'TODO', 'LOW', '550e8400-e29b-41d4-a716-446655440102', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440007', 'Add Monitoring and Logging', 'Integrate Prometheus and structured logging', 'TODO', 'MEDIUM', '550e8400-e29b-41d4-a716-446655440103', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440008', 'Write Unit Tests', 'Achieve 85%+ test coverage for all components', 'IN_PROGRESS', 'HIGH', '550e8400-e29b-41d4-a716-446655440101', NOW(), NOW());