import { Project } from "./Project";

export type Favorite = {
  id: number;
  project: Project;
  can_create_task: boolean;
};
