import React, { useEffect } from "react";
import PropTypes from "prop-types";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import classNames from "classnames";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Check from "@material-ui/icons/Check";
import StepConnector from "@material-ui/core/StepConnector";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import SelectProjects from "./steps/SelectProjects";
import Loader from "./../../components/Loading/Loading";
import MapStatuses from "./steps/MapStatuses";
import MapPriorities from "./steps/MapPriorities";
import MapTypes from "./steps/MapTypes";
import MapUsers from "./steps/MapUsers";
import MapProjects from "./steps/MapProjects";

const QontoConnector = withStyles({
  alternativeLabel: {
    top: 10,
    left: "calc(-50% + 16px)",
    right: "calc(50% + 16px)"
  },
  active: {
    "& $line": {
      borderColor: "#784af4"
    }
  },
  completed: {
    "& $line": {
      borderColor: "#784af4"
    }
  },
  line: {
    borderColor: "#eaeaf0",
    borderTopWidth: 3,
    borderRadius: 1
  }
})(StepConnector);

const useQontoStepIconStyles = makeStyles({
  root: {
    color: "#eaeaf0",
    display: "flex",
    height: 22,
    alignItems: "center"
  },
  active: {
    color: "#784af4"
  },
  circle: {
    width: 8,
    height: 8,
    borderRadius: "50%",
    backgroundColor: "currentColor"
  },
  completed: {
    color: "#784af4",
    zIndex: 1,
    fontSize: 18
  }
});

function QontoStepIcon(props) {
  const classes = useQontoStepIconStyles();
  const { active, completed } = props;

  return (
    <div
      className={classNames(classes.root, {
        [classes.active]: active
      })}
    >
      {completed ? (
        <Check className={classes.completed} />
      ) : (
        <div className={classes.circle} />
      )}
    </div>
  );
}

QontoStepIcon.propTypes = {
  active: PropTypes.bool,
  completed: PropTypes.bool
};

const useStyles = makeStyles(theme => ({
  root: {
    width: "90%"
  },
  button: {
    marginRight: theme.spacing(1)
  }
}));

function getSteps() {
  return [
    "Select projects",
    "Correlate projects",
    "Correlate statuses",
    "Correlate priorities",
    "Correlate types",
    "Correlate users"
  ];
}

function ImportStepper({
  projects,
  setSelectedProjects,
  selectedProjects,
  getSelectedProjectsData,

  importedStatuses,
  loadingSelectedProjects,
  mappedStatuses,
  setMappedStatuses,

  importedPriorities,
  mappedPriorities,
  setMappedPriorities,

  importedTypes,
  mappedTypes,
  setMappedTypes,

  modelMembers,
  importedUsers,
  mappedUsers,
  setMappedUsers,

  modelProjects,
  importedProjects,
  mappedProjects,
  setMappedProjects,

  postJiraImport,
  postingJiraImport
}) {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const steps = getSteps();

  useEffect(() => {
    function onStepSelected(step) {
      switch (step) {
        case 0:
          return;
        case 1:
          return getSelectedProjectsData();
        case 2:
          return;
        case 3:
          return;
        case 4:
          return;
        case 5:
          return;
        default:
          return;
      }
    }

    onStepSelected(activeStep);
  }, [activeStep]);

  function isNextBtnDisabled(step) {
    if (postingJiraImport || loadingSelectedProjects) {
      return true;
    }

    switch (step) {
      case 0:
        return !(selectedProjects && selectedProjects.length > 0);
      case 1:
        return false;
      case 2:
        return Object.keys(mappedStatuses).length < importedStatuses.length;
      case 3:
        return Object.keys(mappedPriorities).length < importedPriorities.length;
      case 4:
        return Object.keys(mappedTypes).length < importedTypes.length;
      case 5:
        return false;
      default:
        return false;
    }
  }

  function getStepContent(step) {
    switch (step) {
      case 0:
        return (
          <SelectProjects
            projects={projects}
            selectedProjects={selectedProjects}
            setSelectedProjects={setSelectedProjects}
          />
        );
      case 1:
        return loadingSelectedProjects ? (
          <Loader />
        ) : (
          <MapProjects
            mappedProjects={mappedProjects}
            setMappedProjects={setMappedProjects}
            modelProjects={modelProjects} // The options for mapping
            projects={importedProjects} // The ones that should be mapped
          />
        );

      case 2:
        return loadingSelectedProjects ? (
          <Loader />
        ) : (
          <MapStatuses
            mappedStatuses={mappedStatuses}
            setMappedStatuses={setMappedStatuses}
            statuses={importedStatuses}
          />
        );
      case 3:
        return loadingSelectedProjects ? (
          <Loader />
        ) : (
          <MapPriorities
            mappedPriorities={mappedPriorities}
            setMappedPriorities={setMappedPriorities}
            priorities={importedPriorities}
          />
        );
      case 4:
        return loadingSelectedProjects ? (
          <Loader />
        ) : (
          <MapTypes
            mappedTypes={mappedTypes}
            setMappedTypes={setMappedTypes}
            types={importedTypes}
          />
        );
      case 5:
        return loadingSelectedProjects ? (
          <Loader />
        ) : (
          <MapUsers
            users={importedUsers}
            mappedUsers={mappedUsers}
            modelMembers={modelMembers}
            setMappedUsers={setMappedUsers}
          />
        );
      default:
        return "Unknown step";
    }
  }

  const handleNext = async () => {
    if (activeStep === steps.length - 1) {
      postJiraImport();
    }
    setActiveStep(prevActiveStep => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep(prevActiveStep => prevActiveStep - 1);
  };

  return (
    <div className={classes.root}>
      <Stepper
        alternativeLabel
        activeStep={activeStep}
        connector={<QontoConnector />}
      >
        {steps.map(label => (
          <Step key={label}>
            <StepLabel StepIconComponent={QontoStepIcon}>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <div>
        {activeStep === steps.length ? (
          <div>
            <Typography className={classes.instructions}>
              Import in progress. It might take some time.
            </Typography>
          </div>
        ) : (
          <div>
            {getStepContent(activeStep)}
            <div>
              <Button
                disabled={activeStep === 0}
                onClick={handleBack}
                className={classes.button}
              >
                Back
              </Button>
              <Button
                variant="contained"
                color="primary"
                onClick={handleNext}
                disabled={isNextBtnDisabled(activeStep)}
                className={classes.button}
              >
                {activeStep === steps.length - 1 ? "Import" : "Next"}
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default ImportStepper;
