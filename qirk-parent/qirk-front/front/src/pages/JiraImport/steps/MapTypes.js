import React from "react";
import { t } from "i18next";
import { makeStyles } from "@material-ui/core/styles";
import { Select, FormControl, InputLabel, MenuItem } from "@material-ui/core";
import constants from "../../../utils/constants";

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 120
  },
  jiraStatus: {
    minWidth: 260
  }
}));

const MapTypes = ({
  mappedTypes = {},
  setMappedTypes = () => {},
  types = []
}) => {
  const classes = useStyles();

  return (
    <>
      {types &&
        types.length > 0 &&
        types.map(s => {
          const id = s.id + "_task_type";

          return (
            <div key={s.id} style={{ display: "flex", paddingBottom: 32 }}>
              <div className={classes.jiraStatus} style={{ display: "flex" }}>
                <span style={{ margin: "auto" }}>{s.name}</span>
              </div>
              <div style={{ flex: 1 }}>
                <FormControl className={classes.formControl}>
                  <InputLabel htmlFor={id}>
                    Qirk {t(`TaskTypes.self`)}
                  </InputLabel>
                  <Select
                    id={id}
                    value={mappedTypes[s.id]}
                    onChange={e => {
                      const localStatusKey = e.target.value;
                      setMappedTypes(oldOnes => ({
                        ...oldOnes,
                        [s.id]: localStatusKey
                      }));
                    }}
                  >
                    {Object.keys(constants.TASK_TYPES).map(value => (
                      <MenuItem key={value} value={value}>
                        {t(`TaskTypes.${value}`)}
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

export default MapTypes;
