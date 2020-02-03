import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Select, FormControl, InputLabel, MenuItem } from "@material-ui/core";
import { uiUserFromOrgMember } from "../../../utils/variousUtils";

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 120
  },
  jiraStatus: {
    minWidth: 260
  }
}));

const MapUsers = ({
  mappedUsers = {},
  setMappedUsers = () => {},
  users = [],
  modelMembers
}) => {
  const classes = useStyles();

  return (
    <>
      {users &&
        users.length > 0 &&
        users.map(s => {
          const id = s.id + "_user";

          return (
            <div key={s.id} style={{ display: "flex", paddingBottom: 32 }}>
              <div className={classes.jiraStatus} style={{ display: "flex" }}>
                <span style={{ margin: "auto" }}>
                  {s.username} {s.email}
                </span>
              </div>
              <div style={{ flex: 1 }}>
                <FormControl className={classes.formControl}>
                  <InputLabel htmlFor={id}>Qirk user</InputLabel>
                  <Select
                    id={id}
                    value={mappedUsers[s.id]}
                    onChange={e => {
                      const localStatusKey = e.target.value;
                      setMappedUsers(oldOnes => ({
                        ...oldOnes,
                        [s.id]: localStatusKey
                      }));
                    }}
                  >
                    {modelMembers.map(member => (
                      <MenuItem key={member.id} value={member.id}>
                        {uiUserFromOrgMember(member)}
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

export default MapUsers;
