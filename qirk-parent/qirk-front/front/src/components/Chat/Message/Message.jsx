import React from "react";
import { connect } from "react-redux";
import classNames from "classnames";
import { t } from "i18next";
import { fetchSenderInfo } from "../../../actions/chatActions";
import { uiUserFromUser } from "./../../../utils/variousUtils";
import ic_spinner_dots from "./../../../assets/icons/ic_spinner_dots.svg";
import { uiDateTime } from "../../../utils/timeUtils";

function Message(props) {
  const sender = props.senders.find(
    sender => sender.id === props.message.sender_id
  );
  if (!sender) {
    props.fetchSenderInfo(props.message.sender_id);
  }

  return (
    <div className="message-single-wrapper">
      <div
        className={classNames({
          "message-single": true,
          "my-message": sender && sender.id && sender.id === props.myId
        })}
        title={
          props.message.delivery_not_confirmed
            ? t("message_is_being_sent")
            : false
        }
      >
        <div className="message-sender">
          {sender && uiUserFromUser(sender)}
          {}
          {!sender && props.message.sender_id}
        </div>
        <div className="message-text">{props.message.message}</div>

        {props.message &&
          !props.message.delivery_not_confirmed &&
          props.message.timestamp &&
          props.message.timestamp.iso8601 && (
            <div
              style={{ textAlign: "right", fontSize: 11, fontWeight: "bold" }}
            >
              {uiDateTime(props.message.timestamp.iso8601)}
            </div>
          )}
        {props.message.delivery_not_confirmed && (
          <img className="spinner" src={ic_spinner_dots} alt="" />
        )}
      </div>
    </div>
  );
}

const mapStateToProps = state => ({
  senders: state.chat.senders,
  myId: state.user.id
});

export default connect(
  mapStateToProps,
  { fetchSenderInfo }
)(Message);
