import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Select, FormControl, InputLabel, MenuItem } from "@material-ui/core";
import constants from "../../../utils/constants";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 120
  },
  jiraStatus: {
    minWidth: 260
  }
}));

const MapStatuses = ({
  mappedStatuses = {},
  setMappedStatuses = () => {},
  statuses = []
}) => {
  const{t}=useTranslation()
  const classes = useStyles();

  return (
    <>
      {statuses &&
        statuses.length > 0 &&
        statuses.map(s => {
          const id = s.id + "_task_status";

          return (
            <div key={s.id} style={{ display: "flex", paddingBottom: 32 }}>
              <div className={classes.jiraStatus} style={{ display: "flex" }}>
                <span style={{ margin: "auto" }}>{s.name}</span>
              </div>
              <div style={{ flex: 1 }}>
                <FormControl className={classes.formControl}>
                  <InputLabel htmlFor={id}>
                    Qirk {t(`TaskStatuses.self`)}
                  </InputLabel>
                  <Select
                    id={id}
                    value={mappedStatuses[s.id]}
                    onChange={e => {
                      const localStatusKey = e.target.value;
                      setMappedStatuses(oldOnes => ({
                        ...oldOnes,
                        [s.id]: localStatusKey
                      }));
                    }}
                  >
                    {Object.keys(constants.TASK_STATUSES).map(value => (
                      <MenuItem key={value} value={value}>
                        {t(`TaskStatuses.${value}`)}
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

export default MapStatuses;
