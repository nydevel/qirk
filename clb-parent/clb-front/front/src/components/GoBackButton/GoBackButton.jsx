import React from "react";
import Button from "@material/react-button";
import "@material/react-button/dist/button.css";
import Icon from "@material-ui/core/Icon";
import { withRouter } from "react-router-dom";
import { t } from "i18next";

function GoBackButton({ pathToBack, history }) {
  return (
    <Button
      type="button"
      onClick={
        pathToBack ? () => history.push(pathToBack) : () => history.goBack()
      }
      icon={
        <Icon
          style={{
            transform: "rotate(180deg)"
          }}
        >
          double_arrow
        </Icon>
      }
    >
      {t("Go back")}
    </Button>
  );
}

export default withRouter(GoBackButton);
