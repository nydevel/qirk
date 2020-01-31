import React from "react";
import Page from "../Page/Page";
import PrivateComponent from "../PrivateComponent/PrivateComponent";

function PrivatePage({ children, onLoginConfirmed }) {
  return (
    <Page>
      <PrivateComponent onLoginConfirmed={onLoginConfirmed}>
        {children}
      </PrivateComponent>
    </Page>
  );
}

export default PrivatePage;
