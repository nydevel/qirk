import React, { useState } from "react";
import { connect } from "react-redux";
import { t } from "i18next";
import { toast } from "react-toastify";
import Button from "@material/react-button";
import TextInput from "../../components/TextInput/TextInput";
import endpointsStatistics from "../../utils/endpointsStatistics";
import axiosStat from "../../utils/axiosStat";
import constants from "../../utils/constants";
import Page from "../../components/Page/Page";

function Feedback({ userId, userEmail, isSignedIn }) {
  const [fb, setFb] = useState("");
  const [requestInProgress, setRequestInProgress] = useState(false);

  const sendFeedback = async e => {
    e.preventDefault();

    try {
      setRequestInProgress(true);
      const response = await axiosStat.post(endpointsStatistics.POST_EVENT, {
        code: constants.STAT_FEEDBACK,
        feedback: fb,
        sender_id: userId || null,
        sender_email: userEmail || null
      });
      if (response && response.status === 200) {
        toast.success(t("sent"));
        setFb("");
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setRequestInProgress(false);
    }
  };

  return (
    <Page>
      <div
        style={{
          width: "50%",
          minWidth: 800,
          margin: "0 auto"
        }}
      >
        <h1>{t("send_us_feedback")}</h1>
        <form onSubmit={sendFeedback}>
          <fieldset disabled={requestInProgress}>
            <div>
              <TextInput
                textFieldStyle={{ width: "100%", minHeight: 300 }}
                maxLength="1023"
                label={t("your_feedback")}
                disabled={requestInProgress}
                textarea
                required
                value={fb}
                onChange={e => setFb(e.target.value)}
              />
            </div>

            {!isSignedIn && (
              <div style={{ paddingBottom: 28 }}>
                {t("provide_feedback_email_message")}
              </div>
            )}

            <div>
              <Button raised disabled={requestInProgress}>
                {t("send")}
              </Button>
            </div>
          </fieldset>
        </form>
      </div>
    </Page>
  );
}

export default connect(state => ({
  userId: state.user.id,
  userEmail: state.user.email,
  isSignedIn: state.auth.isSignedIn
}))(Feedback);
