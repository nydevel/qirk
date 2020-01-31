import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Select, FormControl, InputLabel, MenuItem } from "@material-ui/core";

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 120
  },
  jiraStatus: {
    minWidth: 260
  }
}));

const MapProjects = ({
  mappedProjects = {},
  setMappedProjects = () => {},
  projects = [],
  modelProjects
}) => {
  const classes = useStyles();

  return (
    <>
      {projects &&
        projects.length > 0 &&
        projects.map(s => {
          const id = s.id + "_user";

          return (
            <div key={s.id} style={{ display: "flex", paddingBottom: 32 }}>
              <div className={classes.jiraStatus} style={{ display: "flex" }}>
                <span style={{ margin: "auto" }}>{s.name}</span>
              </div>

              <div style={{ flex: 1 }}>
                <FormControl className={classes.formControl}>
                  <InputLabel htmlFor={id}>Qirk project</InputLabel>
                  <Select
                    id={id}
                    value={mappedProjects[s.id]}
                    onChange={e => {
                      const localStatusKey = e.target.value;
                      setMappedProjects(oldOnes => ({
                        ...oldOnes,
                        [s.id]: localStatusKey
                      }));
                    }}
                  >
                    <MenuItem value={-1}>{"Create new project"}</MenuItem>
                    {modelProjects.map(project => (
                      <MenuItem key={project.id} value={project.id}>
                        Update {project.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </div>
            </div>
          );
        })}
    </>
  );
};

export default MapProjects;
