import { TaskType } from "../enums/TaskType";
import { TaskPriority } from "../enums/TaskPriority";
import { TaskStatus } from "../enums/TaskStatus";

export type Task = {
  id: number;
  number: number;
  summary: string;
  reporter: { id: number };
  assignee: { id: number };
  created_at: string;
  updated_at: string;
  task_type: { name_code: TaskType };
  task_priority: { name_code: TaskPriority };
  task_status: { name_code: TaskStatus };
};
