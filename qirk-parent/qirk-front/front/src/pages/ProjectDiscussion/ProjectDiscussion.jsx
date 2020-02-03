import React from "react";
import { connect } from "react-redux";
import { t } from "i18next";
import Chat from "../../components/Chat/Chat";
import constants from "../../utils/constants";
import Page from "../../components/Page/Page";
import WithFetch from "../../components/WithFetch/WithFetch";

function ProjectDiscussion(props) {
  return (
    <Page>
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        <h1>{t("discussion_label")}</h1>
        <Chat
          noLabel={true}
          chatType={constants.PROJECT}
          chatId={props.projectInfo && props.projectInfo.id}
        />
      </WithFetch>
    </Page>
  );
}

export default connect(state => ({
  projectInfo: state.project.info
}))(ProjectDiscussion);
