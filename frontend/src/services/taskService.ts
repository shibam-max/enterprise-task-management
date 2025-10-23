import { api } from './api';
import { Task } from '../store/taskSlice';

export const taskService = {
  getAllTasks: () => api.get<Task[]>('/tasks'),
  
  getTaskById: (id: string) => api.get<Task>(`/tasks/${id}`),
  
  createTask: (task: Omit<Task, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<Task>('/tasks', task),
  
  updateTask: (id: string, updates: Partial<Task>) => 
    api.put<Task>(`/tasks/${id}`, updates),
  
  deleteTask: (id: string) => api.delete(`/tasks/${id}`),
  
  getTasksByStatus: (status: string) => 
    api.get<Task[]>(`/tasks/status/${status}`),
  
  getTasksByAssignee: (assigneeId: string) => 
    api.get<Task[]>(`/tasks/assignee/${assigneeId}`),
};