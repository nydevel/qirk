import React from "react";
import Checkbox from "./../../../components/Checkbox/Checkbox";

const SelectProjects = ({
  projects = [],
  selectedProjects = [],
  setSelectedProjects = () =>
    console.error("setSelectedProjects prop in SelectProjects is missing")
}) => {
  return (
    <>
      {projects &&
        projects.length > 0 &&
        projects.map(p => (
          <div key={p.id}>
            <Checkbox
              checked={selectedProjects.some(sp => sp === p.id)}
              label={p.name}
              className={`project_id_${p.id}`}
              onChange={e => {
                const checked = e.target.checked;

                if (checked) {
                  setSelectedProjects(sps => [...sps, p.id]);
                } else {
                  setSelectedProjects(sps => sps.filter(sp => sp !== p.id));
                }
              }}
            />
          </div>
        ))}
    </>
  );
};

export default SelectProjects;
